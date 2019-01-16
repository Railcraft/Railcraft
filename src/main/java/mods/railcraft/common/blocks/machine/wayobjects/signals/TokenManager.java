/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.common.plugins.forge.NBTPlugin;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TokenManager {
    public static final String DATA_TAG = "railcraft.tokens";

    public static TokenWorldManager getManager(World world) {
        MapStorage storage = world.getPerWorldStorage();
        TokenWorldManager data = (TokenWorldManager) storage.getOrLoadData(TokenWorldManager.class, DATA_TAG);
        if (data == null) {
            data = new TokenWorldManager(DATA_TAG);
            storage.setData(DATA_TAG, data);
        }
        return data;
    }

    public static TokenManager getEventListener() {
        return new TokenManager();
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
            getManager(event.world).tick(event.world);
    }

    public static class TokenWorldManager extends WorldSavedData {
        private final Map<UUID, TokenRing> tokenRings = new HashMap<>();
        private int clock;

        public TokenWorldManager(String tag) {
            super(tag);
        }

        @Override
        public void readFromNBT(NBTTagCompound data) {
            List<NBTTagCompound> tokenRingList = NBTPlugin.getNBTList(data, "tokenRings", NBTTagCompound.class);
            for (NBTTagCompound entry : tokenRingList) {
                UUID uuid = NBTPlugin.readUUID(entry, "uuid");
                if (uuid != null) {
                    TokenRing tokenRing = new TokenRing(this, uuid);
                    tokenRings.put(uuid, tokenRing);
                    List<NBTTagCompound> signalList = NBTPlugin.getNBTList(entry, "signals", NBTTagCompound.class);
                    Set<BlockPos> signalPositions = signalList.stream()
                            .map(signal -> NBTPlugin.readBlockPos(signal, "pos"))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    tokenRing.loadSignals(signalPositions);
                    List<NBTTagCompound> cartList = NBTPlugin.getNBTList(entry, "carts", NBTTagCompound.class);
                    Set<UUID> carts = cartList.stream()
                            .map(signal -> NBTPlugin.readUUID(signal, "cart"))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    tokenRing.loadCarts(carts);
                }
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound data) {
            NBTTagList tokenRingList = new NBTTagList();
            for (TokenRing tokenRing : tokenRings.values()) {
                NBTTagCompound tokenData = new NBTTagCompound();
                NBTPlugin.writeUUID(tokenData, "uuid", tokenRing.getUUID());
                NBTTagList signalList = new NBTTagList();
                for (BlockPos pos : tokenRing.getSignals()) {
                    NBTTagCompound signal = new NBTTagCompound();
                    NBTPlugin.writeBlockPos(signal, "pos", pos);
                    signalList.appendTag(signal);
                }
                tokenData.setTag("signals", signalList);
                NBTTagList cartList = new NBTTagList();
                for (UUID uuid : tokenRing.getTrackedCarts()) {
                    NBTTagCompound cart = new NBTTagCompound();
                    NBTPlugin.writeUUID(cart, "cart", uuid);
                    cartList.appendTag(cart);
                }
                tokenData.setTag("carts", cartList);
                tokenRingList.appendTag(tokenData);
            }
            data.setTag("tokenRings", tokenRingList);
            return data;
        }

        public void tick(World world) {
            clock++;
            if (clock % 32 == 0) {
                if (tokenRings.entrySet().removeIf(e -> e.getValue().isOrphaned(world)))
                    markDirty();

                tokenRings.values().forEach(t -> t.tick(world));
            }
        }

        public TokenRing getTokenRing(UUID uuid, BlockPos origin) {
            return tokenRings.computeIfAbsent(uuid, k -> new TokenRing(this, uuid, origin));
        }

        public Collection<TokenRing> getTokenRings() {
            return tokenRings.values();
        }
    }
}
