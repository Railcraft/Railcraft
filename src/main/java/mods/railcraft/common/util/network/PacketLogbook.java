/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import mods.railcraft.client.gui.GuiBookPlayerLog;
import mods.railcraft.common.blocks.logbook.TileLogbook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.time.LocalDate;

class PacketLogbook extends RailcraftPacket {
    private Multimap<LocalDate, GameProfile> log;

    PacketLogbook() {
    }

    PacketLogbook(Multimap<LocalDate, GameProfile> log) {
        this.log = log;
    }

    @Override
    public void writeData(RailcraftOutputStream data) throws IOException {
        data.writeNBT(TileLogbook.convertLogToNBT(log));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readData(RailcraftInputStream data) throws IOException {
        EntityPlayer player = Minecraft.getMinecraft().player;
        NBTTagCompound nbt = data.readNBT();
        if (nbt != null) {
            Multimap<LocalDate, GameProfile> log = TileLogbook.convertLogFromNBT(nbt);
            Minecraft.getMinecraft().displayGuiScreen(new GuiBookPlayerLog(log));
        }
    }

    @Override
    public int getID() {
        return PacketType.LOGBOOK_GUI.ordinal();
    }

}
