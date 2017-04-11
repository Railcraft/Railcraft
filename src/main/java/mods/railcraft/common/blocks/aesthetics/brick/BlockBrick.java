/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.List;

import static mods.railcraft.common.blocks.aesthetics.brick.BrickVariant.*;

@RailcraftBlockMetadata(variant = BrickVariant.class)
public class BlockBrick extends BlockRailcraftSubtyped<BrickVariant> {
    private final BrickTheme theme;

    public BlockBrick(BrickTheme theme) {
        super(Material.ROCK);
        this.theme = theme;
        setDefaultState(blockState.getBaseState().withProperty(getVariantProperty(), BrickVariant.BRICK));
        setResistance(15);
        setHardness(5);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public void initializeDefinintion() {
        ForestryPlugin.addBackpackItem("forestry.builder", this);
        theme.initBlock(this);

        for (BrickVariant variant : BrickVariant.VALUES) {
            theme.initVariant(this, variant);
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(getStack(BRICK), getStack(FITTED));
        CraftingPlugin.addShapelessRecipe(getStack(FITTED), getStack(BLOCK));
        CraftingPlugin.addRecipe(getStack(8, ORNATE),
                "III",
                "I I",
                "III",
                'I', getStack(BLOCK));
        CraftingPlugin.addShapelessRecipe(getStack(ETCHED), getStack(BLOCK), new ItemStack(Items.GUNPOWDER));

        ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(new ItemStack(this), false, false);
        recipe.addOutput(getStack(COBBLE), 1.0F);

        CraftingPlugin.addFurnaceRecipe(getStack(COBBLE), getStack(BLOCK), 0.0F);
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
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BrickVariant variant : BrickVariant.VALUES) {
            list.add(theme.getStack(1, variant));
        }
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(BrickVariant.VALUES.length, 1);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @Override
    public MapColor getMapColor(IBlockState state) {
        return theme.getMapColor();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
