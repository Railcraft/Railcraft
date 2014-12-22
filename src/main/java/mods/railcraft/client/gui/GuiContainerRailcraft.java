/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class GuiContainerRailcraft extends GuiContainer {

    public final RailcraftContainer container;
    public final ResourceLocation texture;

    public GuiContainerRailcraft(RailcraftContainer container, String texture) {
        super(container);
        this.container = container;
        this.texture = new ResourceLocation(texture);
    }

    /**
     * Draws the screen and all the components in it.
     * @param mouseX
     * @param mouseY
     * @param par3
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);
        int left = this.guiLeft;
        int top = this.guiTop;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) left, (float) top, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();

        InventoryPlayer playerInv = this.mc.thePlayer.inventory;

        if (playerInv.getItemStack() == null) {
            int mX = mouseX - left;
            int mY = mouseY - top;
            for (Widget element : container.getElements()) {
                if (element.hidden)
                    continue;
                ToolTip tips = element.getToolTip();
                if (tips == null)
                    continue;
                boolean mouseOver = element.isMouseOver(mX, mY);
                tips.onTick(mouseOver);
                if (mouseOver && tips.isReady()) {
                    tips.refresh();
                    drawToolTips(tips, mouseX, mouseY);
                }
            }
            for (Object button : buttonList) {
                if (!(button instanceof GuiBetterButton))
                    continue;
                GuiBetterButton betterButton = (GuiBetterButton) button;
                if (!betterButton.visible)
                    continue;
                ToolTip tips = betterButton.getToolTip();
                if (tips == null)
                    continue;
                boolean mouseOver = betterButton.isMouseOverButton(mouseX, mouseY);
                tips.onTick(mouseOver);
                if (mouseOver && tips.isReady()) {
                    tips.refresh();
                    drawToolTips(tips, mouseX, mouseY);
                }
            }
            for (Object obj : inventorySlots.inventorySlots) {
                if (!(obj instanceof SlotRailcraft))
                    continue;
                SlotRailcraft slot = (SlotRailcraft) obj;
                if (slot.getStack() != null)
                    continue;
                ToolTip tips = slot.getToolTip();
                if (tips == null)
                    continue;
                boolean mouseOver = isMouseOverSlot(slot, mouseX, mouseY);
                tips.onTick(mouseOver);
                if (mouseOver && tips.isReady()) {
                    tips.refresh();
                    drawToolTips(tips, mouseX, mouseY);
                }
            }
        }

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        int mX = mouseX - guiLeft;
        int mY = mouseY - guiTop;

        for (Widget element : container.getElements()) {
            if (element.hidden)
                continue;
            element.draw(this, x, y, mX, mY);
        }
    }

    @Override
    public void drawGradientRect(int x1, int y1, int x2, int y2, int c1, int c2) {
        super.drawGradientRect(x1, y1, x2, y2, c1, c2);
    }

//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
//        super.mouseClicked(mouseX, mouseY, mouseButton);
    // TODO: Fix button mouse mode passing
//        for (Object obj : buttonList) {
//            if (obj instanceof GuiBetterButton && ((GuiBetterButton)obj).mousePressed(this.mc, mouseX, mouseY, mouseButton)) {
//                this.selectedButton = guibutton;
//                this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
//                this.actionPerformed((GuiBetterButton)obj);
//            }
//
//            if () {
//            }
//        }
//        if (mouseButton == 2) {
//            Slot var4 = this.getSlotAtPosition(par1, par2);
//            int var5 = this.guiLeft;
//            int var6 = this.guiTop;
//            boolean var7 = par1 < var5 || par2 < var6 || par1 >= var5 + this.xSize || par2 >= var6 + this.ySize;
//            int var8 = -1;
//
//            if (var4 != null) {
//                var8 = var4.slotNumber;
//            }
//
//            if (var7) {
//                var8 = -999;
//            }
//
//            if (var8 != -1) {
//                boolean var9 = var8 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
//                this.handleMouseClick(var4, var8, mouseButton, var9 ? 1 : 0);
//            }
//        }
//    }
    private Slot getSlotAtPosition(int par1, int par2) {
        for (int var3 = 0; var3 < this.inventorySlots.inventorySlots.size(); ++var3) {
            Slot var4 = (Slot) this.inventorySlots.inventorySlots.get(var3);

            if (this.isMouseOverSlot(var4, par1, par2))
                return var4;
        }
        return null;
    }

    /**
     * Returns if the passed mouse position is over the specified slot.
     */
    private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
        int var4 = this.guiLeft;
        int var5 = this.guiTop;
        par2 -= var4;
        par3 -= var5;
        return par2 >= par1Slot.xDisplayPosition - 1 && par2 < par1Slot.xDisplayPosition + 16 + 1 && par3 >= par1Slot.yDisplayPosition - 1 && par3 < par1Slot.yDisplayPosition + 16 + 1;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {

        int mX = mouseX - guiLeft;
        int mY = mouseY - guiTop;

        for (Widget element : container.getElements()) {
            if (element.hidden)
                continue;
            if (!element.isMouseOver(mX, mY))
                continue;
            if (element.mouseClicked(mX, mY, button))
                return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseClickMove(int x, int y, int mouseButton, long time) {
        Slot slot = getSlotAtPosition(x, y);
        if (mouseButton == 1 && slot instanceof SlotRailcraft && ((SlotRailcraft)slot).isPhantom())
            return;
        super.mouseClickMove(x, y, mouseButton, time);
    }

    private void drawToolTips(ToolTip toolTips, int mouseX, int mouseY) {
        if (toolTips.size() > 0) {
            int left = this.guiLeft;
            int top = this.guiTop;
            int lenght = 0;
            int x;
            int y;

            for (ToolTipLine tip : toolTips) {
                y = this.fontRendererObj.getStringWidth(tip.text);

                if (y > lenght)
                    lenght = y;
            }

            x = mouseX - left + 12;
            y = mouseY - top - 12;
            int var14 = 8;

            if (toolTips.size() > 1)
                var14 += 2 + (toolTips.size() - 1) * 10;

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int var15 = -267386864;
            this.drawGradientRect(x - 3, y - 4, x + lenght + 3, y - 3, var15, var15);
            this.drawGradientRect(x - 3, y + var14 + 3, x + lenght + 3, y + var14 + 4, var15, var15);
            this.drawGradientRect(x - 3, y - 3, x + lenght + 3, y + var14 + 3, var15, var15);
            this.drawGradientRect(x - 4, y - 3, x - 3, y + var14 + 3, var15, var15);
            this.drawGradientRect(x + lenght + 3, y - 3, x + lenght + 4, y + var14 + 3, var15, var15);
            int var16 = 1347420415;
            int var17 = (var16 & 16711422) >> 1 | var16 & -16777216;
            this.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + var14 + 3 - 1, var16, var17);
            this.drawGradientRect(x + lenght + 2, y - 3 + 1, x + lenght + 3, y + var14 + 3 - 1, var16, var17);
            this.drawGradientRect(x - 3, y - 3, x + lenght + 3, y - 3 + 1, var16, var16);
            this.drawGradientRect(x - 3, y + var14 + 2, x + lenght + 3, y + var14 + 3, var17, var17);

            for (ToolTipLine tip : toolTips) {
                String line = tip.text;
                
                line = line.replace('\u00A0', ' ');

                if (tip.format == null)
                    line = "\u00a77" + line;
                else
                    line = tip.format.toString() + line;

                this.fontRendererObj.drawStringWithShadow(line, x, y, -1);

                y += 10 + tip.getSpacing();
            }

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
        }
    }

    public void bindTexture(ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }

    public void drawTexture(int x, int y, int w, int h, float uMin, float vMin, float uMax, float vMax) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + h, zLevel, uMin, vMax);
        tessellator.addVertexWithUV(x + w, y + h, zLevel, uMax, vMax);
        tessellator.addVertexWithUV(x + w, y + 0, zLevel, uMax, vMin);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, uMin, vMin);
        tessellator.draw();
    }

}
