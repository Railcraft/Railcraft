/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockContainerRailcraft;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.RailcraftSoundTypes;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Tile(TileRitual.class)
public class BlockRitual extends BlockContainerRailcraft<TileRitual> {
    public static final PropertyBool CRACKED = PropertyBool.create("cracked");
    public static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().expandHorizontally(-0.3).raiseCeiling(0.0625F * -9.0).shiftY(0.0625F * 12.0).build();

    public BlockRitual() {
        super(Material.ROCK);
        disableStats();
        setSoundType(RailcraftSoundTypes.NULL);
        setLightLevel(1);
        setDefaultState(blockState.getBaseState().withProperty(CRACKED, false));
    }

    @Override
    public void initializeDefinition() {
        super.initializeDefinition();
        RailcraftRegistry.register(TileRitual.class, "ritual");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @Nullable StateMapperBase getStateMapper() {
        return new StateMap.Builder().ignore(CRACKED).build();
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CRACKED, meta != 0);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CRACKED) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CRACKED);
    }

//    @Override
//    public IIcon getIcon(int side, int meta) {
//        return Blocks.OBSIDIAN.getIcon(side, meta);
//    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

//    @Nullable
//    @Override
//    public ItemStack getStack(int qty, int meta) {
//        return null;
//    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return ItemFirestoneRefined.getItemCharged();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileRitual) {
            TileRitual firestone = (TileRitual) tile;
            Item item = state.getValue(CRACKED) ? RailcraftItems.FIRESTONE_CRACKED.item() : RailcraftItems.FIRESTONE_REFINED.item();
            if (item != null) {
                ItemStack drop = new ItemStack(item, 1, ItemFirestoneRefined.CHARGES - firestone.charge);
                if (firestone.hasCustomName())
                    drop.setStackDisplayName(firestone.getItemName());
                drops.add(drop);
            }
        } else {
            drops.add(ItemFirestoneRefined.getItemEmpty());
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (Game.isHost(world))
            dropBlockAsItem(world, pos, WorldPlugin.getBlockState(world, pos), 0);
        return WorldPlugin.setBlockToAir(world, pos);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
        return true;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

}
