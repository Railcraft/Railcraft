/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import com.google.common.collect.ImmutableMap;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class BlockTankIronGauge extends BlockTankIron {

    public static final Map<EnumFacing, PropertyEnum<RenderState>> TOUCHES;

    static {
        ImmutableMap.Builder<EnumFacing, PropertyEnum<RenderState>> builder = ImmutableMap.builder();
        for (EnumFacing face : EnumFacing.VALUES) {
            builder.put(face, PropertyEnum.create(face.getName(), RenderState.class));
        }
        TOUCHES = builder.build();
    }

    public BlockTankIronGauge() {
        super(Material.GLASS);
        IBlockState state = getDefaultState();
        for (PropertyEnum<RenderState> touch : TOUCHES.values()) {
            state = state.withProperty(touch, RenderState.DEFAULT);
        }
        setDefaultState(state);
        fullBlock = false;
        lightOpacity = 0;
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        addRecipe("GPG",
                "PGP",
                "GPG",
                'G', Blocks.GLASS_PANE,
                'P', RailcraftItems.PLATE, Metal.IRON);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        List<IProperty> props = new ArrayList<>();
        props.add(COLOR);
        for (EnumFacing face : EnumFacing.VALUES) {
            props.add(TOUCHES.get(face));
        }
        return new BlockStateContainer(this, props.toArray(new IProperty[7]));
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankIronGauge.class;
    }

    @Override
    public TileMultiBlock<?> createTileEntity(World world, IBlockState state) {
        return new TileTankIronGauge();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public enum RenderState implements IStringSerializable {
        DEFAULT,
        TOPMOST,
        MIDDLE,
        BOTTOMMOST,
        TRANSPARENT;

        private final String name = name().toLowerCase();

        @Override
        public String getName() {
            return name;
        }
    }
}
