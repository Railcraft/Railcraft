/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import mods.railcraft.api.electricity.GridTools;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.api.electricity.IElectricMinecart;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.init.Blocks;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemElectricMeter extends ItemRailcraft implements IActivationBlockingItem {

    private static Item item;

    public static void register() {
        if (item == null) {
            String tag = "railcraft.tool.electric.meter";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemElectricMeter().setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item),
                        "T T",
                        "BGB",
                        " C ",
                        'B', Blocks.stone_button,
                        'G', "paneGlassColorless",
                        'C', "ingotCopper",
                        'T', "ingotTin");

                LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, tag);
            }
//            CreeperPlugin.fixCreepers();
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public ItemElectricMeter() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setFull3D();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        EntityPlayer player = event.entityPlayer;

        Entity entity = event.target;

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof ItemElectricMeter)
            player.swingItem();

        if (Game.isNotHost(player.worldObj))
            return;

        if (stack != null && stack.getItem() instanceof ItemElectricMeter)
            try {
                if (entity instanceof IElectricMinecart) {
                    IElectricMinecart cart = (IElectricMinecart) entity;
                    IElectricMinecart.ChargeHandler ch = cart.getChargeHandler();
                    if (ch != null) {
                        ChatPlugin.sendLocalizedChat(player, "railcraft.gui.electric.meter.charge", ch.getCharge(), ch.getDraw(), ch.getLosses());
                        event.setCanceled(true);
                    }
                }
            } catch (Throwable er) {
                Game.logErrorAPI(Railcraft.MOD_ID, er, IElectricMinecart.class);
                ChatPlugin.sendLocalizedChatFromServer(player, "chat.railcraft.api.error");
            }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (Game.isNotHost(world))
            return false;
        boolean returnValue = false;
        try {
            IElectricGrid gridObject = GridTools.getGridObjectAt(world, x, y, z);
            if (gridObject != null) {
                IElectricGrid.ChargeHandler ch = gridObject.getChargeHandler();
                if (ch != null) {
                    ChatPlugin.sendLocalizedChat(player, "railcraft.gui.electric.meter.charge", ch.getCharge(), ch.getDraw(), ch.getLosses());
                    returnValue = true;
                }
            }
        } catch (Throwable er) {
            Game.logErrorAPI(Railcraft.MOD_ID, er, IElectricGrid.class);
            ChatPlugin.sendLocalizedChatFromServer(player, "chat.railcraft.api.error");
        }
        return returnValue;
    }

}
