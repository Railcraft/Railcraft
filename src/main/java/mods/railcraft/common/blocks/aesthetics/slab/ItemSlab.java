/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.slab;

import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemSlab extends ItemBlock {

    public ItemSlab(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("railcraft.slab");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return BlockRailcraftSlab.getTag(EnumBlockMaterial.fromOrdinal(stack.getItemDamage()));
    }

    /**
     * Callback for item usage. If the item does something special on right
     * clicking, he will have one of those. Return True if something happen and
     * false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (stack.stackSize == 0) {
            return false;
        }

        if (!player.canPlayerEdit(x, y, z, side, stack)) {
            return false;
        } else {
            if (isSingleSlab(world, x, y, z, side)) {
                tryAddSlab(world, x, y, z, stack);
                return true;
            }
            if (isSingleSlabShifted(world, x, y, z, side)) {
                ForgeDirection s = ForgeDirection.getOrientation(side);
                x = MiscTools.getXOnSide(x, s);
                y = MiscTools.getYOnSide(y, s);
                z = MiscTools.getZOnSide(z, s);
                tryAddSlab(world, x, y, z, stack);
                return true;
            }

            return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);

        }
    }

    private boolean isSingleSlab(World world, int x, int y, int z, int side) {
        if (world.getBlock(x, y, z) == field_150939_a) {
            TileSlab slab = BlockRailcraftSlab.getSlabTile(world, x, y, z);
            if (slab != null) {
                boolean up = slab.isTopSlab();
                if ((side == 1 && !up || side == 0 && up) && !slab.isDoubleSlab()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSingleSlabShifted(World world, int x, int y, int z, int side) {
        ForgeDirection s = ForgeDirection.getOrientation(side);
        x = MiscTools.getXOnSide(x, s);
        y = MiscTools.getYOnSide(y, s);
        z = MiscTools.getZOnSide(z, s);

        if (world.getBlock(x, y, z) == field_150939_a) {
            TileSlab slab = BlockRailcraftSlab.getSlabTile(world, x, y, z);
            if (slab != null && !slab.isDoubleSlab()) {
                return true;
            }
        }
        return false;
    }

    private void tryAddSlab(World world, int x, int y, int z, ItemStack stack) {
        TileSlab slab = BlockRailcraftSlab.getSlabTile(world, x, y, z);
        if (slab != null) {
            Block block = BlockRailcraftSlab.getBlock();
            if (world.checkNoEntityCollision(block.getCollisionBoundingBoxFromPool(world, x, y, z)) && slab.addSlab(EnumBlockMaterial.fromOrdinal(stack.getItemDamage()))) {
                world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
            }
        }
    }

    @Override
    public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer par6EntityPlayer, ItemStack stack) {
        if (isSingleSlab(world, x, y, z, side)) {
            return true;
        }
        if (isSingleSlabShifted(world, x, y, z, side)) {
            return true;
        }
        return super.func_150936_a(world, x, y, z, side, par6EntityPlayer, stack);
    }

    /**
     * Called to actually place the block, after the location is determined and
     * all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be
     * changed inside the method.
     * @param player The player who is placing the block. Can be null if the
     * block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        Block block = field_150939_a;
        if (!world.checkNoEntityCollision(block.getCollisionBoundingBoxFromPool(world, x, y, z))) {
            return false;
        }

//        boolean shifted = world.getBlockId(x, y, z) != blockID;
//        ForgeDirection s = ForgeDirection.getOrientation(side).getOpposite();
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

        if (!world.setBlock(x, y, z, field_150939_a)) {
            return false;
        }

        if (world.getBlock(x, y, z) == field_150939_a) {
            TileSlab slab = BlockRailcraftSlab.getSlabTile(world, x, y, z);
            if (slab != null) {
                if (side != 0 && (side == 1 || (double) hitY <= 0.5D)) {
                    slab.setBottomSlab(EnumBlockMaterial.fromOrdinal(stack.getItemDamage()));
                } else {
                    slab.setTopSlab(EnumBlockMaterial.fromOrdinal(stack.getItemDamage()));
                }
            }
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }
}
