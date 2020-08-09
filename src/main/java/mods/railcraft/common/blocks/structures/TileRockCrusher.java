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
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileCrafter;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.entity.RCEntitySelectors;
import mods.railcraft.common.util.entity.RailcraftDamageSource;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mods.railcraft.common.blocks.structures.BlockRockCrusher.ICON;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileRockCrusher extends TileCrafter {
    private static final double SUCKING_POWER_COST = 1000;
    private static final double KILLING_POWER_COST = 5000;

    private static final List<StructurePattern> patterns = new ArrayList<>();

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
        patterns.add(new StructurePattern(map1));

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
        patterns.add(new StructurePattern(map2));
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
                }
                        .addSubLogic(new CrafterParticleEffectLogic(Logic.Adapter.of(this), () -> {
                            ItemStack crushed = getLogic(RockCrusherLogic.class).map(RockCrusherLogic::getCrushed).orElseGet(() -> new ItemStack(Blocks.COBBLESTONE));
                            IBlockState crushedState = InvTools.getBlockStateFromStack(crushed);
                            for (int i = 0; i < 8; i++)
                                ClientEffects.INSTANCE.blockParticle(
                                        theWorldAsserted(),
                                        this,
                                        new Vec3d(getPos()).add(0.5 + MiscTools.RANDOM.nextGaussian() * 0.5, 1.0, 0.5 + MiscTools.RANDOM.nextGaussian() * 0.5),
                                        new Vec3d(MiscTools.RANDOM.nextGaussian() * 0.05, 0.1, MiscTools.RANDOM.nextGaussian() * 0.05),
                                        crushedState,
                                        true,
                                        ""
                                );
                        }))
        );
    }

    public static void placeRockCrusher(World world, BlockPos pos, int patternIndex, @Nullable List<ItemStack> input, @Nullable List<ItemStack> output) {
        StructurePattern pattern = TileRockCrusher.patterns.get(patternIndex);
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
        Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
        tile.flatMap(t -> t.getLogic(StructureLogic.class)).ifPresent(structure -> {
            structure.getFunctionalLogic(InventoryLogic.class).ifPresent(logic -> {
                for (int slot = 0; slot < 9; slot++) {
                    if (input != null && slot < input.size())
                        logic.setInventorySlotContents(RockCrusherLogic.SLOT_INPUT + slot, input.get(slot));
                    if (output != null && slot < output.size())
                        logic.setInventorySlotContents(RockCrusherLogic.SLOT_OUTPUT + slot, output.get(slot));
                }
            });
        });
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
