/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCircuit extends ItemRailcraftSubtyped {

    public ItemCircuit() {
        super(EnumCircuit.class);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumCircuit circuit : EnumCircuit.VALUES) {
            ItemStack stack = getStack(circuit);
            assert stack != null;
            RailcraftRegistry.register(stack);
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumCircuit.CONTROLLER.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', "gemLapis",
                '#', Items.REPEATER,
                'G', "ingotGold",
                'S', new ItemStack(Blocks.WOOL, 1, 14),
                'R', "dustRedstone",
                'B', "slimeball");
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumCircuit.RECEIVER.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', "gemLapis",
                '#', Items.REPEATER,
                'G', "ingotGold",
                'S', new ItemStack(Blocks.WOOL, 1, 13),
                'R', "dustRedstone",
                'B', "slimeball");
        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumCircuit.SIGNAL.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', "gemLapis",
                '#', Items.REPEATER,
                'G', "ingotGold",
                'S', new ItemStack(Blocks.WOOL, 1, 4),
                'R', "dustRedstone",
                'B', "slimeball");
    }

    public enum EnumCircuit implements IVariantEnum {

        CONTROLLER(Items.COMPARATOR),
        RECEIVER(Blocks.REDSTONE_TORCH),
        SIGNAL(Items.REPEATER);
        public static EnumCircuit[] VALUES = values();
        private Object alternate;

        EnumCircuit(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate(IRailcraftObjectContainer container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
