/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.aesthetics.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrickSlab.SlabVariant;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCrafter;

import java.util.Locale;

/**
 * Created by CovertJaguar on 8/6/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(SlabVariant.class)
public abstract class BlockBrickSlab extends BlockRailcraftSlab<SlabVariant> {
    public final BrickTheme brickTheme;

    protected BlockBrickSlab(BrickTheme brickTheme) {
        super(brickTheme.getContainer().getDefaultState());
        this.brickTheme = brickTheme;
    }

    @Override
    public void defineRecipes() {
        for (SlabVariant variant : SlabVariant.VALUES) {
            CraftingPlugin.addShapedRecipe(getStack(6, variant),
                    "III",
                    'I', brickTheme, variant.brickVariant);
            CraftingPlugin.addShapedRecipe(brickTheme.getStack(2, variant.brickVariant),
                    "I",
                    "I",
                    'I', getStack(variant));
            RockCrusherCrafter.INSTANCE.makeRecipe(getStack(variant))
                    .addOutput(brickTheme.getStack(BrickVariant.COBBLE), 0.5F).register();
        }
    }

    public static class Double extends BlockBrickSlab {
        public Double(BrickTheme brickTheme) {
            super(brickTheme);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }

    public static class Half extends BlockBrickSlab {
        public Half(BrickTheme brickTheme) {
            super(brickTheme);
        }

        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public enum SlabVariant implements IVariantEnum {
        BRICK(BrickVariant.BRICK),
        PAVER(BrickVariant.PAVER);
        private final BrickVariant brickVariant;
        public static final SlabVariant[] VALUES = values();

        SlabVariant(BrickVariant brickVariant) {
            this.brickVariant = brickVariant;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
