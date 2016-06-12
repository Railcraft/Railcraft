/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import net.minecraft.network.datasync.EntityDataManager;
import thaumcraft.api.aspects.Aspect;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EssentiaTank {

    private final Aspect aspect;
    private final EntityDataManager dataManager;
    private final int capacity, dataId;

    public EssentiaTank(Aspect aspect, int capacity, EntityDataManager dataManager, int dataId) {
        this.aspect = aspect;
        this.dataManager = dataManager;
        this.dataId = dataId;
        this.capacity = capacity;
        dataManager.register(dataId, (short) 0);
    }

    public Aspect getAspect() {
        return aspect;
    }

    public int getAmount() {
        return dataManager.get(dataId);
    }

    public void setAmount(int amount) {
        dataManager.set(dataId, (short) amount);
    }

    public int fill(int amount, boolean doAdd) {
        if (amount < 0)
            return 0;
        int remainder = 0;
        int contents = getAmount() + amount;
        if (contents > capacity) {
            remainder = contents - capacity;
            contents = capacity;
        }
        if (doAdd)
            setAmount(contents);
        return remainder;
    }

    public boolean contains(int amount) {
        return getAmount() >= amount;
    }

    public boolean remove(int amount, boolean doRemove) {
        if (amount < 0)
            return false;
        if (contains(amount)) {
            if (doRemove) {
                int contents = getAmount() - amount;
                setAmount(Math.max(contents, 0));
            }
            return true;
        }
        return false;
    }

}
