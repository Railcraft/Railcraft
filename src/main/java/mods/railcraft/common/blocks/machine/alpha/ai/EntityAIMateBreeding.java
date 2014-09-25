/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

public class EntityAIMateBreeding extends EntityAIBase
{

    private static final int MAX_ANIMALS = 6;
    private final EntityAnimal theAnimal;
    World theWorld;
    private EntityAnimal targetMate;
    /**
     * Delay preventing a baby from spawning immediately when two mate-able animals find each other.
     */
    int spawnBabyDelay = 0;
    /** The speed the creature moves at during mating behavior. */
    float moveSpeed;

    public EntityAIMateBreeding(EntityAnimal animal, float moveSpeed) {
        this.theAnimal = animal;
        this.theWorld = animal.worldObj;
        this.moveSpeed = moveSpeed;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     * @return 
     */
    @Override
    public boolean shouldExecute() {
        if(!this.theAnimal.isInLove()) {
            return false;
        }

        List nearbyEntites = theAnimal.worldObj.getEntitiesWithinAABB(EntityAnimal.class, theAnimal.boundingBox.expand(1, 1, 1));
        if(nearbyEntites.size() > MAX_ANIMALS) {
            return false;
        }

        targetMate = getNearbyMate();
        return targetMate != null;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     * @return 
     */
    @Override
    public boolean continueExecuting() {
        return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
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
        this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float)this.theAnimal.getVerticalFaceSpeed());
        this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if(this.spawnBabyDelay == 60) {
            double litterSize = 1.75;
            if(theAnimal instanceof EntityCow || theAnimal instanceof EntitySheep) {
                litterSize = 0;
            }
            int babies = 1;
            if(litterSize > 0) {
                babies += (int)Math.round(Math.abs(theAnimal.getRNG().nextGaussian()) * litterSize);
            }
            for(int i = 0; i < babies; i++) {
                spawnBaby();
            }
        }
    }

    public static void modifyAI(EntityAnimal animal) {
        boolean tame = animal instanceof EntityTameable;
        int matePriority = -1;
        int sitPriority = -1;
        boolean hasDespawn = false;
        Iterator<EntityAITaskEntry> it = ((List<EntityAITaskEntry>)animal.tasks.taskEntries).iterator();
        while(it.hasNext()) {
            EntityAITaskEntry task = it.next();
            if(tame && task.action instanceof EntityAISit) {
                sitPriority = task.priority;
                it.remove();
            } else if(task.action instanceof EntityAIMate) {
                matePriority = task.priority;
                it.remove();
            } else if(task.action instanceof EntityAIDespawn) {
                hasDespawn = true;
            }
        }

        if(tame) {
            ((EntityTameable)animal).setTamed(true);
        }

        if(!hasDespawn) {
            animal.tasks.addTask(0, new EntityAIDespawn(animal));
        }

        if(matePriority > 0) {
            animal.tasks.addTask(matePriority, new EntityAIMateBreeding(animal, 0.25f));
            if(tame) {
                animal.tasks.addTask(6, new EntityAISitRandom((EntityTameable)animal));
            }
        }
        if(sitPriority > 0) {
            EntityAISitBred aiSit = new EntityAISitBred((EntityTameable)animal);
            animal.tasks.addTask(sitPriority, aiSit);
//            ObfuscationReflectionHelper.setPrivateValue(EntityTameable.class, (EntityTameable)animal, aiSit, "d", "aiSit");
            ObfuscationReflectionHelper.setPrivateValue(EntityTameable.class, (EntityTameable)animal, aiSit, 0);
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityAnimal getNearbyMate() {
        float var1 = 8.0F;
        List var2 = this.theWorld.getEntitiesWithinAABB(this.theAnimal.getClass(), this.theAnimal.boundingBox.expand((double)var1, (double)var1, (double)var1));
        Iterator entity = var2.iterator();
        EntityAnimal target;

        do {
            if(!entity.hasNext()) {
                return null;
            }

            target = (EntityAnimal)entity.next();
        } while(!canMateWith(theAnimal, target));

        return target;
    }

    public boolean canMateWith(EntityAnimal animal, EntityAnimal target) {
        if(target == animal) {
            return false;
        }

        if(animal.getClass() == target.getClass()) {
            boolean isSitting = target instanceof EntityTameable && ((EntityTameable)target).isSitting();
            return !isSitting && animal.isInLove() && target.isInLove();
        }

        return false;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby() {
        EntityAgeable baby = this.theAnimal.createChild(this.targetMate);

        if(baby instanceof EntityAnimal) {
            this.theAnimal.setGrowingAge(3600); // 6000
            this.targetMate.setGrowingAge(3600); // 6000
            this.theAnimal.resetInLove();
            this.targetMate.resetInLove();
            baby.setGrowingAge(-12000); // -24000

            modifyAI((EntityAnimal)baby);

            Random rand = this.theAnimal.getRNG();
            if(baby instanceof EntityOcelot) {
                EntityOcelot cat = (EntityOcelot)baby;
                if(rand.nextInt(10) == 0) {
                    cat.setTameSkin(baby.worldObj.rand.nextInt(4));
                }
            }

            double x = rand.nextGaussian() * 0.2D;
            double z = rand.nextGaussian() * 0.2D;
            baby.setLocationAndAngles(this.theAnimal.posX + x, this.theAnimal.posY, this.theAnimal.posZ + z, 0.0F, 0.0F);
            this.theWorld.spawnEntityInWorld(baby);

            for(int i = 0; i < 7; ++i) {
                double px = rand.nextGaussian() * 0.02D;
                double py = rand.nextGaussian() * 0.02D;
                double pz = rand.nextGaussian() * 0.02D;
                this.theWorld.spawnParticle("heart", this.theAnimal.posX + (double)(rand.nextFloat() * this.theAnimal.width * 2.0F) - (double)this.theAnimal.width, this.theAnimal.posY + 0.5D + (double)(rand.nextFloat() * this.theAnimal.height), this.theAnimal.posZ + (double)(rand.nextFloat() * this.theAnimal.width * 2.0F) - (double)this.theAnimal.width, px, py, pz);
            }
        }
    }
}
