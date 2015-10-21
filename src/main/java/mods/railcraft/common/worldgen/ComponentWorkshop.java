/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import mods.railcraft.api.tracks.ITrackReversable;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.EnumTrackMeta;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class ComponentWorkshop extends StructureVillagePieces.Village {

    private int averageGroundLevel = -1;
    private boolean hasMadeChest;

    public ComponentWorkshop() {
    }

    public ComponentWorkshop(Start villagePiece, int par2, Random par3Random, StructureBoundingBox sbb, int coordBaseMode) {
        super();
        this.coordBaseMode = coordBaseMode;
        this.boundingBox = sbb;

    }

    public static ComponentWorkshop buildComponent(Start villagePiece, List pieces, Random random, int x, int y, int z, int coordBaseMode, int p5) {
        StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 11, 6, 11, coordBaseMode);
        return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces, box) == null ? new ComponentWorkshop(villagePiece, p5, random, box, coordBaseMode) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.averageGroundLevel < 0)
                return true;

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
        }
        int x = this.boundingBox.minX;
        int y = this.boundingBox.minY;
        int z = this.boundingBox.minZ;

        //Clear area
        fillWithBlocks(world, sbb, 1, 1, 2, 3, 3, 4, Blocks.air, Blocks.air, false);
        fillWithBlocks(world, sbb, 5, 1, 0, 9, 4, 10, Blocks.air, Blocks.air, false);
        // floor
//        fillWithBlocks(world, sbb, 0, 0, 0, 11, 0, 11, Blocks.gravel, Blocks.gravel, false);
        fillWithBlocks(world, sbb, 4, 0, 0, 10, 0, 10, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        fillWithBlocks(world, sbb, 0, 0, 1, 3, 0, 5, Blocks.double_stone_slab, Blocks.double_stone_slab, false);

        // track
        fillWithBlocks(world, sbb, 7, 1, 2, 7, 1, 8, Blocks.rail, Blocks.rail, false);
        placeTrack(EnumTrack.BUFFER_STOP, world, 7, 1, 1, sbb, EnumTrackMeta.NORTH_SOUTH.ordinal(), false);
        placeTrack(EnumTrack.BUFFER_STOP, world, 7, 1, 9, sbb, EnumTrackMeta.NORTH_SOUTH.ordinal(), true);

        // hall walls
        fillWithBlocks(world, sbb, 4, 0, 0, 4, 3, 10, Blocks.brick_block, Blocks.brick_block, false);
        fillWithBlocks(world, sbb, 10, 0, 0, 10, 3, 10, Blocks.brick_block, Blocks.brick_block, false);

        fillWithBlocks(world, sbb, 5, 0, 0, 5, 4, 0, Blocks.brick_block, Blocks.brick_block, false);
        fillWithBlocks(world, sbb, 9, 0, 0, 9, 4, 0, Blocks.brick_block, Blocks.brick_block, false);

        fillWithBlocks(world, sbb, 5, 0, 10, 5, 4, 10, Blocks.brick_block, Blocks.brick_block, false);
        fillWithBlocks(world, sbb, 9, 0, 10, 9, 4, 10, Blocks.brick_block, Blocks.brick_block, false);

        int metaRoofSupportA = getMetadataWithOffset(Blocks.stone_stairs, 1) | 0x4;
        int metaRoofSupportB = getMetadataWithOffset(Blocks.stone_stairs, 0) | 0x4;
        for (int rz = 1; rz <= 9; rz++) {
            placeBlockAtCurrentPosition(world, Blocks.brick_stairs, metaRoofSupportA, 5, 4, rz, sbb);
            placeBlockAtCurrentPosition(world, Blocks.brick_stairs, metaRoofSupportB, 9, 4, rz, sbb);
        }
        placeBlockAtCurrentPosition(world, Blocks.brick_stairs, metaRoofSupportA, 6, 4, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.brick_stairs, metaRoofSupportB, 8, 4, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.brick_stairs, metaRoofSupportA, 6, 4, 10, sbb);
        placeBlockAtCurrentPosition(world, Blocks.brick_stairs, metaRoofSupportB, 8, 4, 10, sbb);

        // hall windows
        fillWithBlocks(world, sbb, 10, 2, 2, 10, 2, 3, Blocks.glass_pane, Blocks.glass_pane, false);
        fillWithBlocks(world, sbb, 10, 2, 7, 10, 2, 8, Blocks.glass_pane, Blocks.glass_pane, false);
        fillWithBlocks(world, sbb, 4, 2, 7, 4, 2, 8, Blocks.glass_pane, Blocks.glass_pane, false);

        // hall roof
        int metaHallRoofA = getMetadataWithOffset(Blocks.stone_stairs, 0);
        int metaHallRoofB = getMetadataWithOffset(Blocks.stone_stairs, 1);
        for (int rz = 0; rz <= 10; rz++) {
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metaHallRoofA, 4, 4, rz, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metaHallRoofB, 10, 4, rz, sbb);

            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metaHallRoofA, 5, 5, rz, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metaHallRoofB, 9, 5, rz, sbb);
        }

        fillWithBlocks(world, sbb, 6, 5, 0, 8, 5, 10, Blocks.stonebrick, Blocks.stonebrick, false);
        fillWithMetadataBlocks(world, sbb, 6, 5, 1, 8, 5, 9, Blocks.stonebrick, 2, Blocks.stonebrick, 2, false);
        fillWithMetadataBlocks(world, sbb, 7, 5, 2, 7, 5, 8, Blocks.stonebrick, 1, Blocks.stonebrick, 1, false);
        placeBlockAtCurrentPosition(world, Blocks.stained_glass, 9, 7, 5, 2, sbb);
        placeBlockAtCurrentPosition(world, Blocks.stained_glass, 9, 7, 5, 5, sbb);
        placeBlockAtCurrentPosition(world, Blocks.stained_glass, 9, 7, 5, 8, sbb);

        // hall torches
//        int meta = getMetadataWithOffset(Blocks.torch, 4);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 9, 3, 1, sbb);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 9, 3, 5, sbb);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 9, 3, 9, sbb);

//        meta = getMetadataWithOffset(Blocks.torch, 3);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 1, sbb);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 5, sbb);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 9, sbb);

        // hut walls
        fillWithBlocks(world, sbb, 0, 0, 1, 0, 3, 5, Blocks.brick_block, Blocks.brick_block, false);
        fillWithBlocks(world, sbb, 1, 0, 1, 3, 3, 1, Blocks.brick_block, Blocks.brick_block, false);
        fillWithBlocks(world, sbb, 1, 0, 5, 3, 3, 5, Blocks.brick_block, Blocks.brick_block, false);

        // hut roof
        fillWithBlocks(world, sbb, 1, 4, 2, 4, 4, 4, Blocks.stonebrick, Blocks.stonebrick, false);
        int metaHutRoofA = getMetadataWithOffset(Blocks.stone_stairs, 0);
        for (int rz = 1; rz <= 5; rz++) {
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metaHutRoofA, 0, 4, rz, sbb);
        }
        int metaHutRoofB = getMetadataWithOffset(Blocks.stone_stairs, 3);
        int metaHutRoofC = getMetadataWithOffset(Blocks.stone_stairs, 2);
        for (int rx = 1; rx <= 3; rx++) {
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metaHutRoofB, rx, 4, 1, sbb);
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metaHutRoofC, rx, 4, 5, sbb);
        }

        // hut door
        fillWithBlocks(world, sbb, 4, 1, 3, 4, 2, 3, Blocks.air, Blocks.air, false);
        fillWithBlocks(world, sbb, 4, 1, 3, 4, 2, 3, Blocks.air, Blocks.air, false);
        placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, 4, 0, 3, sbb);
        placeDoorAtCurrentPosition(world, boundingBox, random, 4, 1, 3, 2);

        // hut windows
        fillWithBlocks(world, sbb, 2, 2, 1, 2, 2, 1, Blocks.glass_pane, Blocks.glass_pane, false);
        fillWithBlocks(world, sbb, 2, 2, 5, 2, 2, 5, Blocks.glass_pane, Blocks.glass_pane, false);
        fillWithBlocks(world, sbb, 0, 2, 3, 0, 2, 3, Blocks.glass_pane, Blocks.glass_pane, false);

        // hut torches
//        meta = getMetadataWithOffset(Blocks.torch, 1);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 2, sbb);
//        meta = getMetadataWithOffset(Blocks.torch, 2);
        placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 4, sbb);

        // machines
        if (EnumMachineAlpha.ROLLING_MACHINE.isAvaliable()) {
            placeBlockAtCurrentPosition(world, RailcraftBlocks.getBlockMachineAlpha(), EnumMachineAlpha.ROLLING_MACHINE.ordinal(), 9, 1, 5, sbb);
            if (EnumMachineBeta.ENGINE_STEAM_HOBBY.isAvaliable() && RailcraftConfig.machinesRequirePower())
                placeEngine(world, 9, 1, 6, sbb);
        }


        // foundation
        for (int k = 0; k < 11; ++k) {
            for (int l = 4; l < 11; ++l) {
                this.clearCurrentPositionBlocksUpwards(world, l, 6, k, sbb);
                this.func_151554_b(world, Blocks.cobblestone, 0, l, -1, k, sbb);
            }
        }
        for (int k = 1; k < 6; ++k) {
            for (int l = 0; l < 4; ++l) {
                this.clearCurrentPositionBlocksUpwards(world, l, 6, k, sbb);
                this.func_151554_b(world, Blocks.cobblestone, 0, l, -1, k, sbb);
            }
        }

        placeChest(world, 9, 1, 4, 3, random, sbb);

        spawnVillagers(world, sbb, 0, 0, 0, 2);
        return true;
    }

    @Override
    protected int getVillagerType(int par1) {
        return 456;
    }

    private void placeTrack(EnumTrack track, World world, int x, int y, int z, StructureBoundingBox sbb, int meta, boolean reversed) {
        if (!track.isEnabled()) {
            placeBlockAtCurrentPosition(world, Blocks.rail, 0, x, y, z, sbb);
            return;
        }
        int xx = this.getXWithOffset(x, z);
        int yy = this.getYWithOffset(y);
        int zz = this.getZWithOffset(x, z);

        if (!sbb.isVecInside(xx, yy, zz))
            return;

//        System.out.println("coordBaseMode = " + coordBaseMode);

        TileTrack tile = TrackTools.placeTrack(track.getTrackSpec(), world, xx, yy, zz, meta);
        boolean r = false;
        switch (this.coordBaseMode) {
            case 0: // checked
            case 1: // checked
                r = false;
                break;
            case 2: // checked
            case 3:
                r = true;
                break;
        }
        ((ITrackReversable) tile.getTrackInstance()).setReversed(r != reversed);
    }

    private void placeEngine(World world, int x, int y, int z, StructureBoundingBox sbb) {
        int xx = this.getXWithOffset(x, z);
        int yy = this.getYWithOffset(y);
        int zz = this.getZWithOffset(x, z);

        if (!sbb.isVecInside(xx, yy, zz))
            return;

        WorldPlugin.setBlock(world, xx, yy, zz, RailcraftBlocks.getBlockMachineBeta(), EnumMachineBeta.ENGINE_STEAM_HOBBY.ordinal());
        TileEntity tile = WorldPlugin.getBlockTile(world, xx, yy, zz);
        if (tile instanceof TileEngineSteamHobby) {
            TileEngineSteamHobby engine = (TileEngineSteamHobby) tile;
            engine.switchOrientation();
            engine.fill(ForgeDirection.UP, Fluids.WATER.getB(4), true);
//            engine.setInventorySlotContents(TileEngineSteamHobby.SLOT_FUEL, new ItemStack(Items.coal, 16));
        }
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setBoolean("Chest", hasMadeChest);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        hasMadeChest = nbt.getBoolean("Chest");
    }

    private void placeChest(World world, int x, int y, int z, int meta, Random rand, StructureBoundingBox sbb) {
        int xx = this.getXWithOffset(x, z);
        int yy = this.getYWithOffset(y);
        int zz = this.getZWithOffset(x, z);

        if (!hasMadeChest && sbb.isVecInside(xx, yy, zz)) {
            hasMadeChest = true;
            if (world.getBlock(xx, yy, zz) != Blocks.chest) {
                world.setBlock(xx, yy, zz, Blocks.chest, getMetadataWithOffset(Blocks.chest, meta), 2);
                TileEntityChest chest = (TileEntityChest) world.getTileEntity(xx, yy, zz);

                if (chest != null)
                    WeightedRandomChestContent.generateChestContents(rand, ChestGenHooks.getItems(LootPlugin.WORKSHOP, rand), chest, ChestGenHooks.getCount(LootPlugin.WORKSHOP, rand));
            }
        }
    }

}
