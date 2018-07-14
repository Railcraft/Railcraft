/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO totally broke
public enum Remapper {
    RENAMED {
        private final Map<String, IRailcraftObjectContainer<?>> names = new HashMap<>();

        {
            names.put("manipulator", RailcraftBlocks.MANIPULATOR);

            names.put("track_abandoned", RailcraftBlocks.TRACK_FLEX_ABANDONED);
            names.put("track_electric", RailcraftBlocks.TRACK_FLEX_ELECTRIC);
            names.put("track_high_speed", RailcraftBlocks.TRACK_FLEX_HIGH_SPEED);
            names.put("track_high_speed_electric", RailcraftBlocks.TRACK_FLEX_HS_ELECTRIC);
            names.put("track_reinforced", RailcraftBlocks.TRACK_FLEX_REINFORCED);
            names.put("track_strap_iron", RailcraftBlocks.TRACK_FLEX_STRAP_IRON);

            names.put("track.abandoned", RailcraftBlocks.TRACK_FLEX_ABANDONED);
            names.put("track.electric", RailcraftBlocks.TRACK_FLEX_ELECTRIC);
            names.put("track.high.speed", RailcraftBlocks.TRACK_FLEX_HIGH_SPEED);
            names.put("track.high.speed.electric", RailcraftBlocks.TRACK_FLEX_HS_ELECTRIC);
            names.put("track.reinforced", RailcraftBlocks.TRACK_FLEX_REINFORCED);
            names.put("track.strap.iron", RailcraftBlocks.TRACK_FLEX_STRAP_IRON);
        }

        @Override
        protected void attemptRemap(FMLModIdMappingEvent.ModRemapping mapping) {
            IRailcraftObjectContainer<?> objectContainer = names.get(MiscTools.cleanTag(mapping.key.toString()));
            if (objectContainer != null)
                if (mapping.registry.equals(GameData.BLOCKS) && objectContainer instanceof IContainerBlock)
                    remap(mapping, ((IContainerBlock) objectContainer).block());
                else if (mapping.registry.equals(GameData.ITEMS) && objectContainer instanceof IContainerItem)
                    remap(mapping, ((IContainerItem) objectContainer).item());
        }
    },
    RENAMED_PARTS {
        @Override
        protected void attemptRemap(FMLModIdMappingEvent.ModRemapping mapping) {
            if (mapping.registry.equals(GameData.ITEMS))
                findItem(mapping.key.toString().replace("part.", "")).ifPresent(item -> remap(mapping, item));
        }
    },
    RENAMED_CARTS {
        @Override
        protected void attemptRemap(FMLModIdMappingEvent.ModRemapping mapping) {
            if (mapping.registry.equals(GameData.ITEMS))
                findItem(mapping.key.toString().replace("entity_", "")).ifPresent(item -> remap(mapping, item));
        }
    },
    REFORMATTED {
        @Override
        protected void attemptRemap(FMLModIdMappingEvent.ModRemapping mapping) {
            if (mapping.registry.equals(GameData.BLOCKS))
                findBlock(mapping.key.toString()).ifPresent(block -> remap(mapping, block));
            else if (mapping.registry.equals(GameData.ITEMS)) {
                findBlock(mapping.key.toString()).ifPresent(block -> remap(mapping, Item.getItemFromBlock(block)));
//                if (mapping.getAction() == FMLMissingMappingsEvent.Action.DEFAULT)
//                    findItem(mapping.key.toString()).ifPresent(item -> remap(mapping, item));
            }
        }

    };

    public static void handle(FMLModIdMappingEvent event) {
        //TODO this already breaks.
        for (FMLModIdMappingEvent.ModRemapping mapping : new FMLModIdMappingEvent.ModRemapping[0]) {
            for (Remapper remapper : Remapper.values()) {
                try {
                    remapper.attemptRemap(mapping);
//                    if (mapping.getAction() != FMLMissingMappingsEvent.Action.DEFAULT)
//                        break;
                } catch (Exception ex) {
                    Game.logThrowable("Remapper Error", ex);
                }
            }
        }
    }

    protected Optional<Block> findBlock(String oldName) {
        String newName = MiscTools.cleanTag(oldName).replace(".", "_");
        Block block = Block.REGISTRY.getObject(RailcraftConstantsAPI.locationOf(newName));
        if (block != null && block != Blocks.AIR)
            return Optional.of(block);
        return Optional.empty();
    }

    protected Optional<Item> findItem(String oldName) {
        String newName = MiscTools.cleanTag(oldName).replace(".", "_");
        Item item = Item.REGISTRY.getObject(RailcraftConstantsAPI.locationOf(newName));
        if (item != null)
            return Optional.of(item);
        return Optional.empty();
    }

    protected abstract void attemptRemap(FMLModIdMappingEvent.ModRemapping mapping);

    protected final void remap(FMLModIdMappingEvent.ModRemapping mapping, @Nullable IForgeRegistryEntry<?> object) {
        if (object != null) {
//            if (object instanceof Block)
//                mapping.remap((Block) object);
//            else if (object instanceof Item)
//                mapping.remap((Item) object);
//            else
//                throw new IllegalArgumentException("unknown object");
            Game.log(Level.WARN, "Remapping " + mapping.registry + " named " + mapping.key + " to " + object.getRegistryName());
        }
    }
}
