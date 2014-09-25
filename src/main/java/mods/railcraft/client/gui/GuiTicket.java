/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.PacketCurrentItemNBT;
import mods.railcraft.common.util.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiTicket extends GuiScreen {

    public static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.GUI_TEXTURE_FOLDER + "ticket_gold.png");
    public static final int WRAP_WIDTH = 226;
    /**
     * The player editing the book
     */
    private final EntityPlayer player;
    private final ItemStack ticket;
    private boolean modified;
    private boolean readingManual;
    /**
     * Update ticks since the gui was opened
     */
    private int updateCount;
    private final int imageWidth = 256;
    private final int imageHeight = 136;
    private String dest = "";
    private GuiBetterButton buttonCancel;
    private GuiBetterButton buttonDone;
    private GuiBetterButton buttonHelp;

    public GuiTicket(EntityPlayer player, ItemStack stack) {
        this.player = player;
        this.ticket = stack;
        this.dest = ItemTicket.getDestination(ticket);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        List<GuiBetterButton> buttons = new ArrayList<GuiBetterButton>();
        buttons.add(buttonDone = new GuiBetterButton(0, 0, imageHeight + 25, 65, StatCollector.translateToLocal("gui.done")));
        buttons.add(buttonHelp = new GuiBetterButton(1, 0, imageHeight + 25, 65, LocalizationPlugin.translate("railcraft.gui.help")));
        buttons.add(buttonCancel = new GuiBetterButton(2, 0, imageHeight + 25, 65, StatCollector.translateToLocal("gui.cancel")));
        GuiTools.newButtonRowAuto(buttonList, width / 2 - 100, 200, buttons);
        updateButtons();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat
     * events
     */
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons() {
        buttonHelp.displayString = readingManual ? StatCollector.translateToLocal("gui.back") : LocalizationPlugin.translate("railcraft.gui.help");
    }

    private void sendToServer() {
        if (modified) {
            NBTTagCompound nbt = InvTools.getItemData(ticket);
            nbt.setString("dest", dest);
            if (!nbt.hasKey("owner")) {
                nbt.setString("owner", Railcraft.proxy.getPlayerUsername(player));
            }
            PacketCurrentItemNBT pkt = new PacketCurrentItemNBT(player, ticket);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button == buttonCancel) {
                this.mc.displayGuiScreen((GuiScreen) null);
            } else if (button == buttonDone) {
                this.mc.displayGuiScreen((GuiScreen) null);
                this.sendToServer();
            } else if (button == buttonHelp) {
                readingManual = !readingManual;
            }

            this.updateButtons();
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char c, int key) {
        super.keyTyped(c, key);
        switch (c) {
            case Keyboard.KEY_U:
                dest += GuiScreen.getClipboardString().replaceAll("\\s", "");
                modified = true;
                return;
            default:
                switch (key) {
                    case Keyboard.KEY_BACK:
                        if (dest.length() > 0) {
                            dest = dest.substring(0, dest.length() - 1);
                            modified = true;
                        }
                        return;
                    default:
                        if (!Character.isWhitespace(c) && ChatAllowedCharacters.isAllowedCharacter(c) && dest.length() + 1 < ItemTicket.LINE_LENGTH) {
                            dest += c;
                            modified = true;
                        }
                }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEXTURE);
        int xOffset = (this.width - this.imageWidth) / 2;
        byte yOffset = 18;
        this.drawTexturedModalRect(xOffset, yOffset, 0, 0, this.imageWidth, this.imageHeight);

        if (readingManual) {
            GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.routing.ticket.manual.title"), yOffset + 14, width);

            String text = LocalizationPlugin.translate("railcraft.gui.routing.ticket.manual");
            this.fontRendererObj.drawSplitString(text, xOffset + 16, yOffset + 30, WRAP_WIDTH, 0);
        } else {
            GL11.glPushMatrix();
            GL11.glScalef(2F, 2F, 2F);
            GuiTools.drawCenteredString(fontRendererObj, EnumChatFormatting.BOLD + LocalizationPlugin.translate("railcraft.gui.routing.ticket.title"), yOffset - 2, width / 2, 0xFFFFFF, true);
            GL11.glPopMatrix();


            GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.routing.ticket.line1"), yOffset + 50, width);
            GuiTools.drawCenteredString(fontRendererObj, LocalizationPlugin.translate("railcraft.gui.routing.ticket.line2"), yOffset + 65, width);
            String text = EnumChatFormatting.BLACK + "Dest=" + dest;
            if (fontRendererObj.getBidiFlag()) {
                text = text + "_";
            } else if (updateCount / 6 % 2 == 0) {
                text = text + "" + EnumChatFormatting.BLACK + "_";
            } else {
                text = text + "" + EnumChatFormatting.GRAY + "_";
            }
            this.fontRendererObj.drawSplitString(text, xOffset + 16, yOffset + 98, WRAP_WIDTH, 0);
        }

        super.drawScreen(par1, par2, par3);
    }

}
