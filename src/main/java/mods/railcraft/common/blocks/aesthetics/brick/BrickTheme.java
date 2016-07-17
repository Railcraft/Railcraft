/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.BLOCK;
import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.COBBLE;

/**
 * The Brick Themes (clever, I know)
 * Created by CovertJaguar on 3/12/2015.
 */
public enum BrickTheme implements IRailcraftObjectContainer {
    ABYSSAL(RailcraftBlocks.brickAbyssal, MapColor.BLACK) {
        @Override
        public void initRecipes(BlockBrick block) {
            if (EnumCube.ABYSSAL_STONE.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumCube.ABYSSAL_STONE.getStack(), new ItemStack(block, 1, 2), 0.2F);
                ItemStack abyssalStone = EnumCube.ABYSSAL_STONE.getStack();
                if (abyssalStone != null) {
                    ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(abyssalStone, true, false);
                    recipe.addOutput(getStack(1, COBBLE), 1.0F);
                }
            }
        }
    },
    BLEACHEDBONE(RailcraftBlocks.brickBleachedBone, MapColor.ADOBE) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapelessRecipe(RailcraftItems.bleachedClay.getStack(), new ItemStack(Items.CLAY_BALL), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.DYE, 1, 15));
            CraftingPlugin.addFurnaceRecipe(RailcraftItems.bleachedClay.getStack(), new ItemStack(block, 1, 2), 0.3F);
        }
    },
    BLOODSTAINED(RailcraftBlocks.brickBloodStained, MapColor.RED) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapelessRecipe(new ItemStack(block, 1, 2), new ItemStack(Blocks.SANDSTONE, 1, 2), new ItemStack(Items.ROTTEN_FLESH));
            CraftingPlugin.addShapelessRecipe(new ItemStack(block, 1, 2), new ItemStack(Blocks.SANDSTONE, 1, 2), new ItemStack(Items.BEEF));
        }
    },
    FROSTBOUND(RailcraftBlocks.brickFrostBound, MapColor.BLUE) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addRecipe(new ItemStack(block, 8, 2),
                    "III",
                    "ILI",
                    "III",
                    'I', new ItemStack(Blocks.ICE),
                    'L', "gemLapis");
        }
    },
    INFERNAL(RailcraftBlocks.brickInfernal, MapColor.GRAY) {
        @Override
        public void initRecipes(BlockBrick block) {
//            ((ReplacerCube) EnumCube.INFERNAL_BRICK.getBlockDef()).replacementState = getBlock().getDefaultState().withProperty(BlockBrick.VARIANT, BrickVariant.BRICK);
            CraftingPlugin.addRecipe(new ItemStack(block, 2, 2),
                    "MB",
                    "BM",
                    'B', new ItemStack(Blocks.NETHER_BRICK),
                    'M', new ItemStack(Blocks.SOUL_SAND));
        }
    },
    QUARRIED(RailcraftBlocks.brickQuarried, MapColor.SNOW) {
        @Override
        public void initRecipes(BlockBrick block) {
            if (EnumCube.QUARRIED_STONE.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumCube.QUARRIED_STONE.getStack(), new ItemStack(block, 1, 2), 0.2F);
                ItemStack quarriedStone = EnumCube.QUARRIED_STONE.getStack();
                if (quarriedStone != null) {
                    ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(quarriedStone, true, false);
                    recipe.addOutput(getStack(1, COBBLE), 1.0F);
                }
            }
        }
    },
    SANDY(RailcraftBlocks.brickSandy, MapColor.SAND) {
        @Override
        public void initRecipes(BlockBrick block) {
//            ((ReplacerCube) EnumCube.SANDY_BRICK.getBlockDef()).replacementState = getBlock().getDefaultState().withProperty(BlockBrick.VARIANT, BrickVariant.BRICK);
            CraftingPlugin.addRecipe(new ItemStack(block, 1, 2),
                    "BM",
                    "MB",
                    'B', "ingotBrick",
                    'M', new ItemStack(Blocks.SAND));
        }
    },
    NETHER(RailcraftBlocks.brickNether, MapColor.NETHERRACK) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.BRICK)
                return new ItemStack(Blocks.NETHER_BRICK, qty);
            return super.getStack(qty, variant);
        }

        @Nullable
        @Override
        public ItemStack getStack(int qty, int meta) {
            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BRICK)
                return new ItemStack(Blocks.NETHER_BRICK, qty);
            return super.getStack(qty, meta);
        }

        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.NETHER_BRICK), getStack(1, BLOCK), 0);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.BRICK)
                super.initVariant(block, variant);
        }
    },;
    public static final BrickTheme[] VALUES = values();
    private final MapColor mapColor;
    private final RailcraftBlocks container;

    BrickTheme(RailcraftBlocks container, MapColor mapColor) {
        this.container = container;
        this.mapColor = mapColor;
    }

    public final MapColor getMapColor() {
        return mapColor;
    }

    public final RailcraftBlocks getContainer() {
        return container;
    }

    @Nullable
    public final BlockBrick getBlock() {
        return (BlockBrick) container.block();
    }

    @Nullable
    public IBlockState getState(@Nullable BrickVariant variant) {
        return container.getState(variant);
    }

    protected void initBlock(BlockBrick block) {
    }

    protected void initRecipes(BlockBrick block) {
    }

    protected void initVariant(BlockBrick block, BrickVariant variant) {
        MicroBlockPlugin.addMicroBlockCandidate(block, variant.ordinal());
    }

    @Override
    @Nullable
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        BlockBrick blockBrick = getBlock();
        if (blockBrick != null) {
            int meta;
            if (variant != null) {
                blockBrick.checkVariant(variant);
                meta = variant.ordinal();
            } else
                meta = 0;
            return new ItemStack(blockBrick, qty, meta);
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, int meta) {
        BlockBrick blockBrick = getBlock();
        if (blockBrick != null)
            return new ItemStack(blockBrick, qty, meta);
        return null;
    }

    @Override
    public boolean isEqual(ItemStack stack) {
        return container.isEqual(stack);
    }

    @Override
    public String getBaseTag() {
        return container.getBaseTag();
    }

    @Override
    public IRailcraftObject getObject() {
        return container.getObject();
    }

    @Override
    public boolean isEnabled() {
        return container.isEnabled();
    }

    @Override
    public boolean isLoaded() {
        return container.isLoaded();
    }
}
