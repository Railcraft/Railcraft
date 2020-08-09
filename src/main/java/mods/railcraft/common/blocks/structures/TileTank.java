/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
// TODO Add light calcs to glass blocks
public abstract class TileTank extends TileLogic {

    private static final List<StructurePattern> patterns = buildPatterns();

    public static void placeIronTank(World world, BlockPos pos, int patternIndex, FluidStack fluid) {
        placeTank(world, pos, patternIndex, RailcraftBlocks.TANK_IRON_WALL.getDefaultState(), fluid);
    }

    public static void placeSteelTank(World world, BlockPos pos, int patternIndex, FluidStack fluid) {
        placeTank(world, pos, patternIndex, RailcraftBlocks.TANK_STEEL_WALL.getDefaultState(), fluid);
    }

    public static void placeTank(World world, BlockPos pos, int patternIndex, IBlockState wallState, FluidStack fluid) {
        StructurePattern pattern = TileTank.patterns.get(patternIndex);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', wallState);
        blockMapping.put('W', RailcraftBlocks.GLASS.getDefaultState());
        Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
        tile.flatMap(t -> t.getLogic(StructureLogic.class)).ifPresent(structure -> {
            structure.getFunctionalLogic(FluidLogic.class).ifPresent(logic -> logic.getTankManager().get(0).setFluid(fluid));
        });
    }

    private static List<StructurePattern> buildPatterns() {
        List<StructurePattern> pats = new ArrayList<>();
        boolean client = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;

        // 3x3
        int xOffset = 2;
        int yOffset = 0;
        int zOffset = 2;

        char[][] bottom = {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'M', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        char[][] middle = {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'W', 'B', 'O'},
                {'O', 'W', 'A', 'W', 'O'},
                {'O', 'B', 'W', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        char[][] top = {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'T', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        char[][] border = {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        for (int i = 4; i <= 8; i++) {
            char[][][] map = buildMap(i, bottom, middle, top, border);
            AxisAlignedBB entityCheck = new AxisAlignedBB(0, 1, 0, 1, i - 1, 1);
            pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
        }

        // 5x5
        if (client || RailcraftConfig.getMaxTankSize() >= 5) {
            xOffset = zOffset = 3;

            bottom = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'M', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            middle = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            top = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'T', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            border = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            for (int i = 4; i <= 8; i++) {
                char[][][] map = buildMap(i, bottom, middle, top, border);
                AxisAlignedBB entityCheck = new AxisAlignedBB(-1, 1, -1, 2, i - 1, 2);
                pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
            }
        }

        // 7x7
        if (client || RailcraftConfig.getMaxTankSize() >= 7) {
            xOffset = zOffset = 4;

            bottom = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'M', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            middle = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            top = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'T', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            border = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            for (int i = 4; i <= 8; i++) {
                char[][][] map = buildMap(i, bottom, middle, top, border);
                AxisAlignedBB entityCheck = new AxisAlignedBB(-2, 1, -2, 3, i - 1, 3);
                pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
            }
        }

        // 9x9
        if (client || RailcraftConfig.getMaxTankSize() >= 9) {
            xOffset = zOffset = 5;

            bottom = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'M', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            middle = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            top = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'T', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'O'},
                    {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            border = new char[][]{
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                    {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
            };

            for (int i = 4; i <= 8; i++) {
                char[][][] map = buildMap(i, bottom, middle, top, border);
                AxisAlignedBB entityCheck = new AxisAlignedBB(-3, 1, -3, 4, i - 1, 4);
                pats.add(buildPattern(map, xOffset, yOffset, zOffset, entityCheck));
            }
        }

        return pats;
    }

    private static StructurePattern buildPattern(char[][][] map, int xOffset, int yOffset, int zOffset, AxisAlignedBB entityCheck) {
        if (!RailcraftConfig.allowTankStacking()) {
            entityCheck.offset(0, 1, 0);
            yOffset = 1;
        }
        int tankSize = (map[0].length - 2) * (map[0][0].length - 2) * (map.length - (RailcraftConfig.allowTankStacking() ? 0 : 2));
        return new StructurePattern(map, new BlockPos(xOffset, yOffset, zOffset), entityCheck, tankSize);
    }

    private static char[][][] buildMap(int height, char[][] bottom, char[][] mid, char[][] top, char[][] border) {
        char[][][] map;
        if (RailcraftConfig.allowTankStacking()) {
            map = new char[height][][];

            map[0] = bottom;
            map[height - 1] = top;

            for (int i = 1; i < height - 1; i++) {
                map[i] = mid;
            }
        } else {
            map = new char[height + 2][][];

            map[0] = border;
            map[1] = bottom;
            map[height] = top;
            map[height + 1] = border;

            for (int i = 2; i < height; i++) {
                map[i] = mid;
            }
        }

        return map;
    }

    protected TileTank() {
        setLogic(new StructureLogic("metal_tank", this, patterns,
                new StorageTankLogic(Logic.Adapter.of(this), getTankDefinition().getCapacityPerBlock())
                        .addSubLogic(new DynamicTankCapacityLogic(Logic.Adapter.of(this), 0, 0, getTankDefinition().getCapacityPerBlock()))
        ) {

            @Override
            public boolean isMapPositionValid(BlockPos pos, char marker) {
                IBlockState state = WorldPlugin.getBlockState(world, pos);
                switch (marker) {
                    case 'O': // Other
                        return !getTankDefinition().isTankBlock(state);
                    case 'W': // Wall, Gauge, or Valve
                        return getTankDefinition().isTankBlock(state);
                    case 'B': // Wall
                        return getTankDefinition().isWallBlock(state);
                    case 'M': // Master
                        if (RailcraftBlocks.GLASS.isEqual(state))
                            return false;
                    case 'T': // Top Block
                        if (!getTankDefinition().isTankBlock(state))
                            return false;
                        TileEntity tile = world.getTileEntity(pos);
                        if (!(tile instanceof TileLogic)) {
                            world.removeTileEntity(pos);
                            return true;
                        }
                        return !((TileLogic) tile).getLogic(StructureLogic.class).map(StructureLogic::isStructureValid).orElse(true);
                    case 'A': // Air
                        return state.getBlock().isAir(state, world, pos);
                }
                return true;
            }

            @Override
            public boolean isPart(Block block) {
                return getTankDefinition().isTankBlock(block);
            }

//            @Override
//            protected void onMasterReset() {
//                super.onMasterReset();
//                getLogic(FluidLogic.class).ifPresent(logic-> logic.getTankManager().get(0).setFluid(null));
//            }

        });
        getLogic(FluidLogic.class).ifPresent(logic -> {
            logic.setHidden(true);
            logic.setTankSync(0);
        });
    }

    @Override
    public final EnumGui getGui() {
        return EnumGui.TANK;
    }

    public abstract TankDefinition getTankDefinition();

    @Override
    public float getResistance(@Nullable Entity exploder) {
        return getTankDefinition().getResistance(exploder);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Prevents players from getting inside tanks using boats
        return player.getHeldItem(hand).getItem() == Items.BOAT || super.blockActivated(player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return Short.MAX_VALUE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        boolean isMaster = getLogic(StructureLogic.class).map(StructureLogic::isValidMaster).orElse(false);
        return isMaster ? pass == 0 : pass == 1;
    }

}
