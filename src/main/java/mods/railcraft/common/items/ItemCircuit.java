/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
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
    public void initializeDefinition() {
        for (EnumCircuit circuit : EnumCircuit.VALUES) {
            ItemStack stack = getStack(circuit);
            assert !InvTools.isEmpty(stack);
            RailcraftRegistry.register(this, circuit, stack);
        }
    }

    @Override
    public void finalizeDefinition() {
        Object[] glueTypes = {"slimeball", ModItems.STICKY_RESIN};
        Object[] gemTypes = {"gemLapis", "gemQuartz", "crystalCertusQuartz"};
        for (Object glue : glueTypes) {
            for (Object gem : gemTypes) {
                CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumCircuit.CONTROLLER.ordinal()),
                        " #S",
                        "BGR",
                        "SRL",
                        'L', gem,
                        '#', Items.REPEATER,
                        'G', RailcraftItems.PLATE, Metal.GOLD,
                        'S', new ItemStack(Blocks.WOOL, 1, 14),
                        'R', "dustRedstone",
                        'B', glue);
                CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumCircuit.RECEIVER.ordinal()),
                        " #S",
                        "BGR",
                        "SRL",
                        'L', gem,
                        '#', Items.REPEATER,
                        'G', RailcraftItems.PLATE, Metal.GOLD,
                        'S', new ItemStack(Blocks.WOOL, 1, 13),
                        'R', "dustRedstone",
                        'B', glue);
                CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumCircuit.SIGNAL.ordinal()),
                        " #S",
                        "BGR",
                        "SRL",
                        'L', gem,
                        '#', Items.REPEATER,
                        'G', RailcraftItems.PLATE, Metal.GOLD,
                        'S', new ItemStack(Blocks.WOOL, 1, 4),
                        'R', "dustRedstone",
                        'B', glue);
                CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumCircuit.RADIO.ordinal()),
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
        }
    }

    public enum EnumCircuit implements IVariantEnum {

        CONTROLLER(Items.COMPARATOR),
        RECEIVER(Blocks.REDSTONE_TORCH),
        SIGNAL(Items.REPEATER),
        RADIO(Blocks.REDSTONE_BLOCK);
        public static EnumCircuit[] VALUES = values();
        private Object alternate;

        EnumCircuit(Object alt) {
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
