/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui;

import mods.railcraft.client.gui.FactoryGui;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.gui.containers.FactoryContainer;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.apache.logging.log4j.Level;

public class GuiHandler implements IGuiHandler {

    public static void openGui(EnumGui gui, EntityPlayer player, World world, BlockPos pos) {
        openGui(gui, player, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void openGui(EnumGui gui, EntityPlayer player, World world, int x, int y, int z) {
        if (Game.isHost(world)) {
            if (gui.hasContainer()) {
                player.openGui(Railcraft.getMod(), gui.ordinal(), world, x, y, z);
            }
        } else if (!gui.hasContainer()) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            FMLClientHandler.instance().displayGuiScreen(player, FactoryGui.build(gui, player.inventory, tile, world, x, y, z));
        }
    }

    public static void openGui(EnumGui gui, EntityPlayer player, World world, Entity entity) {
        if (Game.isHost(world)) {
            if (gui.hasContainer()) {
                player.openGui(Railcraft.getMod(), gui.ordinal(), world, entity.getEntityId(), Integer.MIN_VALUE, 0);
            }
        } else if (!gui.hasContainer()) {
            FMLClientHandler.instance().displayGuiScreen(player, FactoryGui.build(gui, player.inventory, entity, world, entity.getEntityId(), Integer.MIN_VALUE, 0));
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (y == Integer.MIN_VALUE) {
            Entity entity = world.getEntityByID(x);
            if (entity == null) {
                Game.log().msg(Level.WARN, "[Server] Entity not found when opening GUI: {0}", x);
                return null;
            }
            return FactoryContainer.build(EnumGui.fromOrdinal(ID), player.inventory, entity, world, x, y, z);
        }
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return FactoryContainer.build(EnumGui.fromOrdinal(ID), player.inventory, tile, world, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (y == Integer.MIN_VALUE) {
            Entity entity = world.getEntityByID(x);
            if (entity == null) {
                Game.log().msg(Level.WARN, "[Client] Entity not found when opening GUI: {0}", x);
                return null;
            }
            return FactoryGui.build(EnumGui.fromOrdinal(ID), player.inventory, entity, world, x, y, z);
        }
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return FactoryGui.build(EnumGui.fromOrdinal(ID), player.inventory, tile, world, x, y, z);
    }
}
