/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityAIMoveToBlock extends EntityAIBase {

    private final EntityCreature entity;
    /**
     * The closest entity which is being watched by this one.
     */
    protected WorldCoordinate watchedBlock;
    /**
     * This is the Maximum distance that the AI will look for the Entity
     */
    private final int maxDist;
    private final float weight;
    private final Block searchedBlock;
    private final int searchedMeta;

    public EntityAIMoveToBlock(EntityCreature entity, Block searchedBlock, int searchedMeta, int maxDist) {
        this(entity, searchedBlock, searchedMeta, maxDist, 0.001F);
    }

    public EntityAIMoveToBlock(EntityCreature entity, Block searchedBlock, int searchedMeta, int maxDist, float weight) {
        this.entity = entity;
        this.searchedBlock = searchedBlock;
        this.searchedMeta = searchedMeta;
        this.maxDist = maxDist;
        this.weight = weight;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     *
     * @return
     */
    @Override
    public boolean shouldExecute() {
        if (entity.getRNG().nextFloat() >= this.weight)
            return false;

        if (!entity.worldObj.isDaytime())
            return false;

        if (watchedBlock == null || !isBlockValid())
            watchedBlock = WorldPlugin.findBlock(entity.worldObj, (int) entity.posX, (int) entity.posY, (int) entity.posZ, maxDist, searchedBlock, searchedMeta);

        return watchedBlock != null;
    }

    private boolean isBlockValid() {
        if (searchedBlock != WorldPlugin.getBlock(entity.worldObj, watchedBlock))
            return false;
        return WorldPlugin.getDistanceSq(watchedBlock, entity.posX, entity.posY, entity.posZ) <= maxDist * maxDist;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     *
     * @return
     */
    @Override
    public boolean continueExecuting() {
        return !entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        if (entity.getDistanceSq(watchedBlock.x + 0.5D, watchedBlock.y + 0.5D, watchedBlock.z + 0.5D) > 256.0D) {
            Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(entity, 14, 3, Vec3.createVectorHelper(watchedBlock.x + 0.5D, watchedBlock.y + 0.5D, watchedBlock.z + 0.5D));
            if (vec3 != null)
                move(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        } else
            move(watchedBlock.x + 0.5D, watchedBlock.y + 0.5D, watchedBlock.z + 0.5D);
    }

    private void move(double x, double y, double z) {
        entity.getNavigator().tryMoveToXYZ(x, y, z, 0.6D);
//        System.out.println("Moving to Block");
//        EffectManager.instance.teleportEffect(entity, watchedBlock.x + 0.5D, watchedBlock.y + 0.5D, watchedBlock.z + 0.5D);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.watchedBlock = null;
    }

}
