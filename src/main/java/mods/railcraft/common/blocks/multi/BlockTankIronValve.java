/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.BlockMetaTile;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;

@BlockMetaTile(TileTankIronValve.class)
public class BlockTankIronValve extends BlockTankIron<TileTankIronValve> {

    public static final IProperty<OptionalAxis> OPTIONAL_AXIS = PropertyEnum.create("axis", OptionalAxis.class);

    public BlockTankIronValve() {
        super(Material.IRON);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getDefaultState().withProperty(OPTIONAL_AXIS, OptionalAxis.NONE));
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        addRecipe("BPB",
                "PLP",
                "BPB",
                'B', Blocks.IRON_BARS,
                'P', RailcraftItems.PLATE, Metal.IRON,
                'L', Blocks.LEVER);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OPTIONAL_AXIS);
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
