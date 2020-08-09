/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;

/**
 * Created by CovertJaguar on 12/22/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockTankMetalValve<T extends TileTank> extends BlockTankMetal<T> {
    public static final IProperty<OptionalAxis> OPTIONAL_AXIS = PropertyEnum.create("axis", OptionalAxis.class);

    protected BlockTankMetalValve() {
        super(Material.IRON);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getDefaultState().withProperty(OPTIONAL_AXIS, BlockTankIronValve.OptionalAxis.NONE));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), OPTIONAL_AXIS);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    public enum OptionalAxis implements IStringSerializable {
        NONE,
        X,
        Y,
        Z;

        final String name;

        OptionalAxis() {
            this.name = name().toLowerCase();
        }

        public static OptionalAxis from(EnumFacing.Axis axis) {
            switch (axis) {
                case X:
                    return OptionalAxis.X;
                case Y:
                    return OptionalAxis.Y;
                case Z:
                    return OptionalAxis.Z;
                default:
                    return OptionalAxis.NONE;
            }
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
