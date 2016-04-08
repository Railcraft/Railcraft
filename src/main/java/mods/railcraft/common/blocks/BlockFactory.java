/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import mods.railcraft.common.core.RailcraftConfig;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class BlockFactory {

    private boolean needsInit = true;
    public final String tag;

    public BlockFactory(String tag) {
        this.tag = tag;
    }

    public final void initBlock() {
        if (needsInit && isBlockEnabled()) {
            needsInit = false;
            doBlockInit();
        }
    }

    protected boolean isBlockEnabled() {
        return RailcraftConfig.isBlockEnabled(tag);
    }

    protected abstract void doBlockInit();

    public final void initRecipes() {
        if (!needsInit)
            doRecipeInit();
    }

    protected abstract void doRecipeInit();

    public final void finalizeBlocks() {
        if (!needsInit)
            doBlockFinalize();
    }

    protected void doBlockFinalize() {
    }
}
