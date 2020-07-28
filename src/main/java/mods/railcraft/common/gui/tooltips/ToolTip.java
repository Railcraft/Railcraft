/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.tooltips;

import com.google.common.base.Splitter;
import com.google.common.collect.ForwardingList;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ToolTip extends ForwardingList<ToolTipLine> {

    private static final Splitter lineSplitter = Splitter.on("\n").trimResults();
    private static final Splitter keyValueSplitter = Splitter.on('=').trimResults();
    private final List<ToolTipLine> delegate = new ArrayList<>();
    private final long delay;
    private long mouseOverStart;

    public ToolTip() {
        this.delay = 0;
    }

    public ToolTip(int delay) {
        this.delay = delay;
    }

    @Override
    protected List<ToolTipLine> delegate() {
        return delegate;
    }

    public void add(String line) {
        add(new ToolTipLine(line));
    }

    public void add(String line, TextFormatting format) {
        add(new ToolTipLine(line, format));
    }

    public void newline() {
        add("");
    }

    public void set(@Nullable ToolTip other) {
        if (other == null || other.isEmpty())
            return;
        clear();
        delegate.addAll(other.delegate);
    }

    public void onTick(boolean mouseOver) {
        if (delay == 0)
            return;
        if (mouseOver) {
            if (mouseOverStart == 0)
                mouseOverStart = System.currentTimeMillis();
        } else
            mouseOverStart = 0;
    }

    public boolean isReady() {
        if (delay == 0)
            return true;
        if (mouseOverStart == 0)
            return false;
        return System.currentTimeMillis() - mouseOverStart >= delay;
    }

    public void refresh() {
    }

    public List<String> convertToStrings() {
        return stream()
                .map(ToolTipLine::toString)
                .collect(Collectors.toList());
    }

    public static ToolTip buildToolTip(List<String> lines) {
        ToolTip toolTip = new ToolTip(750);
        toolTip.addAll(lines.stream().map(ToolTipLine::new).collect(Collectors.toList()));
        return toolTip;
    }

    public static ToolTip buildToolTip(String tipTag, String... vars) {
        if (!LocalizationPlugin.hasTag(tipTag))
            return new ToolTip();
        try {
            ToolTip toolTip = new ToolTip(750);
            String text = LocalizationPlugin.translate(tipTag);
            for (String var : vars) {
                List<String> pair = keyValueSplitter.splitToList(var);
                text = text.replace(pair.get(0), pair.get(1));
            }
            for (String tip : lineSplitter.split(text)) {
                toolTip.add(new ToolTipLine(tip));
            }
            return toolTip;
        } catch (RuntimeException ex) {
            Game.log().throwable("Failed to parse tooltip: " + tipTag, ex);
            throw ex;
        }
    }

//    private static ToolTip EMPTY = new ToolTip() {
//        @Override
//        protected List<ToolTipLine> delegate() {
//            return Collections.emptyList();
//        }
//    };
//
//    public static ToolTip empty() {
//        return EMPTY;
//    }
}
