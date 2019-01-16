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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.IOException;

public class PacketPairRequest extends RailcraftPacket {
    private AbstractPair pairing;
    private EntityPlayerMP player;
    private PacketType packetType;

    public PacketPairRequest(EntityPlayerMP player, PacketType type) {
        this.player = player;
        this.packetType = type;
    }

    public PacketPairRequest(AbstractPair pairing) {
        this.pairing = pairing;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        TileEntity tile = pairing.getTile();
        data.writeInt(tile.getWorld().provider.getDimension());
        BlockPos pos = tile.getPos();
        data.writeInt(pos.getX());
        data.writeInt(pos.getY());
        data.writeInt(pos.getZ());
    }

    @Override
    public void readData(RailcraftInputStream data) throws IOException {
        World world = DimensionManager.getWorld(data.readInt());
        if (world == null)
            return;

        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        switch (packetType) {
            case CONTROLLER_REQUEST:
                if (tile instanceof IControllerTile)
                    pairing = ((IControllerTile) tile).getController();
                break;
            case RECEIVER_REQUEST:
                if (tile instanceof IReceiverTile)
                    pairing = ((IReceiverTile) tile).getReceiver();
                break;
            case SIGNAL_REQUEST:
                if (tile instanceof ISignalTileBlock)
                    pairing = ((ISignalTileBlock) tile).getSignalBlock();
                break;
        }
        if (pairing != null && player != null) {
            PacketPairUpdate pkt = new PacketPairUpdate(pairing);
            PacketDispatcher.sendToPlayer(pkt, player);
        }
    }

    @Override
    public int getID() {
        if (pairing instanceof SignalController)
            return PacketType.CONTROLLER_REQUEST.ordinal();
        if (pairing instanceof SignalReceiver)
            return PacketType.RECEIVER_REQUEST.ordinal();
        if (pairing instanceof SignalBlock)
            return PacketType.SIGNAL_REQUEST.ordinal();
        return -1;
    }
}
