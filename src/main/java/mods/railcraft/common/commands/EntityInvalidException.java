/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.commands;

import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;

/**
 * Created by CovertJaguar on 6/4/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityInvalidException extends CommandException {

    public EntityInvalidException(Entity entity) {
        this("commands.generic.entity.invalidType", entity);
    }

    public EntityInvalidException(String message, Object... objects) {
        super(message, objects);
    }
}
