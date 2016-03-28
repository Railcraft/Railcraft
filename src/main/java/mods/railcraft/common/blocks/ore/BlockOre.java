/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.ore;

import mods.railcraft.client.particles.ParticleHelper;
import mods.railcraft.client.particles.ParticleHelperCallback;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockOre extends Block {

    public static final PropertyEnum<EnumOre> VARIANT = PropertyEnum.create("variant", EnumOre.class);
    private static final ParticleHelperCallback callback = new ParticleCallback();
    public static int renderPass;
    private static BlockOre instance;
    private final Random rand = new Random();

    public BlockOre() {
        super(Material.rock);
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumOre.SULFUR));
        setRegistryName("railcraft.ore");
        setResistance(5);
        setHardness(3);
        setStepSound(Block.soundTypeStone);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Nullable
    public static BlockOre getBlock() {
        return instance;
    }

    @Nullable
    public static IBlockState getState(EnumOre ore) {
        if (instance == null)
            return null;
        return instance.getDefaultState().withProperty(VARIANT, ore);
    }

    public static void registerBlock() {
        if (instance == null && RailcraftConfig.isBlockEnabled("ore")) {
            instance = new BlockOre();
            RailcraftRegistry.register(instance, ItemOre.class);

            EntityTunnelBore.addMineableBlock(instance);

            for (EnumOre ore : EnumOre.values()) {
                ForestryPlugin.addBackpackItem("miner", ore.getItem());

                switch (ore) {
                    case FIRESTONE:
                        HarvestPlugin.setStateHarvestLevel("pickaxe", 3, ore);
                        break;
                    case DARK_LAPIS:
                    case POOR_IRON:
                    case POOR_TIN:
                    case POOR_COPPER:
                        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, ore);
                        break;
                    default:
                        HarvestPlugin.setStateHarvestLevel("pickaxe", 2, ore);
                }
            }

            RailcraftItem.dust.registerItem();

            registerOre("oreSulfur", EnumOre.SULFUR);
            registerOre("oreSaltpeter", EnumOre.SALTPETER);
            registerOre("oreDiamond", EnumOre.DARK_DIAMOND);
            registerOre("oreEmerald", EnumOre.DARK_EMERALD);
            registerOre("oreLapis", EnumOre.DARK_LAPIS);
            registerOre("oreFirestone", EnumOre.FIRESTONE);
            registerOre("orePoorCopper", EnumOre.POOR_COPPER);
            registerOre("orePoorGold", EnumOre.POOR_GOLD);
            registerOre("orePoorIron", EnumOre.POOR_IRON);
            registerOre("orePoorTin", EnumOre.POOR_TIN);
            registerOre("orePoorLead", EnumOre.POOR_LEAD);

            registerPoorOreRecipe(Metal.COPPER);
            registerPoorOreRecipe(Metal.GOLD);
            registerPoorOreRecipe(Metal.IRON);
            registerPoorOreRecipe(Metal.TIN);
            registerPoorOreRecipe(Metal.LEAD);
        }
    }

    private static void registerPoorOreRecipe(Metal metal) {
        CraftingPlugin.addFurnaceRecipe(metal.getPoorOre(), metal.getNugget(2), 0.1F);
    }

    private static void registerOre(String name, EnumOre ore) {
        if (!ore.isDepreciated() && ore.isEnabled())
            OreDictionary.registerOre(name, ore.getItem());
    }

    public EnumOre getVariant(IBlockState state) {
        return state.getValue(VARIANT);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumOre ore : EnumOre.values()) {
            if (!ore.isDepreciated() && ore.isEnabled())
                list.add(ore.getItem());
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return getVariant(state).getItem();
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
                drops.add(RailcraftItem.dust.getStack(qty, ItemDust.EnumDust.SULFUR));
                return drops;
            }
            case SALTPETER: {
                int qty = 1 + rand.nextInt(2) + rand.nextInt(fortune + 1);
                drops.add(RailcraftItem.dust.getStack(qty, ItemDust.EnumDust.SALTPETER));
                return drops;
            }
            case DARK_DIAMOND: {
                int bonus = rand.nextInt(fortune + 2) - 1;
                if (bonus < 0)
                    bonus = 0;
                int qty = bonus + 1;
                drops.add(new ItemStack(Items.diamond, qty));
                return drops;
            }
            case DARK_EMERALD: {
                int bonus = rand.nextInt(fortune + 2) - 1;
                if (bonus < 0)
                    bonus = 0;
                int qty = bonus + 1;
                drops.add(new ItemStack(Items.emerald, qty));
                return drops;
            }
            case DARK_LAPIS: {
                int bonus = rand.nextInt(fortune + 2) - 1;
                if (bonus < 0)
                    bonus = 0;
                int qty = (4 + rand.nextInt(5)) * (bonus + 1);
                drops.add(new ItemStack(Items.dye, qty, 4));
                return drops;
            }
            default:
                return super.getDrops(world, pos, state, fortune);
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getVariant(state).ordinal();
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return ParticleHelper.addHitEffects(worldObj, instance, target, effectRenderer, callback);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        return ParticleHelper.addDestroyEffects(world, instance, pos, state, effectRenderer, callback);
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        if (EnumOre.FIRESTONE == getVariant(WorldPlugin.getBlockState(world, pos)))
            return 15;
        return super.getLightValue(world, pos);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumOre.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, VARIANT);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    private static class ParticleCallback implements ParticleHelperCallback {
        @Override
        @SideOnly(Side.CLIENT)
        public void addHitEffects(EntityDiggingFX fx, World world, BlockPos pos, IBlockState state) {
            setTexture(fx, world, pos, state);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addDestroyEffects(EntityDiggingFX fx, World world, BlockPos pos, IBlockState state) {
            setTexture(fx, world, pos, state);
        }

        @SideOnly(Side.CLIENT)
        private void setTexture(EntityDiggingFX fx, World world, BlockPos pos, IBlockState state) {
            renderPass = 0;
            fx.setParticleIcon(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
        }
    }
}
