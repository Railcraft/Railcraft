/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.common.blocks.IVariantEnumBlock;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IEnumMachine<M extends Enum<M> & IEnumMachine<M>> extends IVariantEnumBlock<M> {
    class Definition extends IVariantEnumBlock.Definition {
        public final Class<? extends TileMachineBase> tile;
        public boolean passesLight;

        @SafeVarargs
        public Definition(String tag, Class<? extends TileMachineBase> tile, Class<? extends IRailcraftModule>... modules) {
            super(tag, modules);
            this.tile = tile;
        }
    }

    @Override
    Definition getDef();

    default String getToolClass() {
        return "pickaxe:2";
    }

    default boolean passesLight() {
        return getDef().passesLight;
    }

    default Class<? extends TileMachineBase> getTileClass() {
        return getDef().tile;
    }

    default TileMachineBase getTileEntity() {
        try {
            return getTileClass().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
