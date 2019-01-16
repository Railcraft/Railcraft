/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * It also contains some generic code that most carts will find useful.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class CartBase extends EntityMinecart implements IRailcraftCart, IItemCart {

    protected CartBase(World world) {
        super(world);
    }

    protected CartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        cartInit();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        saveToNBT(compound);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        loadFromNBT(compound);
    }

    @Override
    public String getName() {
        return hasCustomName() ? getCustomNameTag() : LocalizationPlugin.translate(getCartType().getEntityLocalizationTag());
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
    }

    @Override
    public final boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand)) || doInteract(player, hand);
    }

    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        return true;
    }

    @Override
    public final ItemStack getCartItem() {
        return createCartItem(this);
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        killAndDrop(this);
    }

    @Override
    public final EntityMinecart.Type getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPoweredCart() {
        return false;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public boolean canPassItemRequests(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    public @Nullable World theWorld() {
        return world;
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartTools.isInRangeToRenderDist(this, distance);
    }

    public List<String> getDebugOutput() {
        List<String> debug = new ArrayList<>();
        debug.add("Railcraft Entity Data Dump");
        debug.add("Object: " + this);
        debug.add(String.format("Coordinates: d=%d, %s", world.provider.getDimension(), getPositionVector()));
        debug.add("Owner: " + CartTools.getCartOwnerEntity(this));
        debug.add("LinkA: " + CartTools.getCartOwnerEntity(this));
        return debug;
    }
}
