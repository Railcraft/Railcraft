/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackKit;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.ItemBottle;
import mods.railcraft.common.items.firestone.ItemFirestone;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.modules.ModuleMagic;
import mods.railcraft.common.modules.ModuleSignals;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.ItemLapotronUpgrade;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftItems implements IRailcraftObjectContainer<IRailcraftItemSimple> {
    ARMOR_BOOTS_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.FEET), "armor_boots_steel", () -> Items.IRON_BOOTS),
    ARMOR_HELMET_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.HEAD), "armor_helmet_steel", () -> Items.IRON_HELMET),
    ARMOR_LEGGINGS_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.LEGS), "armor_leggings_steel", () -> Items.IRON_LEGGINGS),
    ARMOR_CHESTPLATE_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.CHEST), "armor_chestplate_steel", () -> Items.IRON_CHESTPLATE),
    AXE_STEEL(ItemSteelAxe::new, "tool_axe_steel", () -> Items.IRON_AXE),
    BACKPACK_APOTHECARY_T1(() -> ForestryPlugin.instance().getBackpack("apothecary", "NORMAL"),
            "backpack_apothecary_t1") {{
        conditions().add(Mod.FORESTRY);
        conditions().add(ModuleMagic.class);
    }},
    BACKPACK_APOTHECARY_T2(() -> ForestryPlugin.instance().getBackpack("apothecary", "WOVEN"),
            "backpack_apothecary_t2") {{
        conditions().add(Mod.FORESTRY);
        conditions().add(ModuleMagic.class);
    }},
    BACKPACK_ICEMAN_T1(() -> ForestryPlugin.instance().getBackpack("iceman", "NORMAL"),
            "backpack_iceman_t1") {{
        conditions().add(Mod.FORESTRY);
    }},
    BACKPACK_ICEMAN_T2(() -> ForestryPlugin.instance().getBackpack("iceman", "WOVEN"),
            "backpack_iceman_t2") {{
        conditions().add(Mod.FORESTRY);
    }},
    BACKPACK_TRACKMAN_T1(() -> ForestryPlugin.instance().getBackpack("trackman", "NORMAL"),
            "backpack_trackman_t1") {{
        conditions().add(Mod.FORESTRY);
    }},
    BACKPACK_TRACKMAN_T2(() -> ForestryPlugin.instance().getBackpack("trackman", "WOVEN"),
            "backpack_trackman_t2") {{
        conditions().add(Mod.FORESTRY);
    }},
    BACKPACK_SIGNALMAN_T1(() -> ForestryPlugin.instance().getBackpack("signalman", "NORMAL"),
            "backpack_signalman_t1") {{
        conditions().add(Mod.FORESTRY);
        conditions().add(ModuleSignals.class);
    }},
    BACKPACK_SIGNALMAN_T2(() -> ForestryPlugin.instance().getBackpack("signalman", "WOVEN"),
            "backpack_signalman_t2") {{
        conditions().add(Mod.FORESTRY);
        conditions().add(ModuleSignals.class);
    }},
    BLEACHED_CLAY(ItemRailcraft::new, "bleached_clay", () -> Items.CLAY_BALL) {{
        conditions().add(RailcraftBlocks.BRICK_BLEACHED_BONE);
    }},
    BORE_HEAD_BRONZE(ItemBoreHeadBronze::new, "borehead_bronze") {{
        conditions().add(RailcraftCarts.BORE);
    }},
    BORE_HEAD_IRON(ItemBoreHeadIron::new, "borehead_iron") {{
        conditions().add(RailcraftCarts.BORE);
    }},
    BORE_HEAD_STEEL(ItemBoreHeadSteel::new, "borehead_steel") {{
        conditions().add(RailcraftCarts.BORE);
    }},
    BORE_HEAD_DIAMOND(ItemBoreHeadDiamond::new, "borehead_diamond") {{
        conditions().add(RailcraftCarts.BORE);
    }},
    CHARGE(ItemCharge::new, "charge"),
    CHARGE_METER(ItemChargeMeter::new, "tool_charge_meter"),
    CIRCUIT(ItemCircuit::new, "circuit"),
    COKE(ItemCoke::new, "fuel_coke"),
    CONCRETE(ItemConcrete::new, "concrete"),
    BOTTLE_CREOSOTE(() -> new ItemBottle(Fluids.CREOSOTE), "fluid_bottle_creosote"),
    BOTTLE_STEAM(() -> new ItemBottle(Fluids.STEAM), "fluid_bottle_steam"),
    CROWBAR_IRON(ItemCrowbarIron::new, "tool_crowbar_iron"),
    CROWBAR_STEEL(ItemCrowbarSteel::new, "tool_crowbar_steel"),
    //    CROWBAR_THAUMIUM(ItemCrowbarThaumium::new, "tool_crowbar_thaumium") {{
//        conditions().add(Mod.THAUMCRAFT);
//    }},
//    CROWBAR_VOID(ItemCrowbarVoid::new, "tool_crowbar_void") {{
//        conditions().add(Mod.THAUMCRAFT);
//    }},
    CROWBAR_DIAMOND(ItemCrowbarDiamond::new, "tool_crowbar_diamond"),
    CROWBAR_SEASONS(ItemCrowbarSeasons::new, "tool_crowbar_seasons"),
    DUST(ItemDust::new, "dust"),
    GEAR(ItemGear::new, "gear"),
    GOGGLES(ItemGoggles::new, "armor_goggles"),
    FILTER_BEE(SafeReference.makeItem("ItemFilterBee"), "filter_bee") {{
        conditions().add(Mod.FORESTRY);
    }},
    FILTER_BEE_GENOME(SafeReference.makeItem("ItemFilterBeeGenome"), "filter_bee_genome") {{
        conditions().add(Mod.FORESTRY);
    }},
    FILTER_BLANK(ItemFilterBlank::new, "filter_blank"),
    FILTER_TYPE(ItemFilterType::new, "filter_type"),
    FILTER_ORE_DICT(ItemFilterOreDictionary::new, "filter_ore_dict"),
    FIRESTONE_CRACKED(ItemFirestoneCracked::new, "firestone_cracked"),
    FIRESTONE_CUT(ItemFirestone::new, "firestone_cut"),
    FIRESTONE_RAW(ItemFirestone::new, "firestone_raw"),
    FIRESTONE_REFINED(ItemFirestoneRefined::new, "firestone_refined"),
    HOE_STEEL(ItemSteelHoe::new, "tool_hoe_steel", () -> Items.IRON_HOE),
    INGOT(ItemIngot::new, "ingot"),
    LAPOTRON_UPGRADE(ItemLapotronUpgrade::new, "ic2_upgrade_lapotron") {{
        conditions().add(() -> Mod.anyLoaded(Mod.IC2, Mod.IC2_CLASSIC), () -> "Mod Ic2 or Ic2Classic is not installed");
    }},
    MAG_GLASS(ItemMagnifyingGlass::new, "tool_magnifying_glass"),
    NOTEPAD(ItemNotepad::new, "tool_notepad"),
    NUGGET(ItemNugget::new, "nugget"),
    OVERALLS(ItemOveralls::new, "armor_overalls"),
    PICKAXE_STEEL(ItemSteelPickaxe::new, "tool_pickaxe_steel", () -> Items.IRON_PICKAXE),
    PLATE(ItemPlate::new, "plate"),
    RAIL(ItemRail::new, "rail"),
    RAILBED(ItemRailbed::new, "railbed"),
    REBAR(ItemRebar::new, "rebar", () -> "ingotIron"),
    ROUTING_TABLE(ItemRoutingTable::new, "routing_table", () -> Items.WRITABLE_BOOK),
    SHEARS_STEEL(ItemSteelShears::new, "tool_shears_steel", () -> Items.SHEARS),
    SHOVEL_STEEL(ItemSteelShovel::new, "tool_shovel_steel", () -> Items.IRON_SHOVEL),
    SIGNAL_BLOCK_SURVEYOR(ItemSignalBlockSurveyor::new, "tool_signal_surveyor"),
    SIGNAL_LABEL(ItemSignalLabel::new, "tool_signal_label"),
    SIGNAL_LAMP(ItemSignalLamp::new, "signal_lamp", () -> Blocks.REDSTONE_LAMP),
    SIGNAL_TUNER(ItemSignalTuner::new, "tool_signal_tuner"),
    SPIKE_MAUL_IRON(ItemSpikeMaulIron::new, "tool_spike_maul_iron"),
    SPIKE_MAUL_STEEL(ItemSpikeMaulSteel::new, "tool_spike_maul_steel"),
    STONE_CARVER(ItemStoneCarver::new, "tool_stone_carver"),
    SWORD_STEEL(ItemSteelSword::new, "tool_sword_steel", () -> Items.IRON_SWORD),
    TICKET(ItemTicket::new, "routing_ticket", () -> Items.PAPER),
    TICKET_GOLD(ItemTicketGold::new, "routing_ticket_gold", () -> Items.GOLD_NUGGET),
    TIE(ItemTie::new, "tie"),
    TRACK_KIT(ItemTrackKit::new, "track_kit") {{
        conditions().add(RailcraftBlocks.TRACK_OUTFITTED);
    }},
    TRACK_PARTS(ItemTrackParts::new, "track_parts", () -> "ingotIron"),
    TURBINE_BLADE(ItemTurbineBlade::new, "turbine_blade", () -> "ingotSteel") {{
        conditions().add(RailcraftBlocks.STEAM_TURBINE);
    }},
    TURBINE_DISK(ItemTurbineDisk::new, "turbine_disk", () -> "blockSteel") {{
        conditions().add(RailcraftBlocks.STEAM_TURBINE);
    }},
    TURBINE_ROTOR(ItemTurbineRotor::new, "turbine_rotor") {{
        conditions().add(RailcraftBlocks.STEAM_TURBINE);
    }},
    WHISTLE_TUNER(ItemWhistleTuner::new, "tool_whistle_tuner");
    public static final RailcraftItems[] VALUES = values();
    private final Supplier<Item> itemSupplier;
    private final SimpleDef def;
    private Item item;
    private Optional<IRailcraftItemSimple> railcraftObject = Optional.empty();

    RailcraftItems(Supplier<Item> itemSupplier, String tag) {
        this(itemSupplier, tag, null);
    }

    RailcraftItems(Supplier<Item> itemSupplier, String tag, @Nullable Supplier<Object> alt) {
        this.def = new SimpleDef(this, tag, alt);
        this.itemSupplier = itemSupplier;
        conditions().add(RailcraftConfig::isItemEnabled, () -> "disabled via config");
    }

    @Override
    public SimpleDef getDef() {
        return def;
    }

    @Override
    public void register() {
        if (item != null)
            return;

        if (isEnabled()) {
            Item newItem = itemSupplier.get();
            if (newItem == null)
                return;
            if (newItem instanceof IRailcraftItemSimple)
                item = ((IRailcraftItemSimple) newItem).getObject();
            else
                item = newItem;
            item.setRegistryName(getBaseTag());
            item.setTranslationKey(LocalizationPlugin.convertTag(getFullTag()));
            RailcraftRegistry.register(item);
            IRailcraftItemSimple railcraftItem;
            if (newItem instanceof IRailcraftItemSimple)
                railcraftItem = (IRailcraftItemSimple) newItem;
            else
                railcraftItem = new ItemWrapper(newItem);
            railcraftObject = Optional.of(railcraftItem);
            railcraftItem.initializeDefinition();
        } else {
            conditions().printFailureReason(this);
        }
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return !InvTools.isEmpty(stack) && item == stack.getItem();
    }

    public boolean isInstance(ItemStack stack) {
        return !InvTools.isEmpty(stack) && (item == stack.getItem() || item.getClass().isInstance(stack.getItem()));
    }

    public boolean isEqual(@Nullable Item item) {
        return item != null && this.item == item;
    }

    public @Nullable Item item() {
        return getObject().map(IRailcraftObject::getObject).orElse(null);
    }

    public String getFullTag() {
        return "railcraft." + getBaseTag();
    }

    public ItemStack getStack(int qty, int meta) {
        if (item == null)
            return InvTools.emptyStack();
        return new ItemStack(item, qty, meta);
    }

    private void checkVariantObject(@Nullable IVariantEnum variant) {
        getObject().ifPresent(o -> o.checkVariant(variant));
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        checkVariantObject(variant);
        return getObject().map(i -> i.getStack(qty, variant)).orElse(InvTools.emptyStack());
    }

    @Override
    public Optional<IRailcraftItemSimple> getObject() {
        return railcraftObject;
    }

    @Override
    public String toString() {
        return "Item{" + getBaseTag() + "}";
    }
}
