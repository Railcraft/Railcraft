/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
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
@RailcraftBlockMetadata(variant = EnumOre.class)
public class BlockOre extends BlockRailcraftSubtyped<EnumOre> {

    private final Random rand = new Random();

    public BlockOre() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(getVariantProperty(), EnumOre.SULFUR));
        setResistance(5);
        setHardness(3);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void defineRecipes() {
        registerOreRecipe(Metal.COPPER);
        registerOreRecipe(Metal.TIN);
        registerOreRecipe(Metal.LEAD);
        registerOreRecipe(Metal.SILVER);

        registerPoorOreRecipe(Metal.COPPER);
        registerPoorOreRecipe(Metal.GOLD);
        registerPoorOreRecipe(Metal.IRON);
        registerPoorOreRecipe(Metal.TIN);
        registerPoorOreRecipe(Metal.LEAD);
        registerPoorOreRecipe(Metal.SILVER);
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

        for (EnumOre ore : EnumOre.VALUES) {
            ForestryPlugin.addBackpackItem("forestry.miner", ore.getItem());

            switch (ore) {
                case DARK_DIAMOND:
                case DARK_EMERALD:
                    HarvestPlugin.setStateHarvestLevel("pickaxe", 2, ore);
                    break;
                default:
                    HarvestPlugin.setStateHarvestLevel("pickaxe", 1, ore);
            }
        }

        RailcraftItems.DUST.register();

        registerOre("oreSulfur", EnumOre.SULFUR);
        registerOre("oreSaltpeter", EnumOre.SALTPETER);
        registerOre("oreDiamond", EnumOre.DARK_DIAMOND);
        registerOre("oreEmerald", EnumOre.DARK_EMERALD);
        registerOre("oreLapis", EnumOre.DARK_LAPIS);

        registerOre("orePoorGold", EnumOre.POOR_GOLD);
        registerOre("orePoorIron", EnumOre.POOR_IRON);
        registerOre("orePoorCopper", EnumOre.POOR_COPPER);
        registerOre("orePoorTin", EnumOre.POOR_TIN);
        registerOre("orePoorLead", EnumOre.POOR_LEAD);
        registerOre("orePoorSilver", EnumOre.POOR_SILVER);

        registerOre("oreCopper", EnumOre.COPPER);
        registerOre("oreTin", EnumOre.TIN);
        registerOre("oreLead", EnumOre.LEAD);
        registerOre("oreSilver", EnumOre.SILVER);
    }

    private static void registerOre(String name, EnumOre ore) {
        if (ore.isEnabled())
            OreDictionary.registerOre(name, ore.getItem());
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);

        switch (getVariant(state)) {
            case SULFUR:
            case SALTPETER:
            case DARK_DIAMOND:
            case DARK_EMERALD:
            case DARK_LAPIS: {
                int xp = MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
                dropXpOnBlockBreak(worldIn, pos, xp);
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        switch (getVariant(state)) {
            case SULFUR: {
                int qty = 2 + rand.nextInt(4) + rand.nextInt(fortune + 1);
                drops.add(RailcraftItems.DUST.getStack(qty, ItemDust.EnumDust.SULFUR));
                return drops;
            }
            case SALTPETER: {
                int qty = 1 + rand.nextInt(2) + rand.nextInt(fortune + 1);
                drops.add(RailcraftItems.DUST.getStack(qty, ItemDust.EnumDust.SALTPETER));
                return drops;
            }
            case DARK_DIAMOND: {
                int bonus = rand.nextInt(fortune + 2) - 1;
                if (bonus < 0)
                    bonus = 0;
                int qty = bonus + 1;
                drops.add(new ItemStack(Items.DIAMOND, qty));
                return drops;
            }
            case DARK_EMERALD: {
                int bonus = rand.nextInt(fortune + 2) - 1;
                if (bonus < 0)
                    bonus = 0;
                int qty = bonus + 1;
                drops.add(new ItemStack(Items.EMERALD, qty));
                return drops;
            }
            case DARK_LAPIS: {
                int bonus = rand.nextInt(fortune + 2) - 1;
                if (bonus < 0)
                    bonus = 0;
                int qty = (4 + rand.nextInt(5)) * (bonus + 1);
                drops.add(new ItemStack(Items.DYE, qty, 4));
                return drops;
            }
            default:
                return super.getDrops(world, pos, state, fortune);
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
