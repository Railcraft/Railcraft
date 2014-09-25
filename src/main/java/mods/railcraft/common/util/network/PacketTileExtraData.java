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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mods.railcraft.common.util.misc.Game;

public class PacketTileExtraData extends RailcraftPacket
{

    private ITileExtraDataHandler tile;
    private ByteArrayOutputStream bytes;
    private DataOutputStream data;

    public PacketTileExtraData()
    {
        super();
    }

    public PacketTileExtraData(ITileExtraDataHandler tile)
    {
        this.tile = tile;
    }

    public DataOutputStream getDataStream()
    {
        if(data == null) {
            bytes = new ByteArrayOutputStream();
            data = new DataOutputStream(bytes);
        }
        return data;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException
    {
        data.writeInt(tile.getX());
        data.writeInt(tile.getY());
        data.writeInt(tile.getZ());
        data.write(bytes.toByteArray());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(DataInputStream data) throws IOException
    {
        World world = Game.getWorld();
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        TileEntity t = world.getTileEntity(x, y, z);

        if(t instanceof ITileExtraDataHandler) {
            ((ITileExtraDataHandler)t).onUpdatePacket(data);
        }
    }

    @Override
    public int getID()
    {
        return PacketType.TILE_EXTRA_DATA.ordinal();
    }
}
