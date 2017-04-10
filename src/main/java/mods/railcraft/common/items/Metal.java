/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.ore.EnumOreMetal;
import mods.railcraft.common.blocks.ore.EnumOreMetalPoor;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum Metal implements IVariantEnum {

    STEEL("Steel"),
    IRON("Iron"),
    GOLD("Gold"),
    COPPER("Copper"),
    TIN("Tin"),
    LEAD("Lead"),
    SILVER("Silver"),
    BRONZE("Bronze"),
    NICKEL("Nickel"),
    INVAR("Invar");
    public static final Metal[] VALUES = values();
    public static final Metal[] CLASSIC_METALS = {IRON, GOLD, COPPER, TIN, LEAD, SILVER};
    private static final BiMap<Metal, IVariantEnum> oreMap = HashBiMap.create();
    private static final BiMap<Metal, IVariantEnum> poorOreMap = HashBiMap.create();
    private static final BiMap<Metal, IVariantEnum> blockMap = HashBiMap.create();

    public static void init() {
        oreMap.put(COPPER, EnumOreMetal.COPPER);
        oreMap.put(TIN, EnumOreMetal.TIN);
        oreMap.put(LEAD, EnumOreMetal.LEAD);
        oreMap.put(SILVER, EnumOreMetal.SILVER);
        oreMap.put(NICKEL, EnumOreMetal.NICKEL);

        poorOreMap.put(IRON, EnumOreMetalPoor.IRON);
        poorOreMap.put(GOLD, EnumOreMetalPoor.GOLD);
        poorOreMap.put(COPPER, EnumOreMetalPoor.COPPER);
        poorOreMap.put(TIN, EnumOreMetalPoor.TIN);
        poorOreMap.put(LEAD, EnumOreMetalPoor.LEAD);
        poorOreMap.put(SILVER, EnumOreMetalPoor.SILVER);
        poorOreMap.put(NICKEL, EnumOreMetalPoor.NICKEL);

        blockMap.put(STEEL, EnumGeneric.BLOCK_STEEL);
        blockMap.put(COPPER, EnumGeneric.BLOCK_COPPER);
        blockMap.put(TIN, EnumGeneric.BLOCK_TIN);
        blockMap.put(LEAD, EnumGeneric.BLOCK_LEAD);
        blockMap.put(SILVER, EnumGeneric.BLOCK_SILVER);
        blockMap.put(BRONZE, EnumGeneric.BLOCK_BRONZE);
        blockMap.put(NICKEL, EnumGeneric.BLOCK_NICKEL);
        blockMap.put(INVAR, EnumGeneric.BLOCK_INVAR);
    }

    public final Predicate<ItemStack> nuggetFilter;
    public final Predicate<ItemStack> ingotFilter;
    public final Predicate<ItemStack> blockFilter;
    private final String oreSuffix;
    private final String tag;

    Metal(String oreSuffix) {
        this.oreSuffix = oreSuffix;
        this.tag = name().toLowerCase(Locale.ROOT);
        nuggetFilter = StackFilters.ofOreType("nugget" + oreSuffix);
        ingotFilter = StackFilters.ofOreType("ingot" + oreSuffix);
        blockFilter = StackFilters.ofOreType("block" + oreSuffix);
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return Form.containerMap.inverse().get(container).getOreDictTag(this);
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

    @Nullable
    public IBlockState getState(Form form) {
        return form.getState(this);
    }

    public enum Form {
        NUGGET("nugget", RailcraftItems.NUGGET) {
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
        INGOT("ingot", RailcraftItems.INGOT) {
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
        PLATE("plate", RailcraftItems.PLATE) {
            @Override
            public String getOreDictTag(Metal metal) {
                return null;
            }
        },
        BLOCK("block", RailcraftBlocks.GENERIC, blockMap) {
            @Nullable
            @Override
            public IBlockState getState(Metal metal) {
                switch (metal) {
                    case IRON:
                        return Blocks.IRON_BLOCK.getDefaultState();
                    case GOLD:
                        return Blocks.GOLD_BLOCK.getDefaultState();
                }
                return super.getState(metal);
            }

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
        ORE("ore", RailcraftBlocks.ORE_METAL, oreMap) {
            @Nullable
            @Override
            public IBlockState getState(Metal metal) {
                switch (metal) {
                    case IRON:
                        return Blocks.IRON_ORE.getDefaultState();
                    case GOLD:
                        return Blocks.GOLD_ORE.getDefaultState();
                    case STEEL:
                    case BRONZE:
                    case INVAR:
                        return null;
                }
                return super.getState(metal);
            }

            @Nullable
            @Override
            public ItemStack getStack(Metal metal, int qty) {
                switch (metal) {
                    case IRON:
                        return new ItemStack(Blocks.IRON_ORE, qty);
                    case GOLD:
                        return new ItemStack(Blocks.GOLD_ORE, qty);
                }
                return super.getStack(metal, qty);
            }
        },
        POOR_ORE("poorOre", RailcraftBlocks.ORE_METAL_POOR, poorOreMap);
        private static final BiMap<Form, IRailcraftRecipeIngredient> containerMap = HashBiMap.create();
        public static Form[] VALUES = values();
        private final String orePrefix;
        protected final IRailcraftObjectContainer container;
        private final BiMap<Metal, IVariantEnum> variantMap;

        static {
            containerMap.put(NUGGET, NUGGET.container);
            containerMap.put(INGOT, INGOT.container);
        }

        Form(String orePrefix, IRailcraftObjectContainer container) {
            this(orePrefix, container, null);
        }

        Form(String orePrefix, IRailcraftObjectContainer container, @Nullable BiMap<Metal, IVariantEnum> variantMap) {
            this.orePrefix = orePrefix;
            this.container = container;
            this.variantMap = variantMap;
        }

        @Nullable
        public String getOreDictTag(Metal metal) {
            return metal.getOreTag(this);
        }

        @Nullable
        public IVariantEnum getVariantObject(Metal metal) {
            if (variantMap != null)
                return variantMap.get(metal);
            return metal;
        }

        @Nullable
        public IBlockState getState(Metal metal) {
            IVariantEnum variant = getVariantObject(metal);
            if (variant != null && container instanceof IRailcraftBlockContainer)
                return ((IRailcraftBlockContainer) container).getState(variant);
            return null;
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
            if (stack == null) {
                String oreTag = getOreDictTag(metal);
                if (oreTag != null)
                    stack = OreDictPlugin.getOre(oreTag, qty);
            }
            return stack;
        }
    }
}
