/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftItems implements IRailcraftObjectContainer {

    circuit(ItemCircuit.class, "part.circuit"),
    crowbarIron(ItemCrowbarIron.class, "tool.crowbar.iron"),
    crowbarSteel(ItemCrowbarSteel.class, "tool.crowbar.steel"),
    dust(ItemDust.class, "dust"),
    electricMeter(ItemElectricMeter.class, "tool.electric.meter"),
    gear(ItemGear.class, "part.gear"),
    goggles(ItemGoggles.class, "armor.goggles"),
    ingot(ItemIngot.class, "ingot"),
    magGlass(ItemMagnifyingGlass.class, "tool.magnifying.glass"),
    nugget(ItemNugget.class, "nugget"),
    overalls(ItemOveralls.class, "armor.overalls"),
    plate(ItemPlate.class, "part.plate"),
    rail(ItemRail.class, "part.rail"),
    railbed(ItemRailbed.class, "part.railbed"),
    rebar(ItemRebar.class, "part.rebar", "ingotIron"),
    routingTable(ItemRoutingTable.class, "routing.table", Items.writable_book),
    signalBlockSurveyor(ItemSignalBlockSurveyor.class, "tool.signal.surveyor"),
    signalLabel(ItemSignalLabel.class, "tool.signal.label"),
    signalLamp(ItemSignalLamp.class, "part.signal.lamp", Blocks.redstone_lamp),
    signalTuner(ItemSignalTuner.class, "tool.signal.tuner"),
    ticket(ItemTicket.class, "routing.ticket", Items.paper),
    ticketGold(ItemTicketGold.class, "routing.ticket.gold", Items.gold_nugget),
    tie(ItemTie.class, "part.tie"),
    whistleTuner(ItemWhistleTuner.class, "tool.whistle.tuner");
    public static final RailcraftItems[] VALUES = values();
    private final Class<? extends Item> itemClass;
    private final String tag;
    private final Object altRecipeObject;
    private Item item;
    private IRailcraftObject railcraftObject;

    RailcraftItems(Class<? extends Item> itemClass, String tag) {
        this(itemClass, tag, null);
    }

    RailcraftItems(Class<? extends Item> itemClass, String tag, Object alt) {
        this.itemClass = itemClass;
        this.tag = tag;
        this.altRecipeObject = alt;
    }

    public static void definePostRecipes() {
        for (RailcraftItems type : VALUES) {
            if (type.railcraftObject != null)
                type.railcraftObject.finalizeDefinition();
        }
    }

    @Override
    public void register() {
        if (item != null)
            return;

        if (isEnabled()) {
            try {
                item = itemClass.newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException("Invalid Item Constructor");
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Invalid Item Constructor");
            }
            if (!(item instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft Items must implement IRailcraftObject");
            railcraftObject = (IRailcraftObject) item;
            item.setUnlocalizedName("railcraft." + tag);
            RailcraftRegistry.register(item);
            railcraftObject.initializeDefinintion();
            railcraftObject.defineRecipes();
        }
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return stack != null && item == stack.getItem();
    }

    public boolean isEqual(Item item) {
        return item != null && this.item == item;
    }

    public Item item() {
        return item;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    @Override
    public ItemStack getWildcard() {
        return getStack(1, OreDictionary.WILDCARD_VALUE);
    }

    @Override
    public ItemStack getStack() {
        return getStack(1, 0);
    }

    @Override
    public ItemStack getStack(int qty) {
        return getStack(qty, 0);
    }

    @Override
    public ItemStack getStack(int qty, int meta) {
        register();
        if (item == null)
            return null;
        return new ItemStack(item, qty, meta);
    }

    private void checkVariantObject(IVariantEnum variant) {
        if (variant == null || variant.getParentClass() != itemClass)
            throw new RuntimeException("Incorrect Variant object used.");
    }

    @Override
    public ItemStack getStack(IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Override
    public ItemStack getStack(int qty, IVariantEnum variant) {
        checkVariantObject(variant);
        return getStack(qty, variant.ordinal());
    }

    @Override
    public Object getRecipeObject() {
        register();
        if (railcraftObject != null)
            return railcraftObject.getRecipeObject(null);
        Object obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public Object getRecipeObject(IVariantEnum variant) {
        checkVariantObject(variant);
        register();
        if (railcraftObject != null)
            return railcraftObject.getRecipeObject(variant);
        Object obj = variant.getAlternate();
        if (obj == null)
            obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public IRailcraftObject getObject() {
        return railcraftObject;
    }

    @Override
    public boolean isEnabled() {
        return RailcraftConfig.isItemEnabled(tag);
    }

    @Override
    public boolean isLoaded() {
        return item != null;
    }
}
