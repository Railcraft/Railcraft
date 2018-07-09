package mods.railcraft.common.blocks.single;

import mods.railcraft.client.render.tesr.TESRChest;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockChestMetals extends BlockChestRailcraft {

    public BlockChestMetals() {
        super(Material.IRON);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
        setSoundType(SoundType.METAL);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileChestMetals.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileChestMetals();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestMetals.class, new TESRChest(RailcraftBlocks.CHEST_METALS));
        Item.getItemFromBlock(this).setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            private final TileChestMetals template = new TileChestMetals();

            @Override
            @SideOnly(Side.CLIENT)
            public void renderByItem(ItemStack p_192838_1_, float partialTicks) {
                TileEntityRendererDispatcher.instance.render(template, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
            }
        });
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "GPG",
                "PAP",
                "GPG",
                'A', new ItemStack(Blocks.ANVIL),
                'P', new ItemStack(Blocks.PISTON),
                'G', "gearSteel");
    }
}
