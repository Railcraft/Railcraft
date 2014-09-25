/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.tooltips;

import com.google.common.base.Splitter;
import com.google.common.collect.ForwardingList;
import java.util.ArrayList;
import java.util.List;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ToolTip extends ForwardingList<ToolTipLine> {

    private static final Splitter lineSplitter = Splitter.on("\n").trimResults();
    private static final Splitter keyValueSplitter = Splitter.on('=').trimResults();
    private final List<ToolTipLine> delegate = new ArrayList<ToolTipLine>();
    private final long delay;
    private long mouseOverStart;

    public ToolTip() {
        this.delay = 0;
    }

    public ToolTip(int delay) {
        this.delay = delay;
    }

    @Override
    protected final List<ToolTipLine> delegate() {
        return delegate;
    }

    public boolean add(String line) {
        return add(new ToolTipLine(line));
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
        List<String> tips = new ArrayList<String>(size());
        for (ToolTipLine line : this) {
            tips.add(line.text);
        }
        return tips;
    }

    public static ToolTip buildToolTip(String tipTag, String... vars) {
        if (!LocalizationPlugin.hasTag(tipTag))
            return null;
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
            Game.logThrowable("Failed to parse tooltip: " + tipTag, ex);
            throw ex;
        }
    }

}
