/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logbook;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.blocks.TileRailcraftTicking;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 6/23/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileLogbook extends TileRailcraftTicking {
    private static final float SEARCH_RADIUS = 16;
    private final Multimap<LocalDate, GameProfile> log = HashMultimap.create();

    public Multimap<LocalDate, GameProfile> getLog() {
        return log;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world))
            return;

        if (clock % 32 == 0) {
            List<EntityPlayer> players = EntitySearcher.find(EntityPlayer.class).around(getPos()).outTo(SEARCH_RADIUS).in(world);
            if (!players.isEmpty()) {
                LocalDate date = LocalDate.now();
                log.putAll(date, players.stream().map(EntityPlayer::getGameProfile).collect(Collectors.toList()));
            }
        }
    }

    public static NBTTagCompound convertLogToNBT(Multimap<LocalDate, GameProfile> log) {
        NBTTagCompound nbt = new NBTTagCompound();
        LocalDate monthAgo = LocalDate.now().minusMonths(1);

        NBTTagList logList = new NBTTagList();
        for (Map.Entry<LocalDate, Collection<GameProfile>> entry : log.asMap().entrySet()) {
            if (entry.getKey().isBefore(monthAgo))
                continue;
            NBTTagCompound dateEntry = new NBTTagCompound();
            NBTTagList players = new NBTTagList();
            for (GameProfile player : entry.getValue()) {
                NBTTagCompound playerTag = NBTPlugin.makeGameProfileTag(player);
                if (playerTag != null)
                    players.appendTag(playerTag);
            }
            dateEntry.setString("date", entry.getKey().toString());
            dateEntry.setTag("players", players);
            logList.appendTag(dateEntry);
        }
        nbt.setTag("entries", logList);
        return nbt;
    }

    public static Multimap<LocalDate, GameProfile> convertLogFromNBT(NBTTagCompound data) {
        Multimap<LocalDate, GameProfile> log = HashMultimap.create();

        LocalDate monthAgo = LocalDate.now().minusMonths(1);

        List<NBTTagCompound> logList = NBTPlugin.getNBTList(data, "entries", NBTTagCompound.class);
        for (NBTTagCompound dateEntry : logList) {
            try {
                LocalDate date = LocalDate.parse(dateEntry.getString("date"));
                if (date.isBefore(monthAgo))
                    continue;
                List<NBTTagCompound> playerList = NBTPlugin.getNBTList(dateEntry, "players", NBTTagCompound.class);
                Set<GameProfile> players = playerList.stream().map(NBTPlugin::readGameProfileTag).collect(Collectors.toSet());
                log.putAll(date, players);
            } catch (DateTimeParseException ignored) {
            }
        }
        return log;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("log", convertLogToNBT(log));
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        log.clear();
        log.putAll(convertLogFromNBT(data.getCompoundTag("log")));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeUUID(getOwner().getId() != null ? getOwner().getId() : new UUID(0, 0));
        data.writeUTF(getOwner().getName() != null ? getOwner().getName() : RailcraftConstantsAPI.RAILCRAFT_PLAYER);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        setOwner(new GameProfile(data.readUUID(), data.readUTF()));
    }
}
