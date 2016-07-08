/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.items;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;

import static mods.railcraft.common.items.Metal.Form;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class ItemMetal extends ItemRailcraft {
    private final BiMap<Integer, Metal> variants;
    private final Form form;
    private final String tagPrefix;
    private final boolean registerOreDict;
    private final boolean registerMinerBackpack;

    protected ItemMetal(Metal.Form form, String tagPrefix, boolean registerOreDict, boolean registerMinerBackpack, BiMap<Integer, Metal> variants) {
        setHasSubtypes(true);
        setMaxDamage(0);
        this.form = form;
        this.tagPrefix = tagPrefix;
        this.registerOreDict = registerOreDict;
        this.registerMinerBackpack = registerMinerBackpack;
        this.variants = Maps.unmodifiableBiMap(variants);
    }

    public final BiMap<Integer, Metal> variants() {
        return variants;
    }

    @Override
    public void initializeDefinintion() {
        for (Integer meta : variants().keySet()) {
            ItemStack stack = new ItemStack(this, 1, meta);
            RailcraftRegistry.register(stack);
        }
        if (registerOreDict)
            for (Metal m : variants().values()) {
                OreDictionary.registerOre(m.getOreTag(form), m.getStack(form));
            }
        if (registerMinerBackpack)
            for (Integer meta : variants().keySet()) {
                ItemStack stack = new ItemStack(this, 1, meta);
                ForestryPlugin.addBackpackItem("forestry.miner", stack);
            }
    }

    @Nullable
    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        IVariantEnum.tools.checkVariantObject(getClass(), variant);
        Integer meta = variants.inverse().get((Metal) variant);
        if (meta == null)
            meta = 0;
        return new ItemStack(this, qty, meta);
    }

    @Override
    public String getOreTag(@Nullable IVariantEnum variant) {
        IVariantEnum.tools.checkVariantObject(getClass(), variant);
        return ((Metal) variant).getOreTag(form);
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (Metal metal : variants.values()) {
            ItemStack stack = metal.getStack(form);
            if (stack != null)
                list.add(stack);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        Metal metal = variants.get(damage);
        if (metal == null)
            return tagPrefix + "invalid";
        return tagPrefix + metal.getName();
    }

}
