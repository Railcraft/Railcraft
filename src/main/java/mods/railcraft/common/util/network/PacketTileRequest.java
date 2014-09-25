/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class PacketTileRequest extends RailcraftPacket {

    private TileEntity tile;
    private EntityPlayerMP player;

    public PacketTileRequest(EntityPlayerMP player) {
        super();
        this.player = player;
    }

    public PacketTileRequest(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(tile.getWorldObj().provider.dimensionId);
        data.writeInt(tile.xCoord);
        data.writeInt(tile.yCoord);
        data.writeInt(tile.zCoord);
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        World world = DimensionManager.getWorld(data.readInt());
        if (world == null)
            return;

        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        tile = world.getTileEntity(x, y, z);

        if (tile instanceof RailcraftTileEntity && player != null)
            PacketDispatcher.sendToPlayer(new PacketTileEntity((RailcraftTileEntity) tile), player);
    }

    @Override
    public int getID() {
        return PacketType.TILE_REQUEST.ordinal();
    }

}
