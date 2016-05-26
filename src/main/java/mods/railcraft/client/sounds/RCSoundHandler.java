/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.sounds;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RCSoundHandler {

    public static final RCSoundHandler INSTANCE = new RCSoundHandler();

    private RCSoundHandler() {
    }

    //TODO: test, catch PlaySoundAtEntityEvent?
    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event) {
        ISound soundEvent = event.getSound();
        if (soundEvent != null && soundEvent.getSoundLocation().getResourcePath().contains("railcraft")) {
            World world = Railcraft.getProxy().getClientWorld();
            if (world != null) {
                float x = soundEvent.getXPosF();
                float y = soundEvent.getYPosF();
                float z = soundEvent.getZPosF();
                BlockPos pos = new BlockPos(x, y, z);
                String soundPath = soundEvent.getSoundLocation().getResourcePath();
                SoundType blockSound = SoundRegistry.getBlockSound(world, pos);
                if (blockSound == null) {
                    if (soundPath.contains("place")) {
                        event.getManager().playDelayedSound(event.getSound(), 3); //Play sound later to adjust for the block not being there yet.
                    } else if (soundPath.contains("step")) {
                        blockSound = SoundRegistry.getBlockSound(world, pos.down());
                    }
                }
                if (blockSound != null) {
                    SoundEvent newSound = SoundRegistry.matchSoundType(soundPath, blockSound);
                    event.setResultSound(new PositionedSoundRecord(newSound, soundEvent.getCategory(), soundEvent.getVolume(), soundEvent.getPitch() * blockSound.getPitch(), x, y, z));
                }
            }
        }
    }

}
