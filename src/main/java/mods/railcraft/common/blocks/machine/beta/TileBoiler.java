/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.Steam;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoiler extends TileMultiBlock implements IFluidHandler {

    public static final int TANK_WATER = 0;
    public static final int TANK_STEAM = 1;
    public static final int TRANSFER_RATE = FluidHelper.BUCKET_VOLUME;
    public static final int TICKS_LOW = 16;
    public static final int TICKS_HIGH = 8;
    public static final int STEAM_LOW = 16;
    public static final int STEAM_HIGH = 32;
    public static final float HEAT_LOW = Steam.MAX_HEAT_LOW;
    public static final float HEAT_HIGH = Steam.MAX_HEAT_HIGH;
    protected static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private static final Set<Integer> boilerBlocks = new HashSet<Integer>();
    private static final Set<Integer> fireboxBlocks = new HashSet<Integer>();
    protected final TankManager tankManager = new TankManager();
    protected final FilteredTank tankWater;
    protected final FilteredTank tankSteam;
    private boolean explode;

    static {
        fireboxBlocks.add(EnumMachineBeta.BOILER_FIREBOX_SOLID.ordinal());
        fireboxBlocks.add(EnumMachineBeta.BOILER_FIREBOX_FLUID.ordinal());

        boilerBlocks.addAll(fireboxBlocks);
        boilerBlocks.add(EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.ordinal());
        boilerBlocks.add(EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.ordinal());

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

        tankWater = new FilteredTank(4 * FluidHelper.BUCKET_VOLUME, Fluids.WATER.get(), this);
        tankManager.add(tankWater);

        tankSteam = new FilteredTank(16 * FluidHelper.BUCKET_VOLUME, Fluids.STEAM.get(), this);
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
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side) {
        if (heldItem != null && heldItem.getItem() != Items.BUCKET)
            if (Game.isHost(worldObj)) {
                TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
                if (mBlock != null)
                    if (mBlock.handleClick(player, side))
                        return true;
            } else if (FluidItemHelper.isContainer(heldItem))
                return true;
        return super.blockActivated(player, hand, heldItem, side);
    }

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

    @Nullable
    public TankManager getTankManager() {
        TileBoiler mBlock = (TileBoiler) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(worldObj)) {
            if (explode) {
                worldObj.createExplosion(null, getX(), getY(), getZ(), 5f + 0.1f * getNumTanks(), true);
                explode = false;
                return;
            }
            TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
            if (mBlock != null) {
                StandardTank tank = mBlock.tankManager.get(TANK_STEAM);
                FluidStack steam = tank.getFluid();
                if (steam != null && (!mBlock.boiler.isBoiling() || steam.amount >= tank.getCapacity() / 2))
                    mBlock.tankManager.outputLiquid(tileCache, getOutputFilter(), EnumFacing.VALUES, TANK_STEAM, TRANSFER_RATE);
            }
        }
    }

    public abstract Predicate<TileEntity> getOutputFilter();

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock mBlock = getMasterBlock();
        return mBlock != null && mBlock.openGui(player);
    }

    @Override
    protected int getMaxRecursionDepth() {
        return 20;
    }

    @Override
    protected boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState state = WorldPlugin.getBlockState(worldObj, getPos());
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);

        switch (mapPos) {
            case 'O': // Other
                if (block == getBlockType() && boilerBlocks.contains(meta))
                    return false;
                break;
            case 'L': // Tank
                if (block != getBlockType() || meta != EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.ordinal())
                    return false;
                break;
            case 'H': // Tank
                if (block != getBlockType() || meta != EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.ordinal())
                    return false;
                break;
            case 'F': // Firebox
                if (block != getBlockType() || meta != getBlockMetadata() || !fireboxBlocks.contains(meta))
                    return false;
                break;
            case 'A': // Air
                if (!worldObj.isAirBlock(pos))
                    return false;
                break;
        }
        return true;
    }

    @Override
    protected boolean isStructureTile(TileEntity tile) {
        return tile instanceof TileBoiler;
    }

    @Nullable
    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock == null)
            return null;
        return mBlock.tankManager.get(TANK_STEAM).drain(maxDrain, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (Fluids.STEAM.is(resource))
            return drain(from, resource.amount, doDrain);
        return null;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return 0;
    }

    protected int fill(int tankIndex, FluidStack resource, boolean doFill) {
        if (tankIndex == TANK_STEAM)
            return 0;
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock == null)
            return 0;
        if (doFill && Fluids.WATER.is(resource))
            onFillWater();
        return mBlock.tankManager.fill(tankIndex, resource, doFill);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return Fluids.WATER.is(fluid);
    }

    @Override
    public boolean canDrain(EnumFacing from, @Nullable Fluid fluid) {
        return fluid == null || Fluids.STEAM.is(fluid);
    }

    protected void onFillWater() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null && mBlock.boiler.isSuperHeated() && Steam.BOILERS_EXPLODE) {
            FluidStack water = mBlock.tankManager.get(TANK_WATER).getFluid();
            if (water == null || water.amount <= 0) {
                mBlock.boiler.setHeat(Steam.SUPER_HEATED - 1);
                explode();
            }
        }
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing dir) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager.getTankInfo();
        return FakeTank.INFO;
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
