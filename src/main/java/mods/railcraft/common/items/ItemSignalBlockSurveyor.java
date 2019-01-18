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
import mods.railcraft.api.signals.IPair;
import mods.railcraft.api.signals.ISignalTile;
import mods.railcraft.api.signals.ISignalTileBlock;
import mods.railcraft.api.signals.TrackLocator;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
public class ItemSignalBlockSurveyor extends ItemPairingTool implements IBoxable {
    public ItemSignalBlockSurveyor() {
        super("gui.railcraft.surveyor.");
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                " C ",
                "BGB",
                " R ",
                'G', "paneGlassColorless",
                'C', Items.COMPASS,
                'B', Blocks.STONE_BUTTON,
                'R', "dustRedstone");
    }

    //TODO: Add chat name highlighting formatting styles
    //TODO: This function could probably be picked apart and pulled into the super class, but meh...
    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        System.out.println("click");
        ItemStack stack = playerIn.getHeldItem(hand);
        if (actionCleanPairing(stack, playerIn, worldIn, ISignalTileBlock.class, ISignalTileBlock::getSignalBlock)) {
            return EnumActionResult.SUCCESS;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null)
            if (tile instanceof ISignalTile) {
//            System.out.println("target found");
                if (Game.isHost(worldIn)) {
                    ISignalTile signalTile = (ISignalTile) tile;
                    IPair signalBlock = signalTile.getPair();
                    WorldCoordinate signalPos = getPairData(stack);
                    TrackLocator.Status trackStatus = signalTile.getTrackLocator().getTrackStatus();
                    if (trackStatus == TrackLocator.Status.INVALID)
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.track", signalTile.getDisplayName());
                    else if (signalPos == null) {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.begin");
                        setPairData(stack, tile);
                        signalBlock.startPairing();
                    } else if (!Objects.equals(pos, signalPos.getPos())) {
//                System.out.println("attempt pairing");
                        tile = WorldPlugin.getBlockTile(worldIn, signalPos.getPos());
                        if (tile instanceof ISignalTile) {
                            if (signalBlock.createPair(tile)) {
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.success");
                                clearPairData(stack);
                            } else
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.invalid");
                        } else if (WorldPlugin.isBlockLoaded(worldIn, signalPos.getPos())) {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.lost");
                            signalBlock.endPairing();
                            clearPairData(stack);
                        } else
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.unloaded");
                    } else {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.abandon");
                        signalBlock.endPairing();
                        clearPairData(stack);
                    }
                }
                return EnumActionResult.SUCCESS;
            } else if (Game.isHost(worldIn))
                ChatPlugin.sendLocalizedChatFromServer(playerIn, "gui.railcraft.surveyor.wrong");
        return EnumActionResult.PASS;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}
