/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.worldspike;

import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleChunkLoading;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author CovertJaguar
 */
public enum WorldspikeVariant implements IEnumMachine<WorldspikeVariant> {
    ADMIN("admin", TileWorldspikeAdmin.class),
    PASSIVE("passive", TileWorldspikePassive.class) {
        @Override
        public Map<Ingredient, Float> getFuelList() {
            return RailcraftConfig.worldspikeFuelPassive;
        }
    },
    PERSONAL("personal", TileWorldspikePersonal.class) {
        @Override
        public Map<Ingredient, Float> getFuelList() {
            return RailcraftConfig.worldspikeFuelPersonal;
        }
    },
    STANDARD("standard", TileWorldspike.class) {
        @Override
        public Map<Ingredient, Float> getFuelList() {
            return RailcraftConfig.worldspikeFuelStandard;
        }
    };

    private static final List<WorldspikeVariant> creativeList = new ArrayList<>();
    public static final WorldspikeVariant[] VALUES = values();

    static {
        creativeList.add(ADMIN);
        creativeList.add(PASSIVE);
        creativeList.add(PERSONAL);
        creativeList.add(STANDARD);
    }

    private final Definition def;

    WorldspikeVariant(String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(tag, tile, ModuleChunkLoading.class);
    }

    public static WorldspikeVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<WorldspikeVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    public Map<Ingredient, Float> getFuelList() {
        return Collections.emptyMap();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 1);
    }

    @Override
    public String getTag() {
        return "tile.railcraft.worldspike_" + getBaseTag();
    }

    @Override
    public String getName() {
        return "worldspike_" + getBaseTag();
    }

    @Override
    public String getToolClass() {
        return HarvestPlugin.ToolClass.PICKAXE.getToolString(3);
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.WORLDSPIKE;
    }

}
