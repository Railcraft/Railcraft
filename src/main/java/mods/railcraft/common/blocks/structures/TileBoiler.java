/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileFurnace;
import mods.railcraft.common.blocks.interfaces.ITileTank;
import mods.railcraft.common.blocks.logic.BoilerLogic;
import mods.railcraft.common.blocks.logic.FluidLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.steam.SteamConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoiler extends TileFurnace implements ITemperature, ITileTank {

    public static final int TANK_WATER = 0;
    public static final int TANK_STEAM = 1;
    public static final int TRANSFER_RATE = FluidTools.BUCKET_VOLUME;
    public static final int TICKS_LOW = 16;
    public static final int TICKS_HIGH = 8;
    public static final int STEAM_LOW = 16;
    public static final int STEAM_HIGH = 32;
    public static final int WATER_CAPACITY = 4;
    public static final float HEAT_LOW = SteamConstants.MAX_HEAT_LOW;
    public static final float HEAT_HIGH = SteamConstants.MAX_HEAT_HIGH;
    protected static final List<StructurePattern> patterns = new ArrayList<>();
    private static final Set<IBlockState> boilerBlocks = new HashSet<>();
    private static final Set<IBlockState> fireboxBlocks = new HashSet<>();

    static {
        fireboxBlocks.add(RailcraftBlocks.BOILER_FIREBOX_SOLID.getDefaultState());
        fireboxBlocks.add(RailcraftBlocks.BOILER_FIREBOX_FLUID.getDefaultState());

        boilerBlocks.addAll(fireboxBlocks);
        boilerBlocks.add(RailcraftBlocks.BOILER_TANK_PRESSURE_LOW.getDefaultState());
        boilerBlocks.add(RailcraftBlocks.BOILER_TANK_PRESSURE_HIGH.getDefaultState());

        patterns.add(buildMap(3, 4, 2, 'H', TICKS_HIGH, HEAT_HIGH, STEAM_HIGH));
        patterns.add(buildMap(3, 3, 2, 'H', TICKS_HIGH, HEAT_HIGH, STEAM_HIGH));
        patterns.add(buildMap(3, 2, 2, 'H', TICKS_HIGH, HEAT_HIGH, STEAM_HIGH));

        patterns.add(buildMap(2, 3, 1, 'H', TICKS_HIGH, HEAT_HIGH, STEAM_HIGH));
        patterns.add(buildMap(2, 2, 1, 'H', TICKS_HIGH, HEAT_HIGH, STEAM_HIGH));

        patterns.add(buildMap(1, 1, 1, 'H', TICKS_HIGH, HEAT_HIGH, STEAM_HIGH));

        patterns.add(buildMap(3, 4, 2, 'L', TICKS_LOW, HEAT_LOW, STEAM_LOW));
        patterns.add(buildMap(3, 3, 2, 'L', TICKS_LOW, HEAT_LOW, STEAM_LOW));
        patterns.add(buildMap(3, 2, 2, 'L', TICKS_LOW, HEAT_LOW, STEAM_LOW));

        patterns.add(buildMap(2, 3, 1, 'L', TICKS_LOW, HEAT_LOW, STEAM_LOW));
        patterns.add(buildMap(2, 2, 1, 'L', TICKS_LOW, HEAT_LOW, STEAM_LOW));

        patterns.add(buildMap(1, 1, 1, 'L', TICKS_LOW, HEAT_LOW, STEAM_LOW));
    }

    protected TileBoiler() {
        setRootLogic(new StructureLogic("boiler", this, patterns) {
//            @Override
//            protected void onMasterReset() {
//                super.onMasterReset();
//                getFunctionalLogic(BoilerLogic.class).ifPresent(BoilerLogic::reset);
//            }

            @Override
            public boolean isMapPositionValid(BlockPos pos, char mapPos) {
                IBlockState other = WorldPlugin.getBlockState(world, pos);
                IBlockState self = tile().getBlockState();

                switch (mapPos) {
                    case 'O': // Other
                        if (boilerBlocks.contains(other))
                            return false;
                        break;
                    case 'L': // Tank
                        if (!RailcraftBlocks.BOILER_TANK_PRESSURE_LOW.isEqual(other))
                            return false;
                        break;
                    case 'H': // Tank
                        if (!RailcraftBlocks.BOILER_TANK_PRESSURE_HIGH.isEqual(other))
                            return false;
                        break;
                    case 'F': // Firebox
                        if (!fireboxBlocks.contains(other) || other != self)
                            return false;
                        break;
                    case 'A': // Air
                        if (!other.getBlock().isAir(other, world, pos))
                            return false;
                        break;
                }
                return true;
            }
        }.addParts(RailcraftBlocks.BOILER_FIREBOX_SOLID,
                RailcraftBlocks.BOILER_FIREBOX_FLUID,
                RailcraftBlocks.BOILER_TANK_PRESSURE_LOW,
                RailcraftBlocks.BOILER_TANK_PRESSURE_HIGH));
    }

    private static StructurePattern buildMap(int width, int tankHeight, int offset, char tank, int ticks, float heat, int steamCapacity) {
        StructurePattern.Builder builder = StructurePattern.builder();
        char[][] level = new char[width + 2][width + 2];
        for (int x = 0; x < width + 2; x++) {
            for (int z = 0; z < width + 2; z++) {
                level[x][z] = StructurePattern.EMPTY_MARKER;
            }
        }
        builder.level(level);

        level = new char[width + 2][width + 2];
        for (int x = 0; x < width + 2; x++) {
            for (int z = 0; z < width + 2; z++) {
                char m = x == 0 || z == 0 || x == width + 1 || z == width + 1 ? StructurePattern.EMPTY_MARKER : 'F';
                level[x][z] = m;
            }
        }
        builder.level(level);

        for (int y = 2; y < tankHeight + 2; y++) {
            level = new char[width + 2][width + 2];
            for (int x = 0; x < width + 2; x++) {
                for (int z = 0; z < width + 2; z++) {
                    char m = x == 0 || z == 0 || x == width + 1 || z == width + 1 ? StructurePattern.EMPTY_MARKER : tank;
                    level[x][z] = m;
                }
            }
            builder.level(level);
        }

        level = new char[width + 2][width + 2];
        for (int x = 0; x < width + 2; x++) {
            for (int z = 0; z < width + 2; z++) {
                level[x][z] = StructurePattern.EMPTY_MARKER;
            }
        }
        int numTanks = width * width * tankHeight;
        //noinspection UnnecessaryLocalVariable
        StructurePattern ret = builder
                .level(level)
                .attachedData(new BoilerLogic.BoilerData(numTanks, ticks, 1.0, heat, numTanks * WATER_CAPACITY, numTanks * steamCapacity))
                .master(offset, 1, offset)
                .build();
        //Game.log(Game.DEBUG_REPORT, "============Boiler logging: \n{}\n=============", ret);
        return ret;
    }

    BoilerLogic.BoilerData boilerData() {
        return getLogic(StructureLogic.class)
                .map(logic -> Optional.ofNullable(logic.getPattern()).<BoilerLogic.BoilerData>map(p -> p.getAttachedData(0)).orElse(BoilerLogic.BoilerData.EMPTY))
                .orElse(BoilerLogic.BoilerData.EMPTY);
    }

    @Override
    public double getTemp() {
        return getLogic(ITemperature.class).map(ITemperature::getTemp).orElse((double) SteamConstants.COLD_TEMP);
    }

    @Override
    public TankManager getTankManager() {
        return getLogic(FluidLogic.class).map(FluidLogic::getTankManager).orElse(TankManager.NIL);
    }
}

