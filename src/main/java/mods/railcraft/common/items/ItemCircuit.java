/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCircuit extends ItemRailcraftSubtyped {

    public ItemCircuit() {
        super(EnumCircuit.class);
    }

    @Override
    public void initializeDefinition() {
        for (EnumCircuit circuit : EnumCircuit.VALUES) {
            ItemStack stack = getStack(circuit);
            assert !InvTools.isEmpty(stack);
            RailcraftRegistry.register(this, circuit, stack);
        }
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        Ingredient glue = Ingredients.from("slimeball", ModItems.STICKY_RESIN);
        Ingredient gem = Ingredients.from("gemLapis", "gemQuartz", "crystalCertusQuartz");
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumCircuit.CONTROLLER.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', gem,
                '#', Items.REPEATER,
                'G', RailcraftItems.PLATE, Metal.GOLD,
                'S', new ItemStack(Blocks.WOOL, 1, 14),
                'R', "dustRedstone",
                'B', glue);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumCircuit.RECEIVER.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', gem,
                '#', Items.REPEATER,
                'G', RailcraftItems.PLATE, Metal.GOLD,
                'S', new ItemStack(Blocks.WOOL, 1, 13),
                'R', "dustRedstone",
                'B', glue);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumCircuit.SIGNAL.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', gem,
                '#', Items.REPEATER,
                'G', RailcraftItems.PLATE, Metal.GOLD,
                'S', new ItemStack(Blocks.WOOL, 1, 4),
                'R', "dustRedstone",
                'B', glue);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumCircuit.RADIO.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', gem,
                '#', Items.REPEATER,
                'G', RailcraftItems.PLATE, Metal.GOLD,
                'S', new ItemStack(Blocks.WOOL, 1, 11),
                'R', "dustRedstone",
                'B', glue);
    }

    public enum EnumCircuit implements IVariantEnum {

        CONTROLLER(Items.COMPARATOR),
        RECEIVER(Blocks.REDSTONE_TORCH),
        SIGNAL(Items.REPEATER),
        RADIO(Blocks.REDSTONE_BLOCK);
        public static EnumCircuit[] VALUES = values();
        private Ingredient alternate;

        EnumCircuit(Object alt) {
            this.alternate = Ingredients.from(alt);
        }

        @Override
        public Ingredient getAlternate(IIngredientSource container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
