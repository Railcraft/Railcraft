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
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.tracks.TrackFactory;
import mods.railcraft.common.util.misc.Game;

public class PacketTileEntity extends RailcraftPacket {

    private RailcraftTileEntity tile;

    public PacketTileEntity() {
        super();
    }

    public PacketTileEntity(RailcraftTileEntity tile) {
        this.tile = tile;
//        System.out.println("Created Tile Packet: " + tile.getClass().getSimpleName());
    }

//    @Override
//    public FMLProxyPacket getPacket() {
//        Packet pkt = super.getPacket();
//        pkt.isChunkDataPacket = true;
//        return pkt;
//    }
    @Override
    public void writeData(DataOutputStream data) throws IOException {
        data.writeInt(tile.xCoord);
        data.writeInt(tile.yCoord);
        data.writeInt(tile.zCoord);
        data.writeShort(tile.getId());
        tile.writePacketData(data);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(DataInputStream data) throws IOException {
        World world = Game.getWorld();
        if (world == null) {
//            Game.logDebug("Receive Tile Packet: World Null");
            return;
        }

        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();
        short id = data.readShort();

        if (id < 0 || y < 0 || !world.blockExists(x, y, z)) {
//            Game.logDebug("Receive Tile Packet: Block not found");
            return;
        }

        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof RailcraftTileEntity) {
            tile = (RailcraftTileEntity) te;
            if (tile.getId() != id)
                tile = null;
        } else
            tile = null;

        if (tile == null) {
            Block block = world.getBlock(x, y, z);
            Block blockTrack = RailcraftBlocks.getBlockTrack();
            if (blockTrack != null && blockTrack == block) {
                tile = TrackFactory.makeTrackTile(id);
                world.setTileEntity(x, y, z, tile);
            } else {
                world.removeTileEntity(x, y, z);
                te = world.getTileEntity(x, y, z);
                if (te instanceof RailcraftTileEntity)
                    tile = (RailcraftTileEntity) te;
            }
        }

        if (tile == null)
            return;
        
//        Game.logDebug("Receive Tile Packet: {0}", tile.getClass().toString());

        try {
            tile.readPacketData(data);
        } catch (IOException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            if (Game.IS_DEBUG)
                throw ex;
            else
                Game.logThrowable("Exception in PacketTileEntity.readData:", ex);
        }
    }

    @Override
    public int getID() {
        return PacketType.TILE_ENTITY.ordinal();
    }

}
