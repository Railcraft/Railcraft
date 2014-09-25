/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.railcraft.common.gui.containers.ContainerAnvil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiAnvil extends GuiContainer implements ICrafting {

    private static final ResourceLocation anvilGuiTextures = new ResourceLocation("textures/gui/container/anvil.png");
    private ContainerRepair repairContainer;
    private GuiTextField itemNameField;
    private InventoryPlayer playerInv;

    public GuiAnvil(InventoryPlayer playerInv, World world, int x, int y, int z) {
        super(new ContainerAnvil(playerInv, world, x, y, z, Minecraft.getMinecraft().thePlayer));
        this.playerInv = playerInv;
        this.repairContainer = (ContainerRepair) this.inventorySlots;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.itemNameField = new GuiTextField(this.fontRendererObj, i + 62, j + 24, 103, 12);
        this.itemNameField.setTextColor(-1);
        this.itemNameField.setDisabledTextColour(-1);
        this.itemNameField.setEnableBackgroundDrawing(false);
        this.itemNameField.setMaxStringLength(40);
        this.inventorySlots.removeCraftingFromCrafters(this);
        this.inventorySlots.addCraftingToCrafters(this);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat
     * events
     */
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.inventorySlots.removeCraftingFromCrafters(this);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        GL11.glDisable(GL11.GL_LIGHTING);
        this.fontRendererObj.drawString(I18n.format("container.repair"), 60, 6, 4210752);

        if (this.repairContainer.maximumCost > 0) {
            int k = 8453920;
            boolean flag = true;
            String s = I18n.format("container.repair.cost", new Object[]{Integer.valueOf(this.repairContainer.maximumCost)});

            if (this.repairContainer.maximumCost >= 40 && !this.mc.thePlayer.capabilities.isCreativeMode) {
                s = I18n.format("container.repair.expensive");
                k = 16736352;
            } else if (!this.repairContainer.getSlot(2).getHasStack())
                flag = false;
            else if (!this.repairContainer.getSlot(2).canTakeStack(this.playerInv.player))
                k = 16736352;

            if (flag) {
                int l = -16777216 | (k & 16579836) >> 2 | k & -16777216;
                int i1 = this.xSize - 8 - this.fontRendererObj.getStringWidth(s);
                byte b0 = 67;

                if (this.fontRendererObj.getUnicodeFlag()) {
                    drawRect(i1 - 3, b0 - 2, this.xSize - 7, b0 + 10, -16777216);
                    drawRect(i1 - 2, b0 - 1, this.xSize - 8, b0 + 9, -12895429);
                } else {
                    this.fontRendererObj.drawString(s, i1, b0 + 1, l);
                    this.fontRendererObj.drawString(s, i1 + 1, b0, l);
                    this.fontRendererObj.drawString(s, i1 + 1, b0 + 1, l);
                }

                this.fontRendererObj.drawString(s, i1, b0, k);
            }
        }

        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char par1, int par2) {
        if (this.itemNameField.textboxKeyTyped(par1, par2))
            this.func_135015_g();
        else
            super.keyTyped(par1, par2);
    }

    private void func_135015_g() {
        String s = this.itemNameField.getText();
        Slot slot = this.repairContainer.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
            s = "";

        this.repairContainer.updateItemName(s);
        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", s.getBytes()));
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        this.itemNameField.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        GL11.glDisable(GL11.GL_LIGHTING);
        this.itemNameField.drawTextBox();
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(anvilGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(k + 59, l + 20, 0, this.ySize + (this.repairContainer.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((this.repairContainer.getSlot(0).getHasStack() || this.repairContainer.getSlot(1).getHasStack()) && !this.repairContainer.getSlot(2).getHasStack())
            this.drawTexturedModalRect(k + 99, l + 45, this.xSize, 0, 28, 21);
    }

    @Override
    public void sendContainerAndContentsToPlayer(Container par1Container, List par2List) {
        this.sendSlotContents(par1Container, 0, par1Container.getSlot(0).getStack());
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container.
     * This doesn't have to match the actual contents of that slot. Args:
     * Container, slot number, slot contents
     */
    @Override
    public void sendSlotContents(Container par1Container, int par2, ItemStack par3ItemStack) {
        if (par2 == 0) {
            this.itemNameField.setText(par3ItemStack == null ? "" : par3ItemStack.getDisplayName());
            this.itemNameField.setEnabled(par3ItemStack != null);

            if (par3ItemStack != null)
                this.func_135015_g();
        }
    }

    /**
     * Sends two ints to the client-side Container. Used for furnace burning
     * time, smelting progress, brewing progress, and enchanting level. Normally
     * the first int identifies which variable to update, and the second
     * contains the new value. Both are truncated to shorts in non-local SMP.
     */
    @Override
    public void sendProgressBarUpdate(Container par1Container, int par2, int par3) {
    }

}
