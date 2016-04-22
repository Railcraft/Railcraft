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

        addGift(Items.snowball, 16, 50);

        addGift(Blocks.cake, 25);
        addGift(Items.bowl, 25);
        addGift(Items.cookie, 15, 75);
        addGift(Items.cookie, 10, 75);
        addGift(Items.cookie, 5, 75);
        addGift(Items.pumpkin_pie, 20);

        addGift(Blocks.jukebox, 5);
        addGift(Items.painting, 10);
        addGift(Items.flower_pot, 25);
        addGift(Items.compass, 25);
        addGift(Items.clock, 25);
        addGift(RailcraftItems.crowbarIron, 20);
        addGift(RailcraftItems.crowbarSteel, 10);

        addGift(Items.experience_bottle, 32, 5);
        addGift(Items.experience_bottle, 16, 10);
        addGift(Items.experience_bottle, 8, 20);
        addGift(Items.experience_bottle, 4, 40);
        addGift(Items.experience_bottle, 2, 80);

        addGift(Blocks.diamond_block, 1);
        addGift(Blocks.emerald_block, 2);
        addGift(Items.emerald, 30);
        addGift(Items.diamond, 20);

        addGift(Items.gold_ingot, 30);
        addGift(Items.gold_ingot, 2, 30);

        addGift(Items.gold_nugget, 8, 80);
        addGift(Items.gold_nugget, 16, 40);
        addGift(Items.gold_nugget, 32, 20);

        addGift(Items.ender_pearl, 30);
        addGift(Items.nether_star, 2);

        int recordChance = 1;

        addGift(Items.record_11, recordChance);
        addGift(Items.record_13, recordChance);
        addGift(Items.record_blocks, recordChance);
        addGift(Items.record_cat, recordChance);
        addGift(Items.record_chirp, recordChance);
        addGift(Items.record_far, recordChance);
        addGift(Items.record_mall, recordChance);
        addGift(Items.record_mellohi, recordChance);
        addGift(Items.record_stal, recordChance);
        addGift(Items.record_strad, recordChance);
        addGift(Items.record_ward, recordChance);
        addGift(Items.record_wait, recordChance);

        int toolChance = 10;

        addGift(Items.fishing_rod, toolChance);
        addGift(Items.bow, toolChance);
        addGift(Items.shears, toolChance);

        addGift(Items.diamond_axe, toolChance);
        addGift(Items.diamond_pickaxe, toolChance);
        addGift(Items.diamond_shovel, toolChance);
        addGift(Items.diamond_sword, toolChance);
        addGift(Items.diamond_hoe, toolChance);

        addGift(Items.golden_axe, toolChance);
        addGift(Items.golden_pickaxe, toolChance);
        addGift(Items.golden_shovel, toolChance);
        addGift(Items.golden_sword, toolChance);
        addGift(Items.golden_hoe, toolChance);

        addGift(Items.iron_axe, toolChance);
        addGift(Items.iron_pickaxe, toolChance);
        addGift(Items.iron_shovel, toolChance);
        addGift(Items.iron_sword, toolChance);
        addGift(Items.iron_hoe, toolChance);

        addGift(RailcraftToolItems.getSteelAxe(), toolChance);
        addGift(RailcraftToolItems.getSteelPickaxe(), toolChance);
        addGift(RailcraftToolItems.getSteelSword(), toolChance);
        addGift(RailcraftToolItems.getSteelShovel(), toolChance);
        addGift(RailcraftToolItems.getSteelHoe(), toolChance);

        int armorChance = 5;

        addGift(Items.diamond_helmet, armorChance);
        addGift(Items.diamond_chestplate, armorChance);
        addGift(Items.diamond_leggings, armorChance);
        addGift(Items.diamond_boots, armorChance);

        addGift(Items.golden_helmet, armorChance);
        addGift(Items.golden_chestplate, armorChance);
        addGift(Items.golden_leggings, armorChance);
        addGift(Items.golden_boots, armorChance);

        addGift(Items.iron_helmet, armorChance);
        addGift(Items.iron_chestplate, armorChance);
        addGift(Items.iron_leggings, armorChance);
        addGift(Items.iron_boots, armorChance);

        addGift(Items.leather_helmet, armorChance);
        addGift(Items.leather_chestplate, armorChance);
        addGift(Items.leather_leggings, armorChance);
        addGift(Items.leather_boots, armorChance);

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
            items.add(new ItemStack(Items.minecart));
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
        spawnItem(new ItemStack(Items.coal));
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
            return new ItemStack(Items.potionitem, 1, meta);
        }

        @Override
        public int getChance() {
            return 100;
        }

    }

}
