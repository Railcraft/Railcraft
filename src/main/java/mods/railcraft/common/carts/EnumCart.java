/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import cpw.mods.fml.common.registry.EntityRegistry;
import java.lang.reflect.Constructor;
import java.util.Locale;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.EntityIDs;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public enum EnumCart {

    BASIC(0, EntityCartBasic.class, null),
    CHEST(0, EntityCartChest.class, new ItemStack(Blocks.chest)),
    FURNACE(0, EntityCartFurnace.class, new ItemStack(Blocks.furnace)),
    TNT_WOOD(0, EntityCartTNTWood.class, new ItemStack(Blocks.tnt)),
    TANK(0, EntityCartTank.class, null),
    ANCHOR(0, EntityCartAnchor.class, null),
    WORK(0, EntityCartWork.class, new ItemStack(Blocks.crafting_table)),
    TRACK_RELAYER(1, EntityCartTrackRelayer.class, null),
    UNDERCUTTER(1, EntityCartUndercutter.class, null),
    PUMPKIN(3, EntityCartPumpkin.class, new ItemStack(Blocks.pumpkin)),
    GIFT(3, EntityCartGift.class, null),
    ANCHOR_PERSONAL(0, EntityCartAnchorPersonal.class, null),
    ANCHOR_ADMIN(3, EntityCartAnchorAdmin.class, null),
    TNT(0, EntityCartTNT.class, new ItemStack(Blocks.tnt)),
    LOCO_STEAM_SOLID(1, EntityLocomotiveSteamSolid.class, null),
    LOCO_STEAM_MAGIC(1, EntityLocomotiveSteamMagic.class, null),
    LOCO_ELECTRIC(1, EntityLocomotiveElectric.class, null),
    BORE(1, EntityTunnelBore.class, null),
    ENERGY_BATBOX(0, EntityCartEnergyBatBox.class, null),
    ENERGY_CESU(0, EntityCartEnergyCESU.class, null),
    ENERGY_MFE(0, EntityCartEnergyMFE.class, null),
    HOPPER(0, EntityMinecartHopper.class, new ItemStack(Blocks.hopper)),
    TRACK_LAYER(1, EntityCartTrackLayer.class, null),
    TRACK_REMOVER(1, EntityCartTrackRemover.class, null);
    public static final EnumCart[] VALUES = values();
    private final Class<? extends EntityMinecart> type;
    private final byte id;
    private final byte rarity;
    private ItemStack contents = null;
    private ItemStack cartItem;

    private EnumCart(int rarity, Class<? extends EntityMinecart> type, ItemStack contents) {
        int entityId = -1;
        try {
            entityId = (byte) EntityIDs.class.getField("CART_" + name()).getInt(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.id = (byte) entityId;
        this.rarity = (byte) rarity;
        this.type = type;
        this.contents = contents;
    }

    public byte getId() {
        return id;
    }

    public String getTag() {
        return "railcraft.cart." + name().toLowerCase(Locale.ENGLISH).replace('_', '.');
    }

    public Class<? extends EntityMinecart> getCartClass() {
        return type;
    }

    public void setContents(ItemStack stack) {
        contents = stack.copy();
    }

    public ItemStack getContents() {
        switch (this) {
            case TANK:
                if (EnumMachineBeta.TANK_IRON_GAUGE.isAvaliable())
                    return EnumMachineBeta.TANK_IRON_GAUGE.getItem();
            default: {
                if (contents == null)
                    return null;
                return contents.copy();
            }
        }
    }

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
                return new ItemLocomotive(this, LocomotiveRenderType.STEAM_SOLID);
            case LOCO_STEAM_MAGIC:
                return new ItemLocomotive(this, LocomotiveRenderType.STEAM_MAGIC);
            case LOCO_ELECTRIC:
                return new ItemLocomotive(this, LocomotiveRenderType.ELECTRIC);
            case ANCHOR:
            case ANCHOR_ADMIN:
            case ANCHOR_PERSONAL:
                return new ItemCartAnchor(this);
            default:
                return new ItemCart(this);
        }
    }

    public void registerEntity() {
        if (id < 0)
            return;
        EntityRegistry.registerModEntity(type, MiscTools.cleanTag(getTag()), id, Railcraft.getMod(), 256, 3, true);

        // Legacy stuff
        EntityList.stringToClassMapping.put("Railcraft." + getTag(), type);
        if (this == LOCO_STEAM_SOLID)
            EntityList.stringToClassMapping.put("Railcraft.railcraft.cart.loco.steam", type);
    }

    public boolean setup() {
        String tag = getTag();
        boolean cartEnabled = RailcraftConfig.isCartEnabled(tag);
        if (cartEnabled) {
            registerEntity();
            ItemCart item = defineItem();
            item.setUnlocalizedName(tag);
            item.setRarity(rarity);
            RailcraftRegistry.register(item);
            ItemStack stack = new ItemStack(item);
            setCartItem(stack);
            return true;
        }
        return false;
    }

    public boolean isEnabled() {
        String tag = getTag();
        return RailcraftConfig.isCartEnabled(tag);
    }

    public static EnumCart fromClass(Class<? extends EntityMinecart> cls) {
        for (EnumCart cart : VALUES) {
            if (cls.equals(cart.type))
                return cart;
        }
        return BASIC;
    }

    public static EnumCart fromCart(EntityMinecart cart) {
        return fromClass(cart.getClass());
    }

    public static EnumCart getCartType(ItemStack cart) {
        if (cart == null)
            return null;
        if (cart.getItem() == Items.minecart)
            return EnumCart.BASIC;
        if (cart.getItem() == Items.chest_minecart)
            return EnumCart.CHEST;
        if (cart.getItem() == Items.tnt_minecart)
            return EnumCart.TNT;
        if (cart.getItem() == Items.furnace_minecart)
            return EnumCart.FURNACE;
        if (cart.getItem() == Items.hopper_minecart)
            return EnumCart.HOPPER;
        if (cart.getItem() instanceof ItemCart)
            return ((ItemCart) cart.getItem()).getCartType();
        return null;
    }

}
