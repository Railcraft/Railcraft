/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logbook;

import mods.railcraft.api.core.items.IActivationBlockingItem;
import mods.railcraft.common.blocks.BlockContainerRailcraft;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockLogbook extends BlockContainerRailcraft {
    public static final PropertyEnum<EnumFacing.Axis> ROTATION = PropertyEnum.create("rotation", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);

    public BlockLogbook() {
        super(Material.WOOD);
        setResistance(6000000.0F);
        setBlockUnbreakable();
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(ROTATION, EnumFacing.Axis.X));
    }

    @Override
    public void initializeDefinintion() {
        RailcraftRegistry.register(TileLogbook.class, "logbook");
        HarvestPlugin.setBlockHarvestLevel("axe", 1, this);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(getStack(1),
                " B ",
                "GCG",
                "WWW",
                'B', Items.WRITABLE_BOOK,
                'C', new ItemStack(Blocks.WOOL, 1, EnumColor.RED.ordinal()),
                'G', "ingotGold",
                'W', "plankWood");
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta != 0 && meta != 2)
            meta = 0;
        return getDefaultState().withProperty(ROTATION, EnumFacing.Axis.values()[meta]);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION).ordinal();
    }

    public EnumFacing.Axis getAxis(IBlockAccess world, BlockPos pos) {
        return getAxis(WorldPlugin.getBlockState(world, pos));
    }

    public EnumFacing.Axis getAxis(IBlockState state) {
        return state.getValue(ROTATION);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing.Axis axis = facing.getAxis();
        if (axis.isVertical()) {
            IBlockState state = WorldPlugin.getBlockState(worldIn, pos.offset(facing.getOpposite()));
            if (state.getBlock() == this) {
                axis = ((BlockLogbook) state.getBlock()).getAxis(state);
            } else {
                axis = placer.getHorizontalFacing().getAxis();
            }
        }
        return getDefaultState().withProperty(ROTATION, axis);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileLogbook)
            ((TileLogbook) tile).onBlockPlacedBy(state, placer, stack);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;
        if (!InvTools.isEmpty(heldItem)) {
            if (heldItem.getItem() instanceof IActivationBlockingItem)
                return false;
            if (TrackTools.isRailItem(heldItem.getItem()))
                return false;
        }
        if (Game.isClient(worldIn))
            return true;
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileLogbook) {
            PacketBuilder.instance().sendLogbookGuiPacket((EntityPlayerMP) player, ((TileLogbook) tile).getLog());
            return true;
        }
        return false;
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileLogbook && ((TileLogbook) tile).isOwner(player.getGameProfile())) {
            float hardness = 50F;
            if (!ForgeHooks.canHarvestBlock(state.getBlock(), player, world, pos)) {
                return player.getDigSpeed(state, pos) / hardness / 100F;
            } else {
                return player.getDigSpeed(state, pos) / hardness / 30F;
            }
        }
        return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileLogbook();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(ROTATION, state.getValue(ROTATION) == EnumFacing.Axis.Z ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
    }
}
