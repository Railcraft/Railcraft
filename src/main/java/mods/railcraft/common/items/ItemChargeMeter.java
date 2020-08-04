/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.charge.*;
import mods.railcraft.api.items.ActivationBlockingItem;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.charge.BatteryBlock;
import mods.railcraft.common.util.charge.ChargeNetwork;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Capabilities;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.block.state.IBlockState;
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
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@ActivationBlockingItem
public class ItemChargeMeter extends ItemRailcraft {
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
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "T T",
                "BGB",
                " C ",
                'B', Blocks.STONE_BUTTON,
                'G', "paneGlassColorless",
                'C', "ingotBrass",
                'T', "ingotCopper");
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer player = event.getEntityPlayer();

        Entity entity = event.getTarget();

        ItemStack stack = event.getItemStack();
        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof ItemChargeMeter)
            player.swingArm(event.getHand());

        if (Game.isClient(player.world))
            return;

        if (!InvTools.isEmpty(stack) && stack.getItem() instanceof ItemChargeMeter)
            try {
                Capabilities.get(entity, CapabilitiesCharge.CART_BATTERY, null).ifPresent(battery -> {
                    sendChat(player, "gui.railcraft.charge.meter.cart", battery.getCharge(), battery.getDraw(), battery.getLosses());
                    event.setCanceled(true);
                });
            } catch (Throwable er) {
                Game.log().api(Railcraft.MOD_ID, er, IBatteryCart.class);
                ChatPlugin.sendLocalizedChatFromServer(player, "chat.railcraft.api.error");
            }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (Game.isClient(world))
            return EnumActionResult.PASS;
        EnumActionResult returnValue = EnumActionResult.PASS;
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (state.getBlock() instanceof IChargeBlock) {
            ChargeNetwork.ChargeNode node = (ChargeNetwork.ChargeNode) ((IChargeBlock) state.getBlock()).getMeterAccess(Charge.distribution, state, world, pos);
            if (node != null && node.isValid() && !node.isGridNull()) {
                sendChat(player, "gui.railcraft.charge.meter.start", SECONDS_TO_RECORD);
                node.startUsageRecording(SECONDS_TO_RECORD * 20, avg -> {
                    ChargeNetwork.ChargeGrid grid = node.getGrid();
                    sendChat(player, "gui.railcraft.charge.meter.network", grid.size(),
                            grid.isInfinite() ? "INF" : grid.getCharge(), grid.getAverageUsagePerTick(),
                            grid.getMaxDraw(), grid.getLosses(), grid.getEfficiency() * 100.0);

                    @Nullable BatteryBlock battery = node.getBattery().orElse(null);
                    if (battery == null)
                        sendChat(player, "gui.railcraft.charge.meter.node", avg, node.getChargeSpec().getLosses());
                    else {
                        // TODO: Handle all battery states better
                        boolean infiniteBat = battery.getState() == IBatteryBlock.State.INFINITE;
                        sendChat(player, "gui.railcraft.charge.meter.producer",
                                infiniteBat ? "INF" : battery.getCharge(),
                                infiniteBat ? "INF" : "NA",
                                battery.getMaxDraw(),
                                node.getChargeSpec().getLosses() * RailcraftConfig.chargeLossMultiplier(),
                                battery.getEfficiency() * 100.0);
                    }
                });
                returnValue = EnumActionResult.SUCCESS;
            }
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
