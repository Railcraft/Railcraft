/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import io.netty.buffer.Unpooled;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.gui.containers.ContainerAnvil;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiAnvil extends GuiContainer implements IContainerListener {

    private static final ResourceLocation anvilGuiTextures = new ResourceLocation("textures/gui/container/anvil.png");
    private final ContainerRepair repairContainer;
    private GuiTextField itemNameField;
    private final InventoryPlayer playerInv;

    public GuiAnvil(InventoryPlayer playerInv, World world, BlockPos pos) {
        super(new ContainerAnvil(playerInv, world, pos, Minecraft.getMinecraft().player));
        this.playerInv = playerInv;
        this.repairContainer = (ContainerRepair) inventorySlots;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        this.itemNameField = new GuiTextField(0, fontRenderer, i + 62, j + 24, 103, 12);
        itemNameField.setTextColor(-1);
        itemNameField.setDisabledTextColour(-1);
        itemNameField.setEnableBackgroundDrawing(false);
        itemNameField.setMaxStringLength(40);
        inventorySlots.removeListener(this);
        inventorySlots.addListener(this);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat
     * events
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        inventorySlots.removeListener(this);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        OpenGL.glDisable(GL11.GL_LIGHTING);
        fontRenderer.drawString(I18n.format("container.repair"), 60, 6, 4210752);

        if (repairContainer.maximumCost > 0) {
            int msgColor = 8453920;
            boolean flag = true;
            String msg = I18n.format("container.repair.cost", repairContainer.maximumCost);

            if (repairContainer.maximumCost >= ContainerAnvil.MAX_COST && !mc.player.capabilities.isCreativeMode) {
                msg = I18n.format("container.repair.expensive");
                msgColor = 16736352;
            } else if (!repairContainer.getSlot(2).getHasStack())
                flag = false;
            else if (!repairContainer.getSlot(2).canTakeStack(playerInv.player))
                msgColor = 16736352;

            if (flag) {
                int color = -16777216 | (msgColor & 16579836) >> 2 | msgColor & -16777216;
                int x = xSize - 8 - fontRenderer.getStringWidth(msg);
                byte y = 67;

                if (fontRenderer.getUnicodeFlag()) {
                    drawRect(x - 3, y - 2, xSize - 7, y + 10, -16777216);
                    drawRect(x - 2, y - 1, xSize - 8, y + 9, -12895429);
                } else {
                    fontRenderer.drawString(msg, x, y + 1, color);
                    fontRenderer.drawString(msg, x + 1, y, color);
                    fontRenderer.drawString(msg, x + 1, y + 1, color);
                }

                fontRenderer.drawString(msg, x, y, msgColor);
            }
        }

        OpenGL.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        if (itemNameField.textboxKeyTyped(par1, par2))
            func_135015_g();
        else
            super.keyTyped(par1, par2);
    }

    private void func_135015_g() {
        String s = itemNameField.getText();
        Slot slot = repairContainer.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
            s = "";

        repairContainer.updateItemName(s);
        mc.player.connection.sendPacket(new CPacketCustomPayload("MC|ItemName", (new PacketBuffer(Unpooled.buffer())).writeString(s)));
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        itemNameField.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        super.drawScreen(par1, par2, par3);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        itemNameField.drawTextBox();
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(anvilGuiTextures);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
        drawTexturedModalRect(k + 59, l + 20, 0, ySize + (repairContainer.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((repairContainer.getSlot(0).getHasStack() || repairContainer.getSlot(1).getHasStack()) && !repairContainer.getSlot(2).getHasStack())
            drawTexturedModalRect(k + 99, l + 45, xSize, 0, 28, 21);
    }

    @Override
    public void sendAllContents(Container container, NonNullList<ItemStack> itemStackList) {
        sendSlotContents(container, 0, container.getSlot(0).getStack());
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container.
     * This doesn't have to match the actual contents of that slot. Args:
     * Container, slot number, slot contents
     */
    @Override
    public void sendSlotContents(Container par1Container, int par2, @Nullable ItemStack stack) {
        if (par2 == 0) {
            itemNameField.setText(InvTools.isEmpty(stack) ? "" : stack.getDisplayName());
            itemNameField.setEnabled(!InvTools.isEmpty(stack));

            if (!InvTools.isEmpty(stack))
                func_135015_g();
        }
    }

    /**
     * Sends two integers to the client-side Container. Used for furnace burning
     * time, smelting progress, brewing progress, and enchanting level. Normally
     * the first int identifies which variable to update, and the second
     * contains the new value. Both are truncated to shorts in non-local SMP.
     */
    @Override
    public void sendWindowProperty(Container par1Container, int par2, int par3) {
    }

    @Override
    public void sendAllWindowProperties(Container p_175173_1_, IInventory p_175173_2_) {
    }
}
