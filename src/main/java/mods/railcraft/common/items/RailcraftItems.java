/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftItems implements IRailcraftObjectContainer {

    circuit(ItemCircuit.class, "part.circuit"),
    coke(ItemCircuit.class, "fuel.coke"),
    crowbarIron(ItemCrowbarIron.class, "tool.crowbar.iron"),
    crowbarSteel(ItemCrowbarSteel.class, "tool.crowbar.steel"),
    dust(ItemDust.class, "dust"),
    electricMeter(ItemElectricMeter.class, "tool.electric.meter"),
    gear(ItemGear.class, "part.gear"),
    goggles(ItemGoggles.class, "armor.goggles"),
    ingot(ItemIngot.class, "ingot"),
    magGlass(ItemMagnifyingGlass.class, "tool.magnifying.glass"),
    notepad(ItemNotepad.class, "tool.notepad"),
    nugget(ItemNugget.class, "nugget"),
    overalls(ItemOveralls.class, "armor.overalls"),
    plate(ItemPlate.class, "part.plate"),
    rail(ItemRail.class, "part.rail"),
    railbed(ItemRailbed.class, "part.railbed"),
    rebar(ItemRebar.class, "part.rebar", "ingotIron"),
    routingTable(ItemRoutingTable.class, "routing.table", Items.WRITABLE_BOOK),
    signalBlockSurveyor(ItemSignalBlockSurveyor.class, "tool.signal.surveyor"),
    signalLabel(ItemSignalLabel.class, "tool.signal.label"),
    signalLamp(ItemSignalLamp.class, "part.signal.lamp", Blocks.REDSTONE_LAMP),
    signalTuner(ItemSignalTuner.class, "tool.signal.tuner"),
    stoneCarver(ItemStoneCarver.class, "tool.stone.carver"),
    ticket(ItemTicket.class, "routing.ticket", Items.PAPER),
    ticketGold(ItemTicketGold.class, "routing.ticket.gold", Items.GOLD_NUGGET),
    tie(ItemTie.class, "part.tie"),
    turbineBlade(ItemTurbineBlade.class, "part.turbine.blade", "ingotSteel") {
        @Override
        public boolean isEnabled() {
            return super.isEnabled() && EnumMachineAlpha.TURBINE.isAvailable();
        }
    },
    turbineDisk(ItemTurbineDisk.class, "part.turbine.disk", "blockSteel") {
        @Override
        public boolean isEnabled() {
            return super.isEnabled() && EnumMachineAlpha.TURBINE.isAvailable();
        }
    },
    turbineRotor(ItemTurbineRotor.class, "part.turbine.rotor") {
        @Override
        public boolean isEnabled() {
            return super.isEnabled() && EnumMachineAlpha.TURBINE.isAvailable();
        }
    },
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
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("Invalid Item Constructor");
            }
            if (!(item instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft Items must implement IRailcraftObject");
            railcraftObject = (IRailcraftObject) item;
            item.setUnlocalizedName(getFullTag());
            RailcraftRegistry.register(item);
            railcraftObject.initializeDefinintion();
            railcraftObject.defineRecipes();
        }
    }

    @Override
    public boolean isEqual(@Nullable ItemStack stack) {
        return stack != null && item == stack.getItem();
    }

    public boolean isInstance(@Nullable ItemStack stack) {
        return stack != null && (item == stack.getItem() || itemClass.isInstance(stack.getItem()));
    }

    public boolean isEqual(@Nullable Item item) {
        return item != null && this.item == item;
    }

    @Nullable
    public Item item() {
        return item;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    public String getFullTag() {
        return "railcraft." + tag;
    }

    @Nullable
    @Override
    public ItemStack getWildcard() {
        return getStack(1, OreDictionary.WILDCARD_VALUE);
    }

    @Nullable
    @Override
    public ItemStack getStack() {
        return getStack(1, 0);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty) {
        return getStack(qty, 0);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, int meta) {
        register();
        if (item == null)
            return null;
        return new ItemStack(item, qty, meta);
    }

    private void checkVariantObject(@Nullable IVariantEnum variant) {
        IVariantEnum.tools.checkVariantObject(itemClass, variant);
    }

    @Override
    public ItemStack getStack(@Nonnull IVariantEnum variant) {
        return getStack(1, variant);
    }

    @Override
    public ItemStack getStack(int qty, @Nonnull IVariantEnum variant) {
        checkVariantObject(variant);
        return railcraftObject.getStack(qty, variant);
    }

    @Override
    public Object getRecipeObject() {
        return getRecipeObject(null);
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        checkVariantObject(variant);
        register();
        Object obj = null;
        if (railcraftObject != null)
            obj = railcraftObject.getRecipeObject(variant);
        if (obj == null && variant != null)
            obj = variant.getAlternate(this);
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
