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
import mods.railcraft.common.blocks.logic.BlastFurnaceLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static mods.railcraft.common.blocks.multi.BlockBlastFurnace.ICON;

public final class TileBlastFurnace extends TileCrafter {

    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

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
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'W', 'B', 'W', 'O'},
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
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
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
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                }
        };
        patterns.add(new MultiBlockPattern(map, 2, 1, 2));
    }

    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> {
        if (tile instanceof TileBlastFurnace)
            return false;
        return InventoryComposite.of(tile).slotCount() >= 27;
    }, InventorySorter.SIZE_DESCENDING);

    public static void placeBlastFurnace(World world, BlockPos pos, ItemStack input, ItemStack output, ItemStack secondOutput, ItemStack fuel) {
        MultiBlockPattern pattern = TileBlastFurnace.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.BLAST_FURNACE.getDefaultState());
        blockMapping.put('W', RailcraftBlocks.BLAST_FURNACE.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileBlastFurnace) {
            // FIXME this might not work if the structure isn't ready
//            TileBlastFurnace master = (TileBlastFurnace) tile;
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_INPUT, input);
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_OUTPUT, output);
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_SLAG, secondOutput);
//            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_FUEL, fuel);
        }
    }

    {
        setLogic(new StructureLogic("blast_furnace", this, patterns, new BlastFurnaceLogic(Logic.Adapter.of(this))) {

            @Override
            public boolean isMapPositionValid(BlockPos pos, char mapPos) {
                IBlockState self = getBlockState();
                IBlockState state = world.getBlockState(pos);
                switch (mapPos) {
                    case 'O':
                        if (self != state)
                            return true;
                        break;
                    case 'B':
                    case 'W':
                        if (self == state)
                            return true;
                        break;
                    case 'A':
                        if (state.getBlock().isAir(state, world, pos) || state.getMaterial() == Material.LAVA)
                            return true;
                        break;
                }
                return false;
            }
        });
    }

    private void setLavaIdle() {
        BlockPos offsetPos = getPos().add(0, 1, 0);
        if (world.isAirBlock(offsetPos))
            world.setBlockState(offsetPos, Blocks.LAVA.getStateFromMeta(7), 3);
    }

    private void setLavaBurn() {
        BlockPos offsetPos = getPos().add(0, 1, 0);
        if (world.isAirBlock(offsetPos))
            world.setBlockState(offsetPos, Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
        offsetPos = offsetPos.up();
        if (world.isAirBlock(offsetPos))
            world.setBlockState(offsetPos, Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
    }

    /*private void destroyLava() {
        int xLava = x + 1;
        int yLava = y + 2;
        int zLava = z + 1;
        if (world.getBlock(xLava, yLava, zLava).getMaterial() == Material.LAVA)
            world.setBlockToAir(xLava, yLava, zLava);
        yLava -= 1;
        if (world.getBlock(xLava, yLava, zLava).getMaterial() == Material.LAVA)
            world.setBlockToAir(xLava, yLava, zLava);
    }*/

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld()))
            return;

        if (clock(128)) {
            getLogic(BlastFurnaceLogic.class).map(l -> l.invFuel).ifPresent(invFuel ->
                    invCache.getAdjacentInventories().moveOneItemTo(invFuel, BlastFurnaceLogic.FUEL_FILTER));

            if (getLogic(StructureLogic.class).map(StructureLogic::isValidMaster).orElse(false)) {
                if (isBurning())
                    setLavaBurn();
                else
                    setLavaIdle();
            }
        }
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
    public EnumGui getGui() {
        return EnumGui.BLAST_FURNACE;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class).map(l -> l.getPatternMarker() == 'W').orElse(false)
                ? hasFlames()
                ? base.withProperty(ICON, 2)
                : base.withProperty(ICON, 1)
                : base.withProperty(ICON, 0);
    }
}
