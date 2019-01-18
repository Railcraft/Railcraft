/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.tools;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by CovertJaguar on 5/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>, inspired by some code posted by MamiyaOtaru (http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/modification-development/2231761-opengl-calls-glstatemanager)
 */
@SideOnly(Side.CLIENT)
public class OpenGL {

    public static void glEnable(int attrib) {
        switch (attrib) {
            case GL11.GL_ALPHA_TEST:
                GlStateManager.enableAlpha();
                break;
            case GL11.GL_BLEND:
                GlStateManager.enableBlend();
                break;
            case GL11.GL_CULL_FACE:
                GlStateManager.enableCull();
                break;
            case GL11.GL_DEPTH_TEST:
                GlStateManager.enableDepth();
                break;
            case GL11.GL_FOG:
                GlStateManager.enableFog();
                break;
            case GL11.GL_LIGHTING:
                GlStateManager.enableLighting();
                break;
            case GL11.GL_NORMALIZE:
                GlStateManager.enableNormalize();
                break;
            case GL11.GL_POLYGON_OFFSET_FILL:
                GlStateManager.enablePolygonOffset();
                break;
            case GL12.GL_RESCALE_NORMAL:
                GlStateManager.enableRescaleNormal();
                break;
            case GL11.GL_TEXTURE_2D:
                GlStateManager.enableTexture2D();
                break;
            default:
                GL11.glEnable(attrib);
        }
    }

    public static void glDisable(int attrib) {
        switch (attrib) {
            case GL11.GL_ALPHA_TEST:
                GlStateManager.disableAlpha();
                break;
            case GL11.GL_BLEND:
                GlStateManager.disableBlend();
                break;
            case GL11.GL_CULL_FACE:
                GlStateManager.disableCull();
                break;
            case GL11.GL_DEPTH_TEST:
                GlStateManager.disableDepth();
                break;
            case GL11.GL_FOG:
                GlStateManager.disableFog();
                break;
            case GL11.GL_LIGHTING:
                GlStateManager.disableLighting();
                break;
            case GL11.GL_NORMALIZE:
                GlStateManager.disableNormalize();
                break;
            case GL11.GL_POLYGON_OFFSET_FILL:
                GlStateManager.disablePolygonOffset();
                break;
            case GL12.GL_RESCALE_NORMAL:
                GlStateManager.disableRescaleNormal();
                break;
            case GL11.GL_TEXTURE_2D:
                GlStateManager.disableTexture2D();
                break;
            default:
                GL11.glDisable(attrib);
        }
    }

    public static void glHint(int target, int mode) {
        GL11.glHint(target, mode);
    }

    public static void glAlphaFunc(int func, float ref) {
        GlStateManager.alphaFunc(func, ref);
    }

    public static void glBlendFunc(int sfactor, int dfactor) {
        GlStateManager.blendFunc(sfactor, dfactor);
    }

    public static void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
        GlStateManager.tryBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
    }

    public static void glCallList(int list) {
        GlStateManager.callList(list);
    }

    public static void glClear(int mask) {
        GlStateManager.clear(mask);
    }

    public static void glClearColor(float red, float green, float blue,
                                    float alpha) {
        GlStateManager.clearColor(red, green, blue, alpha);
    }

    public static void glClearDepth(double depth) {
        GlStateManager.clearDepth(depth);
    }

    public static void glColor3f(float red, float green, float blue) {
        GlStateManager.color(red, green, blue, 1.0F);
    }

    public static void glColor4f(float red, float green, float blue, float alpha) {
        GlStateManager.color(red, green, blue, alpha);
    }

    public static void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GlStateManager.colorMask(red, green, blue, alpha);
    }

    public static void glColorMaterial(int face, int mode) {
        GlStateManager.colorMaterial(face, mode);
    }

    public static void glCullFace(int mode) {
        if (mode == GlStateManager.CullFace.BACK.mode) {
            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        } else if (mode == GlStateManager.CullFace.FRONT.mode) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        } else if (mode == GlStateManager.CullFace.FRONT_AND_BACK.mode) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT_AND_BACK);
        }
    }

    public static void glDepthFunc(int func) {
        GlStateManager.depthFunc(func);
    }

    public static void glDepthMask(boolean flag) {
        GlStateManager.depthMask(flag);
    }

    public static void glGetFloat(int pname, FloatBuffer params) {
        GlStateManager.getFloat(pname, params);
    }

    public static void glLoadIdentity() {
        GlStateManager.loadIdentity();
    }

    public static void glLogicOp(int opcode) {
        GlStateManager.colorLogicOp(opcode);
    }

    public static void glMatrixMode(int mode) {
        GlStateManager.matrixMode(mode);
    }

    public static void glMultMatrix(FloatBuffer m) {
        GlStateManager.multMatrix(m);
    }

    public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) {
        GlStateManager.ortho(left, right, bottom, top, zNear, zFar);
    }

    public static void glPolygonOffset(float factor, float units) {
        GlStateManager.doPolygonOffset(factor, units);
    }

    public static void glPopAttrib() {
        GL11.glPopAttrib();  // Forge #1637
//        GlStateManager.popAttrib();
    }

    public static void glPopMatrix() {
        GlStateManager.popMatrix();
    }

    public static void glPushAttrib() {
        GL11.glPushAttrib(8256); // Forge #1637
//        GlStateManager.pushAttrib();
    }

    public static void glPushMatrix() {
        GlStateManager.pushMatrix();
    }

    public static void glRotatef(float angle, float x, float y, float z) {
        GlStateManager.rotate(angle, x, y, z);
    }

    public static void glScaled(double x, double y, double z) {
        GlStateManager.scale(x, y, z);
    }

    public static void glScalef(float x, float y, float z) {
        GlStateManager.scale(x, y, z);
    }

    public static void glSetActiveTextureUnit(int texture) {
        GlStateManager.setActiveTexture(texture);
    }

    public static void glShadeModel(int mode) {
        GlStateManager.shadeModel(mode);
    }

    public static void glTranslated(double x, double y, double z) {
        GlStateManager.translate(x, y, z);
    }

    public static void glTranslatef(float x, float y, float z) {
        GlStateManager.translate(x, y, z);
    }

    public static void glViewport(int x, int y, int width, int height) {
        GlStateManager.viewport(x, y, width, height);
    }

    public static void glBegin(int mode) {
        GL11.glBegin(mode);
    }

    public static void glEnd() {
        GL11.glEnd();
    }

    public static int glGenTextures() {
        return GL11.glGenTextures();
    }

    public static int glGetTexLevelParameteri(int target, int level, int pname) {
        return GL11.glGetTexLevelParameteri(target, level, pname);
    }

    public static void glNormal3f(float nx, float ny, float nz) {
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void glPushAttrib(int mask) {
        GL11.glPushAttrib(mask);
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, IntBuffer pixels) {
        GL11.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void glTexParameteri(int target, int pname, int param) {
        GL11.glTexParameteri(target, pname, param);
    }

    public static void glVertex2f(float x, float y) {
        GL11.glVertex2f(x, y);
    }

    public static void glVertex3f(float x, float y, float z) {
        GL11.glVertex3f(x, y, z);
    }

    public static void glVertex(Vec3d vertex) {
        GL11.glVertex3f((float) vertex.x, (float) vertex.y, (float) vertex.z);
    }

    public static void glLineWidth(float width) {
        GlStateManager.glLineWidth(width);
    }
}