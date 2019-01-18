/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class UnlistedProperty<T> implements IUnlistedProperty<T> {
    private final String name;
    private final Class<T> classType;

    private static class UnlistedPropertySerializable<T extends IStringSerializable> extends UnlistedProperty<T> {
        private UnlistedPropertySerializable(String name, Class<T> classType) {
            super(name, classType);
        }

        @Override
        public String valueToString(T value) {
            return value.getName();
        }
    }

    public static <T> UnlistedProperty<T> create(String name, Class<T> classType) {
        if (IStringSerializable.class.isAssignableFrom(classType))
            //noinspection unchecked
            return new UnlistedPropertySerializable(name, classType);
        return new UnlistedProperty<>(name, classType);
    }

    private UnlistedProperty(String name, Class<T> classType) {
        this.name = name;
        this.classType = classType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(T value) {
        return true;
    }

    @Override
    public Class<T> getType() {
        return classType;
    }

    @Override
    public String valueToString(T value) {
        return value.toString();
    }
}
