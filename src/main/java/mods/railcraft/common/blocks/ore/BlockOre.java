/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
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
@BlockMeta.Variant(EnumOre.class)
public class BlockOre extends BlockRailcraftSubtyped<EnumOre> {

    private final Random rand = new Random();

    public BlockOre() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), EnumOre.SULFUR));
        setResistance(5);
        setHardness(3);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
    }

    @Override
    public void initializeDefinition() {
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
                int xp = MathHelper.getInt(worldIn.rand, 2, 5);
                dropXpOnBlockBreak(worldIn, pos, xp);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

}
