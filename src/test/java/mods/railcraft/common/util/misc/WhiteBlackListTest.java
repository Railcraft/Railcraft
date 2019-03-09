package mods.railcraft.common.util.misc;

import com.google.common.collect.Sets;
import mods.railcraft.common.util.misc.WhiteBlackList.PermissionLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class WhiteBlackListTest {

    @Test
    void test() {
        Set<String> whiteList = Sets.newHashSet("apple", "bear", "pear");
        Set<String> blackList = Sets.newHashSet("apple", "non", "nana");
        WhiteBlackList<String> whiteBlackList = WhiteBlackList.create(blackList, whiteList);
        Assertions.assertFalse(whiteBlackList.permits("apple"));
        Assertions.assertTrue(whiteBlackList.permits("pear"));
        Assertions.assertFalse(whiteBlackList.permits("dana"));
        Assertions.assertEquals(PermissionLevel.DEFAULT, whiteBlackList.getPermissionLevel("abcde"));
        Assertions.assertEquals(PermissionLevel.BLACKLISTED, whiteBlackList.getPermissionLevel("non"));
        Assertions.assertEquals(PermissionLevel.WHITELISTED, whiteBlackList.getPermissionLevel("bear"));
        Assertions.assertEquals(PermissionLevel.BLACKLISTED, whiteBlackList.getPermissionLevel("apple"));
        Assertions.assertFalse(whiteBlackList.permits("nana"));
        Assertions.assertFalse(whiteBlackList.permits("non"));
        Assertions.assertTrue(whiteBlackList.permits("bear"));
    }
}
