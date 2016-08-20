/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
public class UnlistedProperty<T extends IStringSerializable> implements IUnlistedProperty<T> {
    private final String name;
    private final Class<T> classType;

    public static <T extends IStringSerializable> UnlistedProperty<T> create(String name, Class<T> classType) {
        return new UnlistedProperty<T>(name, classType);
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
        return value.getName();
    }
}
