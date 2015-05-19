/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by CovertJaguar on 5/13/2015.
 */
public class TokenSignalManager {
    public static final String DATA_TAG = "Railcraft:TokenSignals";
    private final TokenWorldSaveData data;

    private TokenSignalManager(TokenWorldSaveData data) {
        this.data = data;
    }

    public static TokenSignalManager forWorld(World world) {
        TokenWorldSaveData data = (TokenWorldSaveData) world.loadItemData(TokenWorldSaveData.class, DATA_TAG);
        if (data == null) {
            data = new TokenWorldSaveData(DATA_TAG);
            world.setItemData(DATA_TAG, data);
        }
        return new TokenSignalManager(data);
    }

    private static class TokenWorldSaveData extends WorldSavedData {
        public TokenWorldSaveData(String tag) {
            super(tag);
        }

        @Override
        public void readFromNBT(NBTTagCompound data) {

        }

        @Override
        public void writeToNBT(NBTTagCompound data) {

        }
    }

    public static class TokenSet {
        public final UUID id;
        public final Set<WorldCoordinate> exchanges = new HashSet<WorldCoordinate>();
        public final Set<UUID> trackedCarts = new HashSet<UUID>();
        private WorldCoordinate centroid;

        public TokenSet(UUID id) {
            this.id = id;
        }

        private void calculateCentroid() {
            int x = 0;
            int y = 0;
            int z = 0;
            for (WorldCoordinate coord : exchanges) {
                x += coord.x;
                y += coord.y;
                z += coord.z;
            }
            int size = exchanges.size();
            centroid = new WorldCoordinate(0, x / size, y / size, z / size);
        }
    }
}
