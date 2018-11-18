/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Random;

public class BlockForceTrackEmitter extends BlockEntityDelegate implements IChargeBlock, ColorPlugin.IColoredBlock {

    public static final int DEFAULT_SHADE = EnumColor.LIGHT_BLUE.getHexColor();
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final Map<Charge, ChargeSpec> CHARGE_SPECS = ChargeSpec.make(Charge.distribution, ConnectType.BLOCK, 0.1);

    public BlockForceTrackEmitter() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileForceTrackEmitter.class;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileForceTrackEmitter();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_SPECS;
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileForceTrackEmitter) {
            TileForceTrackEmitter emitter = (TileForceTrackEmitter) tile;
            int rcColor = EnumColor.fromDye(color).getHexColor();
            if (rcColor != emitter.getColor()) {
                emitter.setColor(rcColor);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public IBlockColor colorHandler() {
        return (state, worldIn, pos, tintIndex) ->
                WorldPlugin.getTileEntity(worldIn, pos, TileForceTrackEmitter.class)
                        .map(TileForceTrackEmitter::getColor)
                        .orElse(DEFAULT_SHADE);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "PIP",
                "RBR",
                "PIP",
                'P', "plateTin",
                'R', RailcraftItems.CHARGE, ItemCharge.EnumCharge.COIL,
                'I', RailcraftItems.DUST, ItemDust.EnumDust.ENDER,
                'B', "blockDiamond");
    }

    private ItemStack getItem(IBlockAccess world, BlockPos pos) {
        int color = DEFAULT_SHADE;
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileForceTrackEmitter) {
            color = ((TileForceTrackEmitter) tile).getColor();
        }
        return ItemForceTrackEmitter.setColor(new ItemStack(this), color);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getItem(world, pos);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(getItem(world, pos));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(POWERED))
            Charge.effects().throwSparks(stateIn, worldIn, pos, rand, 10);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        deregisterNode(worldIn, pos);
    }
}
