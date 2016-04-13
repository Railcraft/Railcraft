/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import buildcraft.api.tools.IToolWrench;
import ic2.api.item.IBoxable;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.BlockTrackElevator;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ItemCrowbar extends ItemTool implements IToolCrowbar, IBoxable, IToolWrench, IRailcraftObject {

    private static final byte BOOST_DAMAGE = 3;
    private final Set<Class<? extends Block>> shiftRotations = new HashSet<Class<? extends Block>>();
    private final Set<Class<? extends Block>> bannedRotations = new HashSet<Class<? extends Block>>();

    protected ItemCrowbar(ToolMaterial material) {
        super(3, material, new HashSet<Block>(Arrays.asList(new Block[]{
                Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.activator_rail
        })));
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        shiftRotations.add(BlockLever.class);
        shiftRotations.add(BlockButton.class);
        shiftRotations.add(BlockChest.class);
        bannedRotations.add(BlockRailBase.class);

        setHarvestLevel("crowbar", 2);
    }

    @Override
    public float getDigSpeed(ItemStack stack, IBlockState state) {
        if (TrackTools.isRailBlock(state))
            return efficiencyOnProperMaterial;
        return super.getDigSpeed(stack, state);
    }

    @Override
    public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    private boolean isShiftRotation(Class<? extends Block> cls) {
        for (Class<? extends Block> shift : shiftRotations) {
            if (shift.isAssignableFrom(cls))
                return true;
        }
        return false;
    }

    private boolean isBannedRotation(Class<? extends Block> cls) {
        for (Class<? extends Block> banned : bannedRotations) {
            if (banned.isAssignableFrom(cls))
                return true;
        }
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        Block block = WorldPlugin.getBlock(world, pos);

        if (block == null)
            return false;

        if (player.isSneaking() != isShiftRotation(block.getClass()))
            return false;

        if (isBannedRotation(block.getClass()))
            return false;

        if (block.rotateBlock(world, pos, side)) {
            player.swingItem();
            return !world.isRemote;
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, BlockPos pos, EntityLivingBase entity) {
        if (!world.isRemote)
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (!player.isSneaking()) {
                    int level = EnchantmentHelper.getEnchantmentLevel(RailcraftEnchantments.destruction.effectId, stack) * 2 + 1;
                    if (level > 0)
                        checkBlocks(world, level, pos);
                }
            }
        return super.onBlockDestroyed(stack, world, block, pos, entity);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean canWrench(EntityPlayer player, BlockPos pos) {
        return true;
    }

    @Override
    public void wrenchUsed(EntityPlayer player, BlockPos pos) {
        player.getCurrentEquippedItem().damageItem(1, player);
        player.swingItem();
    }

    @Override
    public boolean canWrench(EntityPlayer player, Entity entity) {
        return true;
    }

    @Override
    public void wrenchUsed(EntityPlayer player, Entity entity) {
        player.getCurrentEquippedItem().damageItem(1, player);
        player.swingItem();
    }

    @Override
    public boolean canWhack(EntityPlayer player, ItemStack crowbar, BlockPos pos) {
        return true;
    }

    @Override
    public void onWhack(EntityPlayer player, ItemStack crowbar, BlockPos pos) {
        crowbar.damageItem(1, player);
        player.swingItem();
    }

    @Override
    public boolean canLink(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {
        return player.isSneaking();
    }

    @Override
    public void onLink(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {
        crowbar.damageItem(1, player);
        player.swingItem();
    }

    @Override
    public boolean canBoost(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {
        return !player.isSneaking();
    }

    @Override
    public void onBoost(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {
        crowbar.damageItem(BOOST_DAMAGE, player);
        player.swingItem();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean advInfo) {
        info.add(LocalizationPlugin.translate("item.railcraft.tool.crowbar.tip"));
    }

    private void removeAndDrop(World world, BlockPos pos, IBlockState state) {
        List<ItemStack> drops = state.getBlock().getDrops(world, pos, state, 0);
        InvTools.dropItems(drops, world, pos);
        world.setBlockToAir(pos);
    }

    private void removeExtraBlocks(World world, int level, BlockPos pos, IBlockState state) {
        if (level > 0) {
            removeAndDrop(world, pos, state);
            checkBlocks(world, level, pos);
        }
    }

    private void checkBlock(World world, int level, BlockPos pos) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (TrackTools.isRailBlock(state) || state.getBlock() instanceof BlockTrackElevator || state.getBlock().isToolEffective("crowbar", state))
            removeExtraBlocks(world, level - 1, pos, state);
    }

    private void checkBlocks(World world, int level, BlockPos pos) {
        //NORTH
        checkBlock(world, level, pos.add(0, 0, -1));
        checkBlock(world, level, pos.add(0, 1, -1));
        checkBlock(world, level, pos.add(0, -1, -1));
        //SOUTH
        checkBlock(world, level, pos.add(0, 0, 1));
        checkBlock(world, level, pos.add(0, 1, 1));
        checkBlock(world, level, pos.add(0, -1, 1));
        //EAST
        checkBlock(world, level, pos.add(1, 0, 0));
        checkBlock(world, level, pos.add(1, 1, 0));
        checkBlock(world, level, pos.add(1, -1, 0));
        //WEST
        checkBlock(world, level, pos.add(-1, 0, 0));
        checkBlock(world, level, pos.add(-1, 1, 0));
        checkBlock(world, level, pos.add(-1, -1, 0));
        //UP_DOWN
        checkBlock(world, level, pos.up());
        checkBlock(world, level, pos.down());
    }

    @Override
    public Object getRecipeObject(IVariantEnum meta) {
        return ORE_TAG;
    }

    @Override
    public void defineRecipes() {
    }

    @Override
    public void finalizeDefinition() {
    }

    @Override
    public void initializeDefinintion() {
        OreDictionary.registerOre(ORE_TAG, new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE));
    }
}