/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.sounds;

import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SoundHelper {

    public static final String SOUND_LOCOMOTIVE_STEAM_WHISTLE = "railcraft:locomotive.steam.whistle";
    public static final String SOUND_LOCOMOTIVE_ELECTRIC_WHISTLE = "railcraft:locomotive.electric.whistle";
    public static final String SOUND_STEAM_BURST = "railcraft:machine.steamburst";
    public static final String SOUND_STEAM_HISS = "railcraft:machine.steamhiss";
    private static final Map<String, Integer> soundLimiterClient = new HashMap<String, Integer>();
    private static final Map<String, Integer> soundLimiterServer = new HashMap<String, Integer>();

    private static Map<String, Integer> getSoundLimiter() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return soundLimiterClient;
        return soundLimiterServer;
    }

    private static boolean canPlaySound(String name) {
        if (!RailcraftConfig.playSounds())
            return false;
        Integer limit = getSoundLimiter().get(name);
        return limit == null || limit <= 10;
    }

    private static void incrementLimiter(String name) {
        Integer limit = getSoundLimiter().get(name);
        if (limit == null)
            limit = 0;
        limit++;
        getSoundLimiter().put(name, limit);
    }

    public static void decrementLimiters() {
        for (Map.Entry<String, Integer> entry : getSoundLimiter().entrySet()) {
            Integer limit = entry.getValue();
            if (limit > 0) {
                limit--;
                entry.setValue(limit);
            }
        }
    }

    public static void playSound(World world, BlockPos pos, String name, float volume, float pitch) {
        if (canPlaySound(name)) {
            incrementLimiter(name);
            world.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, name, volume, pitch);
        }
    }

    public static void playSoundClient(World world, BlockPos pos, String name, float volume, float pitch) {
        if (canPlaySound(name)) {
            incrementLimiter(name);
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, name, volume, pitch, false);
        }
    }

    public static void playSoundAtEntity(Entity entity, String name, float volume, float pitch) {
        if (canPlaySound(name)) {
            incrementLimiter(name);
            entity.worldObj.playSoundAtEntity(entity, name, volume, pitch);
        }
    }

    public static void playBlockSound(World world, BlockPos pos, String soundName, float volume, float pitch, Block block, int meta) {
        if (world != null && soundName != null) {
            if (soundName.contains("railcraft")) {
                SoundType sound = SoundRegistry.getSound(block, meta);
                if (sound != null) {
                    String newName = soundName.contains("dig") ? sound.getBreakSound() : soundName.contains("step") ? sound.getStepSound() : sound.getPlaceSound();
                    world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), newName, volume, pitch * sound.getFrequency());
                }
            }
            world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), soundName, volume, pitch);
        }
    }

    public static void playFX(World world, EntityPlayer player, int id, BlockPos pos, int data) {
        if (RailcraftConfig.playSounds())
            world.playAuxSFXAtEntity(player, id, pos, data);
    }

}
