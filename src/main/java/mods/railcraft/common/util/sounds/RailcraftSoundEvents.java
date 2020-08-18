/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        soundEvent.setRegistryName(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, path));
    }

    public SoundEvent getSoundEvent() {
        return soundEvent;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (RailcraftSoundEvents rse : RailcraftSoundEvents.values()) {
            event.getRegistry().registerAll(rse.soundEvent);
        }
    }
}
