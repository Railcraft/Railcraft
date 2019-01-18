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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * Created by CovertJaguar on 5/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftSoundEvents {
    ENTITY_LOCOMOTIVE_STEAM_WHISTLE("locomotive.steam.whistle"),
    ENTITY_LOCOMOTIVE_ELECTRIC_WHISTLE("locomotive.electric.whistle"),
    MECHANICAL_STEAM_BURST("machine.steamburst"),
    MECHANICAL_STEAM_HISS("machine.steamhiss"),
    MECHANICAL_ZAP("machine.zap");
    private final SoundEvent soundEvent;

    RailcraftSoundEvents(String path) {
        this.soundEvent = new SoundEvent(new ResourceLocation(RailcraftConstants.SOUND_FOLDER + path));
    }

    public SoundEvent getSoundEvent() {
        return soundEvent;
    }
}
