/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockPost extends BlockPostBase implements IPostConnection {

    public static final PropertyEnum<EnumPost> VARIANT = PropertyEnum.create("variant", EnumPost.class);

    public BlockPost() {
        setTranslationKey("railcraft.post");
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumPost.WOOD));
    }

    @Override
    public @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return EnumPost.class;
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(VARIANT, (EnumPost) variant);
        }
        return state;
    }

    @Override
    public void initializeDefinition() {
        RailcraftRegistry.register(TilePostEmblem.class, "post_emblem");

        for (EnumPost post : EnumPost.VALUES) {
            ItemStack stack = post.getStack();
            if (!stack.isEmpty())
                RailcraftRegistry.register(this, post, stack);
        }

//            HarvestPlugin.setStateHarvestLevel(block, "crowbar", 0);
        HarvestPlugin.setStateHarvestLevel("axe", 0, EnumPost.WOOD.getDefaultState());
        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumPost.STONE.getDefaultState());
        HarvestPlugin.setStateHarvestLevel("pickaxe", 2, EnumPost.METAL_UNPAINTED.getDefaultState());
        HarvestPlugin.setStateHarvestLevel("pickaxe", 2, EnumPost.EMBLEM.getDefaultState());
        HarvestPlugin.setStateHarvestLevel("axe", 0, EnumPost.WOOD_PLATFORM.getDefaultState());
        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, EnumPost.STONE_PLATFORM.getDefaultState());
        HarvestPlugin.setStateHarvestLevel("pickaxe", 2, EnumPost.METAL_PLATFORM_UNPAINTED.getDefaultState());

        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    @Override
    public boolean isPlatform(IBlockState state) {
        switch (getVariant(state)) {
            case WOOD_PLATFORM:
            case STONE_PLATFORM:
            case METAL_PLATFORM_UNPAINTED:
                return true;
        }
        return false;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumPost post : EnumPost.values()) {
            if (post == EnumPost.EMBLEM) continue;
            CreativePlugin.addToList(list, post.getStack());
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (state.getValue(VARIANT) == EnumPost.EMBLEM) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem post = (TilePostEmblem) tile;
                List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
                if (post.getColor() != null)
                    post.getColor().setItemColor(drops.get(0));
                ItemPost.setEmblem(drops.get(0), post.getEmblem());
                return drops;
            }
        }
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, WorldPlugin.getBlockState(world, pos), 0);
        return WorldPlugin.setBlockToAir(world, pos);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return !isVariant(world, pos, EnumPost.EMBLEM) && (side == EnumFacing.DOWN || side == EnumFacing.UP);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (isVariant(state, EnumPost.EMBLEM))
            return new TilePostEmblem();
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(VARIANT) == EnumPost.EMBLEM;
    }

    private boolean isVariant(IBlockState state, EnumPost variant) {
        return state.getValue(VARIANT) == variant;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean isVariant(IBlockAccess world, BlockPos pos, EnumPost variant) {
        return getVariant(world, pos) == variant;
    }

    private EnumPost getVariant(IBlockState state) {
        return state.getValue(VARIANT);
    }

    private EnumPost getVariant(IBlockAccess world, BlockPos pos) {
        return getVariant(WorldPlugin.getBlockState(world, pos));
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        if (getVariant(world, pos).canBurn())
            return 300;
        return 0;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        if (getVariant(world, pos).canBurn())
            return 5;
        return 0;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getVariant(world, pos).canBurn();
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (isVariant(state, EnumPost.METAL_UNPAINTED))
            if (RailcraftBlocks.POST_METAL.isLoaded()) {
                WorldPlugin.setBlockState(world, pos, RailcraftBlocks.POST_METAL.getDefaultState().withProperty(BlockPostMetalBase.COLOR, EnumColor.fromDye(color)));
                return true;
            }
        if (isVariant(state, EnumPost.METAL_PLATFORM_UNPAINTED))
            if (RailcraftBlocks.POST_METAL_PLATFORM.isLoaded()) {
                WorldPlugin.setBlockState(world, pos, RailcraftBlocks.POST_METAL_PLATFORM.getDefaultState().withProperty(BlockPostMetalBase.COLOR, EnumColor.fromDye(color)));
                return true;
            }
        if (isVariant(state, EnumPost.EMBLEM)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem tileEmblem = (TilePostEmblem) tile;
                tileEmblem.setColor(EnumColor.fromDye(color));
                return true;
            }
        }
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePostEmblem) {
            TilePostEmblem tileEmblem = (TilePostEmblem) tile;
            if (tileEmblem.getFacing() == side)
                return ConnectStyle.NONE;
        }
        return ConnectStyle.TWO_THIN;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);
        if (isVariant(world, pos, EnumPost.EMBLEM)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem post = (TilePostEmblem) tile;
                post.onBlockPlacedBy(state, entity, stack);
            }
        }
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return getVariant(state).getMapColor();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override

    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumPost.fromId(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return getVariant(state).ordinal();
    }

    @Override

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

}
