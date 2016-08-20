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
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemCrowbar extends ItemTool implements IToolCrowbar, IBoxable, IToolWrench {

    public static final byte BOOST_DAMAGE = 3;
    private static final String ITEM_TAG = "railcraft.tool.crowbar";
    private static Item item;
    private final Set<Class<? extends Block>> shiftRotations = new HashSet<Class<? extends Block>>();
    private final Set<Class<? extends Block>> bannedRotations = new HashSet<Class<? extends Block>>();

    public static void registerItem() {
        if (item == null && RailcraftConfig.isItemEnabled(ITEM_TAG)) {
            item = new ItemCrowbar(ToolMaterial.IRON);
            item.setUnlocalizedName(ITEM_TAG);
            RailcraftRegistry.register(item);

            CraftingPlugin.addShapedRecipe(new ItemStack(item),
                    " RI",
                    "RIR",
                    "IR ",
                    'I', "ingotIron",
                    'R', "dyeRed");

            LootPlugin.addLootTool(new ItemStack(item), 1, 1, ITEM_TAG);
            LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, ITEM_TAG);
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public static Item getItemObj() {
        return item;
    }

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
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        if (TrackTools.isRailBlock(block))
            return efficiencyOnProperMaterial;
        return super.getDigSpeed(stack, block, meta);
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:" + MiscTools.cleanTag(getUnlocalizedName()));
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
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block == null)
            return false;

        if (player.isSneaking() != isShiftRotation(block.getClass()))
            return false;

        if (isBannedRotation(block.getClass()))
            return false;

        if (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
            player.swingItem();
            return !world.isRemote;
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
        if (!world.isRemote)
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (!player.isSneaking()) {
                    int level = EnchantmentHelper.getEnchantmentLevel(RailcraftEnchantments.destruction.effectId, stack) * 2 + 1;
                    if (level > 0)
                        checkBlocks(world, level, x, y, z, entity, stack);
                }
            }
        return super.onBlockDestroyed(stack, world, block, x, y, z, entity);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
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
    public boolean canWrench(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    @Override
    public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
        player.getCurrentEquippedItem().damageItem(1, player);
        player.swingItem();
    }

    @Override
    public boolean canWhack(EntityPlayer player, ItemStack crowbar, int x, int y, int z) {
        return true;
    }

    @Override
    public void onWhack(EntityPlayer player, ItemStack crowbar, int x, int y, int z) {
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
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advInfo) {
        info.add(LocalizationPlugin.translate("item.railcraft.tool.crowbar.tip"));
    }

    private void removeAndDrop(World world, int x, int y, int z, Block block, EntityLivingBase entity, ItemStack stack, int meta) {
        if (!ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) entity).theItemInWorldManager.getGameType(), (EntityPlayerMP) entity, x, y, z).isCanceled()) {
            InvTools.dropItems(block.getDrops(world, x, y, z, meta, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack)), world, x, y, z);
            world.setBlockToAir(x, y, z);
        }
    }

    private void removeExtraBlocks(World world, int level, int x, int y, int z, Block block, EntityLivingBase entity, ItemStack stack, int meta) {
        if (level > 0) {
            removeAndDrop(world, x, y, z, block, entity, stack, meta);
            checkBlocks(world, level, x, y, z, entity, stack);
        }
    }

    private void checkBlock(World world, int level, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        Block block = WorldPlugin.getBlock(world, x, y, z);
        int meta = WorldPlugin.getBlockMetadata(world, x, y, z);
        if (TrackTools.isRailBlock(block) || block instanceof BlockTrackElevator || block.isToolEffective("crowbar", meta))
            removeExtraBlocks(world, level - 1, x, y, z, block, entity, stack, meta);
    }

    private void checkBlocks(World world, int level, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        //NORTH
        checkBlock(world, level, x, y, z - 1, entity, stack);
        checkBlock(world, level, x, y + 1, z - 1, entity, stack);
        checkBlock(world, level, x, y - 1, z - 1, entity, stack);
        //SOUTH
        checkBlock(world, level, x, y, z + 1, entity, stack);
        checkBlock(world, level, x, y + 1, z + 1, entity, stack);
        checkBlock(world, level, x, y - 1, z + 1, entity, stack);
        //EAST
        checkBlock(world, level, x + 1, y, z, entity, stack);
        checkBlock(world, level, x + 1, y + 1, z, entity, stack);
        checkBlock(world, level, x + 1, y - 1, z, entity, stack);
        //WEST
        checkBlock(world, level, x - 1, y, z, entity, stack);
        checkBlock(world, level, x - 1, y + 1, z, entity, stack);
        checkBlock(world, level, x - 1, y - 1, z, entity, stack);
        //UP_DOWN
        checkBlock(world, level, x, y + 1, z, entity, stack);
        checkBlock(world, level, x, y - 1, z, entity, stack);
    }

}