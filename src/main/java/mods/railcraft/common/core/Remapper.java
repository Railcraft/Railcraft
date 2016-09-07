/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Remapper {
    RENAMED {
        private final Map<String, IRailcraftObjectContainer<?>> names = new HashMap<>();

        {
            names.put("track_abandoned", RailcraftBlocks.TRACK_FLEX_ABANDONED);
            names.put("track_electric", RailcraftBlocks.TRACK_FLEX_ELECTRIC);
            names.put("track_high_speed", RailcraftBlocks.TRACK_FLEX_HIGH_SPEED);
            names.put("track_high_speed_electric", RailcraftBlocks.TRACK_FLEX_HS_ELECTRIC);
            names.put("track_reinforced", RailcraftBlocks.TRACK_FLEX_REINFORCED);
            names.put("track_strap_iron", RailcraftBlocks.TRACK_FLEX_STRAP_IRON);
        }

        @Override
        protected void attemptRemap(FMLMissingMappingsEvent.MissingMapping mapping) {
            IRailcraftObjectContainer<?> objectContainer = names.get(MiscTools.cleanTag(mapping.name));
            if (objectContainer != null)
                if (mapping.type == GameRegistry.Type.BLOCK && objectContainer instanceof IContainerBlock)
                    remap(mapping, ((IContainerBlock) objectContainer).block());
                else if (mapping.type == GameRegistry.Type.ITEM && objectContainer instanceof IContainerItem)
                    remap(mapping, ((IContainerItem) objectContainer).item());
        }
    },
    RENAMED_PARTS {
        @Override
        protected void attemptRemap(FMLMissingMappingsEvent.MissingMapping mapping) {
            if (mapping.type == GameRegistry.Type.ITEM)
                findItem(mapping.name.replace("part.", "")).ifPresent(item -> remap(mapping, item));
        }
    },
    REFORMATTED {
        @Override
        protected void attemptRemap(FMLMissingMappingsEvent.MissingMapping mapping) {
            if (mapping.type == GameRegistry.Type.BLOCK)
                findBlock(mapping.name).ifPresent(block -> remap(mapping, block));
            else if (mapping.type == GameRegistry.Type.ITEM) {
                findBlock(mapping.name).ifPresent(block -> remap(mapping, Item.getItemFromBlock(block)));
                if (mapping.getAction() == FMLMissingMappingsEvent.Action.DEFAULT)
                    findItem(mapping.name).ifPresent(item -> remap(mapping, item));
            }
        }

    };

    public static void handle(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            for (Remapper remapper : Remapper.values()) {
                try {
                    remapper.attemptRemap(mapping);
                    if (mapping.getAction() != FMLMissingMappingsEvent.Action.DEFAULT)
                        break;
                } catch (Exception ex) {
                    Game.logThrowable("Remapper Error", ex);
                }
            }
        }
    }

    protected Optional<Block> findBlock(String oldName) {
        String newName = MiscTools.cleanTag(oldName).replace(".", "_");
        Block block = GameRegistry.findBlock(Railcraft.MOD_ID, newName);
        if (block != null && block != Blocks.AIR)
            return Optional.of(block);
        return Optional.empty();
    }

    protected Optional<Item> findItem(String oldName) {
        String newName = MiscTools.cleanTag(oldName).replace(".", "_");
        Item item = GameRegistry.findItem(Railcraft.MOD_ID, newName);
        if (item != null)
            return Optional.of(item);
        return Optional.empty();
    }

    protected abstract void attemptRemap(FMLMissingMappingsEvent.MissingMapping mapping);

    protected final void remap(FMLMissingMappingsEvent.MissingMapping mapping, @Nullable IForgeRegistryEntry<?> object) {
        if (object != null) {
            if (object instanceof Block)
                mapping.remap((Block) object);
            else if (object instanceof Item)
                mapping.remap((Item) object);
            else
                throw new IllegalArgumentException("unknown object");
            Game.log(Level.WARN, "Remapping " + mapping.type + " named " + mapping.name + " to " + object.getRegistryName());
        }
    }
}
