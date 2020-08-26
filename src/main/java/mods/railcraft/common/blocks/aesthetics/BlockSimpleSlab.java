/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.blocks.aesthetics.BlockSimpleSlab.Variant;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Created by CovertJaguar on 8/26/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(Variant.class)
public abstract class BlockSimpleSlab extends BlockRailcraftSlab<Variant> {
    private final BlockRailcraft baseBlock;

    protected BlockSimpleSlab(IBlockState baseState) {
        super(baseState);
        this.baseBlock = (BlockRailcraft) baseState.getBlock();

        setDefaultState(getDefaultState().withProperty(getVariantEnumProperty(), Variant.DEFAULT));
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(6),
                "III",
                'I', baseBlock);
        CraftingPlugin.addShapedRecipe(baseBlock.getStack(2),
                "I",
                "I",
                'I', getStack());
    }

    @Override
    public @Nullable StateMapperBase getStateMapper() {
        return new StateMap.Builder().ignore(getVariantEnumProperty()).build();
    }

    public static class Double extends BlockSimpleSlab {
        public Double(IBlockState baseState) {
            super(baseState);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }

    public static class Half extends BlockSimpleSlab {
        public Half(IBlockState baseState) {
            super(baseState);
        }

        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public enum Variant implements IVariantEnum {
        DEFAULT;
        public static final Variant[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
