/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

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
import mods.railcraft.common.items.firestone.ItemFirestone;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
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
public enum RailcraftItems implements IRailcraftObjectContainer<IRailcraftItem> {
    ARMOR_BOOTS_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.FEET), "armor.boots.steel", Items.IRON_BOOTS),
    ARMOR_HELMET_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.HEAD), "armor.helmet.steel", Items.IRON_HELMET),
    ARMOR_LEGGINGS_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.LEGS), "armor.leggings.steel", Items.IRON_LEGGINGS),
    ARMOR_CHESTPLATE_STEEL(() -> new ItemSteelArmor(EntityEquipmentSlot.CHEST), "armor.chestplate.steel", Items.IRON_CHESTPLATE),
    AXE_STEEL(ItemSteelAxe::new, "tool.axe.steel", Items.IRON_AXE),
    BLEACHED_CLAY(ItemRailcraft::new, "bleached.clay", Items.CLAY_BALL, RailcraftBlocks.BRICK_BLEACHED_BONE::isEnabled),
    BORE_HEAD_IRON(ItemBoreHeadIron::new, "borehead.iron", null, RailcraftCarts.BORE::isEnabled),
    BORE_HEAD_STEEL(ItemBoreHeadSteel::new, "borehead.steel", null, RailcraftCarts.BORE::isEnabled),
    BORE_HEAD_DIAMOND(ItemBoreHeadDiamond::new, "borehead.diamond", null, RailcraftCarts.BORE::isEnabled),
    CHARGE_METER(ItemChargeMeter::new, "tool.charge.meter"),
    CIRCUIT(ItemCircuit::new, "part.circuit"),
    COKE(ItemCoke::new, "fuel.coke"),
    CROWBAR_IRON(ItemCrowbarIron::new, "tool.crowbar.iron"),
    CROWBAR_STEEL(ItemCrowbarSteel::new, "tool.crowbar.steel"),
    CROWBAR_THAUMIUM(ItemCrowbarThaumium::new, "tool.crowbar.thaumium", null, Mod.THAUMCRAFT::isLoaded),
    CROWBAR_VOID(ItemCrowbarVoid::new, "tool.crowbar.void", null, Mod.THAUMCRAFT::isLoaded),
    DUST(ItemDust::new, "dust"),
    GEAR(ItemGear::new, "part.gear"),
    GOGGLES(ItemGoggles::new, "armor.goggles"),
    FIRESTONE_CRACKED(ItemFirestoneCracked::new, "firestone.cracked"),
    FIRESTONE_CUT(ItemFirestone::new, "firestone.cut"),
    FIRESTONE_RAW(ItemFirestone::new, "firestone.raw"),
    FIRESTONE_REFINED(ItemFirestoneRefined::new, "firestone.refined"),
    HOE_STEEL(ItemSteelHoe::new, "tool.hoe.steel", Items.IRON_HOE),
    INGOT(ItemIngot::new, "ingot"),
    LAPOTRON_UPGRADE(ItemIngot::new, "ic2.upgrade.lapotron", null, () -> Mod.areLoaded(Mod.IC2, Mod.IC2_CLASSIC)),
    MAG_GLASS(ItemMagnifyingGlass::new, "tool.magnifying.glass"),
    NOTEPAD(ItemNotepad::new, "tool.notepad"),
    NUGGET(ItemNugget::new, "nugget"),
    OVERALLS(ItemOveralls::new, "armor.overalls"),
    PICKAXE_STEEL(ItemSteelPickaxe::new, "tool.pickaxe.steel", Items.IRON_PICKAXE),
    PLATE(ItemPlate::new, "part.plate"),
    RAIL(ItemRail::new, "part.rail"),
    RAILBED(ItemRailbed::new, "part.railbed"),
    REBAR(ItemRebar::new, "part.rebar", "ingotIron"),
    ROUTING_TABLE(ItemRoutingTable::new, "routing.table", Items.WRITABLE_BOOK),
    SHEARS_STEEL(ItemSteelShears::new, "tool.shears.steel", Items.SHEARS),
    SHOVEL_STEEL(ItemSteelShovel::new, "tool.shovel.steel", Items.IRON_SHOVEL),
    SIGNAL_BLOCK_SURVEYOR(ItemSignalBlockSurveyor::new, "tool.signal.surveyor"),
    SIGNAL_LABEL(ItemSignalLabel::new, "tool.signal.label"),
    SIGNAL_LAMP(ItemSignalLamp::new, "part.signal.lamp", Blocks.REDSTONE_LAMP),
    SIGNAL_TUNER(ItemSignalTuner::new, "tool.signal.tuner"),
    STONE_CARVER(ItemStoneCarver::new, "tool.stone.carver"),
    SWORD_STEEL(ItemSteelSword::new, "tool.sword.steel", Items.IRON_SWORD),
    TICKET(ItemTicket::new, "routing.ticket", Items.PAPER),
    TICKET_GOLD(ItemTicketGold::new, "routing.ticket.gold", Items.GOLD_NUGGET),
    TIE(ItemTie::new, "part.tie"),
    TRACK_KIT(ItemTrackKit::new, "track_kit", null, RailcraftBlocks.TRACK_OUTFITTED::isEnabled),
    TURBINE_BLADE(ItemTurbineBlade::new, "part.turbine.blade", "ingotSteel", EnumMachineAlpha.TURBINE::isEnabled),
    TURBINE_DISK(ItemTurbineDisk::new, "part.turbine.disk", "blockSteel", EnumMachineAlpha.TURBINE::isEnabled),
    TURBINE_ROTOR(ItemTurbineRotor::new, "part.turbine.rotor", null, EnumMachineAlpha.TURBINE::isEnabled),
    WHISTLE_TUNER(ItemWhistleTuner::new, "tool.whistle.tuner");
    public static final RailcraftItems[] VALUES = values();
    private final Supplier<Item> itemSupplier;
    private final String tag;
    @Nullable
    private final Object altRecipeObject;
    private final Supplier<Boolean> prerequisites;
    private Item item;
    private Optional<IRailcraftItem> railcraftObject = Optional.empty();

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
        Arrays.stream(VALUES).forEach(i -> i.getObject().ifPresent(IRailcraftItem::finalizeDefinition));
    }

    @Override
    public void register() {
        if (item != null)
            return;

        if (isEnabled()) {
            item = itemSupplier.get();
            if (!(item instanceof IRailcraftItem))
                throw new RuntimeException("Railcraft Items must implement IRailcraftItem");
            IRailcraftItem railcraftItem = (IRailcraftItem) item;
            railcraftObject = Optional.of(railcraftItem);
            item.setRegistryName(getBaseTag());
            item.setUnlocalizedName(getFullTag());
            RailcraftRegistry.register(item);
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
        return getObject().map(i -> i.getStack(qty, variant)).orElse(null);
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        checkVariantObject(variant);
        register();
        Object obj = getObject().map(i -> i.getRecipeObject(variant)).orElse(null);
        if (obj == null && variant != null)
            obj = variant.getAlternate(tag);
        if (obj == null)
            obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public Optional<IRailcraftItem> getObject() {
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
