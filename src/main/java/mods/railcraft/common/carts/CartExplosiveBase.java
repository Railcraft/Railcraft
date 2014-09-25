/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.api.carts.CartTools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import mods.railcraft.api.carts.IExplosiveCart;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;

public abstract class CartExplosiveBase extends CartBase implements IExplosiveCart, IGuiReturnHandler {

    private static final byte FUSE_DATA_ID = 25;
    private static final byte BLAST_DATA_ID = 26;
    private static final byte PRIMED_DATA_ID = 27;
    private final static float BLAST_RADIUS_BYTE_MULTIPLIER = 0.5f;
    private final static float BLAST_RADIUS_MIN = 2;
    private final static float BLAST_RADIUS_MAX = 6;
    private final static float BLAST_RADIUS_MAX_BONUS = 5;
    public static final short MAX_FUSE = 500;
    public static final short MIN_FUSE = 0;
    private boolean isExploding;

    public CartExplosiveBase(World world) {
        super(world);

    }

    public CartExplosiveBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataWatcher.addObject(FUSE_DATA_ID, Short.valueOf((short) 80));
        dataWatcher.addObject(BLAST_DATA_ID, Byte.valueOf((byte) 8));
        dataWatcher.addObject(PRIMED_DATA_ID, Byte.valueOf((byte) 0));
    }

    @Override
    public Block func_145820_n() {
        return Blocks.tnt;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.isCollidedHorizontally) {
            double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;

            if (d0 >= 0.01) {
                explode(getBlastRadiusWithSpeedModifier());
            }
        }

        if (isPrimed()) {
            setFuse((short) (getFuse() - 1));
            if (getFuse() <= 0) {
                explode();
            } else {
                worldObj.spawnParticle("smoke", posX, posY + 0.8D, posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void explode() {
        explode(getBlastRadius());
    }

    protected void explode(float blastRadius) {
        isExploding = true;
        if (Game.isHost(getWorld())) {
            worldObj.createExplosion(this, posX, posY, posZ, blastRadius, true);
            setDead();
        }
    }

    @Override
    public void killMinecart(DamageSource damageSource) {
        if (isDead || isExploding) {
            return;
        }
        double speedSq = this.motionX * this.motionX + this.motionZ * this.motionZ;
        if (damageSource.isFireDamage() || damageSource.isExplosion() || speedSq >= 0.01D) {
            explode(getBlastRadiusWithSpeedModifier());
        } else {
            super.killMinecart(damageSource);
        }
    }

    protected float getBlastRadiusWithSpeedModifier() {
        double blast = Math.min(CartTools.getCartSpeedUncapped(this), getMaxBlastRadiusBonus());
        return (float) (getBlastRadius() + rand.nextDouble() * 1.5 * blast);
    }

    protected float getBlastRadiusWithFallModifier(float distance) {
        double blast = Math.min(distance / 10.0, getMaxBlastRadiusBonus());
        return (float) (getBlastRadius() + rand.nextDouble() * 1.5 * blast);
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean powered) {
        setPrimed(powered);
    }

    @Override
    protected void fall(float distance) {
        if (distance >= 3.0F) {
            explode(getBlastRadiusWithFallModifier(distance));
        }

        super.fall(distance);
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack != null) {
            if (stack.getItem() == Items.flint_and_steel
                    || stack.getItem() instanceof ItemFirestoneRefined) {
                setPrimed(true);
                stack.damageItem(1, player);
            } else if (stack.getItem() == Items.string) {
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
                GuiHandler.openGui(EnumGui.CART_TNT_FUSE, player, worldObj, this);
            } else if (stack.getItem() == Items.gunpowder) {
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
                GuiHandler.openGui(EnumGui.CART_TNT_BLAST, player, worldObj, this);
            }
        }
        return true;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public boolean isPrimed() {
        return dataWatcher.getWatchableObjectByte(PRIMED_DATA_ID) != 0;
    }

    @Override
    public void setPrimed(boolean primed) {
        if (Game.isHost(worldObj) && isPrimed() != primed) {
            if (primed) {
                worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 1.0F);
            }
            dataWatcher.updateObject(PRIMED_DATA_ID, primed ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0));
        }
    }

    @Override
    public int getFuse() {
        return dataWatcher.getWatchableObjectShort(FUSE_DATA_ID);
    }

    @Override
    public void setFuse(int f) {
        f = (short) Math.max(f, MIN_FUSE);
        f = (short) Math.min(f, MAX_FUSE);
        dataWatcher.updateObject(FUSE_DATA_ID, (short) f);
    }

    protected float getMinBlastRadius() {
        return BLAST_RADIUS_MIN;
    }

    protected float getMaxBlastRadius() {
        return BLAST_RADIUS_MAX;
    }

    protected float getMaxBlastRadiusBonus() {
        return BLAST_RADIUS_MAX_BONUS;
    }

    @Override
    public float getBlastRadius() {
        return dataWatcher.getWatchableObjectByte(BLAST_DATA_ID) * BLAST_RADIUS_BYTE_MULTIPLIER;
    }

    @Override
    public void setBlastRadius(float b) {
        b = Math.max(b, getMinBlastRadius());
        b = Math.min(b, getMaxBlastRadius());
        b /= BLAST_RADIUS_BYTE_MULTIPLIER;
        dataWatcher.updateObject(BLAST_DATA_ID, Byte.valueOf((byte) b));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        data.setShort("Fuse", (short) getFuse());
        data.setByte("blastRadius", dataWatcher.getWatchableObjectByte(BLAST_DATA_ID));
        data.setBoolean("Primed", isPrimed());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        setFuse(data.getShort("Fuse"));
        setBlastRadius(data.getByte("blastRadius"));
        setPrimed(data.getBoolean("Primed"));
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeShort(getFuse());
        data.writeByte(dataWatcher.getWatchableObjectByte(BLAST_DATA_ID));
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        setFuse(data.readShort());
        setBlastRadius(data.readByte());
    }

    @Override
    public World getWorld() {
        return worldObj;
    }

    @Override
    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }
}
