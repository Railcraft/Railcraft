/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxAnalog;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiBoxAnalogController extends GuiBasic {

    private final TileBoxAnalog tile;
    private static final Pattern PATTERN_RANGE = Pattern.compile("(\\d+)-(\\d+)|(\\d+)");
    //When doing Pattern.matcher, these are the groups:           ^ 1    ^ 2    ^ 3

    private final EnumMap<SignalAspect, BitSet> aspects = new EnumMap<>(SignalAspect.class);
    private final EnumMap<SignalAspect, GuiTextField> textBox = new EnumMap<>(SignalAspect.class);

    public GuiBoxAnalogController(TileBoxAnalog tile) {
        super(LocalizationPlugin.translate(tile.getName()), "gui_basic_large.png", 176, 113);
        this.tile = tile;
        for (Map.Entry<SignalAspect, BitSet> entry : tile.aspects.entrySet()) {
            aspects.put(entry.getKey(), (BitSet) entry.getValue().clone());
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) throws IOException {
        super.mouseClicked(i, j, k);
        for (GuiTextField t : textBox.values()) {
            t.mouseClicked(i, j, k);
        }
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        //Disallow any PRINTABLE characters that are not digits, commas, or dashes
        if (c < ' ' || (c >= '0' && c <= '9') || c == '-' || c == ',' || c > '~')
            for (GuiTextField t : textBox.values()) {
                t.textboxKeyTyped(c, i);
            }
    }

    private String rangeToString(BitSet b) {
        StringBuilder s = new StringBuilder();
        int start = -1;
        for (int i = 0; i < 16; i++) {
            if (b.get(i)) {
                if (start == -1) {
                    s.append(i);
                    start = i;
                }
            } else if (start != -1) {
                if (i - 1 == start)
                    s.append(",");
                else
                    s.append("-").append(i - 1).append(",");
                start = -1;
            }
        }
        if (start != -1 && start != 15) {
            s.append("-15");
            start = 15;
        }

        if ((s.length() == 0) || start == 15)
            return s.toString();
        else
            return s.substring(0, s.length() - 1);    //Remove trailing comma
    }

    private void parseRegex(String regex, BitSet bits) {
        bits.clear();
        Matcher m = PATTERN_RANGE.matcher(regex);
        while (m.find()) {
            if (m.groupCount() >= 3 && m.group(3) != null) {
                int i = Integer.parseInt(m.group(3));
                if (i >= 0 && i <= 15)
                    bits.set(i);
            } else {
                int start = Integer.parseInt(m.group(1));
                int end = Integer.parseInt(m.group(2));
                if (start >= 0 && end >= 0 && end <= 15 && start <= end)
                    for (int i = start; i <= end; i++) {
                        bits.set(i);
                    }
            }
        }
    }

    @Override
    public void initGui() {
        if (tile == null)
            return;
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
            GuiTextField textField = new GuiTextField(entry.getKey().ordinal(), fontRenderer, w + 72, h + getYPosFromIndex(entry.getKey().ordinal()), 95, 10);
            textField.setMaxStringLength(37);
            textField.setText(rangeToString(entry.getValue()));
            textBox.put(entry.getKey(), textField);
        }

    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        textBox.values().forEach(GuiTextField::drawTextBox);
    }

    @Override
    protected void drawExtras(int x, int y, float f) {
        for (SignalAspect aspect : SignalAspect.VALUES) {
            drawAlignedString(fontRenderer, LocalizationPlugin.translate(aspect.getLocalizationTag()), 10, getYPosFromIndex(aspect.ordinal()) + 1, 50);
        }
    }

    @Override
    public void updateScreen() {
        textBox.values().forEach(GuiTextField::updateCursorCounter);

        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        if (Game.isClient(tile.getWorld())) {
            for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
                parseRegex(textBox.get(entry.getKey()).getText(), entry.getValue());
            }
            tile.aspects = aspects;
            PacketGuiReturn pkt = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(pkt);
        }
    }

    private static void drawAlignedString(FontRenderer fr, String s, int x, int y, int width) {
        fr.drawString(s, x + (width - fr.getStringWidth(s)) / 2, y, 0x404040);
    }

    private static int getYPosFromIndex(int i) {
        return 22 + i * 14;
    }

}
