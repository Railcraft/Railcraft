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
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SoundHelper {

    private static final Map<SoundEvent, Integer> soundLimiterClient = new HashMap<>();
    private static final Map<SoundEvent, Integer> soundLimiterServer = new HashMap<>();

    private static Map<SoundEvent, Integer> getSoundLimiter() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return soundLimiterClient;
        return soundLimiterServer;
    }

    private static boolean canPlaySound(SoundEvent name) {
        if (!RailcraftConfig.playSounds())
            return false;
        Integer limit = getSoundLimiter().get(name);
        return limit == null || limit <= 10;
    }

    private static void incrementLimiter(SoundEvent name) {
        Integer limit = getSoundLimiter().get(name);
        if (limit == null)
            limit = 0;
        limit++;
        getSoundLimiter().put(name, limit);
    }

    public static void decrementLimiters() {
        for (Map.Entry<SoundEvent, Integer> entry : getSoundLimiter().entrySet()) {
            Integer limit = entry.getValue();
            if (limit > 0) {
                limit--;
                entry.setValue(limit);
            }
        }
    }

    public static void playSound(World world, @Nullable EntityPlayer player, BlockPos pos, RailcraftSoundEvents sound, SoundCategory category, float volume, float pitch) {
        playSound(world, player, pos, sound.getSoundEvent(), category, volume, pitch);
    }

    public static void playSound(World world, @Nullable EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (canPlaySound(sound)) {
            incrementLimiter(sound);
            world.playSound(player, pos, sound, category, volume, pitch);
        }
    }

    public static void playSoundClient(World world, BlockPos pos, RailcraftSoundEvents sound, SoundCategory category, float volume, float pitch) {
        playSoundClient(world, pos, sound.getSoundEvent(), category, volume, pitch);
    }

    public static void playSoundClient(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (canPlaySound(sound)) {
            incrementLimiter(sound);
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, category, volume, pitch, false);
        }
    }

    public static void playSoundForEntity(Entity entity, SoundEvent sound, float volume, float pitch) {
        if (!entity.isSilent())
            playSoundAtEntity(entity, sound, entity.getSoundCategory(), volume, pitch);
    }

    public static void playSoundAtEntity(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (canPlaySound(sound)) {
            incrementLimiter(sound);
            entity.worldObj.playSound(null, entity.posX, entity.posY, entity.posZ, sound, category, volume, pitch);
        }
    }

    //TODO: test
    public static void playBlockSound(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, IBlockState state) {
        if (world != null && sound != null) {
            ResourceLocation soundPath = sound.getSoundName();
            if (matchesSoundResource(sound.getSoundName(), "override")) {
                SoundType blockSound = SoundRegistry.getBlockSound(state, world, pos);
                if (blockSound != null) {
                    SoundEvent newSound = matchSoundEvent(soundPath, blockSound);
                    playSound(world, null, pos, newSound, category, volume, pitch * blockSound.getPitch());
                }
            }
            playSound(world, null, pos, sound, category, volume, pitch);
        }
    }

    public static void playFX(World world, @Nullable EntityPlayer player, int id, BlockPos pos, int data) {
        if (RailcraftConfig.playSounds())
            world.playEvent(player, id, pos, data);
    }

    public static boolean matchesSoundResource(ResourceLocation resource, String type) {
        return resource.getResourceDomain().startsWith("railcraft") && resource.getResourcePath().startsWith(type);
    }

    public static SoundEvent matchSoundEvent(ResourceLocation resource, SoundType soundType) {
        String soundPath = resource.getResourcePath();
        String typeString = soundPath.substring(soundPath.lastIndexOf(".") + 1);
        switch (typeString) {
            case "break":
                return soundType.getBreakSound();
            case "fall":
                return soundType.getFallSound();
            case "hit":
                return soundType.getHitSound();
            case "place":
                return soundType.getPlaceSound();
            case "step":
                return soundType.getStepSound();
        }
        return soundType.getStepSound();
    }
}
