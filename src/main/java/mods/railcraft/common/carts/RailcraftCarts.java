/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.collect.Lists;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItem;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.thaumcraft.EntityLocomotiveSteamMagic;
import mods.railcraft.common.util.crafting.CartDisassemblyRecipe;
import mods.railcraft.common.util.misc.EntityIDs;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

public enum RailcraftCarts implements IRailcraftCartContainer {

    // Vanilla Carts
    // TODO: listing vanilla classes here causes weird error messages
    BASIC(0, EntityCartBasic.class, (c) -> Items.MINECART),
    CHEST(0, EntityCartChest.class, (c) -> Items.CHEST_MINECART, from(Blocks.CHEST)),
    COMMAND_BLOCK(3, EntityCartCommand.class, (c) -> Items.COMMAND_BLOCK_MINECART, from(Blocks.COMMAND_BLOCK)),
    FURNACE(0, EntityCartFurnace.class, (c) -> Items.FURNACE_MINECART, from(Blocks.FURNACE)),
    HOPPER(0, EntityMinecartHopper.class, (c) -> Items.HOPPER_MINECART, from(Blocks.HOPPER)),
    TNT(0, EntityCartTNT.class, (c) -> Items.TNT_MINECART, from(Blocks.TNT)),

    // Railcraft Carts
    ANCHOR_WORLD(0, EntityCartAnchor.class, ItemCartAnchorWorld::new, EnumMachineAlpha.ANCHOR_WORLD::getItem),
    ANCHOR_ADMIN(3, EntityCartAnchorAdmin.class, ItemCartAnchor::new),
    ANCHOR_PERSONAL(0, EntityCartAnchorPersonal.class, ItemCartAnchorPersonal::new, EnumMachineAlpha.ANCHOR_PERSONAL::getItem),
    BORE(1, EntityTunnelBore.class, ItemTunnelBore::new),
    CARGO(0, EntityCartCargo.class, ItemCartCargo::new, from(Blocks.TRAPPED_CHEST)),
    ENERGY_BATBOX(0, EntityCartEnergyBatBox.class, ItemCart::new, ModItems.BAT_BOX::get),
    ENERGY_CESU(0, EntityCartEnergyCESU.class, ItemCart::new, ModItems.CESU::get),
    ENERGY_MFE(0, EntityCartEnergyMFE.class, ItemCart::new, ModItems.MFE::get),
    ENERGY_MFSU(1, EntityCartEnergyMFSU.class, ItemCart::new, ModItems.MFSU::get),
    GIFT(3, EntityCartGift.class, ItemCart::new),
    MOW_TRACK_LAYER(1, EntityCartTrackLayer.class, ItemCartMOWTrackLayer::new),
    MOW_TRACK_RELAYER(1, EntityCartTrackRelayer.class, ItemCartMOWTrackRelayer::new),
    MOW_TRACK_REMOVER(1, EntityCartTrackRemover.class, ItemCartMOWTrackRemover::new),
    MOW_UNDERCUTTER(1, EntityCartUndercutter.class, ItemCartMOWUndercutter::new),
    PUMPKIN(3, EntityCartPumpkin.class, ItemCart::new),
    REDSTONE_FLUX(0, EntityCartRF.class, ItemCartRF::new),
    TANK(0, EntityCartTank.class, ItemCart::new, () -> {
        ItemStack stack = EnumMachineBeta.TANK_IRON_GAUGE.getItem();
        return stack != null ? stack : new ItemStack(Blocks.GLASS, 8);
    }),
    TNT_WOOD(0, EntityCartTNTWood.class, ItemCart::new),
    WORK(0, EntityCartWork.class, ItemCartWork::new, from(Blocks.CRAFTING_TABLE)),

    // Railcraft Locomotives
    LOCO_STEAM_SOLID(1, EntityLocomotiveSteamSolid.class, ItemLocoSteamSolid::new) {
        {
            addModule(ModuleLocomotives.class);
            addModule(ModuleSteam.class);
        }
    },
    LOCO_STEAM_MAGIC(1, EntityLocomotiveSteamMagic.class, (c) -> new ItemLocomotive(c, LocomotiveRenderType.STEAM_MAGIC, EnumColor.PURPLE, EnumColor.SILVER)) {
        {
            addModule(ModuleLocomotives.class);
            addModule(ModuleThaumcraft.class);
        }
    },
    LOCO_ELECTRIC(1, EntityLocomotiveElectric.class, ItemLocoElectric::new) {
        {
            addModule(ModuleLocomotives.class);
            addModule(ModuleElectricity.class);
        }
    },
    LOCO_CREATIVE(3, EntityLocomotiveCreative.class, (c) -> new ItemLocomotive(c, LocomotiveRenderType.ELECTRIC, EnumColor.BLACK, EnumColor.MAGENTA)) {
        {
            addModule(ModuleLocomotives.class);
        }
    },;
    @SuppressWarnings("WeakerAccess")
    public static final RailcraftCarts[] VALUES = values();
    private final Class<? extends EntityMinecart> type;
    private final byte id;
    private final byte rarity;
    private final Function<RailcraftCarts, Item> itemSupplier;
    @Nullable
    private final Supplier<ItemStack> contentsSupplier;
    private final List<Class<? extends IRailcraftModule>> modules = Lists.newArrayList();
    private Item item;
    private boolean isSetup;

    private static Supplier<ItemStack> from(Block block) {
        return () -> new ItemStack(block);
    }

    RailcraftCarts(int rarity, Class<? extends EntityMinecart> type, Function<RailcraftCarts, Item> itemSupplier) {
        this(rarity, type, itemSupplier, null);
    }

    RailcraftCarts(int rarity, Class<? extends EntityMinecart> type, Function<RailcraftCarts, Item> itemSupplier, @Nullable Supplier<ItemStack> contentsSupplier) {
        int entityId;
        try {
            entityId = (byte) EntityIDs.class.getField("CART_" + name()).getInt(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.itemSupplier = itemSupplier;
        this.contentsSupplier = contentsSupplier;
        this.id = (byte) entityId;
        this.rarity = (byte) rarity;
        this.type = type;
    }

    @SuppressWarnings("WeakerAccess")
    public static IRailcraftCartContainer fromClass(Class<? extends EntityMinecart> cls) {
        for (RailcraftCarts cart : VALUES) {
            if (cls.equals(cart.type))
                return cart;
        }
        return BASIC;
    }

    public static IRailcraftCartContainer fromCart(EntityMinecart cart) {
        return fromClass(cart.getClass());
    }

    public static IRailcraftCartContainer getCartType(ItemStack cart) {
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
        if (cart.getItem() instanceof ItemCart)
            return ((ItemCart) cart.getItem()).getCartType();
        return null;
    }

    public static void finalizeDefinitions() {
        for (RailcraftCarts type : VALUES) {
            IRailcraftItem object = type.getObject();
            if (object != null)
                object.finalizeDefinition();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected final void addModule(Class<? extends IRailcraftModule> module) {
        modules.add(module);
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return false;
    }

    @Override
    public String getBaseTag() {
        return name().toLowerCase(Locale.ROOT).replace('_', '.');
    }

    @Override
    @Nullable
    public ItemStack getStack(int qty, int meta) {
        if (item != null)
            return new ItemStack(item, qty, meta);
        return null;
    }

    @Override
    @Nullable
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        if (item != null)
            return new ItemStack(item, qty);
        return null;
    }

    @Override
    public IRailcraftItem getObject() {
        if (item instanceof ItemCart)
            return (ItemCart) item;
        return null;
    }

    @Nullable
    @Override
    public Object getRecipeObject() {
        return item;
    }

    @Nullable
    @Override
    public Object getRecipeObject(IVariantEnum variant) {
        return item;
    }

    @Override
    public String getTag() {
        return "railcraft.cart." + getBaseTag();
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
                    return EnumMachineBeta.TANK_IRON_GAUGE.getItem();
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
        EntityRegistry.registerModEntity(type, MiscTools.cleanTag(getTag()), id, Railcraft.getMod(), 256, 3, true);

        // Legacy stuff
//        EntityList.NAME_TO_CLASS.put("Railcraft." + getTag(), type);
//        if (this == LOCO_STEAM_SOLID)
//            EntityList.NAME_TO_CLASS.put("Railcraft.railcraft.cart.loco.steam", type);
    }

    @Override
    public void register() {
        String tag = getTag();
        if (!isSetup && isEnabled()) {
            isSetup = true;
            registerEntity();
            item = itemSupplier.apply(this);
            if (item instanceof ItemCart) {
                ItemCart itemCart = (ItemCart) item;
                itemCart.setRegistryName("cart." + getBaseTag());
                itemCart.setUnlocalizedName(tag);
                itemCart.setRarity(rarity);
                RailcraftRegistry.register(itemCart);

                itemCart.initializeDefinintion();
                itemCart.defineRecipes();
            }

            if (contentsSupplier != null)
                CraftingPlugin.addRecipe(new CartDisassemblyRecipe.RailcraftVariant(this));
        }
    }

    @Override
    public boolean isEnabled() {
        if (isVanillaCart())
            return true;
        for (Class<? extends IRailcraftModule> module : modules) {
            if (!RailcraftModuleManager.isModuleEnabled(module))
                return false;
        }
        return RailcraftConfig.isCartEnabled(getTag());
    }

    @Override
    public boolean isLoaded() {
        return isSetup;
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

}
