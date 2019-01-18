/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.sounds;

import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * Created by CovertJaguar on 6/10/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftSoundTypes {
    public static SoundType OVERRIDE = new SimpleSoundType("override");
    public static SoundType NULL = new SimpleSoundType("null");

    public static class SimpleSoundType extends SoundType {

        private SimpleSoundType(String prefix) {
            super(1F, 1F,
                    makeSound(prefix, "break"),
                    makeSound(prefix, "step"),
                    makeSound(prefix, "place"),
                    makeSound(prefix, "hit"),
                    makeSound(prefix, "fall")
            );
        }

        private static SoundEvent makeSound(String prefix, String type) {
            return new SoundEvent(new ResourceLocation(RailcraftConstants.SOUND_FOLDER + prefix + "." + type));
        }
    }
}
