/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ITrackKitPowered;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitDumping extends TrackKitSuspended implements ITrackKitPowered {

    private static final int TIME_TILL_NEXT_MOUNT = 40;
    private boolean powered;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.DUMPING;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered())
            return;
        World world = theWorldAsserted();
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain().setPos(getTile().getPos());
        for (int i = 0; i < 2; i++) {
            pos.move(EnumFacing.DOWN);
            if (world.getBlockState(pos).getBlock().causesSuffocation(world.getBlockState(pos)))
                return;
        }
        if (cart.isBeingRidden()) {
            CartTools.removePassengers(cart, cart.getPositionVector().add(0, -2, 0));
        }
        cart.getEntityData().setInteger("MountPrevention", TIME_TILL_NEXT_MOUNT);
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, @Nullable Block neighborBlock) {
        super.onNeighborBlockChange(state, neighborBlock);
        testPower(state);
    }

//    @Override
//    public IExtendedBlockState getExtendedState(IExtendedBlockState state) {
//        state = super.getExtendedState(state);
////        state = state.withProperty(POWERED, isPowered());
//        return state;
//    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("powered", powered);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        powered = nbttagcompound.getBoolean("powered");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (p != powered) {
            powered = p;
            markBlockNeedsUpdate();
        }
    }

}
