/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.tracks.TrackFactory;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

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
    public void writeData(RailcraftOutputStream data) throws IOException {
        BlockPos pos = tile.getPos();
        data.writeInt(pos.getX());
        data.writeInt(pos.getY());
        data.writeInt(pos.getZ());
        data.writeShort(tile.getId());
        tile.writePacketData(data);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        World world = Game.getWorld();
        if (world == null) {
//            Game.logDebug("Receive Tile Packet: World Null");
            return;
        }

        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();
        short id = data.readShort();
        BlockPos pos = new BlockPos(x, y, z);

        if (id < 0 || y < 0 || !WorldPlugin.isBlockLoaded(world, pos)) {
//            Game.logDebug("Receive Tile Packet: Block not found");
            return;
        }

        TileEntity te = world.getTileEntity(pos);

        if (te instanceof RailcraftTileEntity) {
            tile = (RailcraftTileEntity) te;
            if (tile.getId() != id)
                tile = null;
        } else
            tile = null;

        if (tile == null) {
            Block block = WorldPlugin.getBlock(world, pos);
            Block blockTrack = RailcraftBlocks.track.block();
            if (blockTrack != null && blockTrack == block) {
                tile = TrackFactory.makeTrackTile(id);
                world.setTileEntity(pos, tile);
            } else {
                world.removeTileEntity(pos);
                te = world.getTileEntity(pos);
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
