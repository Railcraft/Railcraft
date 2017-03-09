/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.BLOCK;
import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.COBBLE;

/**
 * The Brick Themes (clever, I know)
 * Created by CovertJaguar on 3/12/2015.
 */
public enum BrickTheme implements IRailcraftObjectContainer<IRailcraftBlock> {
    ABYSSAL(RailcraftBlocks.BRICK_ABYSSAL, MapColor.BLACK) {
        @Override
        public void initRecipes(BlockBrick block) {
            if (EnumGeneric.STONE_ABYSSAL.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumGeneric.STONE_ABYSSAL.getStack(), new ItemStack(block, 1, 2), 0.2F);
                ItemStack abyssalStone = EnumGeneric.STONE_ABYSSAL.getStack();
                if (abyssalStone != null) {
                    ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(abyssalStone, true, false);
                    recipe.addOutput(getStack(1, COBBLE), 1.0F);
                }
            }
        }
    },
    BLEACHEDBONE(RailcraftBlocks.BRICK_BLEACHED_BONE, MapColor.ADOBE) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapelessRecipe(RailcraftItems.BLEACHED_CLAY.getStack(), new ItemStack(Items.CLAY_BALL), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.DYE, 1, 15));
            CraftingPlugin.addFurnaceRecipe(RailcraftItems.BLEACHED_CLAY.getStack(), new ItemStack(block, 1, 2), 0.3F);
        }
    },
    BLOODSTAINED(RailcraftBlocks.BRICK_BLOOD_STAINED, MapColor.RED) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapelessRecipe(new ItemStack(block, 1, 2), new ItemStack(Blocks.SANDSTONE, 1, 2), new ItemStack(Items.ROTTEN_FLESH));
            CraftingPlugin.addShapelessRecipe(new ItemStack(block, 1, 2), new ItemStack(Blocks.SANDSTONE, 1, 2), new ItemStack(Items.BEEF));
        }
    },
    FROSTBOUND(RailcraftBlocks.BRICK_FROST_BOUND, MapColor.BLUE) {
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
    INFERNAL(RailcraftBlocks.BRICK_INFERNAL, MapColor.GRAY) {
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
    QUARRIED(RailcraftBlocks.BRICK_QUARRIED, MapColor.SNOW) {
        @Override
        public void initRecipes(BlockBrick block) {
            if (EnumGeneric.STONE_QUARRIED.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumGeneric.STONE_QUARRIED.getStack(), new ItemStack(block, 1, 2), 0.2F);
                ItemStack quarriedStone = EnumGeneric.STONE_QUARRIED.getStack();
                if (quarriedStone != null) {
                    ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(quarriedStone, true, false);
                    recipe.addOutput(getStack(1, COBBLE), 1.0F);
                }
            }
        }
    },
    SANDY(RailcraftBlocks.BRICK_SANDY, MapColor.SAND) {
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
    REDSANDY(RailcraftBlocks.BRICK_RED_SANDY, MapColor.DIRT) {
        @Override
        public void initRecipes(BlockBrick block) {
//            ((ReplacerCube) EnumCube.SANDY_BRICK.getBlockDef()).replacementState = getBlock().getDefaultState().withProperty(BlockBrick.VARIANT, BrickVariant.BRICK);
            CraftingPlugin.addRecipe(new ItemStack(block, 1, 2),
                    "BM",
                    "MB",
                    'B', "ingotBrick",
                    'M', new ItemStack(Blocks.SAND, 1, 1));
        }
    },
    NETHER(RailcraftBlocks.BRICK_NETHER, MapColor.NETHERRACK) {
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


    },
    REDNETHER(RailcraftBlocks.BRICK_RED_NETHER, MapColor.NETHERRACK) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.BRICK)
                return new ItemStack(Blocks.RED_NETHER_BRICK, qty);
            return super.getStack(qty, variant);
        }

        @Nullable
        @Override
        public ItemStack getStack(int qty, int meta) {
            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BRICK)
                return new ItemStack(Blocks.RED_NETHER_BRICK, qty);
            return super.getStack(qty, meta);
        }

        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.RED_NETHER_BRICK), getStack(1, BLOCK), 0);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.BRICK)
                super.initVariant(block, variant);
        }


    },
    ANDESITE(RailcraftBlocks.BRICK_ANDESITE, MapColor.STONE) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 6);
            return super.getStack(qty, variant);
        }

        @Nullable
        @Override
        public ItemStack getStack(int qty, int meta) {
            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 6);
            return super.getStack(qty, meta);
        }
    },
    DIORITE(RailcraftBlocks.BRICK_DIORITE, MapColor.QUARTZ) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 4);
            return super.getStack(qty, variant);
        }

        @Nullable
        @Override
        public ItemStack getStack(int qty, int meta) {
            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 4);
            return super.getStack(qty, meta);
        }
    },
    GRANITE(RailcraftBlocks.BRICK_GRANITE, MapColor.DIRT) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 2);
            return super.getStack(qty, variant);
        }

        @Nullable
        @Override
        public ItemStack getStack(int qty, int meta) {
            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 2);
            return super.getStack(qty, meta);
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
    public Optional<IRailcraftBlock> getObject() {
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
