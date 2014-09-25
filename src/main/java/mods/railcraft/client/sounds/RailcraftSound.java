/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.sounds;

import net.minecraft.block.Block.SoundType;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftSound extends SoundType {

    private static RailcraftSound instance;

    public static RailcraftSound getInstance() {
        if (instance == null) {
            instance = new RailcraftSound();
        }
        return instance;
    }

    private RailcraftSound() {
        super("railcraft", 1, 1);
    }

    @Override
    public String func_150496_b() {
        return "place." + this.soundName;
    }
}
