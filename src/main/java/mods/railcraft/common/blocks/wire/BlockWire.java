/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.wire;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.EnumTools;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Created by CovertJaguar on 7/22/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockWire extends RailcraftBlockContainer implements IPostConnection {

    public static final PropertyEnum<Addon> ADDON = PropertyEnum.create("addon", Addon.class);
    public static final PropertyEnum<EnumFacing> CONNECTION = PropertyEnum.create("connection", EnumFacing.class);
    public static final PropertyEnum<EnumFacing> PLUG = PropertyEnum.create("plug", EnumFacing.class);

    public BlockWire() {
        super(Material.CIRCUITS, MapColor.BLUE);
        setDefaultState(blockState.getBaseState().withProperty(ADDON, Addon.NONE));
        setResistance(1F);
        setHardness(1F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        super.getSubBlocks(itemIn, tab, list);
    }

    @Override
    public void defineRecipes() {
        RailcraftCraftingManager.rollingMachine.addRecipe(
                getStack(8, null),
                "LPL",
                "PCP",
                "LPL",
                'C', "blockCopper",
                'P', Items.PAPER,
                'L', "ingotLead");
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ADDON, EnumTools.fromOrdinal(meta, Addon.VALUES));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ADDON).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ADDON);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileWire();
    }

    public Addon getAddon(IBlockState state) {
        return state.getValue(ADDON);
    }

    public boolean setAddon(World worldIn, BlockPos pos, IBlockState state, Addon addon) {
        Addon existing = getAddon(state);
        //TODO: drop stuff
        return existing != addon && WorldPlugin.setBlockState(worldIn, pos, state.withProperty(ADDON, addon));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Addon addon = getAddon(state);
        return addon.boundingBox;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
        Addon addon = getAddon(state);
        if (addon.addonObject != null) {
            ItemStack addonItem = addon.addonObject.getStack();
            if (addonItem != null)
                drops.add(addonItem);
        }
        return drops;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (heldItem != null && InvTools.isStackEqualToBlock(heldItem, RailcraftBlocks.frame.block()))
            if (setAddon(worldIn, pos, state, Addon.FRAME)) {
                if (!playerIn.capabilities.isCreativeMode)
                    playerIn.setHeldItem(hand, InvTools.depleteItem(heldItem));
                return true;
            }
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getAddon(base_state) == Addon.FRAME && side == EnumFacing.UP;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        if (getAddon(state) == Addon.FRAME)
            return IPostConnection.ConnectStyle.TWO_THIN;
        return IPostConnection.ConnectStyle.NONE;
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return getAddon(blockState).hardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState blockState = WorldPlugin.getBlockState(world, pos);
        return getAddon(blockState).resistance * 0.6F;
    }

    public enum Addon implements IStringSerializable {

        NONE(1, 1, null, AABBFactory.start().box().grow(-0.25).build()),
        FRAME(5, 10, RailcraftBlocks.frame, FULL_BLOCK_AABB),;
        //        PYLON(null, FULL_BLOCK_AABB);
        public static final Addon[] VALUES = values();
        private final IRailcraftObjectContainer addonObject;
        private final AxisAlignedBB boundingBox;
        private final float hardness, resistance;

        Addon(float hardness, float resistance, @Nullable IRailcraftObjectContainer addonObject, AxisAlignedBB boundingBox) {
            this.hardness = hardness;
            this.resistance = resistance;
            this.addonObject = addonObject;
            this.boundingBox = boundingBox;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
