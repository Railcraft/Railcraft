/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.IBoilerContainer;
import mods.railcraft.common.util.steam.SteamConstants;
import mods.railcraft.common.util.steam.SteamBoiler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoiler<S extends TileBoiler<S>> extends TileMultiBlock<S> implements IBoilerContainer {

    public static final int TANK_WATER = 0;
    public static final int TANK_STEAM = 1;
    public static final int TRANSFER_RATE = FluidTools.BUCKET_VOLUME;
    public static final int TICKS_LOW = 16;
    public static final int TICKS_HIGH = 8;
    public static final int STEAM_LOW = 16;
    public static final int STEAM_HIGH = 32;
    public static final float HEAT_LOW = SteamConstants.MAX_HEAT_LOW;
    public static final float HEAT_HIGH = SteamConstants.MAX_HEAT_HIGH;
    protected static final List<MultiBlockPattern> patterns = new ArrayList<>();
    private static final Set<Integer> boilerBlocks = new HashSet<>();
    private static final Set<Integer> fireboxBlocks = new HashSet<>();
    protected final TankManager tankManager = new TankManager();
    protected final FilteredTank tankWater;
    protected final FilteredTank tankSteam;
    private boolean explode;

    static {
        //TODO
//        fireboxBlocks.add(EnumMachineBeta.BOILER_FIREBOX_SOLID.ordinal());
//        fireboxBlocks.add(EnumMachineBeta.BOILER_FIREBOX_FLUID.ordinal());

        boilerBlocks.addAll(fireboxBlocks);
//        boilerBlocks.add(EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.ordinal());
//        boilerBlocks.add(EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.ordinal());

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

        tankWater = new FilteredTank(4 * FluidTools.BUCKET_VOLUME, this) {
            @Override
            public int fillInternal(FluidStack resource, boolean doFill) {
                if (!isMaster()) return 0;
                TileBoiler.this.onFillWater();
                return super.fillInternal(resource, doFill);
            }
        };
        tankWater.setFilter(Fluids.WATER::get);
        tankManager.add(tankWater);

        tankSteam = new FilteredTank(16 * FluidTools.BUCKET_VOLUME, this);
        tankSteam.setFilter(Fluids.STEAM::get);
        tankManager.add(tankSteam);
    }

    private static MultiBlockPattern buildMap(int width, int tankHeight, int offset, char tank, int ticks, float heat, int capacity) {
        char[][][] map = new char[tankHeight + 3][width + 2][width + 2];

        for (int x = 0; x < width + 2; x++) {
            for (int z = 0; z < width + 2; z++) {
                map[0][x][z] = 'O';
            }
        }

        for (int x = 0; x < width + 2; x++) {
            for (int z = 0; z < width + 2; z++) {
                char m = x == 0 || z == 0 || x == width + 1 || z == width + 1 ? 'O' : 'F';
                map[1][x][z] = m;
            }
        }

        for (int y = 2; y < tankHeight + 2; y++) {
            for (int x = 0; x < width + 2; x++) {
                for (int z = 0; z < width + 2; z++) {
                    char m = x == 0 || z == 0 || x == width + 1 || z == width + 1 ? 'O' : tank;
                    map[y][x][z] = m;
                }
            }
        }

        for (int x = 0; x < width + 2; x++) {
            for (int z = 0; z < width + 2; z++) {
                map[tankHeight + 2][x][z] = 'O';
            }
        }

        return new BoilerPattern(map, width * width * tankHeight, ticks, heat, capacity, offset, offset);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        return (isStructureValid() && FluidUtil.interactWithFluidHandler(player, hand, getMasterTankManager())) || super.blockActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public void explode() {
        explode = true;
    }

    public int getNumTanks() {
        MultiBlockPattern pattern = getPattern();
        return ((BoilerPattern) pattern).numTanks;
    }

    public float getMaxHeat() {
        MultiBlockPattern pattern = getPattern();
        return ((BoilerPattern) pattern).maxHeat;
    }

    public int getTicksPerConversion() {
        MultiBlockPattern pattern = getPattern();
        return ((BoilerPattern) pattern).ticksPerCycle;
    }

    public int getSteamCapacityPerTank() {
        MultiBlockPattern pattern = getPattern();
        return ((BoilerPattern) pattern).steamCapacity;
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

    public TankManager getMasterTankManager() {
        S mBlock = getMasterBlock();
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
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getMasterTankManager());
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
                if (steam != null && (!mBlock.boiler.isBoiling() || steam.amount >= tank.getCapacity() / 2))
                    mBlock.tankManager.push(tileCache, getOutputFilter(), EnumFacing.VALUES, TANK_STEAM, TRANSFER_RATE);
            }
        }
    }

    public abstract Predicate<TileEntity> getOutputFilter();

    @Override
    public boolean openGui(EntityPlayer player) {
        S mBlock = getMasterBlock();
        return mBlock != null && mBlock.openGui(player);
    }

    @Override
    protected int getMaxRecursionDepth() {
        return 20;
    }

    @Override
    protected boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState state = WorldPlugin.getBlockState(world, getPos());
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);

        switch (mapPos) {
            case 'O': // Other
                if (block == getBlockType() && boilerBlocks.contains(meta))
                    return false;
                break;
                //TODO
//            case 'L': // Tank
//                if (block != getBlockType() || meta != EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.ordinal())
//                    return false;
//                break;
//            case 'H': // Tank
//                if (block != getBlockType() || meta != EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.ordinal())
//                    return false;
//                break;
            case 'F': // Firebox
                if (block != getBlockType() || meta != getBlockMetadata() || !fireboxBlocks.contains(meta))
                    return false;
                break;
            case 'A': // Air
                if (!block.isAir(state, world, pos))
                    return false;
                break;
        }
        return true;
    }

    @Override
    protected boolean isStructureTile(@Nullable TileEntity tile) {
        return tile instanceof TileBoiler;
    }

    public static class BoilerPattern extends MultiBlockPattern {
        public final int numTanks;
        public final int ticksPerCycle;
        public final float maxHeat;
        public final int steamCapacity;

        public BoilerPattern(char[][][] pattern, int tanks, int ticks, float heat, int capacity, int xOffset, int yOffset) {
            super(pattern, xOffset, 1, yOffset);
            numTanks = tanks;
            ticksPerCycle = ticks;
            this.maxHeat = heat;
            this.steamCapacity = capacity;
        }
    }
}
