/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.core.IInterModMessageHandler;
import mods.railcraft.common.core.InterModMessageRegistry;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RailcraftModule(value = "railcraft:tracks|high_speed", description = "high speed tracks")
public class ModuleTracksHighSpeed extends RailcraftModulePayload {
    public static Config config;

    public ModuleTracksHighSpeed() {
        add(
                RailcraftBlocks.TRACK_FLEX_HIGH_SPEED,
                TrackKits.HIGH_SPEED_TRANSITION
        );
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                InterModMessageRegistry.getInstance().register("high-speed-explosion-excluded-entities", mess -> {
                    NBTTagCompound nbt = mess.getNBTValue();
                    if (nbt.hasKey("entities")) {
                        String entities = nbt.getString("entities");
                        Iterable<String> split = IInterModMessageHandler.SPLITTER.split(entities);
                        for (String entityName : split) {
                            config.ignoredEntities.add(entityName);
                        }
                    } else {
                        Game.log().msg(Level.WARN, "Mod %s attempted to exclude an entity from H.S. explosions but failed: %s", mess.getSender(), nbt);
                    }
                });
            }
        });
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleTracksHighSpeed.config = new Config(config);
    }

    public static class Config {
        public final float maxSpeed;
        public final Set<String> ignoredEntities = new HashSet<>();

        public Config(Configuration config) {
            maxSpeed = config.getFloat("maxSpeed", CAT_CONFIG, 1f, 0.6f, 1.2f,
                    "change to limit max speed on high speed rails, useful if your computer can't keep up with chunk loading\n" +
                            "iron tracks operate at 0.4 blocks per tick");

            String[] defaultEntities = {
                    "minecraft:bat",
                    "minecraft:blaze",
                    "minecraft:cave_spider",
                    "minecraft:chicken",
                    "minecraft:parrot",
                    "minecraft:rabbit",
                    "minecraft:spider",
                    "minecraft:vex",
            };
            String[] strings = config.getStringList("ignoredEntities", CAT_CONFIG, defaultEntities,
                    "add entity names to exclude them from explosions caused by high speed collisions");
            Collections.addAll(ignoredEntities, strings);
        }

        public boolean isIgnored(Entity entity) {
            return ignoredEntities.contains(EntityList.getEntityString(entity));
        }
    }
}
