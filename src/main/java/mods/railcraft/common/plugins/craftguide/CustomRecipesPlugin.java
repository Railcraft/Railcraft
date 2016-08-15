/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.craftguide;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.modules.ModuleRouting;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import uristqwerty.CraftGuide.api.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CustomRecipesPlugin implements RecipeProvider {

    private final Slot[] slots = new Slot[10];

    public CustomRecipesPlugin() {
        slots[0] = new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                slots[1 + x + y * 3] = new ItemSlot(3 + x * 18, 3 + y * 18, 16, 16, true).setSlotType(SlotType.INPUT_SLOT);
            }
        }
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
        RecipeTemplate template = generator.createRecipeTemplate(slots, null, "/gui/CraftGuideRecipe.png", 1, 1, 82, 1);

        // Rotor Repair
        if (EnumMachineAlpha.TURBINE.isAvailable()) {
            ItemStack[] rotorRepair = new ItemStack[10];
            rotorRepair[0] = RailcraftItems.TURBINE_ROTOR.getStack();
            rotorRepair[0].setItemDamage(1);
            for (int i = 0; i < 9; i++) {
                rotorRepair[i + 1] = RailcraftItems.TURBINE_BLADE.getStack();
            }
            rotorRepair[5] = RailcraftItems.TURBINE_ROTOR.getStack();
            rotorRepair[5].setItemDamage(25000);
            generator.addRecipe(template, rotorRepair);
        }

        // Ticket
        if (RailcraftModuleManager.isModuleEnabled(ModuleRouting.class)) {
            ItemStack[] ticket = new ItemStack[10];
            ticket[0] = RailcraftItems.TICKET.getStack();
            ticket[1] = RailcraftItems.TICKET_GOLD.getStack();
            ticket[2] = new ItemStack(Items.PAPER);
            NBTTagCompound nbt = InvTools.getItemData(ticket[0]);
            nbt.setString("owner", "CovertJaguar");
            nbt.setString("dest", "TheFarLands/Milliways");
            ticket[0].setTagCompound(nbt);
            ticket[1].setTagCompound(nbt);
            generator.addRecipe(template, ticket);

            // Routing Table
            ItemStack[] routingTable = new ItemStack[10];
            routingTable[0] = RailcraftItems.ROUTING_TABLE.getStack();
            if (routingTable[0] != null) {
                routingTable[0].stackSize = 2;
                InvTools.addItemToolTip(routingTable[0], "Edited");
                routingTable[1] = RailcraftItems.ROUTING_TABLE.getStack();
                InvTools.addItemToolTip(routingTable[1], "Edited");
                routingTable[2] = RailcraftItems.ROUTING_TABLE.getStack();
                InvTools.addItemToolTip(routingTable[2], "Blank");
                generator.addRecipe(template, routingTable);
            }
        }
    }

}
