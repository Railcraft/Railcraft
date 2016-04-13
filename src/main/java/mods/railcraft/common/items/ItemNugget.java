/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemNugget extends ItemRailcraft {

    public ItemNugget() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void initializeDefinintion() {
        for (EnumNugget n : EnumNugget.VALUES) {
            ItemStack stack = new ItemStack(this, 1, n.ordinal());
            RailcraftRegistry.register(stack);
            ForestryPlugin.addBackpackItem("miner", stack);
            Metal m = Metal.get(n);
            OreDictionary.registerOre(m.getNuggetTag(), m.getNugget());
        }
    }

    @Override
    public String getOreTag(IVariantEnum meta) {
        assertVariant(meta);
        return ((EnumNugget) meta).oreTag;
    }

    @Override
    public void defineRecipes() {
        for (EnumNugget n : EnumNugget.VALUES) {
            Metal m = Metal.get(n);
            CraftingPlugin.addShapelessRecipe(m.getNugget(9), m.getIngot());
            CraftingPlugin.addRecipe(m.getIngot(),
                    "NNN",
                    "NNN",
                    "NNN",
                    'N', m.getNuggetTag());
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (EnumNugget dust : EnumNugget.VALUES) {
            list.add(new ItemStack(this, 1, dust.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumNugget.VALUES.length)
            return "";
        switch (EnumNugget.VALUES[damage]) {
            case IRON:
                return "item.railcraft.nugget.iron";
            case STEEL:
                return "item.railcraft.nugget.steel";
            case COPPER:
                return "item.railcraft.nugget.copper";
            case TIN:
                return "item.railcraft.nugget.tin";
            case LEAD:
                return "item.railcraft.nugget.lead";
            default:
                return "";
        }
    }

    public enum EnumNugget implements IVariantEnum {

        IRON("nuggetIron"),
        STEEL("nuggetSteel"),
        COPPER("nuggetCopper"),
        TIN("nuggetTin"),
        LEAD("nuggetLead");
        public static EnumNugget[] VALUES = values();
        private String oreTag;

        EnumNugget(String oreTag) {
            this.oreTag = oreTag;
        }

        @Override
        public Object getAlternate() {
            return oreTag;
        }

        @Nonnull
        @Override
        public Class<? extends ItemRailcraft> getParentClass() {
            return ItemNugget.class;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

}
