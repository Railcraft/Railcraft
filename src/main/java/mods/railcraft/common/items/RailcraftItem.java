/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftItem {

    goggles(ItemGoggles.class, "armor.goggles"),
    circuit(ItemCircuit.class, "part.circuit"),
    dust(ItemDust.class, "dust"),
    electricMeter(ItemElectricMeter.class, "tool.electric.meter"),
    gear(ItemGear.class, "part.gear"),
    ingot(ItemIngot.class, "ingot"),
    nugget(ItemNugget.class, "nugget"),
    magGlass(ItemMagnifyingGlass.class, "tool.magnifying.glass"),
    overalls(ItemOveralls.class, "armor.overalls"),
    plate(ItemPlate.class, "part.plate"),
    rail(ItemRail.class, "part.rail"),
    railbed(ItemRailbed.class, "part.railbed"),
    rebar(ItemRebar.class, "part.rebar", "ingotIron"),
    signalLabel(ItemSignalLabel.class, "tool.signal.label"),
    signalLamp(ItemSignalLamp.class, "part.signal.lamp", Blocks.redstone_lamp),
    tie(ItemTie.class, "part.tie"),
    whistleTuner(ItemWhistleTuner.class, "tool.whistle.tuner");
    public static final RailcraftItem[] VALUES = values();
    private final Class<? extends Item> itemClass;
    private final String tag;
    private final Object altRecipeObject;
    private Item item;
    private IRailcraftItem railcraftItem;

    RailcraftItem(Class<? extends Item> itemClass, String tag) {
        this(itemClass, tag, null);
    }

    RailcraftItem(Class<? extends Item> itemClass, String tag, Object alt) {
        this.itemClass = itemClass;
        this.tag = tag;
        this.altRecipeObject = alt;
    }

    public void registerItem() {
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
            if (!(item instanceof IRailcraftItem))
                throw new RuntimeException("Railcraft Items must implement IRailcraftItem");
            railcraftItem = (IRailcraftItem) item;
            item.setUnlocalizedName("railcraft." + tag);
            RailcraftRegistry.register(item);
            railcraftItem.initItem();
            railcraftItem.defineRecipes();
        }
    }

    public boolean isItemEqual(ItemStack stack) {
        return stack != null && this.item == stack.getItem();
    }

    public boolean isItemEqual(Item i) {
        return i != null && this.item == i;
    }

    public Item item() {
        return item;
    }

    public String getBaseTag() {
        return tag;
    }

    public ItemStack getWildcard() {
        return getStack(1, OreDictionary.WILDCARD_VALUE);
    }

    public ItemStack getStack() {
        return getStack(1, 0);
    }

    public ItemStack getStack(int qty) {
        return getStack(qty, 0);
    }

    public ItemStack getStack(int qty, int meta) {
        registerItem();
        if (item == null)
            return null;
        return new ItemStack(item, qty, meta);
    }

    private void checkMetaObject(IItemMetaEnum meta) {
        if (meta == null || meta.getItemClass() != itemClass)
            throw new RuntimeException("Incorrect Item Meta object used.");
    }

    public ItemStack getStack(IItemMetaEnum meta) {
        return getStack(1, meta);
    }

    public ItemStack getStack(int qty, IItemMetaEnum meta) {
        checkMetaObject(meta);
        return getStack(qty, meta.ordinal());
    }

    public Object getRecipeObject() {
        registerItem();
        if (railcraftItem != null)
            return railcraftItem.getRecipeObject(null);
        Object obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    public Object getRecipeObject(IItemMetaEnum meta) {
        checkMetaObject(meta);
        registerItem();
        if (railcraftItem != null)
            return railcraftItem.getRecipeObject(meta);
        Object obj = meta.getAlternate();
        if (obj == null)
            obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    public boolean isEnabled() {
        return RailcraftConfig.isItemEnabled(tag);
    }

    public static void definePostRecipes() {
        for (RailcraftItem type : VALUES) {
            if (type.railcraftItem != null)
                type.railcraftItem.definePostRecipes();
        }
    }
}
