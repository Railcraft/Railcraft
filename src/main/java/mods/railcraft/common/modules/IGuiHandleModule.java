/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.modules;

import mods.railcraft.common.gui.EnumGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface IGuiHandleModule {

    @SideOnly(Side.CLIENT)
    @Nullable
    GuiScreen getGuiScreen(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z);

    @Nullable
    Container getGuiContainer(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z);
}
