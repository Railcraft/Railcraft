/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

public class BlockPost extends BlockPostBase implements IPostConnection {

    public static final PropertyEnum<EnumPost> VARIANT = PropertyEnum.create("variant", EnumPost.class);
    private static BlockPost block;

    private BlockPost(int renderType) {
        super(renderType);
        setUnlocalizedName("railcraft.post");
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumPost.WOOD));
    }

    public static void registerBlock() {
        if (getBlock() != null)
            return;

        if (RailcraftConfig.isBlockEnabled("post")) {
            block = new BlockPost(Railcraft.getProxy().getRenderId());

            GameRegistry.registerTileEntity(TilePostEmblem.class, "RCPostEmblemTile");

            RailcraftRegistry.register(getBlock(), ItemPost.class);

            for (EnumPost post : EnumPost.VALUES) {
                ItemStack stack = post.getItem();
                RailcraftRegistry.register(stack);
            }

//            HarvestPlugin.setStateHarvestLevel(block, "crowbar", 0);
            HarvestPlugin.setStateHarvestLevel("axe", 0, getBlockState(EnumPost.WOOD));
            HarvestPlugin.setStateHarvestLevel("pickaxe", 1, getBlockState(EnumPost.STONE));
            HarvestPlugin.setStateHarvestLevel("pickaxe", 2, getBlockState(EnumPost.METAL_UNPAINTED));
            HarvestPlugin.setStateHarvestLevel("pickaxe", 2, getBlockState(EnumPost.EMBLEM));
            HarvestPlugin.setStateHarvestLevel("axe", 0, getBlockState(EnumPost.WOOD_PLATFORM));
            HarvestPlugin.setStateHarvestLevel("pickaxe", 1, getBlockState(EnumPost.STONE_PLATFORM));
            HarvestPlugin.setStateHarvestLevel("pickaxe", 2, getBlockState(EnumPost.METAL_PLATFORM_UNPAINTED));

            ForestryPlugin.addBackpackItem("builder", getBlock());
        }
    }

    public static IBlockState getBlockState(EnumPost variant) {
        return getBlock().getDefaultState().withProperty(VARIANT, variant);
    }

    public static BlockPost getBlock() {
        return block;
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
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumPost post : EnumPost.values()) {
            if (post == EnumPost.EMBLEM) continue;
            list.add(post.getItem());
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

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
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
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, WorldPlugin.getBlockState(world, pos), 0);
        return WorldPlugin.setBlockToAir(world, pos);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
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
    public MapColor getMapColor(IBlockState state) {
        return getVariant(state).getMapColor();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, EnumPost.fromId(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return getVariant(state).ordinal();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, VARIANT);
    }

}
