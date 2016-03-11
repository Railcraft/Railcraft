package mods.railcraft.client.gui;

import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.signals.TileBoxAnalogController;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketGuiReturn;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.opengl.GL11;

public class GuiBoxAnalogController extends GuiBasic {

    private final TileBoxAnalogController tile;
    private final static Pattern patternRange = Pattern.compile("(\\d+)-(\\d+)|(\\d+)");
    //When doing Pattern.matcher, these are the groups:           ^ 1    ^ 2    ^ 3

    private final EnumMap<SignalAspect, BitSet> aspects = new EnumMap<SignalAspect, BitSet>(SignalAspect.class);
    private final EnumMap<SignalAspect, GuiTextField> textbox = new EnumMap<SignalAspect, GuiTextField>(SignalAspect.class);

    public GuiBoxAnalogController(TileBoxAnalogController tile) {
        super(tile.getName(), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_basic_large.png", 176, 113);
        this.tile = tile;
        for (Map.Entry<SignalAspect, BitSet> entry : tile.aspects.entrySet()) {
            aspects.put(entry.getKey(), (BitSet) entry.getValue().clone());
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        for (GuiTextField t : textbox.values()) {
            t.mouseClicked(i, j, k);
        }
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        //Disallow any PRINTABLE characters that are not digits, commas, or dashes
        if (c < ' ' || (c >= '0' && c <= '9') || c == '-' || c == ',' || c > '~')
            for (GuiTextField t : textbox.values()) {
                t.textboxKeyTyped(c, i);
            }
    }

    private String rangeToString(BitSet b) {
        String s = "";
        int start = -1;
        for (int i = 0; i < 16; i++) {
            if (b.get(i)) {
                if (start == -1) {
                    s += i;
                    start = i;
                }
            } else if (start != -1) {
                if (i - 1 == start)
                    s += ",";
                else
                    s += "-" + (i - 1) + ",";
                start = -1;
            }
        }
        if (start != -1 && start != 15) {
            s += "-15";
            start = 15;
        }

        if (s.isEmpty() || start == 15)
            return s;
        else
            return s.substring(0, s.length() - 1);	//Remove trailing comma
    }

    private void parseRegex(String regex, BitSet bits) {
        bits.clear();
        Matcher m = patternRange.matcher(regex);
        while (m.find()) {
            if (m.groupCount() >= 3 && m.group(3) != null) {
                int i = Integer.parseInt(m.group(3));
                if (i >= 0 && i <= 15)
                    bits.set(i);
            } else {
                int start = Integer.parseInt(m.group(1));
                int end = Integer.parseInt(m.group(2));
                if (start >= 0 && end >= 0 && start <= 15 && end <= 15 && start <= end)
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
            GuiTextField textField = new GuiTextField(fontRendererObj, w + 72, h + getYPosFromIndex(entry.getKey().ordinal()), 95, 10);
            textField.setMaxStringLength(37);
            textField.setText(rangeToString(entry.getValue()));
            textbox.put(entry.getKey(), textField);
        }

    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        GL11.glDisable(GL11.GL_LIGHTING);
        for (GuiTextField t : textbox.values()) {
            t.drawTextBox();
        }
    }

    @Override
    protected void drawExtras(int x, int y, float f) {
        for (SignalAspect aspect : SignalAspect.VALUES) {
            drawAlignedString(fontRendererObj, LocalizationPlugin.translate(aspect.getLocalizationTag()), 10, getYPosFromIndex(aspect.ordinal()) + 1, 50);
        }
    }

    @Override
    public void updateScreen() {
        for (GuiTextField t : textbox.values()) {
            t.updateCursorCounter();
        }

        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        if (Game.isNotHost(tile.getWorld())) {
            for (Map.Entry<SignalAspect, BitSet> entry : aspects.entrySet()) {
                parseRegex(textbox.get(entry.getKey()).getText(), entry.getValue());
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
