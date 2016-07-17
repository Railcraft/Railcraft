/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import com.google.common.collect.Lists;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.core.*;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.thaumcraft.EntityLocomotiveSteamMagic;
import mods.railcraft.common.util.crafting.CartUncraftingRecipe;
import mods.railcraft.common.util.misc.EntityIDs;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;

public enum RailcraftCarts implements ICartType, IRailcraftObjectContainer {

    BASIC(0, EntityCartBasic.class),
    CHEST(0, EntityCartChest.class, true, new ItemStack(Blocks.CHEST)),
    FURNACE(0, EntityCartFurnace.class, true, new ItemStack(Blocks.FURNACE)),
    TNT_WOOD(0, EntityCartTNTWood.class, false, new ItemStack(Blocks.TNT)),
    TANK(0, EntityCartTank.class, true),
    CARGO(0, EntityCartCargo.class, true, new ItemStack(Blocks.TRAPPED_CHEST)),
    ANCHOR(0, EntityCartAnchor.class, true),
    WORK(0, EntityCartWork.class, true, new ItemStack(Blocks.CRAFTING_TABLE)),
    TRACK_RELAYER(1, EntityCartTrackRelayer.class),
    UNDERCUTTER(1, EntityCartUndercutter.class),
    PUMPKIN(3, EntityCartPumpkin.class, false, new ItemStack(Blocks.PUMPKIN)),
    GIFT(3, EntityCartGift.class),
    ANCHOR_PERSONAL(0, EntityCartAnchorPersonal.class, true),
    ANCHOR_ADMIN(3, EntityCartAnchorAdmin.class),
    TNT(0, EntityCartTNT.class, true, new ItemStack(Blocks.TNT)),
    LOCO_STEAM_SOLID(1, EntityLocomotiveSteamSolid.class) {
        {
            addModule(ModuleLocomotives.class);
            addModule(ModuleSteam.class);
        }
    },
    LOCO_STEAM_MAGIC(1, EntityLocomotiveSteamMagic.class) {
        {
            addModule(ModuleLocomotives.class);
            addModule(ModuleThaumcraft.class);
        }
    },
    LOCO_ELECTRIC(1, EntityLocomotiveElectric.class) {
        {
            addModule(ModuleLocomotives.class);
            addModule(ModuleElectricity.class);
        }
    },
    LOCO_CREATIVE(3, EntityLocomotiveCreative.class) {
        {
            addModule(ModuleLocomotives.class);
        }
    },
    BORE(1, EntityTunnelBore.class),
    ENERGY_BATBOX(0, EntityCartEnergyBatBox.class, true),
    ENERGY_CESU(0, EntityCartEnergyCESU.class, true),
    ENERGY_MFE(0, EntityCartEnergyMFE.class, true),
    ENERGY_MFSU(1, EntityCartEnergyMFSU.class, true),
    // TODO: listing vanilla classes here causes weird error messages
    HOPPER(0, EntityMinecartHopper.class, true, new ItemStack(Blocks.HOPPER)),
    TRACK_LAYER(1, EntityCartTrackLayer.class),
    TRACK_REMOVER(1, EntityCartTrackRemover.class),
    COMMAND_BLOCK(3, EntityCartCommand.class, true, new ItemStack(Blocks.COMMAND_BLOCK)),
    REDSTONE_FLUX(0, EntityCartRF.class);
    @SuppressWarnings("WeakerAccess")
    public static final RailcraftCarts[] VALUES = values();
    private final Class<? extends EntityMinecart> type;
    private final byte id;
    private final byte rarity;
    private final boolean canBeUncrafted;
    private final List<Class<? extends IRailcraftModule>> modules = Lists.newArrayList();
    private ItemCart item;
    private ItemStack contents;
    private ItemStack cartItem;
    private boolean isSetup;

    RailcraftCarts(int rarity, Class<? extends EntityMinecart> type) {
        this(rarity, type, false, null);
    }

    RailcraftCarts(int rarity, Class<? extends EntityMinecart> type, boolean canBeUncrafted) {
        this(rarity, type, canBeUncrafted, null);
    }

    RailcraftCarts(int rarity, Class<? extends EntityMinecart> type, boolean canBeUncrafted, ItemStack contents) {
        int entityId;
        try {
            entityId = (byte) EntityIDs.class.getField("CART_" + name()).getInt(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.id = (byte) entityId;
        this.rarity = (byte) rarity;
        this.type = type;
        this.canBeUncrafted = canBeUncrafted;
        this.contents = contents;
    }

    @SuppressWarnings("WeakerAccess")
    public static ICartType fromClass(Class<? extends EntityMinecart> cls) {
        for (RailcraftCarts cart : VALUES) {
            if (cls.equals(cart.type))
                return cart;
        }
        return BASIC;
    }

    public static ICartType fromCart(EntityMinecart cart) {
        return fromClass(cart.getClass());
    }

    public static ICartType getCartType(ItemStack cart) {
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
            if (type.item != null)
                type.item.finalizeDefinition();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected final void addModule(Class<? extends IRailcraftModule> module) {
        modules.add(module);
    }

    @Override
    public byte getId() {
        return id;
    }

    @Override
    public void register() {

    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return false;
    }

    @Override
    public String getBaseTag() {
        return name().toLowerCase(Locale.ROOT).replace('_', '.');
    }

    @Nullable
    @Override
    public ItemStack getWildcard() {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getStack() {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty) {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, int meta) {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getStack(IVariantEnum variant) {
        return null;
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, IVariantEnum variant) {
        if (cartItem != null)
            return cartItem.copy();
        return null;
    }

    @Override
    public IRailcraftObject getObject() {
        return item;
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
    public ItemStack getContents() {
        switch (this) {
            case TANK:
                if (EnumMachineBeta.TANK_IRON_GAUGE.isAvailable())
                    return EnumMachineBeta.TANK_IRON_GAUGE.getItem();
            default: {
                if (contents == null)
                    return null;
                return contents.copy();
            }
        }
    }

    public void setContents(ItemStack stack) {
        contents = stack.copy();
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

    /**
     * @return the cartItem
     */
    @Override
    public ItemStack getCartItem() {
        if (cartItem == null)
            return null;
        return cartItem.copy();
    }

    /**
     * @param cartItem the cartItem to set
     */
    public void setCartItem(ItemStack cartItem) {
        this.cartItem = cartItem.copy();
    }

    private ItemCart defineItem() {
        switch (this) {
            case BORE:
                return new ItemTunnelBore();
            case LOCO_STEAM_SOLID:
                return new ItemLocomotive(this, LocomotiveRenderType.STEAM_SOLID, EnumColor.SILVER, EnumColor.GRAY);
            case LOCO_STEAM_MAGIC:
                return new ItemLocomotive(this, LocomotiveRenderType.STEAM_MAGIC, EnumColor.SILVER, EnumColor.GRAY);
            case LOCO_ELECTRIC:
                return new ItemLocomotive(this, LocomotiveRenderType.ELECTRIC, EnumColor.YELLOW, EnumColor.BLACK);
            case LOCO_CREATIVE:
                return new ItemLocomotive(this, LocomotiveRenderType.ELECTRIC, EnumColor.BLACK, EnumColor.MAGENTA);
            case ANCHOR:
            case ANCHOR_ADMIN:
            case ANCHOR_PERSONAL:
                return new ItemCartAnchor(this);
            default:
                return new ItemCart(this);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerEntity() {
        if (id < 0)
            return;
        EntityRegistry.registerModEntity(type, MiscTools.cleanTag(getTag()), id, Railcraft.getMod(), 256, 3, true);

        // Legacy stuff
        EntityList.NAME_TO_CLASS.put("Railcraft." + getTag(), type);
        if (this == LOCO_STEAM_SOLID)
            EntityList.NAME_TO_CLASS.put("Railcraft.railcraft.cart.loco.steam", type);
    }

    public boolean setup() {
        String tag = getTag();
        if (!isSetup && isEnabled()) {
            isSetup = true;
            registerEntity();
            item = defineItem();
            item.setRegistryName("cart." + getBaseTag());
            item.setUnlocalizedName(tag);
            item.setRarity(rarity);
            RailcraftRegistry.register(item);

            item.initializeDefinintion();
            item.defineRecipes();

            ItemStack cartItem = new ItemStack(item);
            setCartItem(cartItem);
            if (canBeUncrafted)
                CraftingPlugin.addRecipe(new CartUncraftingRecipe.RailcraftCartUncraftingRecipe(this));
            return true;
        }
        return isSetup;
    }

    @Override
    public boolean isEnabled() {
        for (Class<? extends IRailcraftModule> module : modules) {
            if (!RailcraftModuleManager.isModuleEnabled(module))
                return false;
        }
        return RailcraftConfig.isCartEnabled(getTag());
    }

    @Override
    public boolean isLoaded() {
        return false;
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
