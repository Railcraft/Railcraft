/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.sounds;

import mods.railcraft.client.util.sounds.JukeboxSound;
import mods.railcraft.client.util.sounds.MinecartSound;
import mods.railcraft.common.carts.EntityCartJukebox;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class SoundHelper {

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
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, sound, category, volume, pitch);
        }
    }

    public static void playPlaceSoundForBlock(World world, BlockPos pos) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        Block block = state.getBlock();
        SoundType soundType = block.getSoundType(state, world, pos, null);
        playSound(world, null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
    }

    //TODO: test
    public static void playBlockSound(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, IBlockState state) {
        if (world != null && sound != null) {
            ResourceLocation soundPath = sound.soundName; //ReflectionHelper.getPrivateValue(SoundEvent.class, sound, 1);
            if (matchesSoundResource(soundPath, "override")) {
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
        return resource.getNamespace().startsWith("railcraft") && resource.getPath().startsWith(type);
    }

    public static SoundEvent matchSoundEvent(ResourceLocation resource, SoundType soundType) {
        String soundPath = resource.getPath();
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

    public enum MovingSoundType {
        CART {
            @Override
            @SideOnly(Side.CLIENT)
            public void handle(SoundEvent sound, SoundCategory category, EntityMinecart cart, NBTTagCompound tag) {
                Minecraft.getMinecraft().getSoundHandler().playSound(new MinecartSound(sound, category, cart));
            }
        },
        RECORD {
            @Override
            @SideOnly(Side.CLIENT)
            public void handle(SoundEvent sound, SoundCategory category, EntityMinecart cart, NBTTagCompound tag) {
                if (!(cart instanceof EntityCartJukebox))
                    return;
                EntityCartJukebox jukebox = (EntityCartJukebox) cart;
                jukebox.music = new JukeboxSound(sound, category, (EntityCartJukebox) cart);
                Minecraft.getMinecraft().getSoundHandler().playSound(jukebox.music);
                String recordName = tag.getString(EntityCartJukebox.RECORD_DISPLAY_NAME);
                if (!isNullOrEmpty(recordName))
                    Minecraft.getMinecraft().ingameGUI.setRecordPlayingMessage(LocalizationPlugin.translate(recordName));
            }
        },;

        @SideOnly(Side.CLIENT)
        public abstract void handle(SoundEvent sound, SoundCategory category, EntityMinecart cart, NBTTagCompound extraData);
    }
}
