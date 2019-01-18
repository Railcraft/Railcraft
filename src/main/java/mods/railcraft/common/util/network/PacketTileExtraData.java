/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.common.util.misc.Game;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketTileExtraData extends RailcraftPacket {

    private ITileExtraDataHandler tile;
    private ByteArrayOutputStream bytes;
    private DataOutputStream data;

    public PacketTileExtraData() {
    }

    public PacketTileExtraData(ITileExtraDataHandler tile) {
        this.tile = tile;
    }

    public DataOutputStream getDataStream() {
        if (data == null) {
            bytes = new ByteArrayOutputStream();
            data = new DataOutputStream(bytes);
        }
        return data;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {

        BlockPos pos = ((TileEntity) tile).getPos();
        data.writeInt(pos.getX());
        data.writeInt(pos.getY());
        data.writeInt(pos.getZ());
        data.write(bytes.toByteArray());
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

        TileEntity t = world.getTileEntity(new BlockPos(x, y, z));

        if (t instanceof ITileExtraDataHandler) {
            ((ITileExtraDataHandler) t).onUpdatePacket(data);
        }
    }

    @Override
    public int getID() {
        return PacketType.TILE_EXTRA_DATA.ordinal();
    }
}
