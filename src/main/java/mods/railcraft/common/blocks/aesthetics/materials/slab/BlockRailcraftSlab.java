/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.materials.slab;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.common.blocks.BlockContainerRailcraft;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.aesthetics.materials.IMaterialBlock;
import mods.railcraft.common.blocks.aesthetics.materials.Materials;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.RailcraftSoundTypes;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

@BlockMeta.Tile(TileSlab.class)
public class BlockRailcraftSlab extends BlockContainerRailcraft<TileSlab> implements IMaterialBlock {

    public static final IUnlistedProperty<Materials> TOP_MATERIAL = Properties.toUnlisted(PropertyEnum.create("top_material", Materials.class));
    public static final IUnlistedProperty<Materials> BOTTOM_MATERIAL = Properties.toUnlisted(PropertyEnum.create("bottom_material", Materials.class));

    public BlockRailcraftSlab() {
        super(Material.ROCK);
        setSoundType(RailcraftSoundTypes.OVERRIDE);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        useNeighborBrightness = true;
        RailcraftRegistry.register(TileSlab.class, "RCSlabTile");
    }

    @Override
    public @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return Materials.class;
    }

    @Override

    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{TOP_MATERIAL, BOTTOM_MATERIAL});
    }

    @Override
    public void finalizeDefinition() {
        super.finalizeDefinition();
        for (Materials mat : Materials.getValidMats()) {
            RailcraftRegistry.register(this, mat, getStack(1, mat));

            switch (mat) {
                case SNOW:
                case ICE:
                case PACKED_ICE:
                    break;
                default:
                    ForestryPlugin.addBackpackItem("forestry.builder", getStack(1, mat));
            }

            switch (mat) {
                case SNOW: // TODO: is this necessary?
                    CraftingPlugin.addShapedRecipe(getStack(3, mat),
                            " T ",
                            "SSS",
                            'T', RailcraftItems.STONE_CARVER,
                            'S', Blocks.SNOW_LAYER);
                    break;
                default:
                    CraftingPlugin.addShapedRecipe(getStack(6, mat),
                            " T ",
                            "SSS",
                            'T', RailcraftItems.STONE_CARVER,
                            'S', mat.getSourceItem());

                    CraftingPlugin.addShapedRecipe(mat.getSourceItem(), "S", "S", 'S', getStack(1, mat));
            }
        }
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        return Materials.getStack(this, qty, variant);
    }

    @Override
    public String getTranslationKey(Materials mat) {
        return "tile.railcraft.slab." + mat.getLocalizationSuffix();
    }

    @SuppressWarnings("unused")
    public static Materials getTopSlab(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getTopSlab();
        return Materials.NO_MAT;
    }

    @SuppressWarnings("unused")
    public static Materials getBottomSlab(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile).getBottomSlab();
        return Materials.NO_MAT;
    }

    static @Nullable TileSlab getSlabTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileSlab)
            return ((TileSlab) tile);
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileSlab tile = getSlabTile(worldIn, pos);
        if (tile != null)
            state = ((IExtendedBlockState) state).withProperty(TOP_MATERIAL, tile.getTopSlab()).withProperty(BOTTOM_MATERIAL, tile.getBottomSlab());
        return state;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileSlab) {
            Materials slab = ((TileSlab) tile).getUpmostSlab();
            return getStack(1, slab);
        }
        return getStack(1, Materials.getPlaceholder());
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.addAll(Materials.getCreativeList().stream().map(this::getStack).filter(InvTools::nonEmpty).collect(Collectors.toList()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        ArrayList<ItemStack> items = new ArrayList<>();
        if (tile instanceof TileSlab) {
            Materials top = ((TileSlab) tile).getTopSlab();
            Materials bottom = ((TileSlab) tile).getBottomSlab();
            if (top != Materials.NO_MAT)
                items.add(getStack(1, top));
            if (bottom != Materials.NO_MAT)
                items.add(getStack(1, bottom));
        }
        return items;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode) {
            dropBlockAsItem(world, pos, state, 0);
        }
        return world.setBlockToAir(pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileSlab) {
            Materials top = ((TileSlab) tile).getTopSlab();
            Materials bottom = ((TileSlab) tile).getBottomSlab();
            float hardness = 0;
            if (top != Materials.NO_MAT)
                hardness += top.getBlockHardness(worldIn, pos);
            if (bottom != Materials.NO_MAT)
                hardness += bottom.getBlockHardness(worldIn, pos);
            if (top != Materials.NO_MAT && bottom != Materials.NO_MAT)
                hardness = hardness / 2.0F;
            return hardness;
        }
        return super.getBlockHardness(state, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileSlab) {
            TileSlab slab = (TileSlab) tile;
            Materials top = slab.getTopSlab();
            Materials bottom = slab.getBottomSlab();
            float resist = 0;
            if (top != Materials.NO_MAT)
                resist += top.getExplosionResistance(exploder);
            if (bottom != Materials.NO_MAT)
                resist += bottom.getExplosionResistance(exploder);
            if (top != Materials.NO_MAT && bottom != Materials.NO_MAT)
                resist = resist / 2.0F;
            return resist;
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return ParticleHelper.addHitEffects(worldObj, this, target, manager, null);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return ParticleHelper.addDestroyEffects(world, this, pos, state, manager, null);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
        if (tile instanceof TileSlab) {
            Materials slab = ((TileSlab) tile).getUpmostSlab();
            return slab.getSound();
        }
        return SoundType.STONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileSlab slab = getSlabTile(source, pos);
        AABBFactory boxFactory = AABBFactory.start().box();
        if (slab != null)
            if (slab.isBottomSlab())
                boxFactory.raiseCeiling(-0.5);
            else if (slab.isTopSlab())
                boxFactory.raiseFloor(0.5);
        return boxFactory.build();
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether
     * or not to render the shared face of two adjacent blocks and also whether
     * the player can attach torches, redstone wire, etc to this block.
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False
     * (examples: signs, buttons, stairs, etc)
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if
     * the adjacent block is at the given coordinates. Args: blockAccess, x, y,
     * z, side
     */
    @SuppressWarnings({"SimplifiableIfStatement", "deprecation"})
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        BlockPos offsetPos = pos.offset(side);

        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileSlab) {
            TileSlab slab = (TileSlab) tile;
            Materials top = slab.getTopSlab();
            Materials bottom = slab.getBottomSlab();

            if (slab.isDoubleSlab())
                return super.shouldSideBeRendered(state, worldIn, pos, side);

            if (side != UP && side != DOWN && !super.shouldSideBeRendered(state, worldIn, pos, side))
                return false;

            if (top != Materials.NO_MAT) {
                if (side == DOWN)
                    return true;
                if (side == UP && super.shouldSideBeRendered(state, worldIn, pos, side))
                    return true;
                if (!WorldPlugin.isBlockAt(worldIn, offsetPos, this))
                    return true;
                TileSlab otherSlab = getSlabTile(worldIn, offsetPos);
                if (otherSlab != null) {
                    if (slab.isDoubleSlab())
                        return false;
                    return otherSlab.isBottomSlab() || (otherSlab.isTopSlab() && otherSlab.getTopSlab().isTransparent());
                }
            }
            if (bottom != Materials.NO_MAT) {
                if (side == UP)
                    return true;
                if (side == DOWN && super.shouldSideBeRendered(state, worldIn, pos, side))
                    return true;
                if (!WorldPlugin.isBlockAt(worldIn, offsetPos, this))
                    return true;
                TileSlab otherSlab = getSlabTile(worldIn, offsetPos);
                if (otherSlab != null) {
                    if (slab.isDoubleSlab())
                        return false;
                    return otherSlab.isTopSlab() || (otherSlab.isBottomSlab() && otherSlab.getBottomSlab().isTransparent());
                }
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileSlab tile = getSlabTile(world, pos);
        if (tile != null) {
            if (tile.isDoubleSlab())
                return true;
            if (side == DOWN && tile.isBottomSlab())
                return true;
            return side == UP && tile.isTopSlab();
        }
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileSlab tile = getSlabTile(world, pos);
        if (tile != null) {
            if (tile.isDoubleSlab())
                return true;
            return tile.isTopSlab();
        }
        return false;
    }
}
