/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.BlockMeta;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;

@BlockMeta.Tile(TileAdminSteamProducer.class)
public class BlockAdminSteamProducer extends BlockEntityDelegate<TileAdminSteamProducer> {

    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockAdminSteamProducer() {
        super(Material.ROCK);
        setBlockUnbreakable();
        setResistance(6000000f);
        disableStats();
        setDefaultState(getDefaultState().withProperty(POWERED, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        // No drops!
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return emptyStack();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }
}
