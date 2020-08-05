/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@BlockMeta.Tile(TileBlastFurnace.class)
public final class BlockBlastFurnace extends BlockStructure<TileBlastFurnace> {

    public static final PropertyInteger ICON = PropertyInteger.create("icon", 0, 2);

    public BlockBlastFurnace() {
        super(Material.ROCK);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ICON);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 1);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 4);
        CraftingPlugin.addShapedRecipe(stack,
                "MBM",
                "BPB",
                "MBM",
                'B', new ItemStack(Blocks.NETHER_BRICK),
                'M', new ItemStack(Blocks.SOUL_SAND),
                'P', Items.MAGMA_CREAM);
        Crafters.rockCrusher().makeRecipe(this)
                .name("railcraft:recycle_blast_furnace")
                .addOutput(new ItemStack(Blocks.NETHER_BRICK), 0.75f)
                .addOutput(new ItemStack(Blocks.SOUL_SAND), 0.75f)
                .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.05f)
                .register();
    }
}
