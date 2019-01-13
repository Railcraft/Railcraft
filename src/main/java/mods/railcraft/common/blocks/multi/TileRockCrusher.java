/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileCrafter;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.RockCrusherLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.entity.RCEntitySelectors;
import mods.railcraft.common.util.entity.RailcraftDamageSource;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mods.railcraft.common.blocks.multi.BlockRockCrusher.ICON;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileRockCrusher extends TileCrafter {
    private static final double SUCKING_POWER_COST = 1000;
    private static final double KILLING_POWER_COST = 5000;

    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

    static {
        char[][][] map1 = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'D', 'B', 'O'},
                        {'O', 'B', 'D', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'a', 'd', 'f', 'O'},
                        {'O', 'c', 'e', 'h', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                }
        };
        patterns.add(new MultiBlockPattern(map1));

        char[][][] map2 = {
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'D', 'D', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'a', 'f', 'O'},
                        {'O', 'b', 'g', 'O'},
                        {'O', 'c', 'h', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                }
        };
        patterns.add(new MultiBlockPattern(map2));
    }

    public TileRockCrusher() {
        setLogic(new StructureLogic("rock_crusher", this, patterns, new RockCrusherLogic(Logic.Adapter.of(this))) {

            @Override
            public boolean isMapPositionValid(BlockPos pos, char mapPos) {
                IBlockState self = getBlockState();
                IBlockState other = WorldPlugin.getBlockState(world, pos);
                switch (mapPos) {
                    case 'O': // Other
                        if (self != other)
                            return true;
                        break;
                    case 'D': // Window
                    case 'B': // Block
                    case 'a': // Block
                    case 'b': // Block
                    case 'c': // Block
                    case 'd': // Block
                    case 'e': // Block
                    case 'f': // Block
                    case 'g': // Block
                    case 'h': // Block
                        if (self == other)
                            return true;
                        break;
                    case 'A': // Air
                        if (other.getBlock().isAir(other, world, pos))
                            return true;
                        break;
                }
                return false;
            }
        });
    }

    public static void placeRockCrusher(World world, BlockPos pos, int patternIndex, @Nullable List<ItemStack> input, @Nullable List<ItemStack> output) {
        MultiBlockPattern pattern = TileRockCrusher.patterns.get(patternIndex);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        IBlockState state = RailcraftBlocks.ROCK_CRUSHER.getState(null);
        blockMapping.put('B', state);
        blockMapping.put('D', state);
        blockMapping.put('a', state);
        blockMapping.put('b', state);
        blockMapping.put('c', state);
        blockMapping.put('d', state);
        blockMapping.put('e', state);
        blockMapping.put('f', state);
        blockMapping.put('h', state);
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileRockCrusher) {
            TileRockCrusher master = (TileRockCrusher) tile;
            // FIXME
//            for (int slot = 0; slot < 9; slot++) {
//                if (input != null && slot < input.size())
//                    master.inv.setInventorySlotContents(TileRockCrusher.SLOT_INPUT + slot, input.get(slot));
//                if (output != null && slot < output.size())
//                    master.inv.setInventorySlotContents(TileRockCrusher.SLOT_OUTPUT + slot, output.get(slot));
//            }
        }
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(getWorld()) && clock(8)) {
            getLogic(RockCrusherLogic.class).ifPresent(l -> {
                BlockPos pos = getPos();
                BlockPos target = pos.up();

                EntitySearcher.find(EntityItem.class).around(target).in(world).forEach(item -> {
                    if (l.useInternalCharge(SUCKING_POWER_COST)) {
                        ItemStack stack = item.getItem().copy();
                        l.invInput.addStack(stack);
                        item.setDead();
                    }
                });

                EntitySearcher.findLiving().around(target).and(RCEntitySelectors.KILLABLE).in(world).forEach(e -> {
                    if (l.hasInternalCapacity(KILLING_POWER_COST)
                            && e.attackEntityFrom(RailcraftDamageSource.CRUSHER, 5))
                        l.useInternalCharge(KILLING_POWER_COST);
                });
            });
        }
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.ROCK_CRUSHER;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class).map(l -> base.withProperty(ICON, l.getPatternMarker())).orElse(base);
    }
}
