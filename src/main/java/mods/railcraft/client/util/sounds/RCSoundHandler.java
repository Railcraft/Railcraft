/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.util.sounds;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.sounds.SoundHelper;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class RCSoundHandler {

    public static final RCSoundHandler INSTANCE = new RCSoundHandler();

    private RCSoundHandler() {
    }

    //TODO: test, catch PlaySoundAtEntityEvent?
    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event) {
        ISound soundEvent = event.getSound();
        ResourceLocation soundResource = soundEvent.getSoundLocation();
        if (SoundHelper.matchesSoundResource(soundResource, "null")) {
            event.setResultSound(null);
        } else if (SoundHelper.matchesSoundResource(soundResource, "override")) {
            World world = Railcraft.getProxy().getClientWorld();
            if (world != null) {
                float x = soundEvent.getXPosF();
                float y = soundEvent.getYPosF();
                float z = soundEvent.getZPosF();
                BlockPos pos = new BlockPos(x, y, z);
                String soundPath = soundEvent.getSoundLocation().getPath();
                SoundType blockSound = SoundRegistry.getBlockSound(world, pos);
                if (blockSound == null) {
                    if (soundPath.contains("place")) {
                        event.getManager().playDelayedSound(event.getSound(), 3); //Play sound later to adjust for the block not being there yet.
                    } else if (soundPath.contains("step")) {
                        blockSound = SoundRegistry.getBlockSound(world, pos.down());
                    }
                }
                if (blockSound != null) {
                    SoundEvent newSound = SoundHelper.matchSoundEvent(soundResource, blockSound);
//                    ObfuscationReflectionHelper.setPrivateValue(PositionedSound.class, (PositionedSound) soundEvent, newSound.getSoundName(), 3);
                } else {
                    event.setResultSound(null);
                }
            }
        }
    }
}
