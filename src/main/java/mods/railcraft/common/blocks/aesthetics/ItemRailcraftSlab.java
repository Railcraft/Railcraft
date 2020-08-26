/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created by CovertJaguar on 8/6/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemRailcraftSlab extends ItemBlockRailcraftSubtyped<BlockRailcraftSlab> {
    private final ItemSlab delegate;

    public ItemRailcraftSlab(BlockRailcraftSlab block, BlockRailcraftSlab singleSlab, @Nullable BlockRailcraftSlab doubleSlab) {
        super(block);
        delegate = new ItemSlab(block, singleSlab, Objects.requireNonNull(doubleSlab));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return delegate.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        return delegate.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }

    @Override
    public ItemRailcraftSlab getObject() {
        return this;
    }
}
