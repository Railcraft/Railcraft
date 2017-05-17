/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import mods.railcraft.common.plugins.forge.WorldPlugin;
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

import javax.annotation.Nullable;
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
        setTickRandomly(true);
    }

    @Override
    public void defineRecipes() {
        registerOreRecipe(EnumOre.COPPER, Metal.COPPER);
        registerOreRecipe(EnumOre.TIN, Metal.TIN);
        registerOreRecipe(EnumOre.LEAD, Metal.LEAD);
        registerOreRecipe(EnumOre.SILVER, Metal.SILVER);

        registerPoorOreRecipe(EnumOre.POOR_COPPER, Metal.COPPER);
        registerPoorOreRecipe(EnumOre.POOR_GOLD, Metal.GOLD);
        registerPoorOreRecipe(EnumOre.POOR_IRON, Metal.IRON);
        registerPoorOreRecipe(EnumOre.POOR_TIN, Metal.TIN);
        registerPoorOreRecipe(EnumOre.POOR_LEAD, Metal.LEAD);
        registerPoorOreRecipe(EnumOre.POOR_SILVER, Metal.SILVER);
    }

    private static void registerPoorOreRecipe(EnumOre ore, Metal metal) {
        CraftingPlugin.addFurnaceRecipe(ore.getStack(), metal.getStack(Metal.Form.NUGGET, 2), 0.1F);
    }

    private static void registerOreRecipe(EnumOre ore, Metal metal) {
        CraftingPlugin.addFurnaceRecipe(ore.getStack(), metal.getStack(Metal.Form.INGOT), 0.7F);
    }

    @Override
    public void initializeDefinintion() {
        EntityTunnelBore.addMineableBlock(this);

        for (EnumOre ore : EnumOre.VALUES) {
            ForestryPlugin.addBackpackItem("forestry.miner", ore.getStack());

            switch (ore) {
                case DARK_DIAMOND:
                case DARK_EMERALD:
                    HarvestPlugin.setStateHarvestLevel("pickaxe", 2, ore);
                    break;
                default:
                    HarvestPlugin.setStateHarvestLevel("pickaxe", 1, ore);
            }
        }

        for (EnumOre ore : EnumOre.VALUES) {
            if (ore.isEnabled())
                OreDictionary.registerOre(ore.getOreTag(), ore.getStack());
        }
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
            case COPPER: {
                drops.add(EnumOreMetal.COPPER.getStack());
                return drops;
            }
            case TIN: {
                drops.add(EnumOreMetal.TIN.getStack());
                return drops;
            }
            case LEAD: {
                drops.add(EnumOreMetal.LEAD.getStack());
                return drops;
            }
            case SILVER: {
                drops.add(EnumOreMetal.SILVER.getStack());
                return drops;
            }
            case POOR_COPPER: {
                drops.add(EnumOreMetalPoor.COPPER.getStack());
                return drops;
            }
            case POOR_GOLD: {
                drops.add(EnumOreMetalPoor.GOLD.getStack());
                return drops;
            }
            case POOR_IRON: {
                drops.add(EnumOreMetalPoor.IRON.getStack());
                return drops;
            }
            case POOR_LEAD: {
                drops.add(EnumOreMetalPoor.LEAD.getStack());
                return drops;
            }
            case POOR_SILVER: {
                drops.add(EnumOreMetalPoor.SILVER.getStack());
                return drops;
            }
            case POOR_TIN: {
                drops.add(EnumOreMetalPoor.TIN.getStack());
                return drops;
            }
            default:
                return super.getDrops(world, pos, state, fortune);
        }
    }

    @Nullable
    @Override
    protected ItemStack createStackedBlock(IBlockState state) {
        switch (getVariant(state)) {
            case COPPER: {
                return EnumOreMetal.COPPER.getStack();
            }
            case TIN: {
                return EnumOreMetal.TIN.getStack();
            }
            case LEAD: {
                return EnumOreMetal.LEAD.getStack();
            }
            case SILVER: {
                return EnumOreMetal.SILVER.getStack();
            }
            case POOR_COPPER: {
                return EnumOreMetalPoor.COPPER.getStack();
            }
            case POOR_GOLD: {
                return EnumOreMetalPoor.GOLD.getStack();
            }
            case POOR_IRON: {
                return EnumOreMetalPoor.IRON.getStack();
            }
            case POOR_LEAD: {
                return EnumOreMetalPoor.LEAD.getStack();
            }
            case POOR_SILVER: {
                return EnumOreMetalPoor.SILVER.getStack();
            }
            case POOR_TIN: {
                return EnumOreMetalPoor.TIN.getStack();
            }
            default:
                return super.createStackedBlock(state);
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

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        EnumOre ore = getVariant(state);
        IBlockState newState = null;
        switch (ore) {
            case COPPER:
                newState = EnumOreMetal.COPPER.getDefaultState();
                break;
            case LEAD:
                newState = EnumOreMetal.LEAD.getDefaultState();
                break;
            case SILVER:
                newState = EnumOreMetal.SILVER.getDefaultState();
                break;
            case TIN:
                newState = EnumOreMetal.TIN.getDefaultState();
                break;
            case POOR_TIN:
                newState = EnumOreMetalPoor.TIN.getDefaultState();
                break;
            case POOR_GOLD:
                newState = EnumOreMetalPoor.GOLD.getDefaultState();
                break;
            case POOR_COPPER:
                newState = EnumOreMetalPoor.COPPER.getDefaultState();
                break;
            case POOR_IRON:
                newState = EnumOreMetalPoor.IRON.getDefaultState();
                break;
            case POOR_LEAD:
                newState = EnumOreMetalPoor.LEAD.getDefaultState();
                break;
            case POOR_SILVER:
                newState = EnumOreMetalPoor.SILVER.getDefaultState();
                break;
        }
        if (newState != null)
            WorldPlugin.setBlockState(worldIn, pos, newState);
    }
}
