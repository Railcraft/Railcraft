/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.BlockContainerRailcraft;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Optionals;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@BlockMeta.Tile(TileForceTrackEmitter.class)
public class BlockForceTrackEmitter extends BlockContainerRailcraft<TileForceTrackEmitter> implements IChargeBlock, ColorPlugin.IColorHandlerBlock {

    public static final EnumColor DEFAULT_COLOR = EnumColor.LIGHT_BLUE;
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return getTileEntity(state, worldIn, pos)
                .map(tile ->
                        state.withProperty(BlockForceTrackEmitter.FACING, tile.facing)
                                .withProperty(BlockForceTrackEmitter.POWERED, tile.powered && tile.state.appearPowered))
                .orElse(state);
    }

    @Override
    public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_SPECS;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (super.onBlockActivated(worldIn, pos, state, player, hand, facing, hitX, hitY, hitZ))
            return true;
        if (player.isSneaking())
            return false;
        ItemStack heldItem = player.getHeldItem(hand);
        if (InvTools.isEmpty(heldItem) || hand == EnumHand.OFF_HAND)
            return false;
        Optional<? extends TileForceTrackEmitter> tile = getTileEntity(state, worldIn, pos);
        if (tile.isPresent()) {
            TileForceTrackEmitter t = tile.get();
            if (Optionals.test(EnumColor.dyeColorOf(heldItem), t::setColor)) {
                if (!player.capabilities.isCreativeMode)
                    player.setHeldItem(hand, InvTools.depleteItem(heldItem));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        return getTileEntity(world, pos)
                .map(tile -> tile.setColor(EnumColor.fromDye(color)))
                .orElse(false);
    }

    @Override
    public ColorPlugin.IColorFunctionBlock colorHandler() {
        return (state, worldIn, pos, tintIndex) ->
                getTileEntity(state, worldIn, pos)
                        .map(TileForceTrackEmitter::getColor).orElse(DEFAULT_COLOR).getHexColor();
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "PIP",
                "RBR",
                "PIP",
                'P', "plateTin",
                'R', RailcraftItems.CHARGE, ItemCharge.EnumCharge.COIL,
                'I', RailcraftItems.DUST, ItemDust.EnumDust.ENDER,
                'B', "blockDiamond");
    }

    private ItemStack getItem(IBlockAccess world, BlockPos pos) {
        return getTileEntity(world, pos)
                .map(TileForceTrackEmitter::getColor)
                .orElse(DEFAULT_COLOR)
                .setItemColor(new ItemStack(this));
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
        getTileEntity(state, worldIn, pos).ifPresent(TileForceTrackEmitter::clearTracks);
    }
}
