/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.util.sounds;

import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftSound extends SoundType {

    private static RailcraftSound instance;

    public static RailcraftSound instance() {
        if (instance == null) {
            instance = new RailcraftSound();
        }
        return instance;
    }

    private RailcraftSound() {
        super(1F, 1F,
                makeSound("break"),
                makeSound("step"),
                makeSound("place"),
                makeSound("hit"),
                makeSound("fall")
        );
    }

    private static SoundEvent makeSound(String type) {
        return new SoundEvent(new ResourceLocation(RailcraftConstants.SOUND_FOLDER + "block.railcraft." + type));
    }
}
