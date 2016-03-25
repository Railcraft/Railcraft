/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityAnimal;

/**
 * Commands for testing, because it was too much effort to find another mod that did them.
 * Created by CovertJaguar on 3/12/2015.
 */
public class CommandAdmin extends SubCommand {

    public CommandAdmin() {
        super("admin");
        addChildCommand(new CommandAdminKill());
        setPermLevel(PermLevel.ADMIN);
    }

    private static class CommandAdminKill extends SubCommand {
        private CommandAdminKill() {
            super("kill");
            addChildCommand(new CommandAdminKillAnimals());
            addChildCommand(new CommandAdminKillMinecarts());
            setPermLevel(PermLevel.ADMIN);
        }
    }

    private static class CommandAdminKillAnimals extends SubCommand {
        private CommandAdminKillAnimals() {
            super("animals");
            setPermLevel(PermLevel.ADMIN);
        }

        @Override
        public void processSubCommand(ICommandSender sender, String[] args) {
            for (Object obj : sender.getEntityWorld().getLoadedEntityList()) {
                if (obj instanceof EntityAnimal) {
                    ((EntityAnimal) obj).setDead();
                }
            }
        }
    }

    private static class CommandAdminKillMinecarts extends SubCommand {
        private CommandAdminKillMinecarts() {
            super("minecarts");
            addAlias("carts");
            setPermLevel(PermLevel.ADMIN);
        }

        @Override
        public void processSubCommand(ICommandSender sender, String[] args) {
            for (Object obj : sender.getEntityWorld().getLoadedEntityList()) {
                if (obj instanceof EntityMinecart) {
                    ((EntityMinecart) obj).setDead();
                }
            }
        }
    }
}
