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
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.items.ItemDust.EnumDust;
import mods.railcraft.common.items.RailcraftItems;
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

import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.COBBLE;
import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.POLISHED;

/**
 * The Brick Themes (clever, I know)
 * Created by CovertJaguar on 3/12/2015.
 */
public enum BrickTheme implements IRailcraftObjectContainer<IRailcraftBlock> {
    ABYSSAL(RailcraftBlocks.BRICK_ABYSSAL, MapColor.BLACK) {
        @Override
        public void initRecipes(BlockBrick block) {
            if (EnumGeneric.STONE_ABYSSAL.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumGeneric.STONE_ABYSSAL.getStack(), block.getStack(POLISHED), 0.05f);
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
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.BONE_BLOCK), block.getStack(2, POLISHED), 0.3F);
        }
    },
    BLOODSTAINED(RailcraftBlocks.BRICK_BLOOD_STAINED, MapColor.RED) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapelessRecipe(block.getStack(POLISHED), new ItemStack(Blocks.SANDSTONE, 1, 2), new ItemStack(Items.ROTTEN_FLESH));
            CraftingPlugin.addShapelessRecipe(block.getStack(POLISHED), new ItemStack(Blocks.SANDSTONE, 1, 2), new ItemStack(Items.BEEF));
        }
    },
    FROSTBOUND(RailcraftBlocks.BRICK_FROST_BOUND, MapColor.BLUE) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapedRecipe(block.getStack(8, POLISHED),
                    "III",
                    "ILI",
                    "III",
                    'I', new ItemStack(Blocks.ICE),
                    'L', "gemLapis"); // TODO remove ice one in the future
            CraftingPlugin.addShapedRecipe(block.getStack(8, POLISHED),
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
            CraftingPlugin.addShapedRecipe(block.getStack(2, POLISHED),
                    "MB",
                    "BM",
                    'B', new ItemStack(Blocks.NETHER_BRICK),
                    'M', new ItemStack(Blocks.SOUL_SAND)
            );
        }
    },
    QUARRIED(RailcraftBlocks.BRICK_QUARRIED, MapColor.SNOW) {
        @Override
        public void initRecipes(BlockBrick block) {
            if (EnumGeneric.STONE_QUARRIED.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumGeneric.STONE_QUARRIED.getStack(), block.getStack(POLISHED), 0.05f);
                if (COBBLE.isEnabled()) {
                    Crafters.rockCrusher().makeRecipe(EnumGeneric.STONE_QUARRIED)
                            .name("railcraft:stone_quarried")
                            .addOutput(getStack(COBBLE))
                            .register();
                }
            }
        }
    },
    SANDY(RailcraftBlocks.BRICK_SANDY, MapColor.SAND) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapedRecipe(block.getStack(POLISHED),
                    "BM",
                    "MB",
                    'B', "ingotBrick",
                    'M', new ItemStack(Blocks.SAND, 1, 0));
        }
    },
    BADLANDS(RailcraftBlocks.BRICK_BADLANDS, MapColor.ORANGE_STAINED_HARDENED_CLAY) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapedRecipe(block.getStack(POLISHED),
                    "BM",
                    "MB",
                    'B', "ingotBrick",
                    'M', new ItemStack(Blocks.SAND, 1, 1));
        }
    },
    NETHER(RailcraftBlocks.BRICK_NETHER, MapColor.NETHERRACK) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.PAVER)
                return new ItemStack(Blocks.NETHER_BRICK, qty);
            return super.getStack(qty, variant);
        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.PAVER)
                return Blocks.NETHER_BRICK.getDefaultState();
            return super.getState(variant);
        }

        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.NETHER_BRICK), block.getStack(POLISHED), 0);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.PAVER)
                super.initVariant(block, variant);
        }

    },
    RED_NETHER(RailcraftBlocks.BRICK_RED_NETHER, MapColor.NETHERRACK) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.PAVER)
                return new ItemStack(Blocks.RED_NETHER_BRICK, qty);
            return super.getStack(qty, variant);
        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.PAVER)
                return Blocks.RED_NETHER_BRICK.getDefaultState();
            return super.getState(variant);
        }

        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.RED_NETHER_BRICK), block.getStack(POLISHED), 0);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.PAVER)
                super.initVariant(block, variant);
        }

    },
    ANDESITE(RailcraftBlocks.BRICK_ANDESITE, MapColor.STONE) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.POLISHED)
                return new ItemStack(Blocks.STONE, qty, 6);
            return super.getStack(qty, variant);
        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.POLISHED)
                return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH);
            return super.getState(variant);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.POLISHED)
                super.initVariant(block, variant);
        }
    },
    DIORITE(RailcraftBlocks.BRICK_DIORITE, MapColor.QUARTZ) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.POLISHED)
                return new ItemStack(Blocks.STONE, qty, 4);
            return super.getStack(qty, variant);
        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.POLISHED)
                return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
            return super.getState(variant);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.POLISHED)
                super.initVariant(block, variant);
        }
    },
    GRANITE(RailcraftBlocks.BRICK_GRANITE, MapColor.DIRT) {
        @Override
        public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
            if (variant == BrickVariant.POLISHED)
                return new ItemStack(Blocks.STONE, qty, 2);
            return super.getStack(qty, variant);
        }

        @Override
        public @Nullable IBlockState getState(@Nullable BrickVariant variant) {
            if (variant == BrickVariant.POLISHED)
                return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
            return super.getState(variant);
        }

        @Override
        protected void initVariant(BlockBrick block, BrickVariant variant) {
            if (variant != BrickVariant.POLISHED)
                super.initVariant(block, variant);
        }
    },
    PEARLIZED(RailcraftBlocks.BRICK_PEARLIZED, MapColor.GREEN) {
        @Override
        public void initRecipes(BlockBrick block) {
            CraftingPlugin.addShapedRecipe(block.getStack(8, POLISHED),
                    "SSS",
                    "SPS",
                    "SSS",
                    'S', new ItemStack(Blocks.END_STONE),
                    'P', RailcraftItems.DUST, EnumDust.ENDER);
        }
    },
    ;
    public static final BrickTheme[] VALUES = values();
    private final MapColor mapColor;
    private final RailcraftBlocks container;
    private final Definition def;

    BrickTheme(RailcraftBlocks container, MapColor mapColor) {
        this.def = new Definition("brick_" + name().toLowerCase(Locale.ROOT));
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
        block.checkVariant(variant);
        if (variant != null)
            return block.getDefaultState().withProperty(block.getVariantEnumProperty(), variant);
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

    @Override
    public boolean isEnabled() {
        return container.isEnabled();
    }
}
