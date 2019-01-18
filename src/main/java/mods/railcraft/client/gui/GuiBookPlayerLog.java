/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.util.ResourceLocation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CovertJaguar on 6/23/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiBookPlayerLog extends GuiBook {

    public static final ResourceLocation TEXTURE = GuiTools.findTexture("logbook.png");
    public static final String LOG_TAG = "gui.railcraft.logbook.";

    public GuiBookPlayerLog(Multimap<LocalDate, GameProfile> log) {
        super(TEXTURE, LOG_TAG, LocalizationPlugin.translate(LOG_TAG + "title"), "CovertJaguar", getPages(log), false);
    }

    private static List<List<String>> getPages(Multimap<LocalDate, GameProfile> log) {
        List<List<String>> pages = new ArrayList<>();
        List<LocalDate> days = new ArrayList<>(log.keySet());
        days.sort(Comparator.reverseOrder());
        try {
            for (LocalDate day : days) {
                List<String> page = makePage(pages, day);
                for (GameProfile profile : log.get(day)) {
                    if (page.size() > RailcraftConstants.BOOK_LINES_PER_PAGE)
                        page = makePage(pages, day);
                    page.add(profile.getName());
                }
            }
        } catch (TooManyPagesException ignored) {
        }
        return pages;
    }

    private static List<String> makePage(List<List<String>> pages, LocalDate date) throws TooManyPagesException {
        if (pages.size() >= RailcraftConstants.BOOK_MAX_PAGES)
            throw new TooManyPagesException();
        LinkedList<String> page = new LinkedList<>();
        page.add(date.toString());
        page.add("----------------------------------");
        pages.add(page);
        return page;
    }

    private static class TooManyPagesException extends Exception {
    }
}
