/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import java.util.Collections;
import java.util.Set;

/**
 * A combination of black list and white list.
 */
public final class WhiteBlackList<T> {

    private final Set<T> blacklist;
    private final Set<T> whitelist;
    private boolean whitelistEnabled;

    public static <T> WhiteBlackList<T> create(Set<T> blacklist, Set<T> whitelist) {
        return new WhiteBlackList<>(blacklist, whitelist, true);
    }

    public static <T> WhiteBlackList<T> create(Set<T> blacklist) {
        return new WhiteBlackList<>(blacklist, Collections.emptySet(), false);
    }

    private WhiteBlackList(Set<T> blacklist, Set<T> whitelist, boolean whitelistEnabled) {
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.whitelistEnabled = whitelistEnabled;
    }

    public Set<T> getBlacklist() {
        return blacklist;
    }

    public Set<T> getWhitelist() {
        return whitelist;
    }

    public void disableWhiteList() {
        this.whitelistEnabled = false;
        whitelist.clear();
    }

    public void enableWhiteList() {
        this.whitelistEnabled = true;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public boolean permits(T entry) {
        return (!whitelistEnabled || whitelist.contains(entry)) && !blacklist.contains(entry);
    }

    public PermissionLevel getPermissionLevel(T entry) {
        if (blacklist.contains(entry)) {
            return PermissionLevel.BLACKLISTED;
        }
        if (whitelistEnabled && whitelist.contains(entry)) {
            return PermissionLevel.WHITELISTED;
        }
        return PermissionLevel.DEFAULT;
    }

    public enum PermissionLevel {
        BLACKLISTED(false, false),
        DEFAULT(true, false),
        WHITELISTED(true, true);

        private final boolean allowedWithoutWhitelist;
        private final boolean allowedWithWhitelist;

        PermissionLevel(boolean allowedWithoutWhitelist, boolean allowedWithWhitelist) {
            this.allowedWithoutWhitelist = allowedWithoutWhitelist;
            this.allowedWithWhitelist = allowedWithWhitelist;
        }

        public boolean isAllowedWithoutWhitelist() {
            return allowedWithoutWhitelist;
        }

        public boolean isAllowedWithWhitelist() {
            return allowedWithWhitelist;
        }
    }
}
