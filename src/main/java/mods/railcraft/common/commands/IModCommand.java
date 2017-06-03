/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.SortedSet;

/**
 * Our commands will have a few more methods.
 *
 * Created by CovertJaguar on 3/12/2015.
 */
public interface IModCommand extends ICommand {

    String getFullCommandString();

    int getPermissionLevel();

    SortedSet<SubCommand> getChildren();

    void printHelp(ICommandSender sender);
}