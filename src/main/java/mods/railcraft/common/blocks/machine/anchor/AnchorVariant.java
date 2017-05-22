/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.anchor;

import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleChunkLoading;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum AnchorVariant implements IEnumMachine<AnchorVariant> {
    ADMIN("admin", TileAnchorAdmin.class),
    PASSIVE("passive", TileAnchorPassive.class) {
        @Override
        public ItemMap<Float> getFuelList() {
            return RailcraftConfig.anchorFuelPassive;
        }
    },
    PERSONAL("personal", TileAnchorPersonal.class) {
        @Override
        public ItemMap<Float> getFuelList() {
            return RailcraftConfig.anchorFuelPersonal;
        }
    },
    WORLD("world", TileAnchorWorld.class) {
        @Override
        public ItemMap<Float> getFuelList() {
            return RailcraftConfig.anchorFuelWorld;
        }
    };

    private static final List<AnchorVariant> creativeList = new ArrayList<AnchorVariant>();
    public static final AnchorVariant[] VALUES = values();

    static {
        creativeList.add(ADMIN);
        creativeList.add(PASSIVE);
        creativeList.add(PERSONAL);
        creativeList.add(WORLD);
    }

    private final Definition def;

    AnchorVariant(String tag, Class<? extends TileMachineBase> tile) {
        this.def = new Definition(tag, tile, ModuleChunkLoading.class);
    }

    public static AnchorVariant fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    public static List<AnchorVariant> getCreativeList() {
        return creativeList;
    }

    @Override
    public Definition getDef() {
        return def;
    }

    public ItemMap<Float> getFuelList() {
        return ItemMap.emptyMap();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 1);
    }

    @Override
    public String getTag() {
        return "tile.railcraft.anchor_" + getBaseTag();
    }

    @Override
    public String getName() {
        return "anchor_" + getBaseTag();
    }

    @Override
    public String getToolClass() {
        return HarvestPlugin.ToolClass.PICKAXE.getToolString(3);
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.ANCHOR;
    }


}
