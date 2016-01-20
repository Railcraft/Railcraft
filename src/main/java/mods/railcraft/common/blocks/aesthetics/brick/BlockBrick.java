/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockBrick extends Block {

    private final EnumBrick theme;

    public BlockBrick(EnumBrick theme) {
        super(Material.rock);
        this.theme = theme;
        setResistance(15);
        setHardness(5);
        setStepSound(Block.soundTypeStone);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return meta;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (BrickVariant variant : BrickVariant.VALUES) {
            list.add(theme.get(variant, 1));
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }
}
