/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import java.util.*;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IPaintedCart;
import mods.railcraft.api.carts.IRefuelableCart;
import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.common.carts.LinkageManager;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RoutingLogic {

    private Deque<Condition> conditions;
    private RoutingLogicException error;

    public static RoutingLogic buildLogic(LinkedList<String> data) {
        return new RoutingLogic(data);
    }

    private RoutingLogic(LinkedList<String> data) {
        try {
            if (data != null)
                parseTable(data);
            else
                throw new RoutingLogicException("railcraft.gui.routing.logic.blank", null);
        } catch (RoutingLogicException ex) {
            error = ex;
        }
    }

    public RoutingLogicException getError() {
        return error;
    }

    public boolean isValid() {
        return conditions != null;
    }

    private void parseTable(LinkedList<String> data) throws RoutingLogicException {
        Deque<Condition> stack = new LinkedList<Condition>();
        Iterator<String> it = data.descendingIterator();
        while (it.hasNext()) {
            String line = it.next().trim();
            stack.push(parseLine(line, stack));
        }
        conditions = stack;
    }

    private boolean canRouteCart(EntityMinecart cart) {
        Train train = LinkageManager.instance().getTrain(cart);
        if (train == null)
            return false;
        if (train.size() == 1)
            return true;
        if (train.isTrainEnd(cart)) {
            if (cart instanceof IRoutableCart)
                return true;
            if (cart instanceof IPaintedCart)
                return true;
            if (cart instanceof IRefuelableCart)
                return true;
        }
        return false;
    }

    public boolean matches(IRoutingTile tile, EntityMinecart cart) {
        if (conditions == null)
            return false;
        if (!canRouteCart(cart))
            return false;
        for (Condition condition : conditions) {
            if (condition.matches(tile, cart))
                return true;
        }
        return false;
    }

    private Condition parseLine(String line, Deque<Condition> stack) throws RoutingLogicException {
        try {
            if (line.startsWith("Dest="))
                return new DestCondition(line);
            if (line.startsWith("Color="))
                return new ColorCondition(line);
            if (line.startsWith("Owner="))
                return new OwnerCondition(line);
            if (line.startsWith("Name="))
                return new NameCondition(line);
            if (line.startsWith("Type="))
                return new TypeCondition(line);
            if (line.startsWith("NeedsRefuel="))
                return new RefuelCondition(line);
            if (line.startsWith("Ridden="))
                return new RiddenCondition(line);
            if (line.startsWith("Riding="))
                return new RidingCondition(line);
            if (line.startsWith("Redstone="))
                return new RedstoneCondition(line);
        } catch (RoutingLogicException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RoutingLogicException("railcraft.gui.routing.logic.malformed.syntax", line);
        }
        try {
            if (line.equals("NOT"))
                return new NOT(stack.pop());
            if (line.equals("AND"))
                return new AND(stack.pop(), stack.pop());
            if (line.equals("OR"))
                return new OR(stack.pop(), stack.pop());
        } catch (NoSuchElementException ex) {
            throw new RoutingLogicException("railcraft.gui.routing.logic.insufficient.operands", line);
        }
        throw new RoutingLogicException("railcraft.gui.routing.logic.unrecognized.keyword", line);
    }

    public class RoutingLogicException extends Exception {

        private final ToolTip tips = new ToolTip();

        public RoutingLogicException(String errorTag, String line) {
            tips.add(EnumChatFormatting.RED + LocalizationPlugin.translate(errorTag));
            if (line != null)
                tips.add("\"" + line + "\"");
        }

        public ToolTip getToolTip() {
            return tips;
        }

    }

    private abstract class Condition {

        public abstract boolean matches(IRoutingTile tile, EntityMinecart cart);

    }

    private class NOT extends Condition {

        private final Condition a;

        public NOT(Condition a) {
            this.a = a;
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            return !a.matches(tile, cart);
        }

    }

    private class AND extends Condition {

        private final Condition a, b;

        public AND(Condition a, Condition b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            return a.matches(tile, cart) && b.matches(tile, cart);
        }

    }

    private class OR extends Condition {

        private final Condition a, b;

        public OR(Condition a, Condition b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            return a.matches(tile, cart) || b.matches(tile, cart);
        }

    }

    private class DestCondition extends Condition {

        private final String dest;

        public DestCondition(String dest) {
            this.dest = dest.replace("Dest=", "");
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            if (cart instanceof IRoutableCart) {
                String cartDest = ((IRoutableCart) cart).getDestination();
                if (dest.equals("null"))
                    return cartDest == null || cartDest.equals("");
                return cartDest.startsWith(dest);
            }
            return false;
        }

    }

    private class OwnerCondition extends Condition {

        private final String owner;

        public OwnerCondition(String owner) {
            this.owner = owner.replace("Owner=", "");
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            return owner.equals(CartTools.getCartOwner(cart).getName());
        }

    }

    private class NameCondition extends Condition {

        private final String name;

        public NameCondition(String name) {
            this.name = name.replace("Name=", "");
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            String customName = cart.func_95999_t();
            if (customName == null)
                return "null".equals(name);
            return name.equals(customName);
        }

    }

    private class TypeCondition extends Condition {

        private final String type;

        public TypeCondition(String name) {
            this.type = name.replace("Type=", "");
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            ItemStack stack = cart.getCartItem();
            if (stack == null || stack.getItem() == null)
                return false;
            UniqueIdentifier itemName = GameRegistry.findUniqueIdentifierFor(stack.getItem());
            if (itemName != null) {
                String nameString = itemName.modId + ":" + itemName.name;
                return nameString.equalsIgnoreCase(type);
            }
            return false;
        }

    }

    private class RefuelCondition extends Condition {

        private final boolean needsRefuel;

        public RefuelCondition(String line) {
            this.needsRefuel = Boolean.parseBoolean(line.replace("NeedsRefuel=", ""));
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            if (cart instanceof IRefuelableCart) {
                IRefuelableCart rCart = (IRefuelableCart) cart;
                return needsRefuel == rCart.needsRefuel();
            }
            return false;
        }

    }

    private class RiddenCondition extends Condition {

        private final boolean ridden;

        public RiddenCondition(String line) {
            this.ridden = Boolean.parseBoolean(line.replace("Ridden=", ""));
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            for (EntityMinecart c : LinkageManager.instance().getCartsInTrain(cart)) {
                if (c.riddenByEntity != null && c.riddenByEntity instanceof EntityPlayer)
                    return ridden;
            }
            return !ridden;
        }

    }

    private class RidingCondition extends Condition {

        private final String username;

        public RidingCondition(String line) {
            this.username = line.replace("Riding=", "");
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            for (EntityMinecart c : LinkageManager.instance().getCartsInTrain(cart)) {
                if (c.riddenByEntity != null && c.riddenByEntity instanceof EntityPlayer)
                    return c.riddenByEntity.getCommandSenderName().equals(username);
            }
            return false;
        }

    }

    private class RedstoneCondition extends Condition {

        private final boolean powered;

        public RedstoneCondition(String line) {
            this.powered = Boolean.parseBoolean(line.replace("Redstone=", ""));
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            return powered == tile.isPowered();
        }

    }

    private class ColorCondition extends Condition {

        private final EnumColor primary, secondary;

        public ColorCondition(String color) throws RoutingLogicException {
            String params = color.replace("Color=", "");
            String[] colors = params.split(",");
            if (colors[0].equals("Any"))
                primary = null;
            else {
                primary = EnumColor.fromName(colors[0]);
                if (primary == null)
                    throw new RoutingLogicException("railcraft.gui.routing.logic.unrecognized.keyword", colors[0]);
            }
            if (colors.length == 1 || colors[1].equals("Any"))
                secondary = null;
            else {
                secondary = EnumColor.fromName(colors[1]);
                if (secondary == null)
                    throw new RoutingLogicException("railcraft.gui.routing.logic.unrecognized.keyword", colors[1]);
            }
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            if (cart instanceof IPaintedCart) {
                IPaintedCart pCart = (IPaintedCart) cart;
                return (primary == null || primary.ordinal() == pCart.getPrimaryColor()) && (secondary == null || secondary.ordinal() == pCart.getSecondaryColor());
            }
            return false;
        }

    }
}
