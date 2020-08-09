/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Remapper {
    private static final Map<String, RailcraftBlocks> blockRemaps = new HashMap<>();
    private static final Map<String, RailcraftBlocks> itemRemaps = new HashMap<>();

    static {
        itemRemaps.put("tank_iron_gauge", RailcraftBlocks.GLASS);
        itemRemaps.put("tank_steel_gauge", RailcraftBlocks.GLASS);

        blockRemaps.put("brick_red_sandy", RailcraftBlocks.BRICK_BADLANDS);

        blockRemaps.put("manipulator", RailcraftBlocks.MANIPULATOR);

        blockRemaps.put("track_abandoned", RailcraftBlocks.TRACK_FLEX_ABANDONED);
        blockRemaps.put("track_electric", RailcraftBlocks.TRACK_FLEX_ELECTRIC);
        blockRemaps.put("track_high_speed", RailcraftBlocks.TRACK_FLEX_HIGH_SPEED);
        blockRemaps.put("track_high_speed_electric", RailcraftBlocks.TRACK_FLEX_HS_ELECTRIC);
        blockRemaps.put("track_reinforced", RailcraftBlocks.TRACK_FLEX_REINFORCED);
        blockRemaps.put("track_strap_iron", RailcraftBlocks.TRACK_FLEX_STRAP_IRON);

        blockRemaps.put("track.abandoned", RailcraftBlocks.TRACK_FLEX_ABANDONED);
        blockRemaps.put("track.electric", RailcraftBlocks.TRACK_FLEX_ELECTRIC);
        blockRemaps.put("track.high.speed", RailcraftBlocks.TRACK_FLEX_HIGH_SPEED);
        blockRemaps.put("track.high.speed.electric", RailcraftBlocks.TRACK_FLEX_HS_ELECTRIC);
        blockRemaps.put("track.reinforced", RailcraftBlocks.TRACK_FLEX_REINFORCED);
        blockRemaps.put("track.strap.iron", RailcraftBlocks.TRACK_FLEX_STRAP_IRON);
    }

    @SubscribeEvent
    public static void remapBlock(MissingMappings<Block> event) {
        for (Mapping<Block> mapping : event.getMappings()) {
            try {
                Optional.ofNullable(blockRemaps.get(mapping.key.getPath())).ifPresent(v -> {
                    mapping.remap(v.block());
                    Game.log().msg(Level.WARN, "Remapping block " + mapping.key + " to " + v.getRegistryName());
                });
            } catch (Exception ex) {
                Game.log().throwable("Remapper Error", ex);
            }
        }
    }

    @SubscribeEvent
    public static void remapItem(MissingMappings<Item> event) {
        for (Mapping<Item> mapping : event.getMappings()) {
            try {
                Optional.ofNullable(itemRemaps.get(mapping.key.getPath())).ifPresent(v -> {
                    mapping.remap(Objects.requireNonNull(v.item()));
                    Game.log().msg(Level.WARN, "Remapping item " + mapping.key + " to " + v.getRegistryName());
                });
                Optional.ofNullable(blockRemaps.get(mapping.key.getPath())).ifPresent(v -> {
                    mapping.remap(Objects.requireNonNull(v.item()));
                    Game.log().msg(Level.WARN, "Remapping item " + mapping.key + " to " + v.getRegistryName());
                });
            } catch (Exception ex) {
                Game.log().throwable("Remapper Error", ex);
            }
        }
    }
}
