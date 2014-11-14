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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import mods.railcraft.api.core.items.IToolCrowbar;
import mods.railcraft.common.blocks.tracks.BlockTrackElevator;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;

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
            ItemRegistry.registerItem(item);
            HarvestPlugin.setToolClass(item, "crowbar", 0);

            CraftingPlugin.addShapedRecipe(new ItemStack(item),
                    " RI",
                    "RIR",
                    "IR ",
                    'I', Items.iron_ingot,
                    'R', "dyeRed");

            LootPlugin.addLootTool(new ItemStack(item), 1, 1, ITEM_TAG);
            LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, ITEM_TAG);

            ItemRegistry.registerItemStack(ITEM_TAG, new ItemStack(item));
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
        if (!world.isRemote) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (!player.isSneaking()) {
                    if (TrackTools.isRailBlock(block)) {
                        int level = EnchantmentHelper.getEnchantmentLevel(RailcraftEnchantments.destruction.effectId, stack) * 2 + 1;
                        removeTracks(world, level, x, y, z);
                    }
                }
            }
        }
        if ((double)block.getBlockHardness(world, x, y, z) != 0.0D)
        {
            stack.damageItem(1, entity);
        }
        return true;
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

    private void removeTrackAndDrop(int x, int y, int z, World world) {
        Block block = world.getBlock(x, y, z);
        List<ItemStack> drops = block.getDrops(world, x, y, z, 0, 0);
        for (ItemStack stack : drops) {
            if (stack != null && stack.stackSize > 0) {
                EntityItem entityitem = new EntityItem(world, x, y + 1, z, stack);
                entityitem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityitem);
            }
        }
        world.setBlockToAir(x, y, z);
    }

    private void removeTracks(World world, int level, int x, int y, int z) {
        if (level > 0 ) {
            removeTrackAndDrop(x, y, z, world);
            checkBlocks(world, level, x, y, z);
        }
    }

    private void checkBlock(World world, int level, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (TrackTools.isRailBlock(block) || block instanceof BlockTrackElevator) {
            removeTracks(world, level - 1, x, y, z);
        }
    }

    private void checkBlocks(World world, int level, int x, int y, int z) {
        //NORTH
        checkBlock(world, level, x, y, z - 1);
        checkBlock(world, level, x, y + 1, z - 1);
        checkBlock(world, level, x, y - 1, z - 1);
        //SOUTH
        checkBlock(world, level, x, y, z + 1);
        checkBlock(world, level, x, y + 1, z + 1);
        checkBlock(world, level, x, y - 1, z + 1);
        //EAST
        checkBlock(world, level, x + 1, y, z);
        checkBlock(world, level, x + 1, y + 1, z);
        checkBlock(world, level, x + 1, y - 1, z);
        //WEST
        checkBlock(world, level, x - 1, y, z);
        checkBlock(world, level, x - 1, y + 1, z);
        checkBlock(world, level, x - 1, y - 1, z);
        //UP_DOWN
        checkBlock(world, level, x, y + 1, z);
        checkBlock(world, level, x, y - 1, z);
    }
}
