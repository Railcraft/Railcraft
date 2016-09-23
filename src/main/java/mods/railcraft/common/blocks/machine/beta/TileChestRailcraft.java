/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO: investigate chest locking
public abstract class TileChestRailcraft extends TileMachineItem implements ITileRotate {

    private static final EnumFacing[] UP_DOWN_AXES = {UP, DOWN};
    private static final int TICK_PER_SYNC = 64;
    private EnumFacing facing = EnumFacing.EAST;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;

    protected TileChestRailcraft() {
        super(27);
    }

    public final EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        if (entityLiving != null)
            facing = entityLiving.getHorizontalFacing();
    }

    @Override
    public final boolean rotateBlock(EnumFacing axis) {
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
    public final EnumFacing[] getValidRotations() {
        return UP_DOWN_AXES;
    }

    @Override
    public final boolean openGui(EntityPlayer player) {
        if (worldObj.isSideSolid(getPos().up(), DOWN))
            return false;
        else if (isCatOnChest())
            return false;
        if (Game.isHost(worldObj))
            player.displayGUIChest(this);
        return true;
    }

    private boolean isCatOnChest() {
        AxisAlignedBB searchBox = AABBFactory.start().createBoxForTileAt(getPos()).offset(0.0, 1.0, 0.0).build();
        List<EntityOcelot> cats = worldObj.getEntitiesWithinAABB(EntityOcelot.class, searchBox);
        return cats.stream().anyMatch(EntityTameable::isSitting);
    }

    @Override
    public void update() {
        super.update();

        if (clock % TICK_PER_SYNC == 0)
            WorldPlugin.addBlockEvent(worldObj, getPos(), getBlockType(), 1, numUsingPlayers);

        this.prevLidAngle = lidAngle;
        float angleChange = 0.1F;

        if (numUsingPlayers > 0 && lidAngle == 0.0F)
            SoundHelper.playSound(worldObj, null, getPos(), SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);

        if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F) {
            float angle = lidAngle;

            if (numUsingPlayers > 0)
                this.lidAngle += angleChange;
            else
                this.lidAngle -= angleChange;

            if (lidAngle > 1.0F)
                this.lidAngle = 1.0F;

            float openAngle = 0.5F;

            if (lidAngle < openAngle && angle >= openAngle)
                SoundHelper.playSound(worldObj, null, getPos(), SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);

            if (lidAngle < 0.0F)
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
    public void openInventory(EntityPlayer player) {
        ++this.numUsingPlayers;
        WorldPlugin.addBlockEvent(worldObj, getPos(), getBlockType(), 1, numUsingPlayers);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        --this.numUsingPlayers;
        WorldPlugin.addBlockEvent(worldObj, getPos(), getBlockType(), 1, numUsingPlayers);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("facing", (byte) facing.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = EnumFacing.getFront(data.getByte("facing"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        facing = EnumFacing.getFront(data.readByte());
    }

    @Override
    public void setFacing(@Nonnull EnumFacing facing) {
        this.facing = facing;
    }

}
