/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.carts.ItemBoreHeadDiamond;
import mods.railcraft.common.carts.ItemBoreHeadIron;
import mods.railcraft.common.carts.ItemBoreHeadSteel;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.firestone.ItemFirestone;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftItems implements IRailcraftObjectContainer {
    armorBootsSteel(() -> new ItemSteelArmor(EntityEquipmentSlot.FEET), "armor.boots.steel", Items.IRON_BOOTS),
    armorHelmetSteel(() -> new ItemSteelArmor(EntityEquipmentSlot.HEAD), "armor.helmet.steel", Items.IRON_HELMET),
    armorLeggingsSteel(() -> new ItemSteelArmor(EntityEquipmentSlot.LEGS), "armor.leggings.steel", Items.IRON_LEGGINGS),
    armorChestplateSteel(() -> new ItemSteelArmor(EntityEquipmentSlot.CHEST), "armor.chestplate.steel", Items.IRON_CHESTPLATE),
    axeSteel(ItemSteelAxe::new, "tool.axe.steel", Items.IRON_AXE),
    bleachedClay(ItemRailcraft::new, "bleached.clay", Items.CLAY_BALL, RailcraftBlocks.brickBleachedBone::isEnabled),
    boreHeadIron(ItemBoreHeadIron::new, "borehead.iron", null, RailcraftCarts.BORE::isEnabled),
    boreHeadSteel(ItemBoreHeadSteel::new, "borehead.steel", null, RailcraftCarts.BORE::isEnabled),
    boreHeadDiamond(ItemBoreHeadDiamond::new, "borehead.diamond", null, RailcraftCarts.BORE::isEnabled),
    circuit(ItemCircuit::new, "part.circuit"),
    coke(ItemCoke::new, "fuel.coke"),
    crowbarIron(ItemCrowbarIron::new, "tool.crowbar.iron"),
    crowbarSteel(ItemCrowbarSteel::new, "tool.crowbar.steel"),
    dust(ItemDust::new, "dust"),
    electricMeter(ItemElectricMeter::new, "tool.electric.meter"),
    gear(ItemGear::new, "part.gear"),
    goggles(ItemGoggles::new, "armor.goggles"),
    firestoneCracked(ItemFirestoneCracked::new, "firestone.cracked"),
    firestoneCut(ItemFirestone::new, "firestone.cut"),
    firestoneRaw(ItemFirestone::new, "firestone.raw"),
    firestoneRefined(ItemFirestoneRefined::new, "firestone.refined"),
    hoeSteel(ItemSteelHoe::new, "tool.hoe.steel", Items.IRON_HOE),
    ingot(ItemIngot::new, "ingot"),
    magGlass(ItemMagnifyingGlass::new, "tool.magnifying.glass"),
    notepad(ItemNotepad::new, "tool.notepad"),
    nugget(ItemNugget::new, "nugget"),
    overalls(ItemOveralls::new, "armor.overalls"),
    pickaxeSteel(ItemSteelPickaxe::new, "tool.pickaxe.steel", Items.IRON_PICKAXE),
    plate(ItemPlate::new, "part.plate"),
    rail(ItemRail::new, "part.rail"),
    railbed(ItemRailbed::new, "part.railbed"),
    rebar(ItemRebar::new, "part.rebar", "ingotIron"),
    routingTable(ItemRoutingTable::new, "routing.table", Items.WRITABLE_BOOK),
    shearsSteel(ItemSteelShears::new, "tool.shears.steel", Items.SHEARS),
    shovelSteel(ItemSteelShovel::new, "tool.shovel.steel", Items.IRON_SHOVEL),
    signalBlockSurveyor(ItemSignalBlockSurveyor::new, "tool.signal.surveyor"),
    signalLabel(ItemSignalLabel::new, "tool.signal.label"),
    signalLamp(ItemSignalLamp::new, "part.signal.lamp", Blocks.REDSTONE_LAMP),
    signalTuner(ItemSignalTuner::new, "tool.signal.tuner"),
    stoneCarver(ItemStoneCarver::new, "tool.stone.carver"),
    swordSteel(ItemSteelSword::new, "tool.sword.steel", Items.IRON_SWORD),
    ticket(ItemTicket::new, "routing.ticket", Items.PAPER),
    ticketGold(ItemTicketGold::new, "routing.ticket.gold", Items.GOLD_NUGGET),
    tie(ItemTie::new, "part.tie"),
    turbineBlade(ItemTurbineBlade::new, "part.turbine.blade", "ingotSteel", EnumMachineAlpha.TURBINE::isAvailable),
    turbineDisk(ItemTurbineDisk::new, "part.turbine.disk", "blockSteel", EnumMachineAlpha.TURBINE::isAvailable),
    turbineRotor(ItemTurbineRotor::new, "part.turbine.rotor", null, EnumMachineAlpha.TURBINE::isAvailable),
    whistleTuner(ItemWhistleTuner::new, "tool.whistle.tuner");
    public static final RailcraftItems[] VALUES = values();
    private final Supplier<Item> itemSupplier;
    private final String tag;
    @Nullable
    private final Object altRecipeObject;
    private final Supplier<Boolean> prerequisites;
    private Item item;
    private IRailcraftObject railcraftObject;

    RailcraftItems(Supplier<Item> itemSupplier, String tag) {
        this(itemSupplier, tag, null);
    }

    RailcraftItems(Supplier<Item> itemSupplier, String tag, @Nullable Object alt) {
        this(itemSupplier, tag, alt, () -> true);
    }

    RailcraftItems(Supplier<Item> itemSupplier, String tag, @Nullable Object alt, Supplier<Boolean> prerequisites) {
        this.itemSupplier = itemSupplier;
        this.tag = tag;
        this.altRecipeObject = alt;
        this.prerequisites = prerequisites;
    }

    public static void finalizeDefinitions() {
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
            item = itemSupplier.get();
            if (!(item instanceof IRailcraftObject))
                throw new RuntimeException("Railcraft Items must implement IRailcraftObject");
            railcraftObject = (IRailcraftObject) item;
            item.setRegistryName(getBaseTag());
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
        return stack != null && (item == stack.getItem() || item.getClass().isInstance(stack.getItem()));
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
    public ItemStack getStack(int qty, int meta) {
        register();
        if (item == null)
            return null;
        return new ItemStack(item, qty, meta);
    }

    private void checkVariantObject(@Nullable IVariantEnum variant) {
        if (item != null)
            ((IRailcraftObject) item).checkVariant(variant);
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, IVariantEnum variant) {
        checkVariantObject(variant);
        register();
        if (railcraftObject != null)
            return railcraftObject.getStack(qty, variant);
        return null;
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
        return RailcraftConfig.isItemEnabled(tag) && prerequisites.get();
    }

    @Override
    public boolean isLoaded() {
        return item != null;
    }
}
