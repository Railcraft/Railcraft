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
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RCSoundHandler {

    public final static RCSoundHandler INSTANCE = new RCSoundHandler();

    private RCSoundHandler() {
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent event) {
        String soundName = event.name;
        if (soundName != null && event.sound instanceof PositionedSoundRecord && soundName.contains("railcraft")) {
            World world = Railcraft.getProxy().getClientWorld();
            if (world != null) {
                float x = event.sound.getXPosF();
                float y = event.sound.getYPosF();
                float z = event.sound.getZPosF();
                BlockPos pos = new BlockPos(x, y, z);
                SoundType sound = getBlockSound(world, pos);
                if (sound == null) {
                    if (soundName.contains("place")) {
                        event.manager.playDelayedSound(event.sound, 3); //Play sound later to adjust for the block not being there yet.
                    } else if (soundName.contains("step")) {
                        sound = getBlockSound(world, pos.down());
	                }
                }
                if (sound != null) {
                    String newName = sound.getStepSound();
                    if (soundName.contains("dig"))
                        newName = sound.getBreakSound();
                    else if (soundName.contains("place"))
                        newName = sound.getPlaceSound();
                    event.result = new PositionedSoundRecord(new ResourceLocation(newName), event.sound.getVolume(), event.sound.getPitch() * sound.getFrequency(), x, y, z);
                }
            }
        }
    }

    private SoundType getBlockSound(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IBlockSoundProvider)
            return ((IBlockSoundProvider) block).getSound(world, pos);
        else {
            int meta = block.getMetaFromState(state);
            return SoundRegistry.getSound(block, meta);
        }
    }

}
