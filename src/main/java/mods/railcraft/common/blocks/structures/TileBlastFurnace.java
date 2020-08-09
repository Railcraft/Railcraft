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
import mods.railcraft.common.blocks.logic.BlastFurnaceLogic;
import mods.railcraft.common.blocks.logic.ItemPullLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mods.railcraft.common.blocks.structures.BlockBlastFurnace.ICON;

public final class TileBlastFurnace extends TileCrafter {

    private static final List<StructurePattern> patterns = new ArrayList<>();

    static {
        char[][][] map = {
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
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
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
                }
        };
        patterns.add(new StructurePattern(map, 2, 1, 2));
    }

    public static void placeBlastFurnace(World world, BlockPos pos, ItemStack input, ItemStack output, ItemStack secondOutput, ItemStack fuel) {
        StructurePattern pattern = TileBlastFurnace.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.BLAST_FURNACE.getDefaultState());
        blockMapping.put('W', RailcraftBlocks.BLAST_FURNACE.getDefaultState());
        Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
//        if (tile instanceof TileBlastFurnace) {
            // FIXME this might not work if the structure isn't ready
//            TileBlastFurnace master = (TileBlastFurnace) tile;
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_INPUT, input);
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_OUTPUT, output);
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_SLAG, secondOutput);
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_FUEL, fuel);
//        }
    }

    {
        setLogic(new StructureLogic("blast_furnace", this, patterns, new BlastFurnaceLogic(Logic.Adapter.of(this))) {

            @Override
            public boolean isMapPositionValid(BlockPos pos, char mapPos) {
                IBlockState self = getBlockState();
                IBlockState other = world.getBlockState(pos);
                switch (mapPos) {
                    case 'O':
                        if (self != other)
                            return true;
                        break;
                    case 'C': // Corner
                        if (self == other || isCorner(other))
                            return true;
                        break;
                    case 'B':
                    case 'W':
                        if (self == other)
                            return true;
                        break;
                    case 'A':
                        if (other.getBlock().isAir(other, world, pos) || other.getMaterial() == Material.LAVA)
                            return true;
                        break;
                }
                return false;
            }

            private boolean isCorner(IBlockState state) {
                return state.getBlock() instanceof BlockBrickStairs
                        && ((BlockBrickStairs) state.getBlock()).brickTheme == BrickTheme.INFERNAL;
            }

            @Override
            public boolean isPart(Block block) {
                return super.isPart(block) || block instanceof BlockBrickStairs;
            }

            @Override
            protected void onMasterReset() {
                super.onMasterReset();
                BlastFurnaceLogic furnace = (BlastFurnaceLogic) functionalLogic;
                if (furnace.isBurning()) {
                    for (int ii = 0; ii < furnace.getInventory().getSizeInventory(); ii++)
                        furnace.getInventory().decrStackSize(ii, 1);
                    world.setBlockState(getPos().up(), Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
                    world.setBlockState(getPos().up(2), Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
                }
            }
        }.addSubLogic(new ItemPullLogic(Logic.Adapter.of(this), BlastFurnaceLogic.SLOT_FUEL, 1, 128, BlastFurnaceLogic.FUEL_FILTER)));

    }

    private boolean isBurning() {
        return getLogic(BlastFurnaceLogic.class).map(BlastFurnaceLogic::isBurning).orElse(false);
    }

    @Override
    public boolean hasFlames() {
        return getLogic(StructureLogic.class).map(l -> l.getPatternMarker() == 'W').orElse(true)
                && isBurning();
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class).map(l -> l.getPatternMarker() == 'W').orElse(false)
                ? hasFlames()
                ? base.withProperty(ICON, 2)
                : base.withProperty(ICON, 1)
                : base.withProperty(ICON, 0);
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.BLAST_FURNACE;
    }
}
