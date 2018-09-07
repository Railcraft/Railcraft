/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.RollingMachineCraftingManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

import java.util.Locale;

public class ItemRail extends ItemRailcraftSubtyped {

    public ItemRail() {
        super(EnumRail.class);
    }

    @Override
    public void initializeDefinition() {
        for (EnumRail rail : EnumRail.VALUES) {
            ItemStack stack = new ItemStack(this, 1, rail.ordinal());
            RailcraftRegistry.register(this, rail, stack);
        }
    }

    @Override
    public void defineRecipes() {
        RailcraftItems item = RailcraftItems.RAIL;

        Ingredient ironIngot = new OreIngredient("ingotIron");
        RollingMachineCraftingManager.getInstance().newShapedRecipeBuilder()
                .grid(new Ingredient[][] {
                        {ironIngot, Ingredient.EMPTY, ironIngot},
                        {ironIngot, Ingredient.EMPTY, ironIngot},
                        {ironIngot, Ingredient.EMPTY, ironIngot},
                })
                .output(item.getStack(8, EnumRail.STANDARD))
                .buildAndRegister();

        // Standard
//        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(8, EnumRail.STANDARD),
//                "I I",
//                "I I",
//                "I I",
//                'I', Items.IRON_INGOT);

        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(6, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotBronze");

        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(12, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotInvar");

        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(16, EnumRail.STANDARD),
                "I I",
                "I I",
                "I I",
                'I', "ingotSteel");

        // Advanced
        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(8, EnumRail.ADVANCED),
                "R G",
                "R G",
                "R G",
                'R', new ItemStack(Items.REDSTONE),
                'G', new ItemStack(Items.GOLD_INGOT));

        // Wooden
        CraftingPlugin.addShapelessRecipe(item.getStack(6, EnumRail.WOOD), "ingotIron", RailcraftItems.TIE.getRecipeObject(EnumTie.WOOD));

        // Speed
        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(8, EnumRail.SPEED),
                "IBG",
                "IBG",
                "IBG",
                'I', "ingotSteel",
                'B', Items.BLAZE_POWDER,
                'G', Items.GOLD_INGOT);

        // Reinforced
        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(8, EnumRail.REINFORCED),
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotSteel",
                'D', "dustObsidian");

        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(4, EnumRail.REINFORCED),
                "IDI",
                "IDI",
                "IDI",
                'I', "ingotInvar",
                'D', "dustObsidian");

        // Electric
        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(6, EnumRail.ELECTRIC),
                "I I",
                "I I",
                "I I",
                'I', "ingotCopper");
        RollingMachineCraftingManager.getInstance().addRecipe(item.getStack(12, EnumRail.ELECTRIC),
                "ICI",
                "ICI",
                "ICI",
                'I', "ingotSteel",
                'C', "ingotCopper");
    }

    public enum EnumRail implements IVariantEnum {

        STANDARD("ingotIron"), ADVANCED("ingotGold"), WOOD("slabWood"), SPEED("ingotSteel"), REINFORCED(Blocks.OBSIDIAN), ELECTRIC("ingotCopper");
        public static final EnumRail[] VALUES = values();
        private Object alternate;

        EnumRail(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate(IRailcraftRecipeIngredient container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
