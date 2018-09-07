package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.items.ItemDust.EnumDust;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

/**
 *
 */
public final class BlockBoilerTankLow extends BlockBoilerTank {
    @Override
    public TileMultiBlock<?, ?, ?> createTileEntity(World world, IBlockState state) {
        return new TileBoilerTankLow<>();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileBoilerTankLow.class;
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addRecipe(stack,
                "P",
                "P",
                'P', RailcraftItems.PLATE, Metal.IRON);

        BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Item.getItemFromBlock(this)), 2560, RailcraftItems.INGOT.getStack(2, Metal.STEEL), RailcraftItems.DUST.getStack(2, EnumDust.SLAG));
    }
}
