/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */

package mods.railcraft.common.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.*;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public abstract class SubCommand implements IModCommand {


    public enum PermLevel {

        EVERYONE(0), ADMIN(2);
        int permLevel;

        private PermLevel(int permLevel) {
            this.permLevel = permLevel;
        }

    }

    private final String name;
    private final List<String> aliases = new ArrayList<String>();
    private PermLevel permLevel = PermLevel.EVERYONE;
    private IModCommand parent;
    private final SortedSet<SubCommand> children = new TreeSet<SubCommand>(new Comparator<SubCommand>() {

        @Override
        public int compare(SubCommand o1, SubCommand o2) {
            return o1.compareTo(o2);
        }
    });

    public SubCommand(String name) {
        this.name = name;
    }

    @Override
    public final String getCommandName() {
        return name;
    }

    public SubCommand addChildCommand(SubCommand child) {
        child.setParent(this);
        children.add(child);
        return this;
    }

    void setParent(IModCommand parent) {
        this.parent = parent;
    }

    @Override
    public SortedSet<SubCommand> getChildren() {
        return children;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] args) {
        if (!CommandHelpers.processStandardCommands(sender, this, args))
            processSubCommand(sender, args);
    }

    public void processSubCommand(ICommandSender sender, String[] args) {
        CommandHelpers.throwWrongUsage(sender, this);
    }

    public SubCommand setPermLevel(PermLevel permLevel) {
        this.permLevel = permLevel;
        return this;
    }

    @Override
    public final int getRequiredPermissionLevel() {
        return permLevel.permLevel;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), getCommandName());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getFullCommandString() + " help";
    }

    @Override
    public void printHelp(ICommandSender sender) {
        CommandHelpers.printHelp(sender, this);
    }

    @Override
    public String getFullCommandString() {
        return parent.getFullCommandString() + " " + getCommandName();
    }

    public int compareTo(ICommand command) {
        return this.getCommandName().compareTo(command.getCommandName());
    }

    @Override
    public int compareTo(Object command) {
        return this.compareTo((ICommand) command);
    }
}