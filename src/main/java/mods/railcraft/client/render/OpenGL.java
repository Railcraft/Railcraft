/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by CovertJaguar on 5/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>, inspired by some code posted by MamiyaOtaru (http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/modification-development/2231761-opengl-calls-glstatemanager)
 */
public enum OpenGL {
    //GL11
    GL_ALPHA_TEST(GL11.GL_ALPHA_TEST),
    GL_BLEND(GL11.GL_BLEND),
    GL_CLAMP(GL11.GL_CLAMP),
    GL_COLOR_BUFFER_BIT(GL11.GL_COLOR_BUFFER_BIT),
    GL_COLOR_CLEAR_VALUE(GL11.GL_COLOR_CLEAR_VALUE),
    GL_CULL_FACE(GL11.GL_CULL_FACE),
    GL_DEPTH_BUFFER_BIT(GL11.GL_DEPTH_BUFFER_BIT),
    GL_DST_ALPHA(GL11.GL_DST_ALPHA),
    GL_DST_COLOR(GL11.GL_DST_COLOR),
    GL_FOG(GL11.GL_FOG),
    GL_DEPTH_TEST(GL11.GL_DEPTH_TEST),
    GL_FLAT(GL11.GL_FLAT),
    GL_FOG_DENSITY(GL11.GL_FOG_DENSITY),
    GL_FOG_END(GL11.GL_FOG_END),
    GL_FOG_MODE(GL11.GL_FOG_MODE),
    GL_FOG_START(GL11.GL_FOG_START),
    GL_GREATER(GL11.GL_GREATER),
    GL_LIGHTING(GL11.GL_LIGHTING),
    GL_LINEAR(GL11.GL_LINEAR),
    GL_LINE_SMOOTH(GL11.GL_LINE_SMOOTH),
    GL_LINE_SMOOTH_HINT(GL11.GL_LINE_SMOOTH_HINT),
    GL_LINES(GL11.GL_LINES),
    GL_MODELVIEW(GL11.GL_MODELVIEW),
    GL_NEAREST(GL11.GL_NEAREST),
    GL_NICEST(GL11.GL_NICEST),
    GL_NORMALIZE(GL11.GL_NORMALIZE),
    GL_ONE(GL11.GL_ONE),
    GL_ONE_MINUS_DST_ALPHA(GL11.GL_ONE_MINUS_DST_ALPHA),
    GL_ONE_MINUS_SRC_ALPHA(GL11.GL_ONE_MINUS_SRC_ALPHA),
    GL_POLYGON_OFFSET_FILL(GL11.GL_POLYGON_OFFSET_FILL),
    GL_PROJECTION(GL11.GL_PROJECTION),
    GL_QUADS(GL11.GL_QUADS),
    GL_SMOOTH(GL11.GL_SMOOTH),
    GL_SRC_ALPHA(GL11.GL_SRC_ALPHA),
    GL_TEXTURE(GL11.GL_TEXTURE),
    GL_TEXTURE_2D(GL11.GL_TEXTURE_2D),
    GL_TEXTURE_HEIGHT(GL11.GL_TEXTURE_HEIGHT),
    GL_TEXTURE_MAG_FILTER(GL11.GL_TEXTURE_MAG_FILTER),
    GL_TEXTURE_MIN_FILTER(GL11.GL_TEXTURE_MIN_FILTER),
    GL_TEXTURE_WIDTH(GL11.GL_TEXTURE_WIDTH),
    GL_TEXTURE_WRAP_S(GL11.GL_TEXTURE_WRAP_S),
    GL_TEXTURE_WRAP_T(GL11.GL_TEXTURE_WRAP_T),
    GL_TRANSFORM_BIT(GL11.GL_TRANSFORM_BIT),
    GL_VIEWPORT_BIT(GL11.GL_VIEWPORT_BIT),
    GL_ZERO(GL11.GL_ZERO),

    //GL12
    GL_RESCALE_NORMAL(GL12.GL_RESCALE_NORMAL);
    private int mask;

    OpenGL(int mask) {
        this.mask = mask;
    }

    public static void glEnable(OpenGL attrib) {
        switch (attrib) {
            case GL_ALPHA_TEST:
                GlStateManager.enableAlpha();
                break;
            case GL_BLEND:
                GlStateManager.enableBlend();
                break;
            case GL_CULL_FACE:
                GlStateManager.enableCull();
                break;
            case GL_DEPTH_TEST:
                GlStateManager.enableDepth();
                break;
            case GL_FOG:
                GlStateManager.enableFog();
                break;
            case GL_LIGHTING:
                GlStateManager.enableLighting();
                break;
            case GL_NORMALIZE:
                GlStateManager.enableNormalize();
                break;
            case GL_POLYGON_OFFSET_FILL:
                GlStateManager.enablePolygonOffset();
                break;
            case GL_RESCALE_NORMAL:
                GlStateManager.enableRescaleNormal();
                break;
            case GL_TEXTURE_2D:
                GlStateManager.enableTexture2D();
                break;
            default:
                GL11.glEnable(attrib.mask);
        }
    }

    public static void glDisable(OpenGL attrib) {
        switch (attrib) {
            case GL_ALPHA_TEST:
                GlStateManager.disableAlpha();
                break;
            case GL_BLEND:
                GlStateManager.disableBlend();
                break;
            case GL_CULL_FACE:
                GlStateManager.disableCull();
                break;
            case GL_DEPTH_TEST:
                GlStateManager.disableDepth();
                break;
            case GL_FOG:
                GlStateManager.disableFog();
                break;
            case GL_LIGHTING:
                GlStateManager.disableLighting();
                break;
            case GL_NORMALIZE:
                GlStateManager.disableNormalize();
                break;
            case GL_POLYGON_OFFSET_FILL:
                GlStateManager.disablePolygonOffset();
                break;
            case GL_RESCALE_NORMAL:
                GlStateManager.disableRescaleNormal();
                break;
            case GL_TEXTURE_2D:
                GlStateManager.disableTexture2D();
                break;
            default:
                GL11.glDisable(attrib.mask);
        }
    }

    public static void glHint(OpenGL target, OpenGL mode) {
        GL11.glHint(target.mask, mode.mask);
    }

    public static void glFogi(OpenGL pname, int param) {
        switch (pname) {
            case GL_FOG_MODE:
                GlStateManager.setFog(param);
                break;
        }
    }

    public static void glFogf(OpenGL pname, float param) {
        switch (pname) {
            case GL_FOG_DENSITY:
                GlStateManager.setFogDensity(param);
                break;
            case GL_FOG_END:
                GlStateManager.setFogEnd(param);
                break;
            case GL_FOG_START:
                GlStateManager.setFogStart(param);
                break;
        }
    }

    public static void glAlphaFunc(int func, float ref) {
        GlStateManager.alphaFunc(func, ref);
    }

    public static void glBlendFunc(OpenGL sfactor, OpenGL dfactor) {
        GlStateManager.blendFunc(sfactor.mask, dfactor.mask);
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
        GlStateManager.cullFace(mode);
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

    public static void glMatrixMode(OpenGL mode) {
        GlStateManager.matrixMode(mode.mask);
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
        GlStateManager.popAttrib();
    }

    public static void glPopMatrix() {
        GlStateManager.popMatrix();
    }

    public static void glPushAttrib() {
        GlStateManager.pushAttrib();
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

    public static void glBegin(OpenGL mode) {
        GL11.glBegin(mode.mask);
    }

    public static void glBindTexture(OpenGL target, int texture) {
        switch (target) {
            case GL_TEXTURE_2D:
                GlStateManager.bindTexture(texture);
                break;
            default:
                GL11.glBindTexture(target.mask, texture);
        }
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

    public static void glVertex(Vec3 vertex) {
        GL11.glVertex3f((float) vertex.xCoord, (float) vertex.yCoord, (float) vertex.zCoord);
    }

    public static void glLineWidth(float width) {
        GL11.glLineWidth(width);
    }
}