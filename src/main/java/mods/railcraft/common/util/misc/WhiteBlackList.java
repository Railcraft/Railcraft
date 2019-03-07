/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import java.util.HashSet;
import java.util.Set;

/**
 * A combination of black list and white list.
 *
 * The blacklist has a higher priority than the whitelist.
 */
public final class WhiteBlackList<T> {

    private final Set<T> blacklist;
    private final Set<T> whitelist;
    private boolean allowAll;

    public static <T> WhiteBlackList<T> create(Set<T> blacklist, Set<T> whitelist) {
        return new WhiteBlackList<>(blacklist, whitelist, false);
    }

    public static <T> WhiteBlackList<T> create(Set<T> blacklist) {
        return new WhiteBlackList<>(blacklist, new HashSet<>(), true);
    }

    private WhiteBlackList(Set<T> blacklist, Set<T> whitelist, boolean allowAll) {
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.allowAll = allowAll;
    }

    public Set<T> getBlacklist() {
        return blacklist;
    }

    public Set<T> getWhitelist() {
        return whitelist;
    }

    public void restrictToWhitelist() {
        this.allowAll = false;
    }

    public void allowAll() {
        this.allowAll = true;
    }

    public boolean isAllowingAll() {
        return allowAll;
    }

    public boolean permits(T entry) {
        return !blacklist.contains(entry) && (allowAll || whitelist.contains(entry));
    }

    public PermissionLevel getPermissionLevel(T entry) {
        if (blacklist.contains(entry)) {
            return PermissionLevel.BLACKLISTED;
        }
        if (allowAll || whitelist.contains(entry)) {
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
