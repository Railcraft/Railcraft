/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
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
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankWater extends TileLogic {

    private static final int CAP_PER_BLOCK = RailcraftConfig.tankPerBlockCapacity() * FluidTools.BUCKET_VOLUME;
    private static final int OUTPUT_RATE = 40;
    private static final EnumFacing[] OUTPUT_FACES = {EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH};
    private static final List<StructurePattern> patterns = buildPatterns();

    private static List<StructurePattern> buildPatterns() {
        List<StructurePattern> pats = new ArrayList<>();

        // 3x3
        int xOffset = 2;
        int yOffset = 1;
        int zOffset = 2;

        char[][] cap = {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O'}
        };

        char[][] middle = {
                {'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'A', 'B', 'O'},
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
        for (int i = 3; i <= 5; i++) {
            char[][][] map = buildMap(i, cap, middle, border);
            pats.add(buildPattern(map, xOffset, yOffset, zOffset, null));
        }

        // 4x4
        cap =  new char[][]{
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'}
        };
        middle = new char[][]{
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'A', 'A', 'B', 'O'},
                {'O', 'B', 'A', 'A', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'}
        };
        border = new char[][]{
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O'}
        };
        for (int i = 4; i <= 6; i++) {
            char[][][] map = buildMap(i, cap, middle, border);
            pats.add(buildPattern(map, xOffset, yOffset, zOffset, null));
        }

        // 5x5
            xOffset = zOffset = 3;
        cap =  new char[][]{
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'}
        };
        middle = new char[][]{
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'O', 'B', 'B', 'B', 'B', 'B', 'O'},
                {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
                {'O', 'B', 'A', 'A', 'A', 'B', 'O'},
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
        for (int i = 5; i <= 7; i++) {
            char[][][] map = buildMap(i, cap, middle, border);
            pats.add(buildPattern(map, xOffset, yOffset, zOffset, null));
        }

        return pats;
    }

    private static StructurePattern buildPattern(char[][][] map, int xOffset, int yOffset, int zOffset, AxisAlignedBB entityCheck) {
        int tankSize = (map[0].length - 2) * (map[0][0].length - 2) * (map.length - 2);
        return new StructurePattern(map, new BlockPos(xOffset, yOffset, zOffset), entityCheck, tankSize * CAP_PER_BLOCK);
    }

    private static char[][][] buildMap(int height, char[][] cap, char[][] mid, char[][] border) {
        char[][][] map;

        map = new char[height + 2][][];
        map[0] = border;
        map[1] = cap;
        map[height] = cap;
        map[height + 1] = border;
        for (int i = 2; i < height; i++) {
            map[i] = mid;
        }

        return map;
    }

    public TileTankWater() {
        setRootLogic(new StructureLogic("water_tank", this, patterns,
                        new StorageTankLogic(Logic.Adapter.of(this), CAP_PER_BLOCK, Fluids.WATER)
                                .addLogic(new DynamicTankCapacityLogic(Logic.Adapter.of(this), 0, 0))
                )
                        .addLogic(new WaterGeneratorLogic(Logic.Adapter.of(this)))
                        .addLogic(new FluidPushLogic(Logic.Adapter.of(this), 0, OUTPUT_RATE, OUTPUT_FACES))
                        .addLogic(new FluidComparatorLogic(Logic.Adapter.of(this), 0))
                        .addLogic(new BucketInteractionLogic(Logic.Adapter.of(this)))
        );
    }

    @Override
    public String getLocalizationTag() {
        if (getLogic(StructureLogic.class).map(StructureLogic::isStructureValid).orElse(false))
            return "gui.railcraft.tank.water";
        return super.getLocalizationTag();
    }

    public static void placeWaterTank(World world, BlockPos pos, int water) {
        StructurePattern pattern = TileTankWater.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.TANK_WATER.getDefaultState());
        Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
        tile.flatMap(t -> t.getLogic(StructureLogic.class)).ifPresent(structure -> {
            structure.getKernel(FluidLogic.class).ifPresent(logic -> logic.getTankManager().get(0).setFluid(Fluids.WATER.get(water)));
        });
    }
}
