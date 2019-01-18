/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class AIPlugin {

    /**
     * The bit shows that a task needs {@link net.minecraft.entity.ai.EntityLookHelper}
     */
    public static final int LOOK = 1;
    /**
     * The bit shows that a task needs {@link net.minecraft.entity.ai.EntityMoveHelper}
     */
    public static final int MOVE = 2;
    /**
     * The bit shows that a task needs {@link net.minecraft.entity.ai.EntityJumpHelper}
     */
    public static final int JUMP = 4;

    public static boolean addAITask(EntityLiving entity, int priority, EntityAIBase task) {
        for (EntityAITasks.EntityAITaskEntry entry : entity.tasks.taskEntries) {
            if (entry.action.getClass() == task.getClass())
                return false;
        }
        entity.tasks.addTask(priority, task);
        return true;
    }

    private AIPlugin() {
    }

}
