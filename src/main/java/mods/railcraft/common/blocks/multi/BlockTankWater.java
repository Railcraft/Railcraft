package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

/**
 *
 */
public class BlockTankWater extends BlockMultiBlock {

    public BlockTankWater() {
        super(Material.WOOD);
        setHarvestLevel("axe", 0);
    }

    @Override
    public TileMultiBlock<?, ?> createTileEntity(World world, IBlockState state) {
        return new TileTankWater();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankWater.class;
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 6),
                "WWW",
                "ISI",
                "WWW",
                'I', "ingotIron",
                'S', "slimeball",
                'W', "plankWood");
    }
}
