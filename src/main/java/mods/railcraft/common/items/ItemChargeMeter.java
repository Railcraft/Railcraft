/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.core.items.IActivationBlockingItem;
import mods.railcraft.common.blocks.charge.CapabilityCartBattery;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.ChargeNetwork;
import mods.railcraft.common.blocks.charge.ICartBattery;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemChargeMeter extends ItemRailcraft implements IActivationBlockingItem {
    private static final DecimalFormat chargeFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
    private static final int SECONDS_TO_RECORD = 5;

    static {
        chargeFormatter.applyPattern("#,##0.###");
    }

    public ItemChargeMeter() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setFull3D();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.CHARGE_METER, 1, 1, LootPlugin.Type.WORKSHOP);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "T T",
                "BGB",
                " C ",
                'B', Blocks.STONE_BUTTON,
                'G', "paneGlassColorless",
                'C', "ingotCopper",
                'T', "ingotTin");
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer player = event.getEntityPlayer();

        Entity entity = event.getTarget();

        ItemStack stack = event.getItemStack();
        if (stack != null && stack.getItem() instanceof ItemChargeMeter)
            player.swingArm(event.getHand());

        if (Game.isClient(player.worldObj))
            return;

        if (stack != null && stack.getItem() instanceof ItemChargeMeter)
            try {
                if (entity.hasCapability(CapabilityCartBattery.CHARGE_CART_CAPABILITY, null)) {
                    ICartBattery battery = entity.getCapability(CapabilityCartBattery.CHARGE_CART_CAPABILITY, null);
                    if (battery != null) {
                        sendChat(player, "railcraft.gui.charge.meter.cart", battery.getCharge(), battery.getDraw(), battery.getLosses());
                        event.setCanceled(true);
                    }
                }
            } catch (Throwable er) {
                Game.logErrorAPI(Railcraft.MOD_ID, er, ICartBattery.class);
                ChatPlugin.sendLocalizedChatFromServer(player, "chat.railcraft.api.error");
            }
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (Game.isClient(world))
            return EnumActionResult.PASS;
        EnumActionResult returnValue = EnumActionResult.PASS;
        ChargeNetwork.ChargeNode node = ChargeManager.getNetwork(world).getNode(pos);
        if (!node.isNull() && !node.isGraphNull()) {
            sendChat(player, "railcraft.gui.charge.meter.start", SECONDS_TO_RECORD);
            node.startRecordingUsage(SECONDS_TO_RECORD * 20, (n, avg) -> {
                ChargeNetwork.ChargeGraph graph = n.getChargeGraph();
                sendChat(player, "railcraft.gui.charge.meter.network", graph.size(), graph.isInfinite() ? "INF" : graph.getCharge(), graph.getAverageUsagePerTick(), graph.getMaintenanceCost());
                if (n.getBattery() == null)
                    sendChat(player, "railcraft.gui.charge.meter.node", avg, n.getChargeDef().getMaintenanceCost());
                else {
                    boolean infiniteBat = n.getBattery().isInfinite();
                    sendChat(player, "railcraft.gui.charge.meter.producer", infiniteBat ? "INF" : n.getBattery().getCharge(), infiniteBat ? "INF" : 0.0);
                }
            });
            returnValue = EnumActionResult.SUCCESS;
        }
//        } catch (Throwable er) {
//            Game.logErrorAPI(Railcraft.MOD_ID, er, ChargeNetwork.class);
//            ChatPlugin.sendLocalizedChatFromServer(player, "chat.railcraft.api.error");
//        }
        return returnValue;
    }

    private void sendChat(EntityPlayer player, String msg, Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Double)
                args[i] = HumanReadableNumberFormatter.format((Double) args[i]);
        }
        ChatPlugin.sendLocalizedChatFromServer(player, msg, args);
    }
}
