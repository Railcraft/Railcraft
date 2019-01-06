/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.api.signals.*;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Collection;

public class PacketPairUpdate extends RailcraftPacket {
    private AbstractPair pairing;
    private PacketType packetType;

    public PacketPairUpdate(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketPairUpdate(AbstractPair pairing) {
        this.pairing = pairing;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        BlockPos pos = pairing.getCoords();
        data.writeInt(pos.getX());
        data.writeInt(pos.getY());
        data.writeInt(pos.getZ());

        Collection<BlockPos> pairs = pairing.getPairs();
        data.writeByte(pairs.size());
        for (BlockPos coord : pairs) {
            data.writeInt(coord.getX());
            data.writeInt(coord.getY());
            data.writeInt(coord.getZ());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        World world = Game.getWorld();
        if (world == null)
            return;
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        if (packetType == PacketType.CONTROLLER_UPDATE) {
            if (tile instanceof IControllerTile)
                pairing = ((IControllerTile) tile).getController();
        } else if (packetType == PacketType.RECEIVER_UPDATE) {
            if (tile instanceof IReceiverTile)
                pairing = ((IReceiverTile) tile).getReceiver();
        } else if (packetType == PacketType.SIGNAL_UPDATE) {
            if (tile instanceof ISignalTileBlock)
                pairing = ((ISignalTileBlock) tile).getSignalBlock();
        }
        if (pairing != null) {
            try {
                pairing.clearPairings();
            } catch (Throwable error) {
                Game.log().api(Railcraft.NAME, error, AbstractPair.class);
            }
            int size = data.readByte();
            for (int i = 0; i < size; i++) {
                pairing.addPair(new BlockPos(data.readInt(), data.readInt(), data.readInt()));
            }
        }
    }

    @Override
    public int getID() {
        if (pairing instanceof SignalController)
            return PacketType.CONTROLLER_UPDATE.ordinal();
        if (pairing instanceof SignalReceiver)
            return PacketType.RECEIVER_UPDATE.ordinal();
        if (pairing instanceof SignalBlock)
            return PacketType.SIGNAL_UPDATE.ordinal();
        return -1;
    }
}
