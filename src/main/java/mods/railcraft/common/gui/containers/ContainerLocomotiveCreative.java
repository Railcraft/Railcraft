/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.electricity.IElectricMinecart;
import mods.railcraft.common.carts.EntityLocomotiveCreative;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.gui.widgets.ChargeIndicator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerLocomotiveCreative extends ContainerLocomotive {

    private final EntityLocomotiveCreative loco;

    private ContainerLocomotiveCreative(InventoryPlayer playerInv, EntityLocomotiveCreative loco) {
        super(playerInv, loco, 161);
        this.loco = loco;
    }

    public static ContainerLocomotiveCreative make(InventoryPlayer playerInv, EntityLocomotiveCreative loco) {
        ContainerLocomotiveCreative con = new ContainerLocomotiveCreative(playerInv, loco);
        con.init();
        return con;
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        super.updateProgressBar(id, value);
    }

}
