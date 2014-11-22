/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.UP;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileChestRailcraft extends TileMachineItem {

    private static final ForgeDirection[] UP_DOWN_AXES = new ForgeDirection[]{UP, DOWN};
    private static final int TICK_PER_SYNC = 64;
    private ForgeDirection facing = ForgeDirection.EAST;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;

    public TileChestRailcraft() {
        super(27);
    }

    public final ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        facing = MiscTools.getHorizontalSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public final boolean rotateBlock(ForgeDirection axis) {
        if (axis == UP || axis == DOWN)
            return false;
        if (facing == axis)
            facing = axis.getOpposite();
        else
            facing = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public final ForgeDirection[] getValidRotations() {
        return UP_DOWN_AXES;
    }

    @Override
    public final boolean openGui(EntityPlayer player) {
        if (worldObj.isSideSolid(xCoord, yCoord + 1, zCoord, ForgeDirection.DOWN))
            return false;
        else if (isCatOnChest())
            return false;
        if (Game.isHost(worldObj))
            player.displayGUIChest((IInventory) this);
        return true;
    }

    private boolean isCatOnChest() {
        int x = xCoord;
        int y = yCoord;
        int z = zCoord;
        Iterator it = worldObj.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBox(x, (y + 1), z, (x + 1), (y + 2), (z + 1))).iterator();
        EntityOcelot cat;
        do {
            if (!it.hasNext())
                return false;
            EntityOcelot entityocelot = (EntityOcelot) it.next();
            cat = (EntityOcelot) entityocelot;
        } while (!cat.isSitting());
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (clock % TICK_PER_SYNC == 0)
            WorldPlugin.addBlockEvent(worldObj, xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);

        this.prevLidAngle = this.lidAngle;
        float angleChange = 0.1F;

        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0F)
            this.worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);

        if (this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
            float angle = this.lidAngle;

            if (this.numUsingPlayers > 0)
                this.lidAngle += angleChange;
            else
                this.lidAngle -= angleChange;

            if (this.lidAngle > 1.0F)
                this.lidAngle = 1.0F;

            float openAngle = 0.5F;

            if (this.lidAngle < openAngle && angle >= openAngle)
                this.worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);

            if (this.lidAngle < 0.0F)
                this.lidAngle = 0.0F;
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        if (id == 1) {
            this.numUsingPlayers = value;
            return true;
        }
        return super.receiveClientEvent(id, value);
    }

    @Override
    public void openInventory() {
        ++this.numUsingPlayers;
        WorldPlugin.addBlockEvent(worldObj, xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);
    }

    @Override
    public void closeInventory() {
        --this.numUsingPlayers;
        WorldPlugin.addBlockEvent(worldObj, xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("facing", (byte) facing.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = ForgeDirection.getOrientation(data.getByte("facing"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        facing = ForgeDirection.getOrientation(data.readByte());
    }

}
