/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.slab;

import mods.railcraft.common.blocks.aesthetics.BlockMaterial;
import mods.railcraft.common.blocks.aesthetics.MaterialRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemSlab extends ItemBlock {
    public static final String MATERIAL_KEY = "material";

    public ItemSlab(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(false);
        setUnlocalizedName("railcraft.slab");
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return BlockRailcraftSlab.getTag(getMat(stack));
    }

    /**
     * Callback for item usage. If the item does something special on right
     * clicking, he will have one of those. Return True if something happen and
     * false if it don't. This is for ITEMS, not BLOCKS
     */
    @Nonnull
    @Override
    public EnumActionResult onItemUse(ItemStack stack, @Nonnull EntityPlayer playerIn, World worldIn, @Nonnull BlockPos pos, EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (stack.stackSize == 0) {
            return EnumActionResult.PASS;
        }

        if (!playerIn.canPlayerEdit(pos, facing, stack)) {
            return EnumActionResult.PASS;
        } else {
            TileSlab tileSlab = BlockRailcraftSlab.getSlabTile(worldIn, pos);
            if (canAddSlab(tileSlab, facing)) {
                tryAddSlab(tileSlab, worldIn, pos, stack);
                return EnumActionResult.SUCCESS;
            }
            tileSlab = BlockRailcraftSlab.getSlabTile(worldIn, pos.offset(facing));
            if (isSingleSlab(tileSlab)) {
                tryAddSlab(tileSlab, worldIn, pos.offset(facing), stack);
                return EnumActionResult.SUCCESS;
            }

            return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);

        }
    }

    private BlockMaterial getMat(ItemStack stack) {
        return MaterialRegistry.from(stack, MATERIAL_KEY);
    }

    private boolean canAddSlab(TileSlab tileSlab, EnumFacing side) {
        if (tileSlab != null) {
            boolean up = tileSlab.isTopSlab();
            return (side == UP && !up || side == DOWN && up) && !tileSlab.isDoubleSlab();
        }
        return false;
    }

    private boolean isSingleSlab(TileSlab tileSlab) {
        return tileSlab != null && !tileSlab.isDoubleSlab();
    }

    private void tryAddSlab(TileSlab slab, World world, BlockPos pos, ItemStack stack) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        AxisAlignedBB box = state.getCollisionBoundingBox(world, pos);
        if ((box == null || world.checkNoEntityCollision(box)) && slab.addSlab(getMat(stack))) {
            SoundHelper.playBlockSound(world, pos,
                    block.getSoundType().getPlaceSound(),
                    SoundCategory.BLOCKS,
                    (block.getSoundType().getVolume() + 1.0F) / 2.0F,
                    block.getSoundType().getPitch() * 0.8F, state);
            --stack.stackSize;
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean canPlaceBlockOnSide(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, EntityPlayer player, @Nonnull ItemStack stack) {
        TileSlab tileSlab = BlockRailcraftSlab.getSlabTile(world, pos);
        if (canAddSlab(tileSlab, side)) {
            return true;
        }
        tileSlab = BlockRailcraftSlab.getSlabTile(world, pos.offset(side));
        if (isSingleSlab(tileSlab)) {
            return true;
        }
        return super.canPlaceBlockOnSide(world, pos, side, player, stack);
    }

    /**
     * Called to actually place the block, after the location is determined and
     * all permission checks have been made.
     *
     * @param stack  The item stack that was used to place the block. This can be
     *               changed inside the method.
     * @param player The player who is placing the block. Can be null if the
     *               block is not being placed by a player.
     * @param side   The side the player (or machine) right-clicked on.
     */
    @Override
    public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {
        AxisAlignedBB box = newState.getCollisionBoundingBox(world, pos);
        if (box != null && !world.checkNoEntityCollision(box)) {
            return false;
        }

//        boolean shifted = world.getBlockId(x, y, z) != blockID;
//        EnumFacing s = EnumFacing.VALUES[side].getOpposite();
//        int cx = shifted ? MiscTools.getXOnSide(x, s) : x;
//        int cy = shifted ? MiscTools.getYOnSide(y, s) : y;
//        int cz = shifted ? MiscTools.getZOnSide(z, s) : z;
//        if (world.getBlockId(cx, cy, cz) == blockID) {
//            int meta = world.getBlockMetadata(cx, cy, cz);
//            if (!shifted && meta != DOUBLE_SLAB_META || meta == UP_SLAB_META && side == 0 || meta == DOWN_SLAB_META && side == 1) {
//                world.setBlockMetadataWithNotify(cx, cy, cz, DOUBLE_SLAB_META, 3);
//                world.playSoundEffect((double) ((float) cx + 0.5F), (double) ((float) cy + 0.5F), (double) ((float) cz + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
//                --stack.stackSize;
//                return false;
//            }
//        }

        if (!world.setBlockState(pos, newState)) {
            return false;
        }

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == block) {
            TileSlab slab = BlockRailcraftSlab.getSlabTile(world, pos);
            if (slab != null) {
                if (side != DOWN && (side == UP || (double) hitY <= 0.5D)) {
                    slab.setBottomSlab(getMat(stack));
                } else {
                    slab.setTopSlab(getMat(stack));
                }
            }
            setTileEntityNBT(world, player, pos, stack);
            block.onBlockPlacedBy(world, pos, state, player, stack);
        }

        return true;
    }
}
