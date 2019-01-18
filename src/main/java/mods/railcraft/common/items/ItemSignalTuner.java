/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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

@Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "ic2")
public class ItemSignalTuner extends ItemPairingTool implements IBoxable {
    private static final String LOC_PREFIX = "gui.railcraft.tuner.";

    public ItemSignalTuner() {
        super(LOC_PREFIX);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                " T ",
                "BRB",
                "   ",
                'B', Blocks.STONE_BUTTON,
                'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'T', Blocks.REDSTONE_TORCH);
    }

    //TODO: Add chat name highlighting formatting styles
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (actionCleanPairing(stack, playerIn, worldIn, IControllerTile.class, IControllerTile::getController)) {
            return EnumActionResult.SUCCESS;
        }
        TileEntity recTile = worldIn.getTileEntity(pos);
        if (recTile != null) {
            WorldCoordinate previousTarget = getPairData(stack);
            if (recTile instanceof IReceiverTile && previousTarget != null) {
                if (Game.isHost(worldIn)) {
                    SignalReceiver receiver = ((IReceiverTile) recTile).getReceiver();
                    if (!Objects.equals(pos, previousTarget.getPos())) {
                        TileEntity conTile = worldIn.getTileEntity(previousTarget.getPos());
                        if (conTile instanceof IControllerTile) {
                            SignalController controller = ((IControllerTile) conTile).getController();
                            if (recTile != conTile) {
                                controller.createPair(recTile);
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
            } else if (recTile instanceof IControllerTile) {
                if (Game.isHost(worldIn)) {
                    SignalController controller = ((IControllerTile) recTile).getController();
                    if (previousTarget == null || !Objects.equals(pos, previousTarget.getPos())) {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, LOC_PREFIX + "start", controller.getLocalizationTag());
                        setPairData(stack, recTile);
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
