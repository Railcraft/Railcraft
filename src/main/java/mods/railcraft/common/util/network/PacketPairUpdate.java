/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import mods.railcraft.api.signals.ISignalBlockTile;
import mods.railcraft.api.signals.SignalBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import mods.railcraft.common.util.misc.Game;

public class PacketPairUpdate extends RailcraftPacket {
    private AbstractPair pairing;
    private PacketType packetType;

    public PacketPairUpdate(PacketType packetType) {
        super();
        this.packetType = packetType;
    }

    public PacketPairUpdate(AbstractPair pairing) {
        this.pairing = pairing;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(pairing.getTile().xCoord);
        data.writeInt(pairing.getTile().yCoord);
        data.writeInt(pairing.getTile().zCoord);

        Collection<WorldCoordinate> pairs = pairing.getPairs();
        data.writeByte(pairs.size());
        for (WorldCoordinate coord : pairs) {
            data.writeInt(coord.x);
            data.writeInt(coord.y);
            data.writeInt(coord.z);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(DataInputStream data) throws IOException {
        World world = Game.getWorld();
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        TileEntity tile = world.getTileEntity(x, y, z);

        if (packetType == PacketType.CONTROLLER_UPDATE) {
            if (tile instanceof IControllerTile)
                pairing = ((IControllerTile) tile).getController();
        } else if (packetType == PacketType.RECEIVER_UPDATE) {
            if (tile instanceof IReceiverTile)
                pairing = ((IReceiverTile) tile).getReceiver();
        } else if (packetType == PacketType.SIGNAL_UPDATE) {
            if (tile instanceof ISignalBlockTile)
                pairing = ((ISignalBlockTile) tile).getSignalBlock();
        }
        if (pairing != null) {
            try {
                pairing.clearPairings();
            } catch (Throwable error) {
                Game.logErrorAPI("Railcraft", error, AbstractPair.class);
            }
            int size = data.readByte();
            for (int i = 0; i < size; i++) {
                pairing.addPair(data.readInt(), data.readInt(), data.readInt());
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
