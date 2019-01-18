/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.entity.ai;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static net.minecraft.util.EnumParticleTypes.HEART;

public class EntityAIMateBreeding extends EntityAIBase {

    private static final int MAX_ANIMALS = 6;
    private final EntityAnimal theAnimal;
    final World theWorld;
    /**
     * Delay preventing a baby from spawning immediately when two mate-able animals find each other.
     */
    int spawnBabyDelay;
    /**
     * The speed the creature moves at during mating behavior.
     */
    final float moveSpeed;
    private @Nullable EntityAnimal targetMate;

    public EntityAIMateBreeding(EntityAnimal animal, float moveSpeed) {
        this.theAnimal = animal;
        this.theWorld = animal.world;
        this.moveSpeed = moveSpeed;
    }

    public static void modifyAI(EntityAnimal animal) {
        boolean tame = animal instanceof EntityTameable;
        int matePriority = -1;
        int sitPriority = -1;
        boolean hasDespawn = false;
        Iterator<EntityAITaskEntry> it = animal.tasks.taskEntries.iterator();
        while (it.hasNext()) {
            EntityAITaskEntry task = it.next();
            if (tame && task.action instanceof EntityAISit) {
                sitPriority = task.priority;
                it.remove();
            } else if (task.action instanceof EntityAIMate) {
                matePriority = task.priority;
                it.remove();
            } else if (task.action instanceof EntityAIDespawn) {
                hasDespawn = true;
            }
        }

        if (tame) {
            ((EntityTameable) animal).setTamed(true);
        }

        if (!hasDespawn) {
            animal.tasks.addTask(0, new EntityAIDespawn(animal));
        }

        if (matePriority > 0) {
            animal.tasks.addTask(matePriority, new EntityAIMateBreeding(animal, 0.25f));
            if (tame) {
                animal.tasks.addTask(6, new EntityAISitRandom((EntityTameable) animal));
            }
        }
        if (sitPriority > 0) {
            EntityAISitBred aiSit = new EntityAISitBred((EntityTameable) animal);
            animal.tasks.addTask(sitPriority, aiSit);
            ((EntityTameable) animal).aiSit = aiSit;
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (!theAnimal.isInLove()) {
            return false;
        }

        List<EntityAnimal> nearbyEntities = theAnimal.world.getEntitiesWithinAABB(EntityAnimal.class, theAnimal.getEntityBoundingBox().grow(1));
        if (nearbyEntities.size() > MAX_ANIMALS) {
            return false;
        }

        targetMate = getNearbyMate();
        return targetMate != null;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        assert targetMate != null;
        theAnimal.getLookHelper().setLookPositionWithEntity(targetMate, 10.0F, (float) theAnimal.getVerticalFaceSpeed());
        theAnimal.getNavigator().tryMoveToEntityLiving(targetMate, moveSpeed);
        ++this.spawnBabyDelay;

        if (spawnBabyDelay == 60) {
            double litterSize = 1.75;
            if (theAnimal instanceof EntityCow || theAnimal instanceof EntitySheep) {
                litterSize = 0;
            }
            int babies = 1;
            if (litterSize > 0) {
                babies += (int) Math.round(Math.abs(theAnimal.getRNG().nextGaussian()) * litterSize);
            }
            for (int i = 0; i < babies; i++) {
                spawnBaby();
            }
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private @Nullable EntityAnimal getNearbyMate() {
        float var1 = 8.0F;
        List<EntityAnimal> var2 = theWorld.getEntitiesWithinAABB(theAnimal.getClass(), theAnimal.getEntityBoundingBox().grow(var1));
        Iterator<EntityAnimal> entity = var2.iterator();
        EntityAnimal target;

        do {
            if (!entity.hasNext()) {
                return null;
            }

            target = entity.next();
        } while (!canMateWith(theAnimal, target));

        return target;
    }

    public boolean canMateWith(EntityAnimal animal, EntityAnimal target) {
        if (target == animal) {
            return false;
        }

        if (animal.getClass() == target.getClass()) {
            boolean isSitting = target instanceof EntityTameable && ((EntityTameable) target).isSitting();
            return !isSitting && animal.isInLove() && target.isInLove();
        }

        return false;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby() {
        assert targetMate != null;
        EntityAgeable baby = theAnimal.createChild(targetMate);

        if (baby instanceof EntityAnimal) {
            theAnimal.setGrowingAge(3600); // 6000
            targetMate.setGrowingAge(3600); // 6000
            theAnimal.resetInLove();
            targetMate.resetInLove();
            baby.setGrowingAge(-12000); // -24000

            modifyAI((EntityAnimal) baby);

            Random rand = theAnimal.getRNG();
            if (baby instanceof EntityOcelot) {
                EntityOcelot cat = (EntityOcelot) baby;
                if (rand.nextInt(10) == 0) {
                    cat.setTameSkin(baby.world.rand.nextInt(4));
                }
            }

            double x = rand.nextGaussian() * 0.2D;
            double z = rand.nextGaussian() * 0.2D;
            baby.setLocationAndAngles(theAnimal.posX + x, theAnimal.posY, theAnimal.posZ + z, 0.0F, 0.0F);
            theWorld.spawnEntity(baby);

            for (int i = 0; i < 7; ++i) {
                double px = rand.nextGaussian() * 0.02D;
                double py = rand.nextGaussian() * 0.02D;
                double pz = rand.nextGaussian() * 0.02D;
                theWorld.spawnParticle(HEART, theAnimal.posX + (double) (rand.nextFloat() * theAnimal.width * 2.0F) - (double) theAnimal.width, theAnimal.posY + 0.5D + (double) (rand.nextFloat() * theAnimal.height), theAnimal.posZ + (double) (rand.nextFloat() * theAnimal.width * 2.0F) - (double) theAnimal.width, px, py, pz);
            }
        }
    }
}
