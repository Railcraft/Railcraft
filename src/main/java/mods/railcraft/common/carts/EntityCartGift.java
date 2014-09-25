/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemCrowbar;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class EntityCartGift extends EntityCartTNTWood {

    private static final byte SPAWN_DIST = 2;
    private static final List<Gift> gifts = new ArrayList<Gift>();
    private static final List<Integer> potions = new ArrayList<Integer>();

    private static class Gift {

        public final int chance;
        public final ItemStack stack;

        public Gift(ItemStack stack, int chance) {
            this.stack = stack;
            this.chance = chance;
        }

    }

    static {
        gifts.add(new Gift(null /*potion*/, 100));
        gifts.add(new Gift(null /*potion*/, 100));
        gifts.add(new Gift(null /*potion*/, 100));
        gifts.add(new Gift(null /*potion*/, 100));
        gifts.add(new Gift(null /*potion*/, 100));

        gifts.add(new Gift(new ItemStack(Items.snowball, 16), 50));

        gifts.add(new Gift(new ItemStack(Blocks.cake), 25));
        gifts.add(new Gift(new ItemStack(Items.bowl), 25));
        gifts.add(new Gift(new ItemStack(Items.cookie, 15), 75));
        gifts.add(new Gift(new ItemStack(Items.cookie, 10), 75));
        gifts.add(new Gift(new ItemStack(Items.cookie, 5), 75));
        gifts.add(new Gift(new ItemStack(Items.pumpkin_pie), 20));

        gifts.add(new Gift(new ItemStack(Blocks.jukebox), 5));
        gifts.add(new Gift(new ItemStack(Items.painting), 10));
        gifts.add(new Gift(new ItemStack(Items.flower_pot), 25));
        gifts.add(new Gift(new ItemStack(Items.compass), 25));
        gifts.add(new Gift(new ItemStack(Items.clock), 25));
        addGift(ItemCrowbar.getItem(), 20);

        gifts.add(new Gift(new ItemStack(Items.experience_bottle, 32), 5));
        gifts.add(new Gift(new ItemStack(Items.experience_bottle, 16), 10));
        gifts.add(new Gift(new ItemStack(Items.experience_bottle, 8), 20));
        gifts.add(new Gift(new ItemStack(Items.experience_bottle, 4), 40));
        gifts.add(new Gift(new ItemStack(Items.experience_bottle, 2), 80));

        gifts.add(new Gift(new ItemStack(Blocks.diamond_block), 1));
        gifts.add(new Gift(new ItemStack(Blocks.emerald_block), 2));
        gifts.add(new Gift(new ItemStack(Items.emerald), 30));
        gifts.add(new Gift(new ItemStack(Items.diamond), 20));
        gifts.add(new Gift(new ItemStack(Items.gold_ingot), 30));
        gifts.add(new Gift(new ItemStack(Items.gold_ingot, 2), 30));

        gifts.add(new Gift(new ItemStack(Items.gold_nugget, 8), 80));
        gifts.add(new Gift(new ItemStack(Items.gold_nugget, 16), 40));
        gifts.add(new Gift(new ItemStack(Items.gold_nugget, 32), 20));

        gifts.add(new Gift(new ItemStack(Items.ender_pearl), 30));
        gifts.add(new Gift(new ItemStack(Items.nether_star), 2));

        int recordChance = 1;

        gifts.add(new Gift(new ItemStack(Items.record_11), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_13), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_blocks), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_cat), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_chirp), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_far), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_mall), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_mellohi), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_stal), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_strad), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_ward), recordChance));
        gifts.add(new Gift(new ItemStack(Items.record_wait), recordChance));

        int toolChance = 10;

        gifts.add(new Gift(new ItemStack(Items.fishing_rod), toolChance));
        gifts.add(new Gift(new ItemStack(Items.bow), toolChance));
        gifts.add(new Gift(new ItemStack(Items.shears), toolChance));

        gifts.add(new Gift(new ItemStack(Items.diamond_axe), toolChance));
        gifts.add(new Gift(new ItemStack(Items.diamond_pickaxe), toolChance));
        gifts.add(new Gift(new ItemStack(Items.diamond_shovel), toolChance));
        gifts.add(new Gift(new ItemStack(Items.diamond_sword), toolChance));
        gifts.add(new Gift(new ItemStack(Items.diamond_hoe), toolChance));

        gifts.add(new Gift(new ItemStack(Items.golden_axe), toolChance));
        gifts.add(new Gift(new ItemStack(Items.golden_pickaxe), toolChance));
        gifts.add(new Gift(new ItemStack(Items.golden_shovel), toolChance));
        gifts.add(new Gift(new ItemStack(Items.golden_sword), toolChance));
        gifts.add(new Gift(new ItemStack(Items.golden_hoe), toolChance));

        gifts.add(new Gift(new ItemStack(Items.iron_axe), toolChance));
        gifts.add(new Gift(new ItemStack(Items.iron_pickaxe), toolChance));
        gifts.add(new Gift(new ItemStack(Items.iron_shovel), toolChance));
        gifts.add(new Gift(new ItemStack(Items.iron_sword), toolChance));
        gifts.add(new Gift(new ItemStack(Items.iron_hoe), toolChance));

        addGift(RailcraftToolItems.getSteelAxe(), toolChance);
        addGift(RailcraftToolItems.getSteelPickaxe(), toolChance);
        addGift(RailcraftToolItems.getSteelSword(), toolChance);
        addGift(RailcraftToolItems.getSteelShovel(), toolChance);
        addGift(RailcraftToolItems.getSteelHoe(), toolChance);

        int armorChance = 5;

        gifts.add(new Gift(new ItemStack(Items.diamond_helmet), armorChance));
        gifts.add(new Gift(new ItemStack(Items.diamond_chestplate), armorChance));
        gifts.add(new Gift(new ItemStack(Items.diamond_leggings), armorChance));
        gifts.add(new Gift(new ItemStack(Items.diamond_boots), armorChance));

        gifts.add(new Gift(new ItemStack(Items.golden_helmet), armorChance));
        gifts.add(new Gift(new ItemStack(Items.golden_chestplate), armorChance));
        gifts.add(new Gift(new ItemStack(Items.golden_leggings), armorChance));
        gifts.add(new Gift(new ItemStack(Items.golden_boots), armorChance));

        gifts.add(new Gift(new ItemStack(Items.iron_helmet), armorChance));
        gifts.add(new Gift(new ItemStack(Items.iron_chestplate), armorChance));
        gifts.add(new Gift(new ItemStack(Items.iron_leggings), armorChance));
        gifts.add(new Gift(new ItemStack(Items.iron_boots), armorChance));

        gifts.add(new Gift(new ItemStack(Items.leather_helmet), armorChance));
        gifts.add(new Gift(new ItemStack(Items.leather_chestplate), armorChance));
        gifts.add(new Gift(new ItemStack(Items.leather_leggings), armorChance));
        gifts.add(new Gift(new ItemStack(Items.leather_boots), armorChance));

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

    public static void addGift(ItemStack gift, int chance) {
        if (gift != null)
            gifts.add(new Gift(gift, chance));
    }

    public EntityCartGift(World world) {
        super(world);
    }

    public EntityCartGift(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
        setBlastRadius(1.5f);
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

    private Gift getGift() {
        while (true) {
            int index = rand.nextInt(gifts.size());
            Gift gift = gifts.get(index);
            int weight = rand.nextInt(100);
            if (gift.chance >= weight)
                return gift;
        }
    }

    private void spawnGift() {
        Gift gift = getGift();

        if (gift.stack == null) {
            spawnPotion();
            return;
        }

        double x = posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        double y = (double) (posY + 1 + rand.nextInt(3) - 1);
        double z = posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        InvTools.dropItem(gift.stack.copy(), worldObj, x, y, z);
    }

    private void spawnCoal() {
        double x = posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        double y = (double) (posY + 1 + rand.nextInt(3) - 1);
        double z = posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        InvTools.dropItem(new ItemStack(Items.coal), worldObj, x, y, z);
    }

    private void spawnPotion() {
        int meta = potions.get(rand.nextInt(potions.size()));
        ItemStack potion = new ItemStack(Items.potionitem, 1, meta);

        double x = posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        double y = (double) (posY + 1 + rand.nextInt(3) - 1);
        double z = posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        InvTools.dropItem(potion, worldObj, x, y, z);
    }

}
