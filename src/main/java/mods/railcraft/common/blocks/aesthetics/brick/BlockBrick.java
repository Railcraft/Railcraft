/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.*;

@BlockMeta.Variant(BrickVariant.class)
public class BlockBrick extends BlockRailcraftSubtyped<BrickVariant> {
    private final BrickTheme theme;

    public BlockBrick(BrickTheme theme) {
        super(Material.ROCK);
        this.theme = theme;
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), BrickVariant.PAVER));
        setResistance(15);
        setHardness(5);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativePlugin.STRUCTURE_TAB);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public void initializeDefinition() {
        ForestryPlugin.addBackpackItem("forestry.builder", this);
        theme.initBlock(this);

        for (BrickVariant variant : BrickVariant.VALUES) {
            theme.initVariant(this, variant);
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(getStack(4, BRICK),
                "II",
                "II",
                'I', getStack(POLISHED));
        CraftingPlugin.addShapedRecipe(getStack(4, PAVER),
                "II",
                "II",
                'I', getStack(BRICK));
        CraftingPlugin.addShapedRecipe(getStack(8, CHISELED),
                "III",
                "I I",
                "III",
                'I', getStack(POLISHED));
        CraftingPlugin.addShapedRecipe(getStack(8, ETCHED),
                "III",
                "IGI",
                "III",
                'G', new ItemStack(Items.GUNPOWDER),
                'I', getStack(POLISHED));

        Crafters.rockCrusher().makeRecipe(this)
                .addOutput(getStack(COBBLE))
                .register();

        CraftingPlugin.addFurnaceRecipe(getStack(COBBLE), getStack(POLISHED), 0.0F);
        theme.initRecipes(this);
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant instanceof BrickVariant) {
            checkVariant(variant);
            IBlockState newState = theme.getState((BrickVariant) variant);
            if (newState != null)
                state = newState;
        }
        return state;
    }

    @Override
    public ItemStack getStack(int qty, @Nullable IVariantEnum variant) {
        if (variant != null) {
            checkVariant(variant);
            return theme.getStack(qty, variant);
        }
        return new ItemStack(this, qty);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BrickVariant variant : BrickVariant.VALUES) {
            CreativePlugin.addToList(list, theme.getStack(1, variant));
        }
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(BrickVariant.VALUES.length, 1);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @SuppressWarnings("deprecation")
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return theme.getMapColor();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
