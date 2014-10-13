/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.brick;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Locale;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.cube.ReplacerCube;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import static mods.railcraft.common.blocks.aesthetics.brick.BlockBrick.BrickVariant.*;

public class BlockBrick extends Block {

    public enum BrickVariant {

        BRICK, FITTED, BLOCK, ORNATE, ETCHED, COBBLE;
        public static final BrickVariant[] VALUES = values();

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        public static BrickVariant fromOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal >= VALUES.length)
                return BRICK;
            return VALUES[ordinal];
        }

    }
    public static BlockBrick infernal;
    public static BlockBrick abyssal;
    public static BlockBrick sandy;
    public static BlockBrick frostbound;
    public static BlockBrick bloodstained;
    public static BlockBrick quarried;
    public static BlockBrick bleachedbone;
    public static BlockBrick nether;
    private IIcon[] icons;
    private final String theme;

    public static BlockBrick getBlock() {
        return infernal;
    }

    public static void setupBlock() {
        if (infernal == null) {
            infernal = defineBrick("infernal");
            ((ReplacerCube) EnumCube.INFERNAL_BRICK.getBlockDef()).block = infernal;
            CraftingPlugin.addShapedRecipe(new ItemStack(infernal, 2, 2),
                    "MB",
                    "BM",
                    'B', new ItemStack(Blocks.nether_brick),
                    'M', new ItemStack(Blocks.soul_sand));
        }

        if (abyssal == null) {
            abyssal = defineBrick("abyssal");
            if (EnumCube.ABYSSAL_STONE.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumCube.ABYSSAL_STONE.getItem(), new ItemStack(abyssal, 1, 2), 0.2F);
                IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumCube.ABYSSAL_STONE.getItem(), true, false);
                recipe.addOutput(BlockBrick.abyssal.getItemStack(COBBLE, 1), 1.0F);
            }
        }

        if (sandy == null) {
            sandy = defineBrick("sandy");
            ((ReplacerCube) EnumCube.SANDY_BRICK.getBlockDef()).block = sandy;
            CraftingPlugin.addShapedRecipe(new ItemStack(sandy, 1, 2),
                    "BM",
                    "MB",
                    'B', new ItemStack(Items.brick),
                    'M', new ItemStack(Blocks.sand));
        }

        if (frostbound == null) {
            frostbound = defineBrick("frostbound");
            CraftingPlugin.addShapedRecipe(new ItemStack(frostbound, 8, 2),
                    "III",
                    "ILI",
                    "III",
                    'I', new ItemStack(Blocks.ice),
                    'L', new ItemStack(Items.dye, 1, 4));
        }

        if (quarried == null) {
            quarried = defineBrick("quarried");
            if (EnumCube.QUARRIED_STONE.isEnabled()) {
                CraftingPlugin.addFurnaceRecipe(EnumCube.QUARRIED_STONE.getItem(), new ItemStack(quarried, 1, 2), 0.2F);
                IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumCube.QUARRIED_STONE.getItem(), true, false);
                recipe.addOutput(BlockBrick.quarried.getItemStack(COBBLE, 1), 1.0F);
            }
        }

        if (bleachedbone == null) {
            bleachedbone = defineBrick("bleachedbone");
            Item bleachedClay = new ItemRailcraft().setUnlocalizedName("railcraft.part.bleached.clay");
            ItemRegistry.registerItem(bleachedClay);
            CraftingPlugin.addShapelessRecipe(new ItemStack(bleachedClay), new ItemStack(Items.clay_ball), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15));
            CraftingPlugin.addFurnaceRecipe(new ItemStack(bleachedClay), new ItemStack(bleachedbone, 1, 2), 0.3F);
        }

        if (bloodstained == null) {
            bloodstained = defineBrick("bloodstained");
            CraftingPlugin.addShapelessRecipe(new ItemStack(bloodstained, 1, 2), new ItemStack(Blocks.sandstone, 1, 2), new ItemStack(Items.rotten_flesh));
            CraftingPlugin.addShapelessRecipe(new ItemStack(bloodstained, 1, 2), new ItemStack(Blocks.sandstone, 1, 2), new ItemStack(Items.beef));
        }

        if (nether == null) {
            nether = defineBrick("nether");
            CraftingPlugin.addFurnaceRecipe(new ItemStack(Blocks.nether_brick), nether.getItemStack(BLOCK, 1), 0);
        }
    }

    private static BlockBrick makeBrick(String theme) {
        if (theme.equals("nether"))
            return new BlockNetherBrick();
        return new BlockBrick(theme);
    }

    private static BlockBrick defineBrick(String theme) {
        if (RailcraftConfig.isBlockEnabled("brick." + theme)) {
            BlockBrick block = makeBrick(theme);
            block.setBlockName("railcraft.brick." + theme);
            GameRegistry.registerBlock(block, ItemBrick.class, block.getUnlocalizedName());

            for (BrickVariant variant : BrickVariant.VALUES) {
                block.initVarient(variant);
            }

            CraftingPlugin.addShapelessRecipe(block.getItemStack(BRICK, 1), block.getItemStack(FITTED, 1));
            CraftingPlugin.addShapelessRecipe(block.getItemStack(FITTED, 1), block.getItemStack(BLOCK, 1));
            CraftingPlugin.addShapedRecipe(block.getItemStack(ORNATE, 8),
                    "III",
                    "I I",
                    "III",
                    'I', block.getItemStack(BLOCK, 1));
            CraftingPlugin.addShapelessRecipe(block.getItemStack(ETCHED, 1), block.getItemStack(BLOCK, 1), new ItemStack(Items.gunpowder));

            IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(new ItemStack(block), false, false);
            recipe.addOutput(block.getItemStack(COBBLE, 1), 1.0F);

            CraftingPlugin.addFurnaceRecipe(block.getItemStack(COBBLE, 1), block.getItemStack(BLOCK, 1), 0.0F);

            return block;
        }
        return null;
    }

    private static class BlockNetherBrick extends BlockBrick {

        public BlockNetherBrick() {
            super("nether");
        }

        @Override
        protected void initVarient(BrickVariant variant) {
            if (variant != BRICK)
                super.initVarient(variant);
        }

        @Override
        public ItemStack getItemStack(BrickVariant v, int qty) {
            if (v == BRICK)
                return new ItemStack(Blocks.nether_brick, qty);
            return super.getItemStack(v, qty);
        }

    }

    public BlockBrick(String theme) {
        super(Material.rock);
        this.theme = theme;
        setResistance(15);
        setHardness(5);
        setStepSound(Block.soundTypeStone);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHarvestLevel("pickaxe", 0);
    }

    protected void initVarient(BrickVariant variant) {
        ForestryPlugin.addBackpackItem("builder", getItemStack(variant, 1));
        MicroBlockPlugin.addMicroBlockCandidate(this, variant.ordinal());
    }

    public ItemStack getItemStack(BrickVariant v, int qty) {
        return new ItemStack(this, qty, v.ordinal());
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:brick." + theme, BrickVariant.VALUES.length);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta >= icons.length)
            meta = 0;
        return icons[meta];
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (BrickVariant variant : BrickVariant.VALUES) {
            list.add(getItemStack(variant, 1));
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

}
