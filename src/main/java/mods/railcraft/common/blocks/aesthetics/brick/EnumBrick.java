/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.cube.ReplacerCube;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Locale;

import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.*;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public enum EnumBrick {
    ABYSSAL {
        @Override
        public void initRecipes() {
            if (EnumCube.ABYSSAL_STONE.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumCube.ABYSSAL_STONE.getItem(), new ItemStack(getBlock(), 1, 2), 0.2F);
                IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumCube.ABYSSAL_STONE.getItem(), true, false);
                recipe.addOutput(get(COBBLE, 1), 1.0F);
            }
        }
    },
    BLEACHEDBONE {
        Item bleachedClay;

        @Override
        public void initBlock() {
            bleachedClay = new ItemRailcraft().setUnlocalizedName("railcraft.part.bleached.clay");
            RailcraftRegistry.register(bleachedClay);
        }

        @Override
        public void initRecipes() {
            if (bleachedClay != null) {
                CraftingPlugin.addShapelessRecipe(new ItemStack(bleachedClay), new ItemStack(Items.clay_ball), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15));
                CraftingPlugin.addFurnaceRecipe(new ItemStack(bleachedClay), new ItemStack(getBlock(), 1, 2), 0.3F);
            }
        }
    },
    BLOODSTAINED {
        @Override
        public void initRecipes() {
            CraftingPlugin.addShapelessRecipe(new ItemStack(getBlock(), 1, 2), new ItemStack(Blocks.sandstone, 1, 2), new ItemStack(Items.rotten_flesh));
            CraftingPlugin.addShapelessRecipe(new ItemStack(getBlock(), 1, 2), new ItemStack(Blocks.sandstone, 1, 2), new ItemStack(Items.beef));
        }
    },
    FROSTBOUND {
        @Override
        public void initRecipes() {
            CraftingPlugin.addShapedRecipe(new ItemStack(getBlock(), 8, 2),
                    "III",
                    "ILI",
                    "III",
                    'I', new ItemStack(Blocks.ice),
                    'L', "gemLapis");
        }
    },
    INFERNAL {
        @Override
        public void initRecipes() {
            ((ReplacerCube) EnumCube.INFERNAL_BRICK.getBlockDef()).block = getBlock();
            CraftingPlugin.addShapedRecipe(new ItemStack(getBlock(), 2, 2),
                    "MB",
                    "BM",
                    'B', new ItemStack(Blocks.nether_brick),
                    'M', new ItemStack(Blocks.soul_sand));
        }
    },
    QUARRIED {
        @Override
        public void initRecipes() {
            if (EnumCube.QUARRIED_STONE.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumCube.QUARRIED_STONE.getItem(), new ItemStack(getBlock(), 1, 2), 0.2F);
                IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumCube.QUARRIED_STONE.getItem(), true, false);
                recipe.addOutput(get(COBBLE, 1), 1.0F);
            }
        }
    },
    SANDY {
        @Override
        public void initRecipes() {
            ((ReplacerCube) EnumCube.SANDY_BRICK.getBlockDef()).block = getBlock();
            CraftingPlugin.addShapedRecipe(new ItemStack(getBlock(), 1, 2),
                    "BM",
                    "MB",
                    'B', "ingotBrick",
                    'M', new ItemStack(Blocks.sand));
        }
    },
    NETHER {
        @Override
        public ItemStack get(BrickVariant variant, int qty) {
            if (variant == BrickVariant.BRICK)
                return new ItemStack(Blocks.nether_brick, qty);
            return super.get(variant, qty);
        }

        @Override
        public void initRecipes() {
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.nether_brick), get(BLOCK, 1), 0);
        }

        @Override
        protected void initVariant(BrickVariant variant) {
            if (variant != BrickVariant.BRICK)
                super.initVariant(variant);
        }
    },;
    public static final EnumBrick[] VALUES = values();
    private BlockBrick block;

    public final BlockBrick getBlock() {
        return block;
    }

    public final String themeTag() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    protected void initBlock() {
    }

    protected void initRecipes() {
    }

    protected void initVariant(BrickVariant variant) {
        MicroBlockPlugin.addMicroBlockCandidate(getBlock(), variant.ordinal());
    }

    public final ItemStack get(BrickVariant variant) {
        return get(variant, 1);
    }

    public ItemStack get(BrickVariant variant, int qty) {
        if (block == null)
            return null;
        return new ItemStack(getBlock(), qty, variant.ordinal());
    }

    public final BlockFactory makeFactory() {
        return new BlockFactory("brick." + themeTag()) {
            @Override
            protected void doBlockInit() {
                block = new BlockBrick(EnumBrick.this);
                block.setBlockName("railcraft.brick." + themeTag());
                RailcraftRegistry.register(block, ItemBrick.class);
                ForestryPlugin.addBackpackItem("builder", block);

                for (BrickVariant variant : BrickVariant.VALUES) {
                    EnumBrick.this.initVariant(variant);
                }

                EnumBrick.this.initBlock();
            }

            @Override
            protected void doRecipeInit(ModuleManager.Module module) {
                CraftingPlugin.addShapelessRecipe(get(BRICK, 1), get(FITTED, 1));
                CraftingPlugin.addShapelessRecipe(get(FITTED, 1), get(BLOCK, 1));
                CraftingPlugin.addShapedRecipe(get(ORNATE, 8),
                        "III",
                        "I I",
                        "III",
                        'I', get(BLOCK, 1));
                CraftingPlugin.addShapelessRecipe(get(ETCHED, 1), get(BLOCK, 1), new ItemStack(Items.gunpowder));

                IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(block), false, false);
                recipe.addOutput(get(COBBLE, 1), 1.0F);

                CraftingPlugin.addFurnaceRecipe(get(COBBLE, 1), get(BLOCK, 1), 0.0F);
                EnumBrick.this.initRecipes();
            }
        };
    }

}
