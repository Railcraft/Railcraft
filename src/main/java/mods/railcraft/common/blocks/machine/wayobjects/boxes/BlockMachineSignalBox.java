/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockMachineSignalBox<V extends Enum<V> & IEnumMachine<V>> extends BlockMachine<V> {
    @Deprecated
    public static final Set<IEnumMachine<?>> connectionsSenders = new HashSet<>();
    @Deprecated
    public static final Set<IEnumMachine<?>> connectionsListeners = new HashSet<>();
    @Deprecated
    public static final Set<IEnumMachine<?>> connectionsSelf = new HashSet<>();
    public static final PropertyBool CAP = PropertyBool.create("cap");
    public static final PropertyBool CONNECTION_NORTH = PropertyBool.create("connection_north");
    public static final PropertyBool CONNECTION_SOUTH = PropertyBool.create("connection_south");
    public static final PropertyBool CONNECTION_EAST = PropertyBool.create("connection_east");
    public static final PropertyBool CONNECTION_WEST = PropertyBool.create("connection_west");
    public static ResourceLocation[] lampTextures = new ResourceLocation[4];

    protected BlockMachineSignalBox() {
        super(Material.CIRCUITS);
        setDefaultState(getDefaultState()
                .withProperty(CAP, false)
                .withProperty(CONNECTION_NORTH, false)
                .withProperty(CONNECTION_SOUTH, false)
                .withProperty(CONNECTION_EAST, false)
                .withProperty(CONNECTION_WEST, false)
        );
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setHarvestLevel("crowbar", 0);
        setSoundType(SoundType.METAL);
        setResistance(50);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerTextures(TextureMap textureMap) {
        super.registerTextures(textureMap);
        lampTextures = TextureAtlasSheet.unstitchIcons(textureMap, new ResourceLocation(RailcraftConstantsAPI.MOD_ID, "signal_lamp_box"), new Tuple<>(4, 1));
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return 8;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), CAP, CONNECTION_NORTH, CONNECTION_SOUTH, CONNECTION_EAST, CONNECTION_WEST);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        Optional<TileBoxBase> tile = WorldPlugin.getTileEntity(worldIn, pos, TileBoxBase.class);
        tile.ifPresent(t -> t.getTileCache().resetTimers());
        state = state.withProperty(CAP, !WorldPlugin.isBlockAir(worldIn, pos.up()));
        state = state.withProperty(CONNECTION_NORTH, tile.map(t -> t.isConnected(EnumFacing.NORTH)).orElse(false));
        state = state.withProperty(CONNECTION_EAST, tile.map(t -> t.isConnected(EnumFacing.EAST)).orElse(false));
        state = state.withProperty(CONNECTION_SOUTH, tile.map(t -> t.isConnected(EnumFacing.SOUTH)).orElse(false));
        state = state.withProperty(CONNECTION_WEST, tile.map(t -> t.isConnected(EnumFacing.WEST)).orElse(false));
        return state;
    }

    @SuppressWarnings("deprecation")
    @Override
    public final boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean needsSupport() {
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
