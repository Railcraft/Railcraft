package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.charge.ChargeNodeDefinition;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.api.charge.ConnectType;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 *
 */
public final class BlockFluxTransformer extends BlockMultiBlock implements IChargeBlock {

    public static final ChargeNodeDefinition DEFINITION = new ChargeNodeDefinition(ConnectType.BLOCK, 0.5);

    public BlockFluxTransformer() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(50) == 25)
            EffectManager.instance.zapEffectSurface(state, worldIn, pos);
    }

    @Override
    public ChargeNodeDefinition getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return DEFINITION;
    }

    @Override
    public TileMultiBlock<?, ?, ?> createTileEntity(World world, IBlockState state) {
        return new TileFluxTransformer();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public Class<TileFluxTransformer> getTileClass(IBlockState state) {
        return TileFluxTransformer.class;
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 1);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        deregisterNode(worldIn, pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        return ChargeManager.getDimension(worldIn).getGraph(pos).getComparatorOutput();
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 2);
        CraftingPlugin.addRecipe(stack,
                "CGC",
                "GRG",
                "CGC",
                'C', RailcraftItems.PLATE, Metal.COPPER,
                'G', "ingotGold",
                'R', "blockRedstone");
    }
}
