/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.interfaces.ITileTank;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.IBoilerContainer;
import mods.railcraft.common.util.steam.SteamBoiler;
import mods.railcraft.common.util.steam.SteamConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoiler extends TileMultiBlock implements IBoilerContainer, ITileTank {

    public static final int TANK_WATER = 0;
    public static final int TANK_STEAM = 1;
    public static final int TRANSFER_RATE = FluidTools.BUCKET_VOLUME;
    public static final int TICKS_LOW = 16;
    public static final int TICKS_HIGH = 8;
    public static final int STEAM_LOW = 16;
    public static final int STEAM_HIGH = 32;
    public static final float HEAT_LOW = SteamConstants.MAX_HEAT_LOW;
    public static final float HEAT_HIGH = SteamConstants.MAX_HEAT_HIGH;
    protected static final List<StructurePattern> patterns = new ArrayList<>();
    private static final Set<IBlockState> boilerBlocks = new HashSet<>();
    private static final Set<IBlockState> fireboxBlocks = new HashSet<>();
    private boolean explode;

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
        super(patterns);
    }

    private static StructurePattern buildMap(int width, int tankHeight, int offset, char tank, int ticks, float heat, int capacity) {
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
        //noinspection UnnecessaryLocalVariable
        StructurePattern ret = builder
                .level(level)
                .attachedData(new BoilerData(width * width * tankHeight, ticks, heat, capacity))
                .master(offset, 1, offset)
                .build();
        //Game.log(Game.DEBUG_REPORT, "============Boiler logging: \n{}\n=============", ret);
        return ret;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return (isStructureValid() && FluidUtil.interactWithFluidHandler(player, hand, getTankManager())) || super.blockActivated(player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public void steamExplosion(FluidStack resource) {
        explode = true;
    }

    BoilerData boilerData() {
        return Optional.ofNullable(getPattern()).<BoilerData>map(p -> p.getAttachedData(0)).orElse(BoilerData.EMPTY);
    }

    public int getNumTanks() {
        return boilerData().numTanks;
    }

    public float getMaxHeat() {
        return boilerData().maxHeat;
    }

    public int getTicksPerConversion() {
        return boilerData().ticksPerCycle;
    }

    public int getSteamCapacityPerTank() {
        return boilerData().steamCapacity;
    }

    @Override
    public boolean needsFuel() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        return mBlock != null && mBlock.needsFuel();
    }

    @Override
    public float getTemperature() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return (float) mBlock.boiler.getHeat();
        return SteamConstants.COLD_TEMP;
    }

    @Override
    public SteamBoiler getBoiler() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.boiler;
        return null;
    }

    @Override
    public TankManager getTankManager() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return TankManager.NIL;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getTankManager());
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(world)) {
            if (explode) {
                world.createExplosion(null, getX(), getY(), getZ(), 5f + 0.1f * getNumTanks(), true);
                explode = false;
                return;
            }
            TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
            if (mBlock != null) {
                StandardTank tank = mBlock.tankManager.get(TANK_STEAM);
                FluidStack steam = tank.getFluid();
                if (steam != null && (mBlock.boiler.isCold() || steam.amount >= tank.getCapacity() / 2))
                    mBlock.tankManager.push(tileCache, getOutputFilter(), EnumFacing.VALUES, TANK_STEAM, TRANSFER_RATE);
            }
        }
    }

    public abstract Predicate<TileEntity> getOutputFilter();

    @Override
    protected int getMaxRecursionDepth() {
        return 20;
    }

    @Override
    protected boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);

        switch (mapPos) {
            case 'O': // Other
                if (boilerBlocks.contains(state))
                    return false;
                break;
            case 'L': // Tank
                if (!RailcraftBlocks.BOILER_TANK_PRESSURE_LOW.isEqual(state))
                    return false;
                break;
            case 'H': // Tank
                if (!RailcraftBlocks.BOILER_TANK_PRESSURE_HIGH.isEqual(state))
                    return false;
                break;
            case 'F': // Firebox
                if (!fireboxBlocks.contains(state))
                    return false;
                break;
            case 'A': // Air
                if (!state.getBlock().isAir(state, world, pos))
                    return false;
                break;
        }
        return true;
    }

    @Override
    protected boolean isStructureTile(@Nullable TileEntity tile) {
        return tile instanceof TileBoiler;
    }
}

