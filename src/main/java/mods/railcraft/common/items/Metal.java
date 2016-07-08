/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mods.railcraft.api.core.IStackFilter;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum Metal implements IVariantEnum {

    STEEL("Steel"),
    IRON("Iron"),
    GOLD("Gold"),
    COPPER("Copper"),
    TIN("Tin"),
    LEAD("Lead");
    public static final Metal[] VALUES = values();
    //    private static final EnumBiMap<Metal, EnumIngot> ingotMap = EnumBiMap.create(Metal.class, EnumIngot.class);
//    private static final EnumBiMap<Metal, EnumNugget> nuggetMap = EnumBiMap.create(Metal.class, EnumNugget.class);
    private static final BiMap<Metal, IVariantEnum> poorOreMap = HashBiMap.create();
    private static final BiMap<Metal, IVariantEnum> blockMap = HashBiMap.create();
    private static final BiMap<Form, IRailcraftObjectContainer> formMap = HashBiMap.create();
    private static final Set<Class<?>> metalObjects = new HashSet<>();

    static {
        metalObjects.add(ItemIngot.class);
        metalObjects.add(ItemNugget.class);
        metalObjects.add(ItemPlate.class);
//        metalObjects.add(BlockMetal.class);

//        ingotMap.put(STEEL, EnumIngot.STEEL);
//        ingotMap.put(COPPER, EnumIngot.COPPER);
//        ingotMap.put(TIN, EnumIngot.TIN);
//        ingotMap.put(LEAD, EnumIngot.LEAD);
//
//        nuggetMap.put(IRON, EnumNugget.IRON);
//        nuggetMap.put(STEEL, EnumNugget.STEEL);
//        nuggetMap.put(COPPER, EnumNugget.COPPER);
//        nuggetMap.put(TIN, EnumNugget.TIN);
//        nuggetMap.put(LEAD, EnumNugget.LEAD);

        poorOreMap.put(IRON, EnumOre.POOR_IRON);
        poorOreMap.put(GOLD, EnumOre.POOR_GOLD);
        poorOreMap.put(COPPER, EnumOre.POOR_COPPER);
        poorOreMap.put(TIN, EnumOre.POOR_TIN);
        poorOreMap.put(LEAD, EnumOre.POOR_LEAD);

        blockMap.put(STEEL, EnumCube.STEEL_BLOCK);
        blockMap.put(COPPER, EnumCube.COPPER_BLOCK);
        blockMap.put(TIN, EnumCube.TIN_BLOCK);
        blockMap.put(LEAD, EnumCube.LEAD_BLOCK);
    }

    public final IStackFilter nuggetFilter;
    public final IStackFilter ingotFilter;
    public final IStackFilter blockFilter;
    private final String oreSuffix;
    private final String tag;

    Metal(String oreSuffix) {
        this.oreSuffix = oreSuffix;
        this.tag = name().toLowerCase(Locale.ROOT);
        nuggetFilter = StackFilters.ofOreType(getOreTag(Form.NUGGET));
        ingotFilter = StackFilters.ofOreType(getOreTag(Form.INGOT));
        blockFilter = StackFilters.ofOreType(getOreTag(Form.BLOCK));
    }

//    public static Metal get(EnumNugget nugget) {
//        return nuggetMap.inverse().get(nugget);
//    }
//
//    public static Metal get(EnumIngot ingot) {
//        return ingotMap.inverse().get(ingot);
//    }

    public static Metal get(EnumOre ore) {
        return poorOreMap.inverse().get(ore);
    }

    public static Metal get(EnumCube ore) {
        return blockMap.inverse().get(ore);
    }

    @Override
    public boolean isValid(Class<?> clazz) {
        return metalObjects.contains(clazz);
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftObjectContainer container) {
        return formMap.inverse().get(container).getOreDictTag(this);
    }

    @Override
    public String getName() {
        return tag;
    }

    public String getOreTag(Form form) {
        return form.orePrefix + oreSuffix;
    }

    @Nullable
    public ItemStack getStack(Form form) {
        return getStack(form, 1);
    }

    @Nullable
    public ItemStack getStack(Form form, int qty) {
        return form.getStack(this, qty);
    }

    public enum Form {
        NUGGET("nugget", RailcraftItems.nugget) {
            @Nullable
            @Override
            public ItemStack getStack(Metal metal, int qty) {
                switch (metal) {
                    case GOLD:
                        return new ItemStack(Items.GOLD_NUGGET, qty);
                }
                return super.getStack(metal, qty);
            }
        },
        INGOT("ingot", RailcraftItems.ingot) {
            @Nullable
            @Override
            public ItemStack getStack(Metal metal, int qty) {
                switch (metal) {
                    case IRON:
                        return new ItemStack(Items.IRON_INGOT, qty);
                    case GOLD:
                        return new ItemStack(Items.GOLD_INGOT, qty);
                }
                return super.getStack(metal, qty);
            }
        },
        PLATE("plate", RailcraftItems.plate),
        BLOCK("block", RailcraftBlocks.cube, blockMap) {
            @Nullable
            @Override
            public ItemStack getStack(Metal metal, int qty) {
                switch (metal) {
                    case IRON:
                        return new ItemStack(Blocks.IRON_BLOCK, qty);
                    case GOLD:
                        return new ItemStack(Blocks.GOLD_BLOCK, qty);
                }
                return super.getStack(metal, qty);
            }
        },
        POOR_ORE("poorOre", RailcraftBlocks.ore, poorOreMap);
        public static Form[] VALUES = values();
        private final String orePrefix;
        private final IRailcraftObjectContainer container;
        private final BiMap<Metal, IVariantEnum> variantMap;

        Form(String orePrefix, IRailcraftObjectContainer container) {
            this(orePrefix, container, null);
        }

        Form(String orePrefix, IRailcraftObjectContainer container, @Nullable BiMap<Metal, IVariantEnum> variantMap) {
            this.orePrefix = orePrefix;
            this.container = container;
            this.variantMap = variantMap;
            formMap.put(this, container);
        }

        public final String getOreDictTag(Metal metal) {
            return metal.getOreTag(this);
        }

        @Nullable
        public IVariantEnum getVariantObject(Metal metal) {
            if (variantMap != null)
                return variantMap.get(metal);
            return metal;
        }

        @Nullable
        public final ItemStack getStack(Metal metal) {
            return getStack(metal, 1);
        }

        @Nullable
        public ItemStack getStack(Metal metal, int qty) {
            IVariantEnum variant = getVariantObject(metal);
            ItemStack stack = null;
            if (variant != null)
                stack = container.getStack(qty, variant);
            if (stack == null)
                stack = OreDictPlugin.getOre(getOreDictTag(metal), qty);
            return stack;
        }
    }
}
