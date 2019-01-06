/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import mods.railcraft.common.blocks.machine.worldspike.TileWorldspike;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import mods.railcraft.common.carts.EntityCartWorldspike;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.PlayerOrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public final class ChunkManager implements OrderedLoadingCallback, PlayerOrderedLoadingCallback {

    public static ChunkManager getInstance() {
        return Holder.INSTANCE;
    }

    @SubscribeEvent
    public void entityEnteredChunk(EntityEvent.EnteringChunk event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityCartWorldspike) {
            if (Game.isHost(entity.world)) {
                ((EntityCartWorldspike) entity).forceChunkLoading(event.getNewChunkX(), event.getNewChunkZ());
            } else {
                ((EntityCartWorldspike) entity).setupChunks(event.getNewChunkX(), event.getNewChunkZ());
            }
        }
    }

    /**
     * Returns a Set of ChunkCoordIntPair containing the chunks between the
     * start and end chunks.
     * <p/>
     * One of the pairs of start/end coords need to be equal.
     * <p/>
     * Coordinates are in chunk coordinates, not world coordinates.
     *
     * @param xChunkA Start Chunk x-Coord
     * @param zChunkA Start Chunk z-Coord
     * @param xChunkB End Chunk x-Coord
     * @param zChunkB End Chunk z-Coord
     * @param max     Max number of chunks to return
     * @return A set of chunks.
     */
    public Set<ChunkPos> getChunksBetween(int xChunkA, int zChunkA, int xChunkB, int zChunkB, int max) {
        Set<ChunkPos> chunkList = new HashSet<>();

        if (xChunkA != xChunkB && zChunkA != zChunkB) {
            return chunkList;
        }

        int xStart = Math.min(xChunkA, xChunkB);
        int xEnd = Math.max(xChunkA, xChunkB);

        int zStart = Math.min(zChunkA, zChunkB);
        int zEnd = Math.max(zChunkA, zChunkB);

        for (int xx = xStart; xx <= xEnd; xx++) {
            for (int zz = zStart; zz <= zEnd; zz++) {
                chunkList.add(new ChunkPos(xx, zz));
                if (chunkList.size() >= max) {
                    return chunkList;
                }
            }
        }
        return chunkList;
    }

    /**
     * Returns a Set of ChunkCoordIntPair containing the chunks around point [x,
     * z]. Coordinates are in chunk coordinates, not world coordinates.
     *
     * @param xChunk Chunk x-Coord
     * @param zChunk Chunk z-Coord
     * @param radius Distance from [x, z] to include, in number of chunks.
     * @return A set of chunks.
     */
    public Set<ChunkPos> getChunksAround(int xChunk, int zChunk, int radius) {
        Set<ChunkPos> chunkList = new HashSet<>();
        for (int xx = xChunk - radius; xx <= xChunk + radius; xx++) {
            for (int zz = zChunk - radius; zz <= zChunk + radius; zz++) {
                chunkList.add(new ChunkPos(xx, zz));
            }
        }
        return chunkList;
    }

    /**
     * Returns a Set of ChunkCoordIntPair containing the chunks around point [x,
     * z]. Coordinates are in world coordinates, not chunk coordinates.
     *
     * @param xWorld World x-Coord
     * @param zWorld World z-Coord
     * @param radius Distance from [x, z] to include, in blocks.
     * @return A set of chunks.
     */
    public Set<ChunkPos> getBufferAround(int xWorld, int zWorld, int radius) {
        int minX = (xWorld - radius) >> 4;
        int maxX = (xWorld + radius) >> 4;
        int minZ = (zWorld - radius) >> 4;
        int maxZ = (zWorld + radius) >> 4;

        Set<ChunkPos> chunkList = new HashSet<>();
        for (int xx = minX; xx <= maxX; xx++) {
            for (int zz = minZ; zz <= maxZ; zz++) {
                chunkList.add(new ChunkPos(xx, zz));
            }
        }
        return chunkList;
    }

    private void printWorldspike(String type, int x, int y, int z) {
        if (RailcraftConfig.printWorldspikeLocations()) {
            Game.log().msg(Level.INFO, "{0} found at [{1}-{2}-{3}]", type, x, y, z);
        }
    }

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        for (Ticket ticket : tickets) {
            if (ticket.isPlayerTicket())
                continue;
            Entity entity = ticket.getEntity();
            if (entity == null) {
                int x = ticket.getModData().getInteger("x");
                int y = ticket.getModData().getInteger("y");
                int z = ticket.getModData().getInteger("z");

                if (y >= 0) {
                    TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                    if (tile instanceof TileWorldspike) {
                        TileWorldspike worldspike = (TileWorldspike) tile;
                        worldspike.forceChunkLoading(ticket);
                        printWorldspike(worldspike.getName(), x, y, z);
                    }
                }
            } else {
                if (entity instanceof EntityCartWorldspike) {
                    EntityCartWorldspike worldspike = (EntityCartWorldspike) entity;
                    worldspike.setChunkTicket(ticket);
                    worldspike.forceChunkLoading(worldspike.chunkCoordX, worldspike.chunkCoordZ);
                    printWorldspike(worldspike.getName(), (int) entity.posX, (int) entity.posY, (int) entity.posZ);
                }
            }
        }
    }

    @Override
    public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
        Set<Ticket> adminTickets = new HashSet<>();
        Set<Ticket> worldTickets = new HashSet<>();
        Set<Ticket> cartTickets = new HashSet<>();
        for (Ticket ticket : tickets) {
            Entity entity = ticket.getEntity();
            if (entity == null) {
                int y = ticket.getModData().getInteger("y");
                String type = ticket.getModData().getString("type");

                if (y >= 0) {
                    if (type.equals(WorldspikeVariant.ADMIN.getTag()))
                        adminTickets.add(ticket);
                    else if (type.equals(WorldspikeVariant.STANDARD.getTag()))
                        worldTickets.add(ticket);
                    else if (type.isEmpty())
                        worldTickets.add(ticket);
                }
            } else {
                if (entity instanceof EntityCartWorldspike) {
                    cartTickets.add(ticket);
                }
            }
        }

        List<Ticket> claimedTickets = new LinkedList<>();
        claimedTickets.addAll(cartTickets);
        claimedTickets.addAll(adminTickets);
        claimedTickets.addAll(worldTickets);
        return claimedTickets;
    }

    @Override
    public ListMultimap<String, Ticket> playerTicketsLoaded(ListMultimap<String, Ticket> tickets, World world) {
        if (RailcraftConfig.printWorldspikeLocations())
            for (Ticket ticket : tickets.values()) {
                Entity entity = ticket.getEntity();
                if (entity == null) {
                    int x = ticket.getModData().getInteger("x");
                    int y = ticket.getModData().getInteger("y");
                    int z = ticket.getModData().getInteger("z");
                    String type = ticket.getModData().getString("type");

                    if (y >= 0) {
                        printWorldspike(LocalizationPlugin.translate(type + ".name"), x, y, z);
                    }
                }
            }
        return LinkedListMultimap.create();
    }

    ChunkManager() {
    }

    static final class Holder {
        static final ChunkManager INSTANCE = new ChunkManager();

        private Holder() {
        }
    }
}
