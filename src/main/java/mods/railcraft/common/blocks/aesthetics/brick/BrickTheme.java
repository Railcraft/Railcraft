/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
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
                CraftingPlugin.addShapedRecipe(new ItemStack(block, 1, 2),
                        "II",
                        "II",
                        'I', EnumGeneric.STONE_ABYSSAL.getStack());
                if (COBBLE.isEnabled()) {
                    Crafters.rockCrusher().makeRecipe(EnumGeneric.STONE_ABYSSAL)
                            .name("railcraft:stone_abyssal")
                            .addOutput(getStack(COBBLE))
                            .register();
                }
            }
        }
    },
    BLEACHEDBONE(RailcraftBlocks.BRICK_BLEACHED_BONE, MapColor.ADOBE) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.BONE_BLOCK), new ItemStack(block, 2, 2), 0.3F);
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
            CraftingPlugin.addShapedRecipe(new ItemStack(block, 8, 2),
                    "III",
                    "ILI",
                    "III",
                    'I', new ItemStack(Blocks.ICE),
                    'L', "gemLapis");
            CraftingPlugin.addShapedRecipe(new ItemStack(block, 8, 2),
                    "III",
                    "ILI",
                    "III",
                    'I', new ItemStack(Blocks.PACKED_ICE),
                    'L', "gemLapis");
        }
    },
    INFERNAL(RailcraftBlocks.BRICK_INFERNAL, MapColor.GRAY) {
        @Override
        public void initRecipes(BlockBrick block) {
//            ((ReplacerCube) EnumCube.INFERNAL_BRICK.getBlockDef()).replacementState = getBlock().getDefaultState().withProperty(BlockBrick.VARIANT, BrickVariant.BRICK);
            CraftingPlugin.addShapedRecipe(new ItemStack(block, 2, 2),
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
                CraftingPlugin.addShapedRecipe(new ItemStack(block, 1, 2),
                        "II",
                        "II",
                        'I', EnumGeneric.STONE_QUARRIED.getStack());
                Crafters.rockCrusher().makeRecipe(EnumGeneric.STONE_QUARRIED)
                        .name("railrcraft:stone_quarried")
                        .addOutput(getStack(COBBLE))
                        .register();
            }
        }
    },
    SANDY(RailcraftBlocks.BRICK_SANDY, MapColor.SAND) {
        @Override
        public void initRecipes(BlockBrick block) {
//            ((ReplacerCube) EnumCube.SANDY_BRICK.getBlockDef()).replacementState = getBlock().getDefaultState().withProperty(BlockBrick.VARIANT, BrickVariant.BRICK);
            CraftingPlugin.addShapedRecipe(new ItemStack(block, 1, 2),
                    "BM",
                    "MB",
                    'B', "ingotBrick",
                    'M', new ItemStack(Blocks.SAND));
        }
    },
    REDSANDY(RailcraftBlocks.BRICK_RED_SANDY, MapColor.DIRT) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapedRecipe(new ItemStack(block, 1, 2),
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

//        @Nullable
//        @Override
//        public ItemStack getStack(int qty, int meta) {
//            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BRICK)
//                return new ItemStack(Blocks.NETHER_BRICK, qty);
//            return super.getStack(qty, meta);
//        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.BRICK)
                return Blocks.NETHER_BRICK.getDefaultState();
            return super.getState(variant);
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

//        @Nullable
//        @Override
//        public ItemStack getStack(int qty, int meta) {
//            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BRICK)
//                return new ItemStack(Blocks.RED_NETHER_BRICK, qty);
//            return super.getStack(qty, meta);
//        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.BRICK)
                return Blocks.RED_NETHER_BRICK.getDefaultState();
            return super.getState(variant);
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

//        @Nullable
//        @Override
//        public ItemStack getStack(int qty, int meta) {
//            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BLOCK)
//                return new ItemStack(Blocks.STONE, qty, 6);
//            return super.getStack(qty, meta);
//        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.BLOCK)
                return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH);
            return super.getState(variant);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.BLOCK)
                super.initVariant(block, variant);
        }
    },
    DIORITE(RailcraftBlocks.BRICK_DIORITE, MapColor.QUARTZ) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 4);
            return super.getStack(qty, variant);
        }

//        @Nullable
//        @Override
//        public ItemStack getStack(int qty, int meta) {
//            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BLOCK)
//                return new ItemStack(Blocks.STONE, qty, 4);
//            return super.getStack(qty, meta);
//        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.BLOCK)
                return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
            return super.getState(variant);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.BLOCK)
                super.initVariant(block, variant);
        }
    },
    GRANITE(RailcraftBlocks.BRICK_GRANITE, MapColor.DIRT) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.BLOCK)
                return new ItemStack(Blocks.STONE, qty, 2);
            return super.getStack(qty, variant);
        }

//        @Nullable
//        @Override
//        public ItemStack getStack(int qty, int meta) {
//            if (BrickVariant.fromOrdinal(meta) == BrickVariant.BLOCK)
//                return new ItemStack(Blocks.STONE, qty, 2);
//            return super.getStack(qty, meta);
//        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.BLOCK)
                return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
            return super.getState(variant);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.BLOCK)
                super.initVariant(block, variant);
        }
    },
    PEARLIZED(RailcraftBlocks.BRICK_PEARLIZED, MapColor.GREEN) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapedRecipe(new ItemStack(block, 8, 2),
                    "SSS",
                    "SPS",
                    "SSS",
                    'S', new ItemStack(Blocks.END_STONE),
                    'P', Items.ENDER_PEARL);
        }
    },
    ;
    public static final BrickTheme[] VALUES = values();
    private final MapColor mapColor;
    private final RailcraftBlocks container;
    private final Definition def;

    BrickTheme(RailcraftBlocks container, MapColor mapColor) {
        this.def = new Definition(this, "brick_" + name().toLowerCase(Locale.ROOT), null);
        this.container = container;
        this.mapColor = mapColor;
        conditions().add(container);
    }

    @Override
    public Definition getDef() {
        return def;
    }

    @Override
    public void register() {
    }

    public final MapColor getMapColor() {
        return mapColor;
    }

    public final RailcraftBlocks getContainer() {
        return container;
    }

    public final BlockBrick getBlock() {
        return (BlockBrick) container.block();
    }

    public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
        if (!isLoaded()) {
            return null;
        }
        BlockBrick block = getBlock();
        if (block != null) {
            block.checkVariant(variant);
            if (variant != null)
                return block.getDefaultState().withProperty(block.getVariantProperty(), variant);
        }
        return null;
    }

    protected void initBlock(BlockBrick block) {
    }

    protected void initRecipes(BlockBrick block) {
    }

    protected void initVariant(BlockBrick block, BrickVariant variant) {
        MicroBlockPlugin.addMicroBlockCandidate(block, variant.ordinal());
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        if (!isLoaded())
            return ItemStack.EMPTY;
        BlockBrick blockBrick = getBlock();
        int meta;
        if (variant != null) {
            blockBrick.checkVariant(variant);
            meta = variant.ordinal();
        } else
            meta = 0;
        return new ItemStack(blockBrick, qty, meta);
    }

//    @Nullable
//    @Override
//    public ItemStack getStack(int qty, int meta) {
//        BlockBrick blockBrick = getBlock();
//        if (blockBrick != null)
//            return new ItemStack(blockBrick, qty, meta);
//        return null;
//    }

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
    public boolean isLoaded() {
        return container.isLoaded();
    }
}
