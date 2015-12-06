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
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IPaintedCart;
import mods.railcraft.api.carts.IRefuelableCart;
import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RoutingLogic {

    public static final String REGEX_SYMBOL = "\\?";

    private Deque<Condition> conditions;
    private RoutingLogicException error;

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

    public static RoutingLogic buildLogic(LinkedList<String> data) {
        return new RoutingLogic(data);
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

    private EntityMinecart getRoutableCart(EntityMinecart cart) {
        Train train = Train.getTrain(cart);
        if (train == null)
            return null;
        if (train.size() == 1)
            return cart;
        if (train.isTrainEnd(cart)) {
            if (cart instanceof IRoutableCart)
                return cart;
            if (cart instanceof IPaintedCart)
                return cart;
            if (cart instanceof IRefuelableCart)
                return cart;
        }
        return train.getLocomotive();
    }

    public boolean matches(IRoutingTile tile, EntityMinecart cart) {
        if (conditions == null)
            return false;
        EntityMinecart controllingCart = getRoutableCart(cart);
        if (controllingCart == null)
            return false;
        for (Condition condition : conditions) {
            if (condition.matches(tile, controllingCart))
                return true;
        }
        return false;
    }

    private Condition parseLine(String line, Deque<Condition> stack) throws RoutingLogicException {
        try {
            if (line.startsWith("Dest"))
                return new DestCondition(line);
            if (line.startsWith("Color"))
                return new ColorCondition(line);
            if (line.startsWith("Owner"))
                return new OwnerCondition(line);
            if (line.startsWith("Name"))
                return new NameCondition(line);
            if (line.startsWith("Type"))
                return new TypeCondition(line);
            if (line.startsWith("NeedsRefuel"))
                return new RefuelCondition(line);
            if (line.startsWith("Ridden"))
                return new RiddenCondition(line);
            if (line.startsWith("Riding"))
                return new RidingCondition(line);
            if (line.startsWith("Redstone"))
                return new RedstoneCondition(line);
            if (line.startsWith("Loco"))
                return new LocoCondition(line);
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

    private abstract class ParsedCondition extends Condition {

        public final String keyword, line, value;
        public final boolean isRegex;

        private ParsedCondition(String keyword, boolean supportsRegex, String line) throws RoutingLogicException {
            this.keyword = keyword;
            this.line = line;
            String keywordMatch = keyword + REGEX_SYMBOL + "?=";
            if (!line.matches(keywordMatch + ".*"))
                throw new RoutingLogicException("railcraft.gui.routing.logic.unrecognized.keyword", line);
            this.isRegex = line.matches(keyword + REGEX_SYMBOL + "=.*");
            if (!supportsRegex && isRegex)
                throw new RoutingLogicException("railcraft.gui.routing.logic.regex.unsupported", line);
            this.value = line.replaceFirst(keywordMatch, "");
            if (isRegex)
                try {
                    Pattern.compile(value);
                } catch (PatternSyntaxException ex) {
                    throw new RoutingLogicException("railcraft.gui.routing.logic.regex.invalid", line);
                }
        }

        @Override
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

    private class DestCondition extends ParsedCondition {

        public DestCondition(String line) throws RoutingLogicException {
            super("Dest", true, line);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            if (cart instanceof IRoutableCart) {
                String cartDest = ((IRoutableCart) cart).getDestination();
                if (value.equals("null"))
                    return cartDest == null || cartDest.equals("");
                if (isRegex)
                    return cartDest.matches(value);
                return cartDest.startsWith(value);
            }
            return false;
        }

    }

    private class OwnerCondition extends ParsedCondition {

        public OwnerCondition(String line) throws RoutingLogicException {
            super("Owner", false, line);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            return value.equalsIgnoreCase(CartTools.getCartOwner(cart).getName());
        }

    }

    private class NameCondition extends ParsedCondition {

        public NameCondition(String line) throws RoutingLogicException {
            super("Name", true, line);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            String customName = cart.func_95999_t();
            if (customName == null)
                return "null".equals(value);
            if (isRegex)
                return customName.matches(value);
            return value.equals(customName);
        }

    }

    private class TypeCondition extends ParsedCondition {

        public TypeCondition(String line) throws RoutingLogicException {
            super("Type", false, line);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            ItemStack stack = cart.getCartItem();
            if (stack == null || stack.getItem() == null)
                return false;
            UniqueIdentifier itemName = GameRegistry.findUniqueIdentifierFor(stack.getItem());
            if (itemName != null) {
                String nameString = itemName.modId + ":" + itemName.name;
                return nameString.equalsIgnoreCase(value);
            }
            return false;
        }

    }

    private class RefuelCondition extends ParsedCondition {

        private final boolean needsRefuel;

        public RefuelCondition(String line) throws RoutingLogicException {
            super("NeedsRefuel", false, line);
            this.needsRefuel = Boolean.parseBoolean(value);
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

    private class RiddenCondition extends ParsedCondition {

        private final boolean ridden;

        public RiddenCondition(String line) throws RoutingLogicException {
            super("Ridden", false, line);
            this.ridden = Boolean.parseBoolean(value);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            for (EntityMinecart c : Train.getTrain(cart)) {
                if (c != null && c.riddenByEntity instanceof EntityPlayer)
                    return ridden;
            }
            return !ridden;
        }

    }

    private class RidingCondition extends ParsedCondition {

        public RidingCondition(String line) throws RoutingLogicException {
            super("Riding", false, line);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            for (EntityMinecart c : Train.getTrain(cart)) {
                if (c != null && c.riddenByEntity instanceof EntityPlayer)
                    return c.riddenByEntity.getCommandSenderName().equalsIgnoreCase(value);
            }
            return false;
        }

    }

    private class RedstoneCondition extends ParsedCondition {

        private final boolean powered;

        public RedstoneCondition(String line) throws RoutingLogicException {
            super("Redstone", false, line);
            this.powered = Boolean.parseBoolean(value);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
            return powered == tile.isPowered();
        }

    }

    private class ColorCondition extends ParsedCondition {

        private final EnumColor primary, secondary;

        public ColorCondition(String line) throws RoutingLogicException {
            super("Color", false, line);
            String[] colors = value.split(",");
            if (colors[0].equals("Any") || colors[0].equals("*"))
                primary = null;
            else {
                primary = EnumColor.fromName(colors[0]);
                if (primary == null)
                    throw new RoutingLogicException("railcraft.gui.routing.logic.unrecognized.keyword", colors[0]);
            }
            if (colors.length == 1 || colors[1].equals("Any") || colors[1].equals("*"))
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
    
    private class LocoCondition extends ParsedCondition {

        public LocoCondition(String line) throws RoutingLogicException {
            super("Loco", false, line);
        }

        @Override
        public boolean matches(IRoutingTile tile, EntityMinecart cart) {
        	if (cart instanceof EntityLocomotive) {
        		EntityLocomotive loco = (EntityLocomotive)cart;
	            if (value.equalsIgnoreCase("Electric"))
	                return loco.getCartType() == EnumCart.LOCO_ELECTRIC;
	            if (value.equalsIgnoreCase("Steam"))
	            	return loco.getCartType() == EnumCart.LOCO_STEAM_SOLID;
	            if (value.equalsIgnoreCase("Steam_Magic"))
	            	return loco.getCartType() == EnumCart.LOCO_STEAM_MAGIC;
	            if (value.equalsIgnoreCase("None"))
	            	return false;
        	}
        	if (value.equalsIgnoreCase("None"))
        		return true;
            return false;
        }

    }
}
