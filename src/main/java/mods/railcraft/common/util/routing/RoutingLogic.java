/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.routing;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.carts.IPaintedCart;
import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.misc.Optionals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class RoutingLogic {

    private static final String REGEX_SYMBOL = "\\?";

    private Deque<Expression> expressions;
    private RoutingLogicException error;

    private RoutingLogic(@Nullable Deque<String> data) {
        try {
            if (data != null)
                parseTable(data);
            else
                throw new RoutingLogicException("gui.railcraft.routing.logic.blank", null);
        } catch (RoutingLogicException ex) {
            error = ex;
        }
    }

    public static RoutingLogic buildLogic(@Nullable Deque<String> data) {
        return new RoutingLogic(data);
    }

    public @Nullable RoutingLogicException getError() {
        return error;
    }

    public boolean isValid() {
        return expressions != null;
    }

    private void parseTable(Deque<String> data) throws RoutingLogicException {
        Deque<Expression> stack = new ArrayDeque<>();
        Iterator<String> it = data.descendingIterator();
        while (it.hasNext()) {
            String line = it.next().trim();
            if (line.startsWith("//") || line.startsWith("#"))
                continue;
            stack.push(parseLine(line, stack));
        }
        expressions = stack;
    }

    private EntityMinecart getRoutableCart(EntityMinecart cart) {
        // FIXME Train: Will this cause desync?
        // Note that this doesn't actually change the behavior as link information has never been available on the client.
        return Train.get(cart).map(train -> {
            if (train.size() <= 1)
                return cart;
            if (train.isTrainEnd(cart)) {
                if (cart instanceof IRoutableCart)
                    return cart;
                if (cart instanceof IPaintedCart)
                    return cart;
                if (cart instanceof INeedsFuel)
                    return cart;
            }
            return Optionals.get(train.getHeadLocomotive(), cart);
        }).orElse(cart);
    }

    public boolean matches(ITileRouting tile, EntityMinecart cart) {
        return evaluate(tile, cart) != NO_POWER;
    }

    public int evaluate(ITileRouting tile, EntityMinecart cart) {
        if (expressions == null)
            return NO_POWER;
        EntityMinecart controllingCart = getRoutableCart(cart);
        return expressions.stream().mapToInt(expression -> expression.evaluate(tile, controllingCart)).filter(value -> value != NO_POWER).findFirst().orElse(NO_POWER);
    }

    private Expression parseLine(String line, Deque<Expression> stack) throws RoutingLogicException {
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
            if (line.startsWith("Rider"))
                return new RiderCondition(line);
            if (line.startsWith("Redstone"))
                return new RedstoneCondition(line);
            if (line.startsWith("Loco"))
                return new LocoCondition(line);
        } catch (RoutingLogicException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RoutingLogicException("gui.railcraft.routing.logic.malformed.syntax", line);
        }
        if (Objects.equals(line, "TRUE"))
            return ConstantCondition.TRUE;
        if (Objects.equals(line, "FALSE"))
            return ConstantCondition.FALSE;
        try {
            return new ConstantExpression(Integer.parseInt(line));
        } catch (NumberFormatException ignored) {
            // not an integer; pass through
        } catch (IllegalArgumentException ex) {
            throw new RoutingLogicException("gui.railcraft.routing.logic.constant.invalid", line);
        }
        try {
            if (Objects.equals(line, "NOT"))
                return new NOT((Condition) stack.pop());
            if (Objects.equals(line, "AND"))
                return new AND((Condition) stack.pop(), (Condition) stack.pop());
            if (Objects.equals(line, "OR"))
                return new OR((Condition) stack.pop(), (Condition) stack.pop());
            if (Objects.equals(line, "IF"))
                return new IF((Condition) stack.pop(), stack.pop(), stack.pop());
        } catch (NoSuchElementException ex) {
            throw new RoutingLogicException("gui.railcraft.routing.logic.insufficient.operands", line);
        } catch (ClassCastException ex) {
            throw new RoutingLogicException("gui.railcraft.routing.logic.operand.invalid", line);
        }
        throw new RoutingLogicException("gui.railcraft.routing.logic.unrecognized.keyword", line);
    }

    public static class RoutingLogicException extends Exception {

        private final ToolTip tips = new ToolTip();

        RoutingLogicException(String errorTag, @Nullable String line) {
            tips.add(TextFormatting.RED + LocalizationPlugin.translate(errorTag));
            if (line != null)
                tips.add("\"" + line + "\"");
        }

        public ToolTip getToolTip() {
            return tips;
        }

    }

    @SuppressWarnings("NewClassNamingConvention")
    private interface Expression {

        int evaluate(ITileRouting tile, EntityMinecart cart);

    }

    @SuppressWarnings("NewClassNamingConvention")
    private interface Condition extends Expression {

        @Override
        default int evaluate(ITileRouting tile, EntityMinecart cart) {
            return matches(tile, cart) ? FULL_POWER : NO_POWER;
        }

        boolean matches(ITileRouting tile, EntityMinecart cart);

    }

    private abstract static class ParsedCondition implements Condition {

        public final String value;
        final boolean isRegex;

        ParsedCondition(String keyword, boolean supportsRegex, String line) throws RoutingLogicException {
            String keywordMatch = keyword + REGEX_SYMBOL + "?=";
            if (!line.matches(keywordMatch + ".*"))
                throw new RoutingLogicException("gui.railcraft.routing.logic.unrecognized.keyword", line);
            this.isRegex = line.matches(keyword + REGEX_SYMBOL + "=.*");
            if (!supportsRegex && isRegex)
                throw new RoutingLogicException("gui.railcraft.routing.logic.regex.unsupported", line);
            this.value = line.replaceFirst(keywordMatch, "");
            if (isRegex)
                validateRegex(line);
        }

        protected void validateRegex(String line) throws RoutingLogicException {
            try {
                Pattern.compile(value);
            } catch (PatternSyntaxException ex) {
                throw new RoutingLogicException("gui.railcraft.routing.logic.regex.invalid", line);
            }
        }

        @Override
        public abstract boolean matches(ITileRouting tile, EntityMinecart cart);

    }

    private static class IF implements Expression {

        private final Condition cond;
        private final Expression then, else_;

        public IF(Condition cond, Expression then, Expression else_) {
            this.cond = cond;
            this.then = then;
            this.else_ = else_;
        }

        @Override
        public int evaluate(ITileRouting tile, EntityMinecart cart) {
            return (cond.matches(tile, cart) ? then : else_).evaluate(tile, cart);
        }

    }

    private static class NOT implements Condition {

        private final Condition a;

        public NOT(Condition a) {
            this.a = a;
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return !a.matches(tile, cart);
        }

    }

    private static class AND implements Condition {

        private final Condition a, b;

        public AND(Condition a, Condition b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return a.matches(tile, cart) && b.matches(tile, cart);
        }

    }

    private static class OR implements Condition {

        private final Condition a, b;

        public OR(Condition a, Condition b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return a.matches(tile, cart) || b.matches(tile, cart);
        }

    }

    private static class ConstantExpression implements Expression {

        private final int value;

        ConstantExpression(int value) {
            if (value < NO_POWER || value > FULL_POWER)
                throw new IllegalArgumentException("Illegal constant value");
            this.value = value;
        }

        @Override
        public int evaluate(ITileRouting tile, EntityMinecart cart) {
            return value;
        }

    }

    private static final class ConstantCondition implements Condition {

        static final ConstantCondition TRUE = new ConstantCondition(true);
        static final ConstantCondition FALSE = new ConstantCondition(false);

        private final boolean value;

        private ConstantCondition(boolean value) {
            this.value = value;
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return value;
        }

    }

    private static class DestCondition extends ParsedCondition {

        DestCondition(String line) throws RoutingLogicException {
            super("Dest", true, line);
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            if (cart instanceof IRoutableCart) {
                String cartDest = ((IRoutableCart) cart).getDestination();
                if (StringUtils.equalsIgnoreCase("null", value))
                    return StringUtils.isBlank(cartDest);
                if (StringUtils.isBlank(cartDest))
                    return false;
                if (isRegex)
                    return cartDest.matches(value);
                return cartDest.startsWith(value);
            }
            return false;
        }

    }

    private static class OwnerCondition extends ParsedCondition {

        OwnerCondition(String line) throws RoutingLogicException {
            super("Owner", false, line);
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return StringUtils.equalsIgnoreCase(value, CartToolsAPI.getCartOwner(cart).getName());
        }

    }

    private class NameCondition extends ParsedCondition {

        NameCondition(String line) throws RoutingLogicException {
            super("Name", true, line);
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            if (!cart.hasCustomName())
                return StringUtils.equalsIgnoreCase("null", value);
            String customName = cart.getName();
            if (isRegex)
                return customName.matches(value);
            return StringUtils.equalsIgnoreCase(customName, value);
        }

    }

    private static class TypeCondition extends ParsedCondition {

        TypeCondition(String line) throws RoutingLogicException {
            super("Type", false, line);
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return value.equalsIgnoreCase(String.valueOf(EntityList.getKey(cart)));
        }

    }

    private static class RefuelCondition extends ParsedCondition {

        private final boolean needsRefuel;

        RefuelCondition(String line) throws RoutingLogicException {
            super("NeedsRefuel", false, line);
            this.needsRefuel = Boolean.parseBoolean(value);
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return Train.streamCarts(cart).flatMap(Streams.toType(INeedsFuel.class)).anyMatch(needs -> needs.needsFuel() == needsRefuel);
        }

    }

    private static class RiderCondition extends ParsedCondition {
        private final String[] tokens;

        RiderCondition(String line) throws RoutingLogicException {
            super("Rider", true, line);
            tokens = value.split(":");
            if (isRegex)
                switch (tokens[0].toLowerCase(Locale.ROOT)) {
                    case "any":
                    case "none":
                    case "mob":
                    case "animal":
                    case "unnamed":
                    case "entity":
                        throw new RoutingLogicException("gui.railcraft.routing.logic.regex.unsupported", line);
                    case "named":
                    case "player":
                        if (tokens.length == 1)
                            throw new RoutingLogicException("gui.railcraft.routing.logic.regex.unsupported", line);
                }
            switch (tokens[0].toLowerCase(Locale.ROOT)) {
                case "any":
                case "none":
                case "mob":
                case "animal":
                case "unnamed":
                    if (tokens.length > 1)
                        throw new RoutingLogicException("gui.railcraft.routing.logic.malformed.syntax", line);
                    break;
                case "entity":
                    if (tokens.length == 1)
                        throw new RoutingLogicException("gui.railcraft.routing.logic.malformed.syntax", line);
                    break;
                case "named":
                case "player":
                    if (tokens.length > 2)
                        throw new RoutingLogicException("gui.railcraft.routing.logic.malformed.syntax", line);
                    break;
                default:
                    throw new RoutingLogicException("gui.railcraft.routing.logic.unrecognized.keyword", line);
            }
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            switch (tokens[0].toLowerCase(Locale.ROOT)) {
                case "any":
                    return !getPassengers(cart).isEmpty();
                case "none":
                    return getPassengers(cart).isEmpty();
                case "mob":
                    return getPassengers(cart).stream().anyMatch(e -> e instanceof EntityMob);
                case "animal":
                    return getPassengers(cart).stream().anyMatch(e -> e instanceof EntityAnimal);
                case "unnamed":
                    return getPassengers(cart).stream().anyMatch(e -> !e.hasCustomName());
                case "entity":
                    return getPassengers(cart).stream().anyMatch(e -> tokens[1].equalsIgnoreCase(EntityList.getEntityString(e)));
                case "player":
                    if (tokens.length == 2) {
                        if (isRegex) {
                            return getPassengers(cart).stream().anyMatch(e -> e instanceof EntityPlayer && e.getName().matches(tokens[1]));
                        } else {
                            return getPassengers(cart).stream().anyMatch(e -> e instanceof EntityPlayer && e.getName().equalsIgnoreCase(tokens[1]));
                        }
                    }
                    return getPassengers(cart).stream().anyMatch(e -> e instanceof EntityPlayer);
                case "named":
                    if (tokens.length == 2) {
                        if (isRegex) {
                            return getPassengers(cart).stream().anyMatch(e -> e.hasCustomName() && e.getCustomNameTag().matches(tokens[1]));
                        } else {
                            return getPassengers(cart).stream().anyMatch(e -> e.hasCustomName() && e.getCustomNameTag().equalsIgnoreCase(tokens[1]));
                        }
                    }
                    return getPassengers(cart).stream().anyMatch(Entity::hasCustomName);
            }
            return false;
        }

        private List<Entity> getPassengers(EntityMinecart cart) {
            return Train.streamCarts(cart).flatMap(c -> c.getPassengers().stream()).collect(Collectors.toList());
        }

    }

    private class RedstoneCondition extends ParsedCondition {

        private final boolean powered;

        RedstoneCondition(String line) throws RoutingLogicException {
            super("Redstone", false, line);
            this.powered = Boolean.parseBoolean(value);
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            return powered == tile.isPowered();
        }

    }

    private class ColorCondition extends ParsedCondition {

        private final EnumColor primary, secondary;

        ColorCondition(String line) throws RoutingLogicException {
            super("Color", false, line);
            String[] colors = value.split(",");
            if ("Any".equals(colors[0]) || "*".equals(colors[0]))
                primary = null;
            else {
                primary = EnumColor.fromNameStrict(colors[0]);
                if (primary == null)
                    throw new RoutingLogicException("gui.railcraft.routing.logic.unrecognized.keyword", colors[0]);
            }
            if (colors.length == 1 || Objects.equals(colors[1], "Any") || Objects.equals(colors[1], "*"))
                secondary = null;
            else {
                secondary = EnumColor.fromNameStrict(colors[1]);
                if (secondary == null)
                    throw new RoutingLogicException("gui.railcraft.routing.logic.unrecognized.keyword", colors[1]);
            }
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            if (cart instanceof IPaintedCart) {
                IPaintedCart pCart = (IPaintedCart) cart;
                return (primary == null || primary.isEqual(pCart.getPrimaryDyeColor())) && (secondary == null || secondary.isEqual(pCart.getSecondaryDyeColor()));
            }
            return false;
        }
    }

    private class LocoCondition extends ParsedCondition {

        LocoCondition(String line) throws RoutingLogicException {
            super("Loco", false, line);
        }

        @Override
        public boolean matches(ITileRouting tile, EntityMinecart cart) {
            if (cart instanceof EntityLocomotive) {
                EntityLocomotive loco = (EntityLocomotive) cart;
                if ("Electric".equalsIgnoreCase(value))
                    return loco.getCartType() == RailcraftCarts.LOCO_ELECTRIC;
                if ("Steam".equalsIgnoreCase(value))
                    return loco.getCartType() == RailcraftCarts.LOCO_STEAM_SOLID;
                if ("Creative".equalsIgnoreCase(value))
                    return loco.getCartType() == RailcraftCarts.LOCO_CREATIVE;
//                if ("Steam_Magic".equalsIgnoreCase(value))
//                    return loco.getCartType() == RailcraftCarts.LOCO_STEAM_MAGIC;
                if ("None".equalsIgnoreCase(value))
                    return false;
            }
            return "None".equalsIgnoreCase(value);
        }

    }
}
