/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.entity.ai;

import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;

public class EntityAIHalloweenKnights extends EntityAIBase {
    private final EntitySkeletonHorse horse;
    private boolean executed;

    public EntityAIHalloweenKnights(EntitySkeletonHorse horseIn) {
        this.horse = horseIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        return !executed && horse.world.isAnyPlayerWithinRangeAt(horse.posX, horse.posY, horse.posZ, 10.0D);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        executed = true;
        DifficultyInstance difficultyinstance = horse.world.getDifficultyForLocation(new BlockPos(horse));
        horse.setHorseTamed(true);
        horse.setGrowingAge(0);
        horse.world.addWeatherEffect(new EntityLightningBolt(horse.world, horse.posX, horse.posY, horse.posZ, true));
        EntitySkeleton entityskeleton = createSkeleton(difficultyinstance, horse);
        entityskeleton.startRiding(horse);

        for (int i = 0; i < 3; ++i) {
            EntitySkeletonHorse createdHorse = createHorse(difficultyinstance);
            EntitySkeleton skeleton = createSkeleton(difficultyinstance, createdHorse);
            skeleton.startRiding(createdHorse);
            createdHorse.addVelocity(horse.getRNG().nextGaussian() * 0.5D, 0.0D, horse.getRNG().nextGaussian() * 0.5D);
        }
    }

    private EntitySkeletonHorse createHorse(DifficultyInstance difficultyInstance) {
        EntitySkeletonHorse horse = new EntitySkeletonHorse(this.horse.world);
        horse.onInitialSpawn(difficultyInstance, null);
        horse.setPosition(this.horse.posX, this.horse.posY, this.horse.posZ);
        horse.hurtResistantTime = 60;
        horse.enablePersistence();
        horse.setHorseTamed(true);
        horse.setGrowingAge(0);
        horse.world.spawnEntity(horse);
        return horse;
    }

    private EntitySkeleton createSkeleton(DifficultyInstance difficultyInstance, EntitySkeletonHorse entityHorse) {
        EntitySkeleton skeleton = new EntitySkeleton(entityHorse.world);
        skeleton.onInitialSpawn(difficultyInstance, null);
        skeleton.setPosition(entityHorse.posX, entityHorse.posY, entityHorse.posZ);
        skeleton.hurtResistantTime = 60;
        skeleton.enablePersistence();

        skeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(MiscTools.RANDOM.nextFloat() < 0.25F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));

        EnchantmentHelper.addRandomEnchantment(skeleton.getRNG(), skeleton.getHeldItemMainhand(), (int) (5.0F + difficultyInstance.getClampedAdditionalDifficulty() * (float) skeleton.getRNG().nextInt(18)), false);
//        EnchantmentHelper.addRandomEnchantment(skeleton.getRNG(), skeleton.getItemStackFromSlot(EntityEquipmentSlot.HEAD), (int)(5.0F + difficultyInstance.getClampedAdditionalDifficulty() * (float)skeleton.getRNG().nextInt(18)), false);
        skeleton.world.spawnEntity(skeleton);
        return skeleton;
    }
}