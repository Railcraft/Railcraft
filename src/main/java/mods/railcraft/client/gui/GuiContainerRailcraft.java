/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.client.gui.buttons.GuiBetterButton;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.gui.widgets.Widget;
import mods.railcraft.common.util.collections.Streams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public abstract class GuiContainerRailcraft extends GuiContainer {

    public final RailcraftContainer container;
    public final ResourceLocation texture;

    protected GuiContainerRailcraft(RailcraftContainer container, String texture) {
        super(container);
        this.container = container;
        this.texture = GuiTools.findTexture(texture);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, par3);
        int left = guiLeft;
        int top = guiTop;

        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_DEPTH_TEST);
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef((float) left, (float) top, 0.0F);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();

        InventoryPlayer playerInv = mc.player.inventory;

        if (playerInv.getItemStack().isEmpty()) {
            int mX = mouseX - left;
            int mY = mouseY - top;
            for (Widget element : container.getWidgets()) {
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
            for (GuiButton button : buttonList) {
                if (!(button instanceof GuiBetterButton))
                    continue;
                GuiBetterButton<?> betterButton = (GuiBetterButton<?>) button;
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
                if (!slot.getStack().isEmpty())
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

        OpenGL.glPopMatrix();
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glEnable(GL11.GL_DEPTH_TEST);

        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(texture);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        drawGuiBackground(x, y);

        int mX = mouseX - guiLeft;
        int mY = mouseY - guiTop;

        for (Widget element : container.getWidgets()) {
            if (element.hidden)
                continue;
            element.draw(this, x, y, mX, mY);
        }
    }

    protected void drawGuiBackground(int x, int y) {
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void drawGradientRect(int x1, int y1, int x2, int y2, int c1, int c2) {
        super.drawGradientRect(x1, y1, x2, y2, c1, c2);
    }

    private @Nullable Slot getSlotAtPosition(int par1, int par2) {
        return inventorySlots.inventorySlots.stream()
                .filter(var4 -> isMouseOverSlot(var4, par1, par2))
                .findFirst().orElse(null);
    }

    /**
     * Returns if the passed mouse position is over the specified slot.
     */
    private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3) {
        int var4 = guiLeft;
        int var5 = guiTop;
        par2 -= var4;
        par3 -= var5;
        return par2 >= par1Slot.xPos - 1 && par2 < par1Slot.xPos + 16 + 1 && par3 >= par1Slot.yPos - 1 && par3 < par1Slot.yPos + 16 + 1;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {

        int mX = mouseX - guiLeft;
        int mY = mouseY - guiTop;

        if (container.getWidgets().stream()
                .filter(element -> !element.hidden)
                .filter(element -> element.isMouseOver(mX, mY))
                .anyMatch(element -> element.mouseClicked(mX, mY, button))) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseClickMove(int x, int y, int mouseButton, long time) {
        Slot slot = getSlotAtPosition(x, y);
        if (mouseButton == 1 && slot instanceof SlotRailcraft && ((SlotRailcraft) slot).isPhantom())
            return;
        super.mouseClickMove(x, y, mouseButton, time);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button instanceof GuiBetterButton)
            ((GuiBetterButton<?>) button).consumeClick();
        buttonList.stream().flatMap(Streams.toType(GuiBetterButton.class)).forEach(GuiBetterButton::updateStatus);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        buttonList.stream().flatMap(Streams.toType(GuiBetterButton.class)).forEach(GuiBetterButton::updateStatus);
    }

    private void drawToolTips(ToolTip toolTips, int mouseX, int mouseY) {
        if (!toolTips.isEmpty()) {
            int left = guiLeft;
            int top = guiTop;
            int length = 0;
            int x;
            int y;

            for (ToolTipLine tip : toolTips) {
                y = fontRenderer.getStringWidth(tip.text);

                if (y > length)
                    length = y;
            }

            x = mouseX - left + 12;
            y = mouseY - top - 12;
            int var14 = 8;

            if (toolTips.size() > 1)
                var14 += 2 + (toolTips.size() - 1) * 10;

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int var15 = -267386864;
            drawGradientRect(x - 3, y - 4, x + length + 3, y - 3, var15, var15);
            drawGradientRect(x - 3, y + var14 + 3, x + length + 3, y + var14 + 4, var15, var15);
            drawGradientRect(x - 3, y - 3, x + length + 3, y + var14 + 3, var15, var15);
            drawGradientRect(x - 4, y - 3, x - 3, y + var14 + 3, var15, var15);
            drawGradientRect(x + length + 3, y - 3, x + length + 4, y + var14 + 3, var15, var15);
            int var16 = 1347420415;
            int var17 = (var16 & 16711422) >> 1 | var16 & -16777216;
            drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + var14 + 3 - 1, var16, var17);
            drawGradientRect(x + length + 2, y - 3 + 1, x + length + 3, y + var14 + 3 - 1, var16, var17);
            drawGradientRect(x - 3, y - 3, x + length + 3, y - 3 + 1, var16, var16);
            drawGradientRect(x - 3, y + var14 + 2, x + length + 3, y + var14 + 3, var17, var17);

            for (ToolTipLine tip : toolTips) {
                fontRenderer.drawStringWithShadow(tip.toString(), x, y, -1);

                y += 10 + tip.getSpacing();
            }

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
        }
    }

    public void bindTexture(ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }

    public float getZLevel() {
        return zLevel;
    }

//    public void drawTexture(int x, int y, int w, int h, float uMin, float vMin, float uMax, float vMax) {
//        Tessellator tessellator = Tessellator.instance();
//        WorldRenderer wr = tessellator.getWorldRenderer();
//        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        wr.pos(x + 0, y + h, zLevel).tex(uMin, vMax).endVertex();
//        wr.pos(x + w, y + h, zLevel).tex(uMax, vMax).endVertex();
//        wr.pos(x + w, y + 0, zLevel).tex(uMax, vMin).endVertex();
//        wr.pos(x + 0, y + 0, zLevel).tex(uMin, vMin).endVertex();
//    }

}
