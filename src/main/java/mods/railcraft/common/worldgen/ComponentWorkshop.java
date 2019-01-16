/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import mods.railcraft.api.tracks.*;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.equipment.EquipmentVariant;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.outfitted.BlockTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.blocks.tracks.outfitted.TrackTileFactory;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.*;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

//TODO templates
public class ComponentWorkshop extends StructureVillagePieces.Village {

    @ObjectHolder("minecraft:smith")
    private static VillagerRegistry.VillagerProfession smith;
    private int averageGroundLevel = -1;
    private boolean hasMadeChest;
    private final Random random;

    public ComponentWorkshop() {
        random = new Random();
    }

    public ComponentWorkshop(Start villagePiece, int type, Random rand, StructureBoundingBox sbb, EnumFacing facing) {
        super(villagePiece, type);
        this.boundingBox = sbb;
        this.random = rand;
        setCoordBaseMode(facing);
    }

    public static @Nullable ComponentWorkshop buildComponent(Start villagePiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
        StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 11, 6, 11, facing);
        return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces, box) == null ? new ComponentWorkshop(villagePiece, type, random, box, facing) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        if (averageGroundLevel < 0) {
            this.averageGroundLevel = getAverageGroundLevel(world, sbb);

            if (averageGroundLevel < 0)
                return true;

            boundingBox.offset(0, averageGroundLevel - boundingBox.maxY + 4, 0);
        }

        IBlockState torch = Blocks.TORCH.getDefaultState();
        IBlockState blockBrick = Blocks.BRICK_BLOCK.getDefaultState();
        IBlockState glassPane = Blocks.GLASS_PANE.getDefaultState();
        IBlockState stainedGlass = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.CYAN);

        IBlockState stoneBrick = Blocks.STONEBRICK.getDefaultState();
        IBlockState mossyStoneBrick = stoneBrick.withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
        IBlockState crackedStoneBrick = stoneBrick.withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);

        IBlockState roofEast = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
        IBlockState roofWest = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
        IBlockState roofNorth = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
        IBlockState roofSouth = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);

        //Clear area
        fillWithAir(world, sbb, 1, 1, 2, 3, 3, 4);
        fillWithAir(world, sbb, 5, 1, 0, 9, 4, 10);
        // floor
//        fillWithBlocks(world, sbb, 0, 0, 0, 11, 0, 11, Blocks.GRAVEL, Blocks.GRAVEL, false);
        fillWithBlocks(world, sbb, 4, 0, 0, 10, 0, 10, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
        fillWithBlocks(world, sbb, 0, 0, 1, 3, 0, 5, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);

        // track
        fillWithRandomizedBlocks(world, sbb, 7, 1, 2, 7, 1, 8, false, random, new BlockSelector() {
            @Override
            public void selectBlocks(Random rand, int x, int y, int z, boolean wall) {
                float f = rand.nextFloat();

                this.blockstate = TrackToolsAPI.makeTrackState((BlockRailBase) Blocks.RAIL, EnumRailDirection.NORTH_SOUTH);

                if (f < 0.1F && RailcraftBlocks.TRACK_FLEX_STRAP_IRON.isLoaded()) {
                    this.blockstate = TrackToolsAPI.makeTrackState((BlockRailBase) RailcraftBlocks.TRACK_FLEX_STRAP_IRON.block(), EnumRailDirection.NORTH_SOUTH);
                } else if (f < 0.3F && RailcraftBlocks.TRACK_FLEX_ABANDONED.isLoaded()) {
                    this.blockstate = TrackToolsAPI.makeTrackState((BlockRailBase) RailcraftBlocks.TRACK_FLEX_ABANDONED.block(), EnumRailDirection.NORTH_SOUTH);
                }
            }
        });
        placeOutfittedTrack(TrackKits.BUFFER_STOP, world, 7, 1, 1, sbb, EnumRailDirection.NORTH_SOUTH, false);
        placeOutfittedTrack(TrackKits.BUFFER_STOP, world, 7, 1, 9, sbb, EnumRailDirection.NORTH_SOUTH, true);

        // hall walls
        fillWithBlocks(world, sbb, 4, 0, 0, 4, 3, 10, blockBrick, blockBrick, false);
        fillWithBlocks(world, sbb, 10, 0, 0, 10, 3, 10, blockBrick, blockBrick, false);

        fillWithBlocks(world, sbb, 5, 0, 0, 5, 4, 0, blockBrick, blockBrick, false);
        fillWithBlocks(world, sbb, 9, 0, 0, 9, 4, 0, blockBrick, blockBrick, false);

        fillWithBlocks(world, sbb, 5, 0, 10, 5, 4, 10, blockBrick, blockBrick, false);
        fillWithBlocks(world, sbb, 9, 0, 10, 9, 4, 10, blockBrick, blockBrick, false);

        // hall molding
        IBlockState roofSupportWest = Blocks.BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        IBlockState roofSupportEast = Blocks.BRICK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
        fillWithBlocks(world, sbb, 5, 4, 1, 5, 4, 9, roofSupportWest, roofSupportWest, false);
        fillWithBlocks(world, sbb, 9, 4, 1, 9, 4, 9, roofSupportEast, roofSupportEast, false);

        // hall door
        setBlockState(world, roofSupportWest, 6, 4, 0, sbb);
        setBlockState(world, roofSupportEast, 8, 4, 0, sbb);
        setBlockState(world, roofSupportWest, 6, 4, 10, sbb);
        setBlockState(world, roofSupportEast, 8, 4, 10, sbb);

        // hall windows
        fillWithBlocks(world, sbb, 10, 2, 2, 10, 2, 3, glassPane, glassPane, false);
        fillWithBlocks(world, sbb, 10, 2, 7, 10, 2, 8, glassPane, glassPane, false);
        fillWithBlocks(world, sbb, 4, 2, 7, 4, 2, 8, glassPane, glassPane, false);

        // hall roof slope
        fillWithBlocks(world, sbb, 4, 4, 0, 4, 4, 10, roofEast, roofEast, false);
        fillWithBlocks(world, sbb, 5, 5, 0, 5, 5, 10, roofEast, roofEast, false);

        fillWithBlocks(world, sbb, 10, 4, 0, 10, 4, 10, roofWest, roofWest, false);
        fillWithBlocks(world, sbb, 9, 5, 0, 9, 5, 10, roofWest, roofWest, false);

        // hall roof
        BlockSelector roofSelector = new BlockSelector() {
            @Override
            public void selectBlocks(Random rand, int x, int y, int z, boolean boundary) {
                float f = rand.nextFloat();

                if (f < 0.2F) {
                    this.blockstate = crackedStoneBrick;
                } else if (f < 0.5F) {
                    this.blockstate = mossyStoneBrick;
                } else {
                    this.blockstate = stoneBrick;
                }
            }
        };

        fillWithRandomizedBlocks(world, sbb, 6, 5, 0, 8, 5, 10, false, random, roofSelector);
        setBlockState(world, stainedGlass, 7, 5, 2, sbb);
        setBlockState(world, stainedGlass, 7, 5, 5, sbb);
        setBlockState(world, stainedGlass, 7, 5, 8, sbb);

        PropertyDirection facing = BlockTorch.FACING;
        // hall torches
//        int meta = getMetadataWithOffset(torch, 4);
        torch = torch.withProperty(facing, EnumFacing.WEST);
        setBlockState(world, torch, 9, 3, 1, sbb);
        setBlockState(world, torch, 9, 3, 5, sbb);
        setBlockState(world, torch, 9, 3, 9, sbb);

//        meta = getMetadataWithOffset(torch, 3);
        torch = torch.withProperty(facing, EnumFacing.EAST);
        setBlockState(world, torch, 5, 3, 1, sbb);
        setBlockState(world, torch, 5, 3, 5, sbb);
        setBlockState(world, torch, 5, 3, 9, sbb);

        // hut walls
        fillWithBlocks(world, sbb, 0, 0, 1, 0, 3, 5, blockBrick, blockBrick, false);
        fillWithBlocks(world, sbb, 1, 0, 1, 3, 3, 1, blockBrick, blockBrick, false);
        fillWithBlocks(world, sbb, 1, 0, 5, 3, 3, 5, blockBrick, blockBrick, false);

        // hut roof
        fillWithRandomizedBlocks(world, sbb, 1, 4, 2, 4, 4, 4, false, random, roofSelector);
        fillWithBlocks(world, sbb, 0, 4, 1, 0, 4, 5, roofEast, roofEast, false);
        fillWithBlocks(world, sbb, 1, 4, 1, 3, 4, 1, roofNorth, roofNorth, false);
        fillWithBlocks(world, sbb, 1, 4, 5, 3, 4, 5, roofSouth, roofSouth, false);

        // hut door
        fillWithAir(world, sbb, 4, 1, 3, 4, 2, 3);
        fillWithAir(world, sbb, 4, 1, 3, 4, 2, 3);
        setBlockState(world, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 4, 0, 3, sbb);
        createVillageDoor(world, boundingBox, random, 4, 1, 3, EnumFacing.NORTH);

        // hut windows
        fillWithBlocks(world, sbb, 2, 2, 1, 2, 2, 1, glassPane, glassPane, false);
        fillWithBlocks(world, sbb, 2, 2, 5, 2, 2, 5, glassPane, glassPane, false);
        fillWithBlocks(world, sbb, 0, 2, 3, 0, 2, 3, glassPane, glassPane, false);

        // hut torches
        torch = torch.withProperty(facing, EnumFacing.NORTH);
        setBlockState(world, torch, 2, 3, 2, sbb);
        torch = torch.withProperty(facing, EnumFacing.SOUTH);
        setBlockState(world, torch, 2, 3, 4, sbb);

        // machines
        if (EquipmentVariant.ROLLING_MACHINE_MANUAL.isAvailable()) {
            setBlockState(world, EquipmentVariant.ROLLING_MACHINE_MANUAL.getDefaultState(), 9, 1, 5, sbb);
        }

        // foundation
        for (int k = 0; k < 11; ++k) {
            for (int l = 4; l < 11; ++l) {
                clearCurrentPositionBlocksUpwards(world, l, 6, k, sbb);
                replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), l, -1, k, sbb);
            }
        }
        for (int k = 1; k < 6; ++k) {
            for (int l = 0; l < 4; ++l) {
                clearCurrentPositionBlocksUpwards(world, l, 6, k, sbb);
                replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), l, -1, k, sbb);
            }
        }

        generateChest(world, sbb, random, 1, 1, 3, LootPlugin.CHESTS_VILLAGE_WORKSHOP);

//        generateChest(world, sbb, random, 1, 1, 6, LootTableList.CHESTS_VILLAGE_BLACKSMITH);

//        generateChest(world, sbb, random, 1, 1, 8, LootTableList.CHESTS_ABANDONED_MINESHAFT);
//
//        generateChest(world, sbb, random, 1, 1, 10, LootTableList.CHESTS_STRONGHOLD_CORRIDOR);

        spawnVillagers(world, sbb, 3, 1, 3, 2);
        return true;
    }

    @Override
    protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof) {
        // leave null pointer exceptions on different lines
        return ModuleWorld.villagerTrackman == null ? checkNotNull(smith) : count == 0 || random.nextBoolean()
                ? ModuleWorld.villagerTrackman : checkNotNull(smith);
    }

    private BlockPos getPosWithOffset(int x, int y, int z) {
        return new BlockPos(getXWithOffset(x, z), getYWithOffset(y), getZWithOffset(x, z));
    }

    private void placeOutfittedTrack(TrackKits track, World world, int x, int y, int z, StructureBoundingBox sbb, EnumRailDirection trackShape, boolean southEnd) {
        BlockTrackOutfitted blockTrack = (BlockTrackOutfitted) RailcraftBlocks.TRACK_OUTFITTED.block();
        if (blockTrack == null)
            return;

        TrackKit trackKit;
        if (track.isEnabled()) {
            trackKit = track.getTrackKit();
        } else
            trackKit = TrackRegistry.getMissingTrackKit();

        BlockPos pos = getPosWithOffset(x, y, z);

        if (!sbb.isVecInside(pos))
            return;

        float f = random.nextFloat();

        TrackType type = TrackRegistry.TRACK_TYPE.getFallback();

        if (f < 0.1F && RailcraftBlocks.TRACK_FLEX_STRAP_IRON.isLoaded()) {
            type = TrackTypes.STRAP_IRON.getTrackType();
        } else if (f < 0.3F && RailcraftBlocks.TRACK_FLEX_ABANDONED.isLoaded()) {
            type = TrackTypes.ABANDONED.getTrackType();
        }

        setBlockState(world, TrackToolsAPI.makeTrackState(blockTrack, trackShape).withProperty(BlockTrackOutfitted.TICKING, trackKit.requiresTicks()), x, y, z, sbb);
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileTrackOutfitted) {
            TrackTileFactory.initTrackTile((TileTrackOutfitted) tile, type, trackKit);
            ITrackKitInstance kit = ((TileTrackOutfitted) tile).getTrackKitInstance();
            if (kit instanceof ITrackKitReversible) {
                BlockPos tracks = getPosWithOffset(x, y, southEnd ? z - 1 : z + 1);
                BlockPos self = getPosWithOffset(x, y, z);
                ((ITrackKitReversible) kit).setReversed(tracks.south().equals(self) || tracks.west().equals(self));
            }
        }
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound nbt) {
        super.writeStructureToNBT(nbt);
        nbt.setBoolean("Chest", hasMadeChest);
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound nbt, TemplateManager manager) {
        super.readStructureFromNBT(nbt, manager);
        hasMadeChest = nbt.getBoolean("Chest");
    }

}
