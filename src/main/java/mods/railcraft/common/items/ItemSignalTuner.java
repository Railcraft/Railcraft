/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import ic2.api.item.IBoxable;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.Objects;

@Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2")
public class ItemSignalTuner extends ItemPairingTool implements IBoxable {
    private static final String LOC_PREFIX = "gui.railcraft.tuner.";

    public ItemSignalTuner() {
        super(LOC_PREFIX);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                " T ",
                "BRB",
                "   ",
                'B', Blocks.STONE_BUTTON,
                'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'T', Blocks.REDSTONE_TORCH);
    }

    //TODO: Add chat name highlighting formatting styles
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (actionCleanPairing(stack, playerIn, worldIn, IControllerTile.class, IControllerTile::getController)) {
            return EnumActionResult.SUCCESS;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null) {
            WorldCoordinate previousTarget = getPairData(stack);
            if (tile instanceof IReceiverTile && previousTarget != null) {
                if (Game.isHost(worldIn)) {
                    SignalReceiver receiver = ((IReceiverTile) tile).getReceiver();
                    if (!Objects.equals(pos, previousTarget.getPos())) {
                        tile = worldIn.getTileEntity(previousTarget.getPos());
                        if (tile instanceof IControllerTile) {
                            SignalController controller = ((IControllerTile) tile).getController();
                            if (receiver.getTile() != controller.getTile()) {
                                controller.registerReceiver(receiver);
                                controller.endPairing();
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, LOC_PREFIX + "success", controller.getLocalizationTag(), receiver.getLocalizationTag());
                                clearPairData(stack);
                                return EnumActionResult.SUCCESS;
                            }
                        } else if (WorldPlugin.isBlockLoaded(worldIn, previousTarget.getPos())) {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, LOC_PREFIX + "abandon.gone");
                            clearPairData(stack);
                        } else {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, LOC_PREFIX + "abandon.chunk");
                            clearPairData(stack);
                        }
                    }
                }
            } else if (tile instanceof IControllerTile) {
                if (Game.isHost(worldIn)) {
                    SignalController controller = ((IControllerTile) tile).getController();
                    if (previousTarget == null || !Objects.equals(pos, previousTarget.getPos())) {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, LOC_PREFIX + "start", controller.getLocalizationTag());
                        setPairData(stack, tile);
                        controller.startPairing();
                    } else {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, LOC_PREFIX + "stop", controller.getLocalizationTag());
                        controller.endPairing();
                        clearPairData(stack);
                    }
                }
            } else
                return EnumActionResult.PASS;
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}
