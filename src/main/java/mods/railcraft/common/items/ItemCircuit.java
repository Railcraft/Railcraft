/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCircuit extends ItemRailcraft {

    public static enum EnumCircuit {

        CONTROLLER, RECEIVER, SIGNAL;
        public static EnumCircuit[] VALUES = values();
        private IIcon icon;
    };
    private static Item item;

    public static Item getCircuitItem() {
        if (item != null)
            return item;

        String tag = "railcraft.part.circuit";
        item = new ItemCircuit();
        ItemRegistry.registerItem(item);

        for (EnumCircuit circuit : EnumCircuit.VALUES) {
            ItemStack stack = new ItemStack(item, 1, circuit.ordinal());
            ItemRegistry.registerItemStack(item.getUnlocalizedName(stack), stack);
        }

        CraftingPlugin.addShapedRecipe(new ItemStack(item, 1, EnumCircuit.CONTROLLER.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', new ItemStack(Items.dye, 1, 4),
                '#', Items.repeater,
                'G', Items.gold_ingot,
                'S', new ItemStack(Blocks.wool, 1, 14),
                'R', Items.redstone,
                'B', Items.slime_ball);
        CraftingPlugin.addShapedRecipe(new ItemStack(item, 1, EnumCircuit.RECEIVER.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', new ItemStack(Items.dye, 1, 4),
                '#', Items.repeater,
                'G', Items.gold_ingot,
                'S', new ItemStack(Blocks.wool, 1, 13),
                'R', Items.redstone,
                'B', Items.slime_ball);
        CraftingPlugin.addShapedRecipe(new ItemStack(item, 1, EnumCircuit.SIGNAL.ordinal()),
                " #S",
                "BGR",
                "SRL",
                'L', new ItemStack(Items.dye, 1, 4),
                '#', Items.repeater,
                'G', Items.gold_ingot,
                'S', new ItemStack(Blocks.wool, 1, 4),
                'R', Items.redstone,
                'B', Items.slime_ball);

        return item;
    }

    public static ItemStack getControllerCircuit() {
        return getControllerCircuit(1);
    }

    public static ItemStack getControllerCircuit(int qty) {
        return new ItemStack(getCircuitItem(), qty, EnumCircuit.CONTROLLER.ordinal());
    }

    public static ItemStack getReceiverCircuit() {
        return getReceiverCircuit(1);
    }

    public static ItemStack getReceiverCircuit(int qty) {
        return new ItemStack(getCircuitItem(), qty, EnumCircuit.RECEIVER.ordinal());
    }

    public static ItemStack getSignalCircuit() {
        return getSignalCircuit(1);
    }

    public static ItemStack getSignalCircuit(int qty) {
        return new ItemStack(getCircuitItem(), qty, EnumCircuit.SIGNAL.ordinal());
    }

    public ItemCircuit() {
        super();
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("railcraft.part.circuit");
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumCircuit gear : EnumCircuit.VALUES) {
            gear.icon = iconRegister.registerIcon("railcraft:part.circuit." + gear.name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumCircuit circuit : EnumCircuit.VALUES) {
            list.add(new ItemStack(this, 1, circuit.ordinal()));
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage >= EnumCircuit.VALUES.length)
            return EnumCircuit.RECEIVER.icon;
        return EnumCircuit.VALUES[damage].icon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumCircuit.VALUES.length)
            return "";
        switch (EnumCircuit.VALUES[damage]) {
            case CONTROLLER:
                return "item.railcraft.part.circuit.controller";
            case RECEIVER:
                return "item.railcraft.part.circuit.receiver";
            case SIGNAL:
                return "item.railcraft.part.circuit.signal";
            default:
                return "";
        }
    }

}
