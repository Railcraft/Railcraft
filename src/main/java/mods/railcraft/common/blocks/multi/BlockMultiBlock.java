package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.BlockContainerRailcraft;
import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.ISmartBlock;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 *
 */
public abstract class BlockMultiBlock extends BlockEntityDelegate implements ISmartBlock {

    protected BlockMultiBlock(Material materialIn) {
        super(materialIn);
    }

    protected BlockMultiBlock(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public abstract TileMultiBlock createTileEntity(World world, IBlockState state);

}
