package mods.railcraft.common.blocks.single;

import mods.railcraft.client.render.tesr.TESRChest;
import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockChestVoid extends BlockChestRailcraft {

    public BlockChestVoid() {
        super(Material.IRON);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileChestVoid.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileChestVoid();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initializeClient() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestVoid.class, new TESRChest(RailcraftBlocks.CHEST_VOID));
        Item.getItemFromBlock(this).setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            private final TileChestVoid template = new TileChestVoid();

            @Override
            @SideOnly(Side.CLIENT)
            public void renderByItem(ItemStack p_192838_1_, float partialTicks) {
                TileEntityRendererDispatcher.instance.render(template, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
       {
            BlockPos start = new BlockPos(pos.getX() - 10 + rand.nextInt(20), pos.getY(), pos.getZ() - 10 + rand.nextInt(20));
            spawnVoidFaceParticles(worldIn, pos);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnVoidFaceParticles(World worldIn, BlockPos pos) {
        Random random = worldIn.rand;
        double pixel = 0.0625D;

        IBlockState state = WorldPlugin.getBlockState(worldIn, pos);

        for (EnumFacing facing : EnumFacing.VALUES) {
            if (!state.shouldSideBeRendered(worldIn, pos, facing)) continue;

            double px = pos.getX();
            double py = pos.getY();
            double pz = pos.getZ();

            if (facing.getAxis() == EnumFacing.Axis.X)
                px += pixel * facing.getXOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                px += random.nextFloat();

            if (facing.getAxis() == EnumFacing.Axis.Y)
                py += pixel * facing.getYOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                py += random.nextFloat();

            if (facing.getAxis() == EnumFacing.Axis.Z)
                pz += pixel * facing.getZOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                pz += random.nextFloat();

            worldIn.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "OOO",
                "OPO",
                "OOO",
                'P', RailcraftItems.DUST.getStack(ItemDust.EnumDust.VOID),
                'O', new ItemStack(Blocks.OBSIDIAN));
    }

}
