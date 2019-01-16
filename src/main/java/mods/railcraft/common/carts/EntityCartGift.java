/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;

//TODO: Test this
public class EntityCartGift extends CartBaseSurprise {

    static {
        SurpriseCategory coal = createSurpriseCategory(EntityCartGift.class, 100);
        coal.add(COAL);

        SurpriseCategory gifts = createSurpriseCategory(EntityCartGift.class, 100);
        gifts.setNumberToSpawn(2);

        gifts.add(new SurprisePotion(100));
        gifts.add(new SurprisePotion(100));
        gifts.add(new SurprisePotion(100));
        gifts.add(new SurprisePotion(100));
        gifts.add(new SurprisePotion(100));

        gifts.add(Items.SNOWBALL, 16, 50);

        gifts.add(Blocks.CAKE, 25);
        gifts.add(Items.BOWL, 25);
        gifts.add(Items.COOKIE, 15, 75);
        gifts.add(Items.COOKIE, 10, 75);
        gifts.add(Items.COOKIE, 5, 75);
        gifts.add(Items.PUMPKIN_PIE, 20);

        gifts.add(Blocks.JUKEBOX, 5);
        gifts.add(Items.PAINTING, 10);
        gifts.add(Items.FLOWER_POT, 25);
        gifts.add(Items.COMPASS, 25);
        gifts.add(Items.CLOCK, 25);
        gifts.add(RailcraftItems.CROWBAR_IRON, 20);
        gifts.add(RailcraftItems.CROWBAR_STEEL, 10);
        gifts.add(RailcraftItems.CROWBAR_DIAMOND, 5);
        gifts.add(RailcraftItems.CROWBAR_SEASONS, 1);

        gifts.add(Items.EXPERIENCE_BOTTLE, 32, 5);
        gifts.add(Items.EXPERIENCE_BOTTLE, 16, 10);
        gifts.add(Items.EXPERIENCE_BOTTLE, 8, 20);
        gifts.add(Items.EXPERIENCE_BOTTLE, 4, 40);
        gifts.add(Items.EXPERIENCE_BOTTLE, 2, 80);

        gifts.add(Blocks.DIAMOND_BLOCK, 1);
        gifts.add(Blocks.EMERALD_BLOCK, 2);
        gifts.add(Items.EMERALD, 30);
        gifts.add(Items.DIAMOND, 20);

        gifts.add(Items.GOLD_INGOT, 30);
        gifts.add(Items.GOLD_INGOT, 2, 30);

        gifts.add(Items.GOLD_NUGGET, 8, 80);
        gifts.add(Items.GOLD_NUGGET, 16, 40);
        gifts.add(Items.GOLD_NUGGET, 32, 20);

        gifts.add(Items.ENDER_PEARL, 30);
        gifts.add(Items.NETHER_STAR, 2);


        int recordChance = 1;

        gifts.add(Items.RECORD_11, recordChance);
        gifts.add(Items.RECORD_13, recordChance);
        gifts.add(Items.RECORD_BLOCKS, recordChance);
        gifts.add(Items.RECORD_CAT, recordChance);
        gifts.add(Items.RECORD_CHIRP, recordChance);
        gifts.add(Items.RECORD_FAR, recordChance);
        gifts.add(Items.RECORD_MALL, recordChance);
        gifts.add(Items.RECORD_MELLOHI, recordChance);
        gifts.add(Items.RECORD_STAL, recordChance);
        gifts.add(Items.RECORD_STRAD, recordChance);
        gifts.add(Items.RECORD_WARD, recordChance);
        gifts.add(Items.RECORD_WAIT, recordChance);

        int toolChance = 10;

        gifts.add(Items.FISHING_ROD, toolChance);
        gifts.add(Items.BOW, toolChance);
        gifts.add(Items.SHEARS, toolChance);

        gifts.add(Items.DIAMOND_AXE, toolChance);
        gifts.add(Items.DIAMOND_PICKAXE, toolChance);
        gifts.add(Items.DIAMOND_SHOVEL, toolChance);
        gifts.add(Items.DIAMOND_SWORD, toolChance);
        gifts.add(Items.DIAMOND_HOE, toolChance);

        gifts.add(Items.GOLDEN_AXE, toolChance);
        gifts.add(Items.GOLDEN_PICKAXE, toolChance);
        gifts.add(Items.GOLDEN_SHOVEL, toolChance);
        gifts.add(Items.GOLDEN_SWORD, toolChance);
        gifts.add(Items.GOLDEN_HOE, toolChance);

        gifts.add(Items.IRON_AXE, toolChance);
        gifts.add(Items.IRON_PICKAXE, toolChance);
        gifts.add(Items.IRON_SHOVEL, toolChance);
        gifts.add(Items.IRON_SWORD, toolChance);
        gifts.add(Items.IRON_HOE, toolChance);

        gifts.add(RailcraftItems.AXE_STEEL, toolChance);
        gifts.add(RailcraftItems.PICKAXE_STEEL, toolChance);
        gifts.add(RailcraftItems.SWORD_STEEL, toolChance);
        gifts.add(RailcraftItems.SHOVEL_STEEL, toolChance);
        gifts.add(RailcraftItems.HOE_STEEL, toolChance);
        gifts.add(RailcraftItems.SHEARS_STEEL, toolChance);

        int armorChance = 5;

        gifts.add(Items.DIAMOND_HELMET, armorChance);
        gifts.add(Items.DIAMOND_CHESTPLATE, armorChance);
        gifts.add(Items.DIAMOND_LEGGINGS, armorChance);
        gifts.add(Items.DIAMOND_BOOTS, armorChance);

        gifts.add(Items.GOLDEN_HELMET, armorChance);
        gifts.add(Items.GOLDEN_CHESTPLATE, armorChance);
        gifts.add(Items.GOLDEN_LEGGINGS, armorChance);
        gifts.add(Items.GOLDEN_BOOTS, armorChance);

        gifts.add(Items.IRON_HELMET, armorChance);
        gifts.add(Items.IRON_CHESTPLATE, armorChance);
        gifts.add(Items.IRON_LEGGINGS, armorChance);
        gifts.add(Items.IRON_BOOTS, armorChance);

        gifts.add(Items.LEATHER_HELMET, armorChance);
        gifts.add(Items.LEATHER_CHESTPLATE, armorChance);
        gifts.add(Items.LEATHER_LEGGINGS, armorChance);
        gifts.add(Items.LEATHER_BOOTS, armorChance);

        gifts.add(RailcraftItems.ARMOR_HELMET_STEEL, armorChance);
        gifts.add(RailcraftItems.ARMOR_CHESTPLATE_STEEL, armorChance);
        gifts.add(RailcraftItems.ARMOR_LEGGINGS_STEEL, armorChance);
        gifts.add(RailcraftItems.ARMOR_BOOTS_STEEL, armorChance);
    }

    public EntityCartGift(World world) {
        super(world);
    }

    public EntityCartGift(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        setBlastRadius(1.5f);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.GIFT;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.AIR.getDefaultState();
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

}
