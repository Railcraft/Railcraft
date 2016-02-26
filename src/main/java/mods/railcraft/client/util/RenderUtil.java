package mods.railcraft.client.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;

public class RenderUtil {

	public static void renderString(String name, double xOffset, double yOffset, double zOffset) {
		RenderManager rm = RenderManager.instance;
		FontRenderer fontrenderer = rm.getFontRenderer();
		float f = 1.6F;
		float f1 = 1 / 60F * f;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) xOffset, (float) yOffset, (float) zOffset);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-f1, -f1, f1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.instance;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		int j = fontrenderer.getStringWidth(name) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		tessellator.addVertex((double) (-j - 1), (double) -1, 0.0D);
		tessellator.addVertex((double) (-j - 1), (double) 8, 0.0D);
		tessellator.addVertex((double) (j + 1), (double) 8, 0.0D);
		tessellator.addVertex((double) (j + 1), (double) -1, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, 0, 553648127);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, 0, -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}
