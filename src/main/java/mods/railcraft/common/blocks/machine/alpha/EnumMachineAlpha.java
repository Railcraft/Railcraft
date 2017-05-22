/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.*;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumMachineAlpha implements IEnumMachine<EnumMachineAlpha> {

    //    ANCHOR_WORLD(ModuleChunkLoading.class, "anchor.world", TileAnchorWorld.class, 3, 1),
    TURBINE(ModuleCharge.class, "turbine", TileSteamTurbine.class, 3, 3),
    //    ANCHOR_PERSONAL(ModuleChunkLoading.class, "anchor.personal", TileAnchorPersonal.class, 3, 1),
    STEAM_OVEN(ModuleFactory.class, "steam.oven", TileSteamOven.class, 4, 2),
    //    ANCHOR_ADMIN(ModuleChunkLoading.class, "anchor.admin", TileAnchorAdmin.class, 3, 1),
    TRADE_STATION(ModuleAutomation.class, "trade.station", TileTradeStation.class, 3, 1),
    COKE_OVEN(ModuleFactory.class, "coke.oven", TileCokeOven.class, 3, 1),
    STEAM_TRAP_MANUAL(ModuleExtras.class, "steam.trap", TileSteamTrapManual.class, 3, 1),
    STEAM_TRAP_AUTO(ModuleExtras.class, "steam.trap.auto", TileSteamTrapAuto.class, 4, 1),
    BLAST_FURNACE(ModuleFactory.class, "blast.furnace", TileBlastFurnace.class, 3, 1),
    //    ANCHOR_PASSIVE(ModuleChunkLoading.class, "anchor.passive", TileAnchorPassive.class, 3, 1),
    TANK_WATER(ModuleTransport.class, "tank.water", TileTankWater.class, 2, 1),
    ROCK_CRUSHER(ModuleFactory.class, "rock.crusher", TileRockCrusher.class, 4, 3);
    public static final PropertyEnum<EnumMachineAlpha> VARIANT = PropertyEnum.create("variant", EnumMachineAlpha.class);
    public static final EnumMachineAlpha[] VALUES = values();
    private static final List<EnumMachineAlpha> creativeList = new ArrayList<EnumMachineAlpha>();

    static {
        String axe1 = HarvestPlugin.ToolClass.AXE.getToolString(1);
        TANK_WATER.toolClass = axe1;
//        FEED_STATION.toolClass = axe1;

        creativeList.add(COKE_OVEN);
        creativeList.add(BLAST_FURNACE);
        creativeList.add(STEAM_OVEN);
        creativeList.add(TANK_WATER);
        creativeList.add(ROCK_CRUSHER);
        creativeList.add(TRADE_STATION);
        creativeList.add(TURBINE);
        creativeList.add(STEAM_TRAP_MANUAL);
        creativeList.add(STEAM_TRAP_AUTO);
    }

    private final int textureWidth, textureHeight;
    private final Definition def;
    private ToolTip tip;
    private String toolClass = HarvestPlugin.ToolClass.PICKAXE.getToolString(2);

    EnumMachineAlpha(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, int textureWidth, int textureHeight) {
        this.def = new Definition(tag, tile, module);
        def.passesLight = false;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public static EnumMachineAlpha fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.alpha." + getBaseTag();
    }

    @Override
    public String getToolClass() {
        return toolClass;
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(textureWidth, textureHeight);
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.MACHINE_ALPHA;
    }

    /**
     * Block is enabled and defined.
     */
    @Override
    public boolean isAvailable() {
        return block() != null && isEnabled();
    }

    @Override
    public String getName() {
        return getBaseTag().replace(".", "_");
    }
}
