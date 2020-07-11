/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics.materials;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mods.railcraft.common.blocks.aesthetics.materials.Materials.*;

/**
 * Created by CovertJaguar on 7/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MatTools {
    public static void defineCrusherRecipes(IMaterialBlock source) {
        addRockCrusherRecipe(source, BrickTheme.ABYSSAL, ABYSSAL_POLISHED, ABYSSAL_BRICK, ABYSSAL_COBBLE, ABYSSAL_FITTED);
        addRockCrusherRecipe(source, BrickTheme.BLEACHEDBONE, BLEACHEDBONE_POLISHED, BLEACHEDBONE_BRICK, BLEACHEDBONE_COBBLE, BLEACHEDBONE_FITTED);
        addRockCrusherRecipe(source, BrickTheme.BLOODSTAINED, BLOODSTAINED_POLISHED, BLOODSTAINED_BRICK, BLOODSTAINED_COBBLE, BLOODSTAINED_FITTED);
        addRockCrusherRecipe(source, BrickTheme.FROSTBOUND, FROSTBOUND_BLOCK, FROSTBOUND_BRICK, FROSTBOUND_COBBLE, FROSTBOUND_FITTED);
        addRockCrusherRecipe(source, BrickTheme.INFERNAL, INFERNAL_BLOCK, INFERNAL_BRICK, INFERNAL_COBBLE, INFERNAL_FITTED);
        addRockCrusherRecipe(source, BrickTheme.NETHER, NETHER_BLOCK, NETHER_BRICK, NETHER_COBBLE, NETHER_FITTED);
        addRockCrusherRecipe(source, BrickTheme.QUARRIED, QUARRIED_BLOCK, QUARRIED_BRICK, QUARRIED_COBBLE, QUARRIED_FITTED);
        addRockCrusherRecipe(source, BrickTheme.SANDY, SANDY_BLOCK, SANDY_BRICK, SANDY_COBBLE, SANDY_FITTED);
    }

    private static void addRockCrusherRecipe(IMaterialBlock source, BrickTheme brickTheme, Materials... types) {
        if (!brickTheme.isLoaded())
            return;
        ItemStack output = brickTheme.getStack(1, BrickVariant.COBBLE);
        for (Materials mat : types) {
            if (!mat.isSourceValid())
                continue;
            Crafters.rockCrusher().makeRecipe(source.getIngredient(mat))
                    .name("railcraft:recycle_material")
                    .addOutput(output)
                    .register();
        }
    }

    public static ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        IMaterialBlock block = ((IMaterialBlock) state.getBlock());
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMaterial) {
            return block.getStack(1, ((TileMaterial) tile).getMaterial());
        }
        return block.getStack(1, Materials.getPlaceholder());
    }

    public static Materials getMat(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMaterial) {
            return ((TileMaterial) tile).getMaterial();
        }
        return Materials.getPlaceholder();
    }

    public static List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        ArrayList<ItemStack> items = new ArrayList<>();
        if (tile instanceof TileMaterial)
            items.add(((IMaterialBlock) state.getBlock()).getStack(((TileMaterial) tile).getMaterial()));
        return items;
    }

    public static void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMaterial)
            ((TileMaterial) tile).setMaterial(Materials.from(stack, MATERIAL_KEY));
    }

    public static float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMaterial)
            return ((TileMaterial) tile).getMaterial().getBlockHardness(worldIn, pos);
        return Blocks.STONEBRICK.getBlockHardness(state, worldIn, pos);
    }

    public static float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMaterial)
            return ((TileMaterial) tile).getMaterial().getExplosionResistance(exploder);
        return Blocks.STONEBRICK.getExplosionResistance(world, pos, exploder, explosion);
    }

    public static SoundType getSound(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMaterial)
            return ((TileMaterial) tile).getMaterial().getSound();
        return SoundType.STONE;
    }
}
