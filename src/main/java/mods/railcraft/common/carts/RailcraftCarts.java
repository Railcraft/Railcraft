/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.ItemWrapper;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.modules.ModuleCharge;
import mods.railcraft.common.modules.ModuleLocomotives;
import mods.railcraft.common.modules.ModuleSteam;
import mods.railcraft.common.modules.ModuleThaumcraft;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.thaumcraft.EntityLocomotiveSteamMagic;
import mods.railcraft.common.util.crafting.CartDisassemblyRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EntityIDs;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public enum RailcraftCarts implements IRailcraftCartContainer {

    // Vanilla Carts
    BASIC(0, "cart_basic", EntityCartBasic.class, (c) -> Items.MINECART),
    CHEST(0, "cart_chest", EntityCartChest.class, (c) -> Items.CHEST_MINECART, from(Blocks.CHEST)),
    COMMAND_BLOCK(3, "cart_command_block", EntityCartCommand.class, (c) -> Items.COMMAND_BLOCK_MINECART, from(Blocks.COMMAND_BLOCK)),
    FURNACE(0, "cart_furnace", EntityCartFurnace.class, (c) -> Items.FURNACE_MINECART, from(Blocks.FURNACE)),
    HOPPER(0, "cart_hopper", EntityCartHopper.class, (c) -> Items.HOPPER_MINECART, from(Blocks.HOPPER)),
    TNT(0, "cart_tnt", EntityCartTNT.class, (c) -> Items.TNT_MINECART, from(Blocks.TNT)),

    // Railcraft Carts
    BORE(1, "bore", EntityTunnelBore.class, ItemTunnelBore::new),
    CARGO(0, "cart_cargo", EntityCartCargo.class, ItemCartCargo::new, from(Blocks.TRAPPED_CHEST)),
    ENERGY_BATBOX(0, "cart_ic2_batbox", EntityCartEnergyBatBox.class, ItemCart::new, ModItems.BAT_BOX::get),
    ENERGY_CESU(0, "cart_ic2_cesu", EntityCartEnergyCESU.class, ItemCart::new, ModItems.CESU::get),
    ENERGY_MFE(0, "cart_ic2_mfe", EntityCartEnergyMFE.class, ItemCart::new, ModItems.MFE::get),
    ENERGY_MFSU(1, "cart_ic2_MFSU", EntityCartEnergyMFSU.class, ItemCart::new, ModItems.MFSU::get),
    GIFT(3, "cart_gift", EntityCartGift.class, ItemCartGift::new),
    MOW_TRACK_LAYER(1, "mow_track_layer", EntityCartTrackLayer.class, ItemCartMOWTrackLayer::new),
    MOW_TRACK_RELAYER(1, "mow_track_relayer", EntityCartTrackRelayer.class, ItemCartMOWTrackRelayer::new),
    MOW_TRACK_REMOVER(1, "mow_track_remover", EntityCartTrackRemover.class, ItemCartMOWTrackRemover::new),
    MOW_UNDERCUTTER(1, "mow_undercutter", EntityCartUndercutter.class, ItemCartMOWUndercutter::new),
    PUMPKIN(3, "cart_pumpkin", EntityCartPumpkin.class, ItemCartPumpkin::new),
    REDSTONE_FLUX(0, "cart_redstone_flux", EntityCartRF.class, ItemCartRF::new),
    TANK(0, "cart_tank", EntityCartTank.class, ItemCartTank::new, () -> {
        ItemStack stack = EnumMachineBeta.TANK_IRON_GAUGE.getStack();
        return !InvTools.isEmpty(stack) ? stack : new ItemStack(Blocks.GLASS, 8);
    }),
    TNT_WOOD(0, "cart_tnt_wood", EntityCartTNTWood.class, ItemCart::new),
    WORK(0, "cart_work", EntityCartWork.class, ItemCartWork::new, from(Blocks.CRAFTING_TABLE)),

    // Railcraft Locomotives
    LOCO_STEAM_SOLID(1, "locomotive_steam_solid", EntityLocomotiveSteamSolid.class, ItemLocoSteamSolid::new) {
        {
            conditions().add(ModuleLocomotives.class);
            conditions().add(ModuleSteam.class);
        }
    },
    LOCO_STEAM_MAGIC(1, "locomotive_steam_magic", EntityLocomotiveSteamMagic.class, (c) -> new ItemLocomotive(c, LocomotiveRenderType.STEAM_MAGIC, EnumColor.PURPLE, EnumColor.SILVER)) {
        {
            conditions().add(ModuleLocomotives.class);
            conditions().add(ModuleThaumcraft.class);
        }
    },
    LOCO_ELECTRIC(1, "locomotive_electric", EntityLocomotiveElectric.class, ItemLocoElectric::new) {
        {
            conditions().add(ModuleLocomotives.class);
            conditions().add(ModuleCharge.class);
        }
    },
    LOCO_CREATIVE(3, "locomotive_creative", EntityLocomotiveCreative.class, (c) -> new ItemLocomotive(c, LocomotiveRenderType.ELECTRIC, EnumColor.BLACK, EnumColor.MAGENTA)) {
        {
            conditions().add(ModuleLocomotives.class);
        }
    },
    WORLDSPIKE_STANDARD(0, "cart_worldspike_standard", EntityCartWorldspikeStandard.class, ItemCartWorldspikeStandard::new, WorldspikeVariant.STANDARD::getStack) {
        {
            conditions().add(RailcraftBlocks.WORLDSPIKE);
            conditions().add(WorldspikeVariant.STANDARD);
        }
    },
    WORLDSPIKE_ADMIN(3, "cart_worldspike_admin", EntityCartWorldspikeAdmin.class, ItemCartWorldspike::new) {
        {
            conditions().add(RailcraftBlocks.WORLDSPIKE);
            conditions().add(WorldspikeVariant.ADMIN);
        }
    },
    WORLDSPIKE_PERSONAL(0, "cart_worldspike_personal", EntityCartWorldspikePersonal.class, ItemCartWorldspikePersonal::new, WorldspikeVariant.PERSONAL::getStack) {
        {
            conditions().add(RailcraftBlocks.WORLDSPIKE);
            conditions().add(WorldspikeVariant.PERSONAL);
        }
    },;
    @SuppressWarnings("WeakerAccess")
    public static final RailcraftCarts[] VALUES = values();
    private final Class<? extends EntityMinecart> type;
    private final byte id;
    private final byte rarity;
    private final Function<RailcraftCarts, Item> itemSupplier;
    private final Definition def;
    @Nullable
    private final Supplier<ItemStack> contentsSupplier;
    private Item item;
    private boolean isSetup;
    private Optional<IRailcraftItemSimple> railcraftObject = Optional.empty();

    private static Supplier<ItemStack> from(Block block) {
        return () -> new ItemStack(block);
    }

    RailcraftCarts(int rarity, String tag, Class<? extends EntityMinecart> type, Function<RailcraftCarts, Item> itemSupplier) {
        this(rarity, tag, type, itemSupplier, null);
    }

    RailcraftCarts(int rarity, String tag, Class<? extends EntityMinecart> type, Function<RailcraftCarts, Item> itemSupplier, @Nullable Supplier<ItemStack> contentsSupplier) {
        int entityId;
        try {
            entityId = (byte) EntityIDs.class.getField(tag.toUpperCase(Locale.ROOT)).getInt(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.def = new Definition(this, tag, null);
        this.itemSupplier = itemSupplier;
        this.contentsSupplier = contentsSupplier;
        this.id = (byte) entityId;
        this.rarity = (byte) rarity;
        this.type = type;
        conditions().add(RailcraftConfig::isCartEnabled, () -> "disabled via config");
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @SuppressWarnings("WeakerAccess")
    public static IRailcraftCartContainer fromClass(Class<? extends EntityMinecart> clazz) {
        return Arrays.stream(VALUES).filter(cart -> clazz.equals(cart.type)).findFirst().orElse(BASIC);
    }

    public static IRailcraftCartContainer fromCart(EntityMinecart cart) {
        return fromClass(cart.getClass());
    }

    @Nullable
    public static IRailcraftCartContainer getCartType(@Nullable ItemStack cart) {
        if (cart == null)
            return null;
        if (cart.getItem() == Items.MINECART)
            return RailcraftCarts.BASIC;
        if (cart.getItem() == Items.CHEST_MINECART)
            return RailcraftCarts.CHEST;
        if (cart.getItem() == Items.TNT_MINECART)
            return RailcraftCarts.TNT;
        if (cart.getItem() == Items.FURNACE_MINECART)
            return RailcraftCarts.FURNACE;
        if (cart.getItem() == Items.HOPPER_MINECART)
            return RailcraftCarts.HOPPER;
        if (cart.getItem() == Items.COMMAND_BLOCK_MINECART)
            return RailcraftCarts.COMMAND_BLOCK;
        if (cart.getItem() instanceof ItemCart)
            return ((ItemCart) cart.getItem()).getCartType();
        return null;
    }

    public static void finalizeDefinitions() {
        Arrays.stream(VALUES).forEach(cart -> {
            cart.getObject().ifPresent(IRailcraftItemSimple::finalizeDefinition);
            if (cart.contentsSupplier != null)
                CraftingPlugin.addRecipe(new CartDisassemblyRecipe.RailcraftVariant(cart));
        });
    }

    @Override
    public String getEntityLocalizationTag() {
        return "entity.railcraft." + getBaseTag() + ".name";
    }

    public Item getItem() {
        return getObject().map(IRailcraftObject::getObject).orElse(null);
    }

    @Override
    public Optional<IRailcraftItemSimple> getObject() {
        return railcraftObject;
    }

    @Override
    public Class<? extends EntityMinecart> getCartClass() {
        return type;
    }

    @Override
    @Nullable
    public ItemStack getContents() {
        switch (this) {
            case TANK:
                if (EnumMachineBeta.TANK_IRON_GAUGE.isAvailable())
                    return EnumMachineBeta.TANK_IRON_GAUGE.getStack();
            default: {
                if (contentsSupplier == null)
                    return null;
                return contentsSupplier.get();
            }
        }
    }

    @Override
    public EntityMinecart makeCart(ItemStack stack, World world, double i, double j, double k) {
        try {
            Constructor<? extends EntityMinecart> con = type.getConstructor(World.class, double.class, double.class, double.class);
            EntityMinecart entity = con.newInstance(world, i, j, k);
            if (entity instanceof IRailcraftCart)
                ((IRailcraftCart) entity).initEntityFromItem(stack);
            return entity;
        } catch (Throwable ex) {
            Game.logThrowable("Failed to create cart entity!", ex);
        }
        return new EntityCartBasic(world, i, j, k);
    }

//    private ItemCart defineItem() {
//        switch (this) {
//            case LOCO_STEAM_SOLID:
//                return new ItemLocomotive(this, LocomotiveRenderType.STEAM_SOLID, EnumColor.SILVER, EnumColor.GRAY);
//            case LOCO_STEAM_MAGIC:
//                return new ItemLocomotive(this, LocomotiveRenderType.STEAM_MAGIC, EnumColor.SILVER, EnumColor.GRAY);
//            case LOCO_ELECTRIC:
//                return new ItemLocomotive(this, LocomotiveRenderType.ELECTRIC, EnumColor.YELLOW, EnumColor.BLACK);
//            case LOCO_CREATIVE:
//                return new ItemLocomotive(this, LocomotiveRenderType.ELECTRIC, EnumColor.BLACK, EnumColor.MAGENTA);
//            default:
//                return new ItemCart(this);
//        }
//    }

    @SuppressWarnings("unchecked")
    private void registerEntity() {
        if (id < 0)
            return;
        EntityRegistry.registerModEntity(type, getBaseTag(), id, Railcraft.getMod(), 256, 2, true);

        // Legacy stuff
//        EntityList.NAME_TO_CLASS.put("Railcraft." + getTag(), type);
//        if (this == LOCO_STEAM_SOLID)
//            EntityList.NAME_TO_CLASS.put("Railcraft.railcraft.cart.loco.steam", type);
    }

    @Override
    public void register() {
        if (!isSetup) {
            isSetup = true;
            if (isEnabled()) {
                registerEntity();
                item = itemSupplier.apply(this);
                if (item instanceof ItemCart) {
                    ItemCart itemCart = (ItemCart) item;
                    railcraftObject = Optional.of(itemCart);
                    itemCart.setRegistryName(getRegistryName());
                    itemCart.setUnlocalizedName("railcraft.entity." + getBaseTag().replace("_", "."));
                    itemCart.setRarity(rarity);
                    RailcraftRegistry.register((IRailcraftItemSimple) itemCart);

                    itemCart.initializeDefinintion();
                    Railcraft.instance.recipeWaitList.add(itemCart);
                } else if (item != null) {
                    railcraftObject = Optional.of(new ItemWrapper(item));
                }
            } else {
                conditions().printFailureReason(this);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (isVanillaCart())
            return true;
        return conditions().test(this);
    }

    public boolean isVanillaCart() {
        switch (this) {
            case CHEST:
            case HOPPER:
            case COMMAND_BLOCK:
            case BASIC:
            case FURNACE:
            case TNT:
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Entity{" + getBaseTag() + "}";
    }
}
