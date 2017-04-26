/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackKit;
import mods.railcraft.common.carts.ItemBoreHeadDiamond;
import mods.railcraft.common.carts.ItemBoreHeadIron;
import mods.railcraft.common.carts.ItemBoreHeadSteel;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.ItemBottle;
import mods.railcraft.common.items.firestone.ItemFirestone;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.ItemLapotronUpgrade;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarThaumium;
import mods.railcraft.common.plugins.thaumcraft.ItemCrowbarVoid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftItems implements IRailcraftObjectContainer<IRailcraftItemSimple> {
    ARMOR_BOOTS_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.FEET), "armor_boots_steel", Items.IRON_BOOTS),
    ARMOR_HELMET_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.HEAD), "armor_helmet_steel", Items.IRON_HELMET),
    ARMOR_LEGGINGS_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.LEGS), "armor_leggings_steel", Items.IRON_LEGGINGS),
    ARMOR_CHESTPLATE_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.CHEST), "armor_chestplate_steel", Items.IRON_CHESTPLATE),
    AXE_STEEL(ItemSteelAxe::new, "tool_axe_steel", Items.IRON_AXE),
    BLEACHED_CLAY(ItemRailcraft::new, "bleached_clay", Items.CLAY_BALL, RailcraftBlocks.BRICK_BLEACHED_BONE::isEnabled),
    BORE_HEAD_IRON(ItemBoreHeadIron::new, "borehead_iron", null, RailcraftCarts.BORE::isEnabled),
    BORE_HEAD_STEEL(ItemBoreHeadSteel::new, "borehead_steel", null, RailcraftCarts.BORE::isEnabled),
    BORE_HEAD_DIAMOND(ItemBoreHeadDiamond::new, "borehead_diamond", null, RailcraftCarts.BORE::isEnabled),
    CHARGE_METER(ItemChargeMeter::new, "tool_charge_meter"),
    CIRCUIT(ItemCircuit::new, "circuit"),
    COKE(ItemCoke::new, "fuel_coke"),
    CONCRETE(ItemConcrete::new, "concrete"),
    BOTTLE_CREOSOTE(() -> new ItemBottle(Fluids.CREOSOTE), "fluid_bottle_creosote"),
    BOTTLE_STEAM(() -> new ItemBottle(Fluids.STEAM), "fluid_bottle_steam"),
    CROWBAR_IRON(ItemCrowbarIron::new, "tool_crowbar_iron"),
    CROWBAR_STEEL(ItemCrowbarSteel::new, "tool_crowbar_steel"),
    CROWBAR_THAUMIUM(ItemCrowbarThaumium::new, "tool_crowbar_thaumium", null, Mod.THAUMCRAFT::isLoaded),
    CROWBAR_VOID(ItemCrowbarVoid::new, "tool_crowbar_void", null, Mod.THAUMCRAFT::isLoaded),
    DUST(ItemDust::new, "dust"),
    GEAR(ItemGear::new, "gear"),
    GOGGLES(ItemGoggles::new, "armor_goggles"),
    FIRESTONE_CRACKED(ItemFirestoneCracked::new, "firestone_cracked"),
    FIRESTONE_CUT(ItemFirestone::new, "firestone_cut"),
    FIRESTONE_RAW(ItemFirestone::new, "firestone_raw"),
    FIRESTONE_REFINED(ItemFirestoneRefined::new, "firestone_refined"),
    HOE_STEEL(ItemSteelHoe::new, "tool_hoe_steel", Items.IRON_HOE),
    INGOT(ItemIngot::new, "ingot"),
    LAPOTRON_UPGRADE(ItemLapotronUpgrade::new, "ic2_upgrade_lapotron", null, () -> Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC)),
    MAG_GLASS(ItemMagnifyingGlass::new, "tool_magnifying_glass"),
    NOTEPAD(ItemNotepad::new, "tool_notepad"),
    NUGGET(ItemNugget::new, "nugget"),
    OVERALLS(ItemOveralls::new, "armor_overalls"),
    PICKAXE_STEEL(ItemSteelPickaxe::new, "tool_pickaxe_steel", Items.IRON_PICKAXE),
    PLATE(ItemPlate::new, "plate"),
    RAIL(ItemRail::new, "rail"),
    RAILBED(ItemRailbed::new, "railbed"),
    REBAR(ItemRebar::new, "rebar", "ingotIron"),
    ROUTING_TABLE(ItemRoutingTable::new, "routing_table", Items.WRITABLE_BOOK),
    SHEARS_STEEL(ItemSteelShears::new, "tool_shears_steel", Items.SHEARS),
    SHOVEL_STEEL(ItemSteelShovel::new, "tool_shovel_steel", Items.IRON_SHOVEL),
    SIGNAL_BLOCK_SURVEYOR(ItemSignalBlockSurveyor::new, "tool_signal_surveyor"),
    SIGNAL_LABEL(ItemSignalLabel::new, "tool_signal_label"),
    SIGNAL_LAMP(ItemSignalLamp::new, "signal_lamp", Blocks.REDSTONE_LAMP),
    SIGNAL_TUNER(ItemSignalTuner::new, "tool_signal_tuner"),
    SPIKE_MAUL_IRON(ItemSpikeMaulIron::new, "tool_spike_maul_iron"),
    SPIKE_MAUL_STEEL(ItemSpikeMaulSteel::new, "tool_spike_maul_steel"),
    STONE_CARVER(ItemStoneCarver::new, "tool_stone_carver"),
    SWORD_STEEL(ItemSteelSword::new, "tool_sword_steel", Items.IRON_SWORD),
    TICKET(ItemTicket::new, "routing_ticket", Items.PAPER),
    TICKET_GOLD(ItemTicketGold::new, "routing_ticket_gold", Items.GOLD_NUGGET),
    TIE(ItemTie::new, "tie"),
    TRACK_KIT(ItemTrackKit::new, "track_kit", null, RailcraftBlocks.TRACK_OUTFITTED::isEnabled),
    TRACK_PARTS(ItemTrackParts::new, "track_parts", "ingotIron"),
    TURBINE_BLADE(ItemTurbineBlade::new, "turbine_blade", "ingotSteel", EnumMachineAlpha.TURBINE::isEnabled),
    TURBINE_DISK(ItemTurbineDisk::new, "turbine_disk", "blockSteel", EnumMachineAlpha.TURBINE::isEnabled),
    TURBINE_ROTOR(ItemTurbineRotor::new, "turbine_rotor", null, EnumMachineAlpha.TURBINE::isEnabled),
    WHISTLE_TUNER(ItemWhistleTuner::new, "tool_whistle_tuner");
    public static final RailcraftItems[] VALUES = values();
    private final Supplier<Item> itemSupplier;
    private final String tag;
    @Nullable
    private final Object altRecipeObject;
    private final Supplier<Boolean> prerequisites;
    private Item item;
    private Optional<IRailcraftItemSimple> railcraftObject = Optional.empty();
    @Nullable
    private IRailcraftModule module;

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
        Arrays.stream(VALUES).forEach(i -> i.getObject().ifPresent(IRailcraftItemSimple::finalizeDefinition));
    }

    @Override
    public void register() {
        if (item != null)
            return;

        if (isEnabled()) {
            item = itemSupplier.get();
            if (!(item instanceof IRailcraftItemSimple))
                throw new RuntimeException("Railcraft Items must implement IRailcraftItemSimple");
            IRailcraftItemSimple railcraftItem = (IRailcraftItemSimple) item;
            railcraftObject = Optional.of(railcraftItem);
            item.setRegistryName(getBaseTag());
            item.setUnlocalizedName(getFullTag());
            RailcraftRegistry.register(railcraftItem);
            railcraftItem.initializeDefinintion();
            railcraftItem.defineRecipes();
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
        return getObject().map(i -> i.getStack(qty, variant)).orElse(null);
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        checkVariantObject(variant);
        register();
        Object obj = getObject().map(i -> i.getRecipeObject(variant)).orElse(null);
        if (obj == null && variant != null)
            obj = variant.getAlternate(this);
        if (obj == null)
            obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public Optional<IRailcraftItemSimple> getObject() {
        return railcraftObject;
    }

    @Override
    public boolean isEnabled() {
        return module != null && RailcraftConfig.isItemEnabled(tag) && prerequisites.get();
    }

    @Override
    public boolean isLoaded() {
        return item != null;
    }

    @Override
    public void loadBy(IRailcraftModule module) {
        this.module = module;
    }


    @Override
    public String toString() {
        return "Item{" + tag + "}";
    }
}
