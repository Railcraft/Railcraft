/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ITrackKitReversible;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartTools;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitDisembark extends TrackKitPowered implements ITrackKitReversible {

    private static final int TIME_TILL_NEXT_MOUNT = 40;
    private boolean mirrored;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.DISEMBARK;
    }

    //    @Override
//    public IExtendedBlockState getExtendedState(IExtendedBlockState state) {
//        state = super.getExtendedState(state);
////        state = state.withProperty(REVERSED, mirrored);
//        return state;
//    }
    @Override
    public int getRenderState() {
        int state = mirrored ? 1 : 0;
        if (isPowered())
            state += 2;
        return state;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered() && cart.isBeingRidden()) {
            double x = getPos().getX();
            double z = getPos().getZ();
            double offset = 1;
            IBlockState state = theWorldAsserted().getBlockState(getPos());
            EnumRailDirection dir = TrackTools.getTrackDirectionRaw(state);
            if (dir == EnumRailDirection.NORTH_SOUTH)
                if (mirrored)
                    x += offset;
                else
                    x -= offset;
            else if (mirrored)
                z += offset;
            else
                z -= offset;
            CartTools.removePassengers(cart, new Vec3d(x + 0.5, getPos().getY() + 1, z + 0.5));
            cart.getEntityData().setInteger("MountPrevention", TIME_TILL_NEXT_MOUNT);
        }
    }

    @Override
    public boolean isReversed() {
        return mirrored;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.mirrored = reversed;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("mirrored", mirrored);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        mirrored = nbttagcompound.getBoolean("mirrored");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(mirrored);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean m = data.readBoolean();
        if (mirrored != m) {
            mirrored = m;
            markBlockNeedsUpdate();
        }
    }

}
