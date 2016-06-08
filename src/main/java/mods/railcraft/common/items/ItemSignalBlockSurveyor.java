/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 * <p>
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.items;

import ic2.api.item.IBoxable;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.ISignalBlockTile;
import mods.railcraft.api.signals.SignalBlock;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
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

@Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2")
public class ItemSignalBlockSurveyor extends ItemPairingTool implements IBoxable {
    public ItemSignalBlockSurveyor() {
        super("railcraft.gui.surveyor");
    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.signalBlockSurveyor, 1, 1, LootPlugin.Type.WORKSHOP);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
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
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        System.out.println("click");
        if (actionCleanPairing(stack, playerIn, worldIn, ISignalBlockTile.class, ISignalBlockTile::getSignalBlock)) {
            return EnumActionResult.SUCCESS;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null)
            if (tile instanceof ISignalBlockTile) {
//            System.out.println("target found");
                if (Game.isHost(worldIn)) {
                    ISignalBlockTile signalTile = (ISignalBlockTile) tile;
                    SignalBlock signalBlock = signalTile.getSignalBlock();
                    WorldCoordinate signalPos = getPairData(stack);
                    SignalBlock.Status trackStatus = signalBlock.getTrackStatus();
                    if (trackStatus == SignalBlock.Status.INVALID)
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.track", signalTile.getDisplayName());
                    else if (signalPos == null) {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.begin");
                        setPairData(stack, tile);
                        signalBlock.startPairing();
                    } else if (!pos.equals(signalPos)) {
//                System.out.println("attempt pairing");
                        tile = WorldPlugin.getBlockTile(worldIn, signalPos);
                        if (tile instanceof ISignalBlockTile) {
                            ISignalBlockTile otherTile = (ISignalBlockTile) tile;
                            SignalBlock otherSignal = otherTile.getSignalBlock();
                            if (signalBlock.createSignalBlock(otherSignal)) {
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.success");
                                clearPairData(stack);
                            } else
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.invalid");
                        } else if (WorldPlugin.isBlockLoaded(worldIn, signalPos)) {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.lost");
                            signalBlock.endPairing();
                            clearPairData(stack);
                        } else
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.unloaded");
                    } else {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.abandon");
                        signalBlock.endPairing();
                        clearPairData(stack);
                    }
                }
                return EnumActionResult.SUCCESS;
            } else if (Game.isHost(worldIn))
                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.wrong");
        return EnumActionResult.PASS;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}
