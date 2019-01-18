/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.common.blocks.TileRailcraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.IOException;

public class PacketTileRequest extends RailcraftPacket {

    private TileEntity tile;
    private EntityPlayerMP player;

    public PacketTileRequest(EntityPlayerMP player) {
        this.player = player;
    }

    public PacketTileRequest(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
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

        tile = world.getTileEntity(new BlockPos(x, y, z));

        if (tile instanceof TileRailcraft && player != null)
            PacketBuilder.instance().sendTileEntityPacket(tile, player);
    }

    @Override
    public int getID() {
        return PacketType.TILE_REQUEST.ordinal();
    }

}
