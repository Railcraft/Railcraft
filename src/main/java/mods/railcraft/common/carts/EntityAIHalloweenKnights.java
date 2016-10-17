/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseType;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;

public class EntityAIHalloweenKnights extends EntityAIBase {
    private final EntityHorse horse;

    public EntityAIHalloweenKnights(EntityHorse horseIn) {
        this.horse = horseIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        return horse.worldObj.isAnyPlayerWithinRangeAt(horse.posX, horse.posY, horse.posZ, 10.0D);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        DifficultyInstance difficultyinstance = horse.worldObj.getDifficultyForLocation(new BlockPos(horse));
        horse.tasks.removeTask(this);
        horse.setType(HorseType.SKELETON);
        horse.setHorseTamed(true);
        horse.setGrowingAge(0);
        horse.worldObj.addWeatherEffect(new EntityLightningBolt(horse.worldObj, horse.posX, horse.posY, horse.posZ, true));
        EntitySkeleton entityskeleton = createSkeleton(difficultyinstance, horse);
        entityskeleton.startRiding(horse);

        for (int i = 0; i < 3; ++i) {
            EntityHorse entityhorse = createHorse(difficultyinstance);
            EntitySkeleton skeleton = createSkeleton(difficultyinstance, entityhorse);
            skeleton.startRiding(entityhorse);
            entityhorse.addVelocity(horse.getRNG().nextGaussian() * 0.5D, 0.0D, horse.getRNG().nextGaussian() * 0.5D);
        }
    }

    private EntityHorse createHorse(DifficultyInstance difficultyInstance) {
        EntityHorse entityhorse = new EntityHorse(horse.worldObj);
        entityhorse.onInitialSpawn(difficultyInstance, null);
        entityhorse.setPosition(horse.posX, horse.posY, horse.posZ);
        entityhorse.hurtResistantTime = 60;
        entityhorse.enablePersistence();
        entityhorse.setType(HorseType.SKELETON);
        entityhorse.setHorseTamed(true);
        entityhorse.setGrowingAge(0);
        entityhorse.worldObj.spawnEntityInWorld(entityhorse);
        return entityhorse;
    }

    private EntitySkeleton createSkeleton(DifficultyInstance difficultyInstance, EntityHorse entityHorse) {
        EntitySkeleton skeleton = new EntitySkeleton(entityHorse.worldObj);
        skeleton.onInitialSpawn(difficultyInstance, null);
        skeleton.setPosition(entityHorse.posX, entityHorse.posY, entityHorse.posZ);
        skeleton.hurtResistantTime = 60;
        skeleton.enablePersistence();

        skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(MiscTools.RANDOM.nextFloat() < 0.25F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));

        EnchantmentHelper.addRandomEnchantment(skeleton.getRNG(), skeleton.getHeldItemMainhand(), (int) (5.0F + difficultyInstance.getClampedAdditionalDifficulty() * (float) skeleton.getRNG().nextInt(18)), false);
//        EnchantmentHelper.addRandomEnchantment(skeleton.getRNG(), skeleton.getItemStackFromSlot(EntityEquipmentSlot.HEAD), (int)(5.0F + difficultyInstance.getClampedAdditionalDifficulty() * (float)skeleton.getRNG().nextInt(18)), false);
        skeleton.worldObj.spawnEntityInWorld(skeleton);
        return skeleton;
    }
}