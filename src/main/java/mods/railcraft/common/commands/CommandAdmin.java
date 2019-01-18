/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.server.MinecraftServer;

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
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            kill(sender, EntityAnimal.class);
        }
    }

    private static class CommandAdminKillMinecarts extends SubCommand {
        private CommandAdminKillMinecarts() {
            super("minecarts");
            addAlias("carts");
            setPermLevel(PermLevel.ADMIN);
        }

        @Override
        public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) {
            kill(sender, EntityMinecart.class);
        }
    }

    private static void kill(ICommandSender sender, Class<? extends Entity> entityClass) {
        sender.getEntityWorld()
                .loadedEntityList
                .stream()
                .filter(entityClass::isInstance)
                .forEach(Entity::setDead);
    }
}
