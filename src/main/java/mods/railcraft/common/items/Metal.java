/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.metals.EnumMetal;
import mods.railcraft.common.blocks.ore.EnumOreMetal;
import mods.railcraft.common.blocks.ore.EnumOreMetalPoor;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
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
    INVAR("Invar"),
    ZINC("Zinc"),
    BRASS("Brass"),
    ;
    public static final Metal[] VALUES = values();
    public static final Metal[] CLASSIC_METALS = {IRON, GOLD, COPPER, TIN, LEAD, SILVER};
    static final BiMap<Metal, IVariantEnum> oreMap = HashBiMap.create();
    static final BiMap<Metal, IVariantEnum> poorOreMap = HashBiMap.create();
    static final BiMap<Metal, IVariantEnum> blockMap = HashBiMap.create();

    public static void init() {
        oreMap.put(COPPER, EnumOreMetal.COPPER);
        oreMap.put(TIN, EnumOreMetal.TIN);
        oreMap.put(LEAD, EnumOreMetal.LEAD);
        oreMap.put(SILVER, EnumOreMetal.SILVER);
        oreMap.put(NICKEL, EnumOreMetal.NICKEL);
        oreMap.put(ZINC, EnumOreMetal.ZINC);

        poorOreMap.put(IRON, EnumOreMetalPoor.IRON);
        poorOreMap.put(GOLD, EnumOreMetalPoor.GOLD);
        poorOreMap.put(COPPER, EnumOreMetalPoor.COPPER);
        poorOreMap.put(TIN, EnumOreMetalPoor.TIN);
        poorOreMap.put(LEAD, EnumOreMetalPoor.LEAD);
        poorOreMap.put(SILVER, EnumOreMetalPoor.SILVER);
        poorOreMap.put(NICKEL, EnumOreMetalPoor.NICKEL);
        poorOreMap.put(ZINC, EnumOreMetalPoor.ZINC);

        blockMap.put(STEEL, EnumMetal.BLOCK_STEEL);
        blockMap.put(COPPER, EnumMetal.BLOCK_COPPER);
        blockMap.put(TIN, EnumMetal.BLOCK_TIN);
        blockMap.put(LEAD, EnumMetal.BLOCK_LEAD);
        blockMap.put(SILVER, EnumMetal.BLOCK_SILVER);
        blockMap.put(BRONZE, EnumMetal.BLOCK_BRONZE);
        blockMap.put(NICKEL, EnumMetal.BLOCK_NICKEL);
        blockMap.put(INVAR, EnumMetal.BLOCK_INVAR);
        blockMap.put(ZINC, EnumMetal.BLOCK_ZINC);
        blockMap.put(BRASS, EnumMetal.BLOCK_BRASS);
    }

    public final Predicate<ItemStack> nuggetFilter;
    public final Predicate<ItemStack> ingotFilter;
    public final Predicate<ItemStack> blockFilter;
    public final Predicate<ItemStack> plateFilter;
    private final String oreSuffix;
    private final String tag;

    Metal(String oreSuffix) {
        this.oreSuffix = oreSuffix;
        this.tag = name().toLowerCase(Locale.ROOT);
        nuggetFilter = StackFilters.ofOreType("nugget" + oreSuffix);
        ingotFilter = StackFilters.ofOreType("ingot" + oreSuffix);
        blockFilter = StackFilters.ofOreType("block" + oreSuffix);
        plateFilter = StackFilters.ofOreType("plate" + oreSuffix);
    }

    @Override
    public Ingredient getAlternate(IIngredientSource container) {
        Form form = Form.containerMap.inverse().get(container);
        return form != null ? Ingredients.from(form.getOreDictTag(this)) : Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return tag;
    }

    public String getOreTag(Form form) {
        return form.orePrefix + oreSuffix;
    }

    public ItemStack getStack(Form form) {
        return getStack(form, 1);
    }

    public ItemStack getStack(Form form, int qty) {
        return form.getStack(this, qty);
    }

    public @Nullable IBlockState getState(Form form) {
        return form.getState(this);
    }

    public enum Form implements IIngredientSource {
        NUGGET("nugget", RailcraftItems.NUGGET) {
            @Override
            public ItemStack getStack(Metal metal, int qty) {
                switch (metal) {
                    case IRON:
                        return new ItemStack(Items.IRON_NUGGET, qty);
                    case GOLD:
                        return new ItemStack(Items.GOLD_NUGGET, qty);
                }
                return super.getStack(metal, qty);
            }
        },
        INGOT("ingot", RailcraftItems.INGOT) {
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
        },
        BLOCK("block", RailcraftBlocks.METAL, blockMap) {
            @Override
            public @Nullable IBlockState getState(Metal metal) {
                switch (metal) {
                    case IRON:
                        return Blocks.IRON_BLOCK.getDefaultState();
                    case GOLD:
                        return Blocks.GOLD_BLOCK.getDefaultState();
                }
                return super.getState(metal);
            }

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
            @Override
            public @Nullable IBlockState getState(Metal metal) {
                switch (metal) {
                    case IRON:
                        return Blocks.IRON_ORE.getDefaultState();
                    case GOLD:
                        return Blocks.GOLD_ORE.getDefaultState();
                    case STEEL:
                    case BRONZE:
                    case INVAR:
                    case BRASS:
                        return null;
                }
                return super.getState(metal);
            }

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
        POOR_ORE("orePoor", RailcraftBlocks.ORE_METAL_POOR, poorOreMap) {
        };
        static final BiMap<Form, IIngredientSource> containerMap = HashBiMap.create();
        public static final Form[] VALUES = values();
        final String orePrefix;
        protected final IRailcraftObjectContainer<?> container;
        private final BiMap<Metal, IVariantEnum> variantMap;

        static {
            for (Form form : Form.VALUES) {
                containerMap.put(form, form.container);
            }
        }

        Form(String orePrefix, IRailcraftObjectContainer<?> container) {
            this(orePrefix, container, null);
        }

        Form(String orePrefix, IRailcraftObjectContainer<?> container, @Nullable BiMap<Metal, IVariantEnum> variantMap) {
            this.orePrefix = orePrefix;
            this.container = container;
            this.variantMap = variantMap;
        }

        public @Nullable String getOreDictTag(Metal metal) {
            return metal.getOreTag(this);
        }

        public @Nullable IVariantEnum getVariantObject(Metal metal) {
            if (variantMap != null)
                return variantMap.get(metal);
            return metal;
        }

        public @Nullable IBlockState getState(Metal metal) {
            IVariantEnum variant = getVariantObject(metal);
            if (variant != null && container instanceof IRailcraftBlockContainer)
                return ((IRailcraftBlockContainer) container).getState(variant);
            return null;
        }

        public final ItemStack getStack(Metal metal) {
            return getStack(metal, 1);
        }

        public ItemStack getStack(Metal metal, int qty) {
            IVariantEnum variant = getVariantObject(metal);
            ItemStack stack = InvTools.emptyStack();
            if (variant != null)
                stack = container.getStack(qty, variant);
            if (InvTools.isEmpty(stack)) {
                String oreTag = getOreDictTag(metal);
                if (oreTag != null)
                    stack = OreDictPlugin.getOre(oreTag, qty);
            }
            return stack;
        }

        @Override
        public Ingredient getIngredient() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Ingredient getIngredient(@Nullable IVariantEnum variant) {
            Objects.requireNonNull(variant);
            return Ingredients.from(getOreDictTag((Metal) variant));
        }
    }
}
