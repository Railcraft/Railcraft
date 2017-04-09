/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = EnumOreExtra.class)
public class BlockOreExtra extends BlockRailcraftSubtyped<EnumOreExtra> {

    private final Random rand = new Random();

    public BlockOreExtra() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(getVariantProperty(), EnumOreExtra.NICKEL));
        setResistance(5);
        setHardness(3);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void defineRecipes() {
        registerOreRecipe(Metal.NICKEL);

        registerPoorOreRecipe(Metal.NICKEL);
    }

    private static void registerPoorOreRecipe(Metal metal) {
        CraftingPlugin.addFurnaceRecipe(Metal.Form.POOR_ORE.getStack(metal), metal.getStack(Metal.Form.NUGGET, 2), 0.1F);
    }

    private static void registerOreRecipe(Metal metal) {
        CraftingPlugin.addFurnaceRecipe(Metal.Form.ORE.getStack(metal), metal.getStack(Metal.Form.INGOT), 0.7F);
    }

    @Override
    public void initializeDefinintion() {
        EntityTunnelBore.addMineableBlock(this);

        for (EnumOreExtra ore : EnumOreExtra.VALUES) {
            ForestryPlugin.addBackpackItem("forestry.miner", ore.getItem());

            switch (ore) {
                default:
                    HarvestPlugin.setStateHarvestLevel("pickaxe", 1, ore);
            }
        }

        RailcraftItems.DUST.register();


        registerOre("orePoorNickel", EnumOreExtra.POOR_NICKEL);

        registerOre("oreNickel", EnumOreExtra.NICKEL);
    }

    private static void registerOre(String name, EnumOreExtra ore) {
        if (ore.isEnabled())
            OreDictionary.registerOre(name, ore.getItem());
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
