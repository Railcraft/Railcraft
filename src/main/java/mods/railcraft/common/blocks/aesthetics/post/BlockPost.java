/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlockProperties;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockPost extends BlockPostBase implements IPostConnection {

    public static final PropertyEnum<EnumPost> VARIANT = RailcraftBlockProperties.POST_VARIANT;

    public BlockPost() {
        setUnlocalizedName("railcraft.post");
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumPost.WOOD));
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
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
    public void initializeDefinintion() {
        GameRegistry.registerTileEntity(TilePostEmblem.class, "RCPostEmblemTile");

        for (EnumPost post : EnumPost.VALUES) {
            ItemStack stack = post.getStack();
            if (stack != null)
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

        ForestryPlugin.addBackpackItem("builder", this);
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
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumPost post : EnumPost.values()) {
            if (post == EnumPost.EMBLEM) continue;
            list.add(post.getStack());
        }
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister iconRegister) {
//        IIcon woodIcon = iconRegister.registerIcon("railcraft:post.wood");
//        IIcon stoneIcon = iconRegister.registerIcon("railcraft:concrete");
//        IIcon metalIcon = iconRegister.registerIcon("railcraft:post.metal");
//        EnumPost.WOOD.setTexture(woodIcon);
//        EnumPost.WOOD_PLATFORM.setTexture(woodIcon);
//        EnumPost.STONE.setTexture(stoneIcon);
//        EnumPost.STONE_PLATFORM.setTexture(stoneIcon);
//        EnumPost.METAL_UNPAINTED.setTexture(metalIcon);
//        EnumPost.METAL_PLATFORM_UNPAINTED.setTexture(metalIcon);
//        EnumPost.EMBLEM.setTexture(metalIcon);
//    }
//
//    @Override
//    public IIcon getIcon(int side, int meta) {
//        return EnumPost.fromId(meta).getIcon();
//    }
//
//    @Override
//    public IIcon getIcon(IBlockAccess world, BlockPos pos, int side) {
//        int meta = world.getBlockMetadata(x, y, z);
//        if (meta == EnumPost.EMBLEM.ordinal()) {
//            TileEntity tile = world.getTileEntity(x, y, z);
//            if (tile instanceof TilePostEmblem) {
//                TilePostEmblem post = (TilePostEmblem) tile;
//                EnumColor color = post.getColor();
//                if (color != null && BlockPostMetal.textures != null)
//                    return BlockPostMetal.textures[color.ordinal()];
//            }
//        }
//        return super.getIcon(world, x, y, z, side);
//    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        if (state.getValue(VARIANT) == EnumPost.EMBLEM) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem post = (TilePostEmblem) tile;
                List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
                post.getColor().setItemColor(drops.get(0));
                ItemPost.setEmblem(drops.get(0), post.getEmblem());
                return drops;
            }
        }
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, WorldPlugin.getBlockState(world, pos), 0);
        return WorldPlugin.setBlockToAir(world, pos);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return !isVariant(world, pos, EnumPost.EMBLEM) && (side == EnumFacing.DOWN || side == EnumFacing.UP);
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
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
    public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return getVariant(world, pos).canBurn();
    }

    @Override
    public boolean recolorBlock(World world, @Nonnull BlockPos pos, EnumFacing side, @Nonnull EnumDyeColor color) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (isVariant(state, EnumPost.METAL_UNPAINTED))
            if (BlockPostMetal.post != null) {
                WorldPlugin.setBlockState(world, pos, BlockPostMetal.post.getDefaultState().withProperty(BlockPostMetal.COLOR, EnumColor.fromDye(color)));
                return true;
            }
        if (isVariant(state, EnumPost.METAL_PLATFORM_UNPAINTED))
            if (BlockPostMetal.platform != null) {
                WorldPlugin.setBlockState(world, pos, BlockPostMetal.platform.getDefaultState().withProperty(BlockPostMetal.COLOR, EnumColor.fromDye(color)));
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
    public ConnectStyle connectsToPost(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EnumFacing side) {
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
    @Nonnull
    public MapColor getMapColor(IBlockState state) {
        return getVariant(state).getMapColor();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    @Nonnull
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
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

}
