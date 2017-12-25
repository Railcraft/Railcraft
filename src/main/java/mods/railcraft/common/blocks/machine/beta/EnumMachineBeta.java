/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.modules.ModuleSteam;
import mods.railcraft.common.modules.ModuleTransport;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar
 */
public enum EnumMachineBeta implements IEnumMachine<EnumMachineBeta> {

//    TANK_IRON_WALL(ModuleTransport.class, "tank.iron.wall", TileTankIronWall.class, true, 2, 1),
//    TANK_IRON_GAUGE(ModuleTransport.class, "tank.iron.gauge", TileTankIronGauge.class, true, 1, 5),
//    TANK_IRON_VALVE(ModuleTransport.class, "tank.iron.valve", TileTankIronValve.class, true, 4, 1),
//    BOILER_TANK_LOW_PRESSURE(ModuleSteam.class, "boiler.tank.pressure.low", TileBoilerTankLow.class, true, 2, 1),
//    BOILER_TANK_HIGH_PRESSURE(ModuleSteam.class, "boiler.tank.pressure.high", TileBoilerTankHigh.class, true, 2, 1),
//    BOILER_FIREBOX_SOLID(ModuleSteam.class, "boiler.firebox.solid", TileBoilerFireboxSolid.class, false, 3, 1),
//    BOILER_FIREBOX_FLUID(ModuleSteam.class, "boiler.firebox.liquid", TileBoilerFireboxFluid.class, false, 3, 1),
    ENGINE_STEAM_HOBBY(ModuleSteam.class, "engine.steam.hobby", TileEngineSteamHobby.class, true, 1, 1),
    ENGINE_STEAM_LOW(ModuleSteam.class, "engine.steam.low", TileEngineSteamLow.class, true, 1, 1),
    ENGINE_STEAM_HIGH(ModuleSteam.class, "engine.steam.high", TileEngineSteamHigh.class, true, 1, 1),
    VOID_CHEST(ModuleTransport.class, "chest.void", TileChestVoid.class, true, 1, 1),
    METALS_CHEST(ModuleTransport.class, "chest.metals", TileChestMetals.class, true, 1, 1);
//    TANK_STEEL_WALL(ModuleTransport.class, "tank.steel.wall", TileTankSteelWall.class, true, 2, 1),
//    TANK_STEEL_GAUGE(ModuleTransport.class, "tank.steel.gauge", TileTankSteelGauge.class, true, 1, 5),
//    TANK_STEEL_VALVE(ModuleTransport.class, "tank.steel.valve", TileTankSteelValve.class, true, 4, 1);
    public static final PropertyEnum<EnumMachineBeta> VARIANT = PropertyEnum.create("variant", EnumMachineBeta.class);
    private static final List<EnumMachineBeta> creativeList = new ArrayList<EnumMachineBeta>();
    private static final EnumMachineBeta[] VALUES = values();

    static {
//        creativeList.add(TANK_IRON_WALL);
//        creativeList.add(TANK_IRON_GAUGE);
//        creativeList.add(TANK_IRON_VALVE);
//        creativeList.add(TANK_STEEL_WALL);
//        creativeList.add(TANK_STEEL_GAUGE);
//        creativeList.add(TANK_STEEL_VALVE);
//        creativeList.add(BOILER_FIREBOX_SOLID);
//        creativeList.add(BOILER_FIREBOX_FLUID);
//        creativeList.add(BOILER_TANK_LOW_PRESSURE);
//        creativeList.add(BOILER_TANK_HIGH_PRESSURE);
        creativeList.add(ENGINE_STEAM_HOBBY);
        creativeList.add(ENGINE_STEAM_LOW);
        creativeList.add(ENGINE_STEAM_HIGH);
        creativeList.add(VOID_CHEST);
        creativeList.add(METALS_CHEST);
    }

    private final Class<? extends IRailcraftModule> module;
    private final String tag;
    private final Class<? extends TileMachineBase> tile;
    private final int textureWidth, textureHeight;
    private final boolean passesLight;
    private ToolTip tip;
    private String toolClass = "pickaxe:2";

    EnumMachineBeta(Class<? extends IRailcraftModule> module, String tag, Class<? extends TileMachineBase> tile, boolean passesLight, int textureWidth, int textureHeight) {
        this.module = module;
        this.tile = tile;
        this.tag = tag;
        this.passesLight = passesLight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public Definition getDef() {
        return null;
    }

    public static EnumMachineBeta fromId(int id) {
        if (id < 0 || id >= VALUES.length)
            id = 0;
        return VALUES[id];
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    @Override
    public String getTag() {
        return "tile.railcraft.machine.beta." + getBaseTag();
    }

    @Override
    public String getToolClass() {
        return toolClass;
    }

    @Override
    public boolean passesLight() {
        return passesLight;
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(textureWidth, textureHeight);
    }

    @Override
    public Class<? extends TileMachineBase> getTileClass() {
        return tile;
    }

    @Override
    public TileMachineBase getTileEntity() {
        try {
            return tile.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.MACHINE_BETA;
    }

    @Override
    public boolean isAvailable() {
        return block() != null && isEnabled();
    }

    @Override
    public String getName() {
        return tag.replace(".", "_");
    }
}
