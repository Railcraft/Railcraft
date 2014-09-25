/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.sounds;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.railcraft.common.core.Railcraft;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import mods.railcraft.common.util.sounds.IBlockSoundProvider;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RCSoundHandler {

    public final static RCSoundHandler INSTANCE = new RCSoundHandler();

    private RCSoundHandler() {
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent17 event) {
        String soundName = event.name;
        if (soundName != null && event.sound instanceof PositionedSoundRecord && soundName.contains("railcraft")) {
            World world = Railcraft.getProxy().getClientWorld();
            if (world != null) {
                float x = event.sound.getXPosF();
                float y = event.sound.getYPosF();
                float z = event.sound.getZPosF();
                int ix = MathHelper.floor_float(x);
                int iy = MathHelper.floor_float(y);
                int iz = MathHelper.floor_float(z);
                SoundType sound = getBlockSound(world, ix, iy, iz);
                if (sound != null) {
                    String newName = sound.getStepResourcePath();
                    if (soundName.contains("dig"))
                        newName = sound.getBreakSound();
                    else if (soundName.contains("place"))
                        newName = sound.func_150496_b();
                    event.result = new PositionedSoundRecord(new ResourceLocation(newName), event.sound.getVolume(), event.sound.getPitch() * sound.getPitch(), x, y, z);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
        String soundName = event.name;
        Entity entity = event.entity;
        if (soundName != null && soundName.equals("step.railcraft")) {
            World world = entity.worldObj;
            if (world != null) {
                int ix = MathHelper.floor_double(entity.posX);
                int iy = MathHelper.floor_double(entity.posY - 0.2 - (double) entity.yOffset);
                int iz = MathHelper.floor_double(entity.posZ);
                SoundType sound = getBlockSound(world, ix, iy, iz);
                if (sound != null) {
                    world.playSoundAtEntity(entity, sound.getStepResourcePath(), event.volume, event.pitch * sound.getPitch());
                    event.setCanceled(true);
                }
            }
        }
    }

    private SoundType getBlockSound(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof IBlockSoundProvider)
            return ((IBlockSoundProvider) block).getSound(world, x, y, z);
        else {
            int meta = world.getBlockMetadata(x, y, z);
            return SoundRegistry.getSound(block, meta);
        }
    }

}
