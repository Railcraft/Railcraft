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
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityCartPumpkin extends EntityCartTNTWood {

    private static final byte SPAWN_DIST = 2;
    private static final Map<String, Integer> mobWeights = new HashMap<String, Integer>();
    private static final Map<String, Integer> mobNumber = new HashMap<String, Integer>();
    private static final List<String> mobs = new ArrayList<String>();
    private static final List<Integer> potions = new ArrayList<Integer>();

    static {
        mobs.add("Skeleton");
        mobs.add("Bat");
        mobs.add("Witch");
        mobs.add("Ghast");
        mobs.add("PigZombie");
        mobs.add("WitherBoss");

        mobWeights.put("Skeleton", 50);
        mobWeights.put("Bat", 75);
        mobWeights.put("Witch", 25);
        mobWeights.put("Ghast", 25);
        mobWeights.put("PigZombie", 25);
        mobWeights.put("WitherBoss", 5);

        mobNumber.put("Skeleton", 1);
        mobNumber.put("Bat", 3);
        mobNumber.put("Witch", 1);
        mobNumber.put("Ghast", 1);
        mobNumber.put("PigZombie", 1);
        mobNumber.put("WitherBoss", 1);

        for (int meta = 0; meta <= 32767; ++meta) {
            List effects = PotionHelper.getPotionEffects(meta, false);

            if (effects != null && !effects.isEmpty())
                potions.add(meta);
        }
    }

    public EntityCartPumpkin(World world) {
        super(world);
    }

    public EntityCartPumpkin(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.PUMPKIN;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        setBlastRadius(1.5f);
    }

    @Override
    public Block func_145820_n() {
        return Blocks.pumpkin;
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
    protected float getMinBlastRadius() {
        return 0.5f;
    }

    @Override
    protected float getMaxBlastRadius() {
        return 4;
    }

    @Override
    public void explode() {
        if (Game.isHost(getWorld())) {
            worldObj.createExplosion(this, posX, posY, posZ, getBlastRadius(), true);
            setDead();
            spawnMob();
            spawnPotion();
        }
    }

    private String getMobToSpawn() {
        while (true) {
            int index = rand.nextInt(mobs.size());
            String mob = mobs.get(index);
            int weight = rand.nextInt(100);
            if (mobWeights.get(mob) >= weight)
                return mob;
        }
    }

    private void spawnMob() {
        String mobName = getMobToSpawn();
        int numToSpawn = mobNumber.get(mobName);

        for (int i = 0; i < numToSpawn; i++) {
            Entity mob = EntityList.createEntityByName(mobName, worldObj);

            if (mob == null)
                return;

            double x = posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
            double y = posY + mob.height + rand.nextInt(3);
            double z = posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
            EntityLiving living = mob instanceof EntityLiving ? (EntityLiving) mob : null;
            mob.setLocationAndAngles(x, y, z, rand.nextFloat() * 360.0F, 0.0F);

            if (worldObj.checkNoEntityCollision(mob.getEntityBoundingBox()) && worldObj.getCollidingBoundingBoxes(mob, mob.getEntityBoundingBox()).isEmpty() && !worldObj.isAnyLiquid(mob.getEntityBoundingBox())) {

                if (mob instanceof EntitySkeleton) {
                    EntitySkeleton skel = (EntitySkeleton) mob;
                    if (rand.nextInt(4) == 0) {
                        skel.tasks.addTask(4, new EntityAIAttackMelee(skel, EntityPlayer.class, 0.25F, false));
                        skel.setSkeletonType(1);
                        skel.setCurrentItemOrArmor(0, new ItemStack(Items.STONE_SWORD));
                    } else {
                        skel.tasks.addTask(4, new EntityAIAttackRanged(skel, 0.25F, 60, 10.0F));
                        skel.setCurrentItemOrArmor(0, new ItemStack(Items.BOW));
                    }

                    mob.setCurrentItemOrArmor(4, new ItemStack(rand.nextFloat() < 0.25F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                } else if (living != null)
                    living.onInitialSpawn(worldObj.getDifficultyForLocation(new BlockPos(living)), null);

                worldObj.spawnEntityInWorld(mob);
                worldObj.playAuxSFX(2004, new BlockPos(x, y, z), 0);

                if (living != null)
                    living.spawnExplosionParticle();
            }
        }
    }

    private void spawnPotion() {
        int meta = potions.get(rand.nextInt(potions.size()));
        ItemStack potion = new ItemStack(Items.POTIONITEM, 1, meta);

        double x = posX + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        double y = posY + 1 + rand.nextInt(3) - 1;
        double z = posZ + (rand.nextDouble() - rand.nextDouble()) * SPAWN_DIST;
        InvTools.dropItem(potion, worldObj, x, y, z);
    }

}
