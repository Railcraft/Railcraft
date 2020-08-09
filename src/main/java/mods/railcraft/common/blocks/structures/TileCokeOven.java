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
import mods.railcraft.common.blocks.TileCrafter;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrickStairs;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TileCokeOven extends TileCrafter {

    private static final List<StructurePattern> patterns = new ArrayList<>();

    static {
        char[][][] map1 = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'W', 'A', 'W', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'C', 'C', 'C', 'O'},
                        {'O', 'C', 'B', 'C', 'O'},
                        {'O', 'C', 'C', 'C', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },};
        patterns.add(new StructurePattern(map1, new BlockPos(2, 1, 2), null, CokeOvenLogic.TANK_CAPACITY, 1));
        char[][][] map2 = {
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'W', 'A', 'A', 'A', 'A', 'A', 'W', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'O'},
                        {'O', 'C', 'B', 'B', 'B', 'B', 'B', 'C', 'O'},
                        {'O', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },};
        patterns.add(new StructurePattern(map2, new BlockPos(2, 1, 4), null, CokeOvenLogic.TANK_CAPACITY * 3, 3));
        char[][][] map3 = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'C', 'C', 'C', 'O'},
                        {'O', 'C', 'B', 'C', 'O'},
                        {'O', 'C', 'B', 'C', 'O'},
                        {'O', 'C', 'B', 'C', 'O'},
                        {'O', 'C', 'B', 'C', 'O'},
                        {'O', 'C', 'B', 'C', 'O'},
                        {'O', 'C', 'C', 'C', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },};
        patterns.add(new StructurePattern(map3, new BlockPos(4, 1, 2), null, CokeOvenLogic.TANK_CAPACITY * 3, 3));
        char[][][] map4 = {
                {
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'W', 'W', 'B', 'O'},
                        {'O', 'W', 'A', 'A', 'W', 'O'},
                        {'O', 'W', 'A', 'A', 'W', 'O'},
                        {'O', 'B', 'W', 'W', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'C', 'C', 'C', 'C', 'O'},
                        {'O', 'C', 'B', 'B', 'C', 'O'},
                        {'O', 'C', 'B', 'B', 'C', 'O'},
                        {'O', 'C', 'C', 'C', 'C', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O'}
                },};
        AxisAlignedBB entityCheck = new AxisAlignedBB(0, 1, 0, 2, 3, 2);
        patterns.add(new StructurePattern(map4, new BlockPos(2, 1, 2), entityCheck, CokeOvenLogic.TANK_CAPACITY * 3, 3));
        char[][][] map5 = {
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'W', 'B', 'W', 'B', 'O'},
                        {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                        {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                        {'O', 'W', 'A', 'A', 'A', 'W', 'O'},
                        {'O', 'B', 'W', 'B', 'W', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'C', 'C', 'C', 'C', 'C', 'O'},
                        {'O', 'C', 'B', 'B', 'B', 'C', 'O'},
                        {'O', 'C', 'B', 'B', 'B', 'C', 'O'},
                        {'O', 'C', 'B', 'B', 'B', 'C', 'O'},
                        {'O', 'C', 'C', 'C', 'C', 'C', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
                },};
        entityCheck = new AxisAlignedBB(-1, 1, -1, 2, 4, 2);
        patterns.add(new StructurePattern(map5, new BlockPos(3, 1, 3), entityCheck, CokeOvenLogic.TANK_CAPACITY * 5, 5));
    }

    public TileCokeOven() {
        setLogic(new StructureLogic("coke_oven", this, patterns,
                new CokeOvenLogic(Logic.Adapter.of(this))
                        .addSubLogic(new DynamicTankCapacityLogic(Logic.Adapter.of(this), 0, 0))
        ) {

            @Override
            public boolean isMapPositionValid(BlockPos pos, char mapPos) {
                IBlockState other = WorldPlugin.getBlockState(world, pos);
                switch (mapPos) {
                    case 'O': // Other
                        if (isBlock(other))
                            return false;
                        break;
                    case 'C': // Corner
                        if (!isCorner(other))
                            return false;
                        break;
                    case 'W': // Window
                    case 'B': // Block
                        if (!isBlock(other))
                            return false;
                        break;
                    case 'A': // Air
                        if (!other.getBlock().isAir(other, world, pos))
                            return false;
                        break;
                    case '*': // Anything
                        return true;
                }
                return true;
            }

            private boolean isCorner(IBlockState state) {
                return isBlock(state) || (state.getBlock() instanceof BlockBrickStairs
                        && ((((BlockBrickStairs) state.getBlock()).brickTheme == BrickTheme.BADLANDS && RailcraftBlocks.COKE_OVEN_RED.isEqual(getBlockType()))
                        || (((BlockBrickStairs) state.getBlock()).brickTheme == BrickTheme.SANDY && RailcraftBlocks.COKE_OVEN.isEqual(getBlockType()))));
            }

            private boolean isBlock(IBlockState state) {
                return RailcraftBlocks.COKE_OVEN.isEqual(state) || RailcraftBlocks.COKE_OVEN_RED.isEqual(state);
            }

            @Override
            public boolean isPart(Block block) {
                return block instanceof BlockCokeOven || block instanceof BlockBrickStairs;
            }

        });
    }

    public static void placeCokeOven(World world, BlockPos pos, int creosote, ItemStack input, ItemStack output) {
        StructurePattern pattern = TileCokeOven.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.COKE_OVEN.getDefaultState());
        blockMapping.put('W', RailcraftBlocks.COKE_OVEN.getDefaultState());
        Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
        tile.flatMap(t -> t.getLogic(StructureLogic.class)).ifPresent(structure -> {
            structure.getFunctionalLogic(FluidLogic.class).ifPresent(logic -> logic.getTankManager().get(0).setFluid(Fluids.CREOSOTE.get(creosote)));
            structure.getFunctionalLogic(InventoryLogic.class).ifPresent(logic -> {
                logic.setInventorySlotContents(CokeOvenLogic.SLOT_INPUT, input);
                logic.setInventorySlotContents(CokeOvenLogic.SLOT_OUTPUT, output);
            });
        });
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return getLogic(IFluidHandlerImplementor.class)
                .map(l -> FluidTools.interactWithFluidHandler(player, hand, l.getTankManager())).orElse(false)
                || super.blockActivated(player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class).map(l -> l.getPatternMarker() == 'W').orElse(false)
                ? hasFlames()
                ? base.withProperty(BlockCokeOven.ICON, 2)
                : base.withProperty(BlockCokeOven.ICON, 1)
                : base.withProperty(BlockCokeOven.ICON, 0);
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.COKE_OVEN;
    }

}