/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntityCartGift extends EntityCartTNTWood {

    private static final byte SPAWN_DIST = 2;
    private static final List<Gift> gifts = new ArrayList<Gift>();
    private static final List<Integer> potions = new ArrayList<Integer>();

    static {
        gifts.add(new GiftPotion());
        gifts.add(new GiftPotion());
        gifts.add(new GiftPotion());
        gifts.add(new GiftPotion());
        gifts.add(new GiftPotion());

        addGift(Items.SNOWBALL, 16, 50);

        addGift(Blocks.cake, 25);
        addGift(Items.BOWL, 25);
        addGift(Items.COOKIE, 15, 75);
        addGift(Items.COOKIE, 10, 75);
        addGift(Items.COOKIE, 5, 75);
        addGift(Items.PUMPKIN_PIE, 20);

        addGift(Blocks.jukebox, 5);
        addGift(Items.PAINTING, 10);
        addGift(Items.FLOWER_POT, 25);
        addGift(Items.COMPASS, 25);
        addGift(Items.CLOCK, 25);
        addGift(RailcraftItems.crowbarIron, 20);
        addGift(RailcraftItems.crowbarSteel, 10);

        addGift(Items.EXPERIENCE_BOTTLE, 32, 5);
        addGift(Items.EXPERIENCE_BOTTLE, 16, 10);
        addGift(Items.EXPERIENCE_BOTTLE, 8, 20);
        addGift(Items.EXPERIENCE_BOTTLE, 4, 40);
        addGift(Items.EXPERIENCE_BOTTLE, 2, 80);

        addGift(Blocks.diamond_block, 1);
        addGift(Blocks.emerald_block, 2);
        addGift(Items.EMERALD, 30);
        addGift(Items.DIAMOND, 20);

        addGift(Items.GOLD_INGOT, 30);
        addGift(Items.GOLD_INGOT, 2, 30);

        addGift(Items.GOLD_NUGGET, 8, 80);
        addGift(Items.GOLD_NUGGET, 16, 40);
        addGift(Items.GOLD_NUGGET, 32, 20);

        addGift(Items.ENDER_PEARL, 30);
        addGift(Items.NETHER_STAR, 2);

        int recordChance = 1;

        addGift(Items.RECORD_11, recordChance);
        addGift(Items.RECORD_13, recordChance);
        addGift(Items.RECORD_BLOCKS, recordChance);
        addGift(Items.RECORD_CAT, recordChance);
        addGift(Items.RECORD_CHIRP, recordChance);
        addGift(Items.RECORD_FAR, recordChance);
        addGift(Items.RECORD_MALL, recordChance);
        addGift(Items.RECORD_MELLOHI, recordChance);
        addGift(Items.RECORD_STAL, recordChance);
        addGift(Items.RECORD_STRAD, recordChance);
        addGift(Items.RECORD_WARD, recordChance);
        addGift(Items.RECORD_WAIT, recordChance);

        int toolChance = 10;

        addGift(Items.FISHING_ROD, toolChance);
        addGift(Items.BOW, toolChance);
        addGift(Items.SHEARS, toolChance);

        addGift(Items.DIAMOND_AXE, toolChance);
        addGift(Items.DIAMOND_PICKAXE, toolChance);
        addGift(Items.DIAMOND_SHOVEL, toolChance);
        addGift(Items.DIAMOND_SWORD, toolChance);
        addGift(Items.DIAMOND_HOE, toolChance);

        addGift(Items.GOLDEN_AXE, toolChance);
        addGift(Items.GOLDEN_PICKAXE, toolChance);
        addGift(Items.GOLDEN_SHOVEL, toolChance);
        addGift(Items.GOLDEN_SWORD, toolChance);
        addGift(Items.GOLDEN_HOE, toolChance);

        addGift(Items.IRON_AXE, toolChance);
        addGift(Items.IRON_PICKAXE, toolChance);
        addGift(Items.IRON_SHOVEL, toolChance);
        addGift(Items.IRON_SWORD, toolChance);
        addGift(Items.IRON_HOE, toolChance);

        addGift(RailcraftToolItems.getSteelAxe(), toolChance);
        addGift(RailcraftToolItems.getSteelPickaxe(), toolChance);
        addGift(RailcraftToolItems.getSteelSword(), toolChance);
        addGift(RailcraftToolItems.getSteelShovel(), toolChance);
        addGift(RailcraftToolItems.getSteelHoe(), toolChance);

        int armorChance = 5;

        addGift(Items.DIAMOND_HELMET, armorChance);
        addGift(Items.DIAMOND_CHESTPLATE, armorChance);
        addGift(Items.DIAMOND_LEGGINGS, armorChance);
        addGift(Items.DIAMOND_BOOTS, armorChance);

        addGift(Items.GOLDEN_HELMET, armorChance);
        addGift(Items.GOLDEN_CHESTPLATE, armorChance);
        addGift(Items.GOLDEN_LEGGINGS, armorChance);
        addGift(Items.GOLDEN_BOOTS, armorChance);

        addGift(Items.IRON_HELMET, armorChance);
        addGift(Items.IRON_CHESTPLATE, armorChance);
        addGift(Items.IRON_LEGGINGS, armorChance);
        addGift(Items.IRON_BOOTS, armorChance);

        addGift(Items.LEATHER_HELMET, armorChance);
        addGift(Items.LEATHER_CHESTPLATE, armorChance);
        addGift(Items.LEATHER_LEGGINGS, armorChance);
        addGift(Items.LEATHER_BOOTS, armorChance);

        addGift(RailcraftToolItems.getSteelHelm(), armorChance);
        addGift(RailcraftToolItems.getSteelArmor(), armorChance);
        addGift(RailcraftToolItems.getSteelLegs(), armorChance);
        addGift(RailcraftToolItems.getSteelBoots(), armorChance);

        for (int meta = 0; meta <= 32767; ++meta) {
            List effects = PotionHelper.getPotionEffects(meta, false);

            if (effects != null && !effects.isEmpty())
                potions.add(meta);
        }
    }

    private EntityCartGift(World world) {
        super(world);
    }

    public EntityCartGift(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + getYOffset(), d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
        setBlastRadius(1.5f);
    }

    private static void addGift(ItemStack gift, int chance) {
        if (gift != null)
            gifts.add(new GiftItem(gift, chance));
    }

    private static void addGift(Item gift, int chance) {
        if (gift != null)
            gifts.add(new GiftItem(new ItemStack(gift), chance));
    }

    private static void addGift(Item gift, int stackSize, int chance) {
        if (gift != null)
            gifts.add(new GiftItem(new ItemStack(gift, stackSize), chance));
    }

    private static void addGift(Block gift, int chance) {
        if (gift != null)
            gifts.add(new GiftItem(new ItemStack(gift), chance));
    }

    private static void addGift(RailcraftItems gift, int chance) {
        if (gift != null)
            gifts.add(new GiftItem(gift.getStack(), chance));
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.GIFT;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        if (RailcraftConfig.doCartsBreakOnDrop()) {
            items.add(new ItemStack(Items.MINECART));
            items.add(new ItemStack(Blocks.pumpkin));
        } else
            items.add(getCartItem());
        return items;
    }

    @Override
    public Block func_145820_n() {
        return null;
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    @Override
    protected float getMinBlastRadius() {
        return 0.5f;
    }

    @Override
    protected float getMaxBlastRadius() {
        return 1;
    }

    @Override
    public void explode() {
        if (Game.isHost(getWorld())) {
            worldObj.createExplosion(this, posX, posY, posZ, getBlastRadius(), true);
            setDead();

            if (rand.nextInt(100) >= 50) {
                spawnGift();
                spawnGift();
            } else
                spawnCoal();
        }
    }

    private Gift generateGift() {
        while (true) {
            int index = rand.nextInt(gifts.size());
            Gift gift = gifts.get(index);
            int weight = rand.nextInt(100);
            if (gift.getChance() >= weight)
                return gift;
        }
    }

    private void spawnGift() {
        spawnItem(generateGift().getStack(rand));
    }

    private void spawnCoal() {
        spawnItem(new ItemStack(Items.COAL));
    }

    private void spawnItem(ItemStack stack) {
        double x = posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        double y = posY + 1 + rand.nextInt(3) - 1;
        double z = posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        InvTools.dropItem(stack, worldObj, x, y, z);
    }

    private interface Gift {

        ItemStack getStack(Random rand);

        int getChance();

    }

    private static class GiftItem implements Gift {

        public final int chance;
        public final ItemStack stack;

        GiftItem(ItemStack stack, int chance) {
            this.stack = stack;
            this.chance = chance;
        }

        @Override
        public ItemStack getStack(Random rand) {
            return stack.copy();
        }

        @Override
        public int getChance() {
            return chance;
        }

    }

    private static class GiftPotion implements Gift {

        @Override
        public ItemStack getStack(Random rand) {
            int meta = potions.get(rand.nextInt(potions.size()));
            return new ItemStack(Items.POTIONITEM, 1, meta);
        }

        @Override
        public int getChance() {
            return 100;
        }

    }

}
