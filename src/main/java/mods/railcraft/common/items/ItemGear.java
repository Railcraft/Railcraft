/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Locale;

public class ItemGear extends ItemRailcraftSubtyped {

    public ItemGear() {
        super(EnumGear.class);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumGear gear : EnumGear.values()) {
            ItemStack stack = new ItemStack(this, 1, gear.ordinal());
            RailcraftRegistry.register(stack);
        }

        OreDictionary.registerOre("gearIron", RailcraftItems.gear.getStack(1, EnumGear.IRON));

        ItemStack itemStack = new ItemStack(this, 1, EnumGear.BUSHING.ordinal());
        LootPlugin.addLoot(itemStack, 1, 8, LootPlugin.Type.RAILWAY, "gear.bushing");
    }

    @Override
    public void defineRecipes() {
        ItemStack bushing = RailcraftItems.gear.getStack(EnumGear.BUSHING);

        RailcraftItems gear = RailcraftItems.gear;

        CraftingPlugin.addRecipe(gear.getStack(2, EnumGear.BUSHING),
                "TT",
                "TT",
                'T', "ingotTin");

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.GOLD_PLATE),
                " G ",
                "GBG",
                " G ",
                'G', "nuggetGold",
                'B', bushing);

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.IRON),
                " I ",
                "IBI",
                " I ",
                'I', "ingotIron",
                'B', bushing);

        CraftingPlugin.addRecipe(gear.getStack(EnumGear.STEEL),
                " I ",
                "IBI",
                " I ",
                'I', "ingotSteel",
                'B', bushing);
    }

    public enum EnumGear implements IVariantEnum {

        GOLD_PLATE("ingotGold"),
        IRON("blockIron"),
        STEEL("blockSteel"),
        BUSHING("ingotTin");
        public static final EnumGear[] VALUES = values();
        private Object alternate;

        EnumGear(Object alt) {
            this.alternate = alt;
        }

        @Override
        public Object getAlternate(String objectTag) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH).replace('_', '.');
        }
    }

}
