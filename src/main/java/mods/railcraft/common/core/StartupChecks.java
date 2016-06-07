/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.core;

import mods.railcraft.common.util.misc.Game;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StartupChecks {

    private static String latest = Railcraft.getVersion();
    private static boolean versionCheckCompleted;
    private static boolean hasUpdated;
    private static boolean sendMessage = true;
    //    private static final String RELEASE_URL = "http://bit.ly/version_RC_main";
    private static final String RELEASE_URL = "http://www.railcraft.info/version";
    private static final String BETA_URL = "http://bit.ly/version_RC_beta";

    private static class VersionCheckThread extends Thread {

        public VersionCheckThread() {
            super("Railcraft Version Check");
        }

        @Override
        public void run() {
            try {
                String location = RELEASE_URL;

                if (!Railcraft.getVersion().endsWith("0")) {
                    location = BETA_URL;
                }

                HttpURLConnection connection = null;
                while (location != null && !location.isEmpty()) {
                    URL url = new URL(location);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
                    connection.connect();
                    location = connection.getHeaderField("Location");
                }

                if (connection == null) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                latest = reader.readLine().trim();
                reader.close();
                connection.disconnect();
                String[] currentTokens = Railcraft.getVersion().trim().split("\\.");
                String[] latestTokens = latest.split("\\.");

                if (currentTokens.length != latestTokens.length) {
                    Game.log(Level.INFO, "Could not parse version check, an updated version is probably available: {0}", latest);
                    hasUpdated = true;
                    return;
                }

                for (int i = 0; i < currentTokens.length; i++) {
                    int c = Integer.valueOf(currentTokens[i]);
                    int l = Integer.valueOf(latestTokens[i]);
                    if (l > c) {
                        Game.log(Level.INFO, "An updated version of Railcraft is available from <http://railcraft.info>: {0}", latest);
                        hasUpdated = true;
                    }
                    if (c > l) break;
                }
            } catch (Exception ex) {
                Game.log(Level.WARN, "Latest Version Check Failed: {0}", ex);
            }

            if (hasUpdated) {
                FileInputStream fis = null;
                FileOutputStream fos = null;
                try {
                    File configFolder = Railcraft.getMod().getConfigFolder();
                    if (!configFolder.exists()) {
                        configFolder.mkdirs();
                    }
                    File versionFile = new File(configFolder, "version.prop");
                    Properties versionProp = new Properties();
                    if (versionFile.exists()) {
                        fis = new FileInputStream(versionFile);
                        versionProp.load(fis);
                    }
                    String lastSeenVersion = versionProp.getProperty("latest-version");
                    if (lastSeenVersion == null) {
                        lastSeenVersion = "";
                    }
                    lastSeenVersion = lastSeenVersion.trim();
                    versionProp.setProperty("latest-version", latest);
                    String lastMessageString = versionProp.getProperty("last-message");
                    boolean timeElapsed = true;
                    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.ROOT);
                    if (lastMessageString != null) {
                        try {
                            Date lastMessageDate = dateFormat.parse(lastMessageString);
                            long threeDays = TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS);
                            timeElapsed = System.currentTimeMillis() - lastMessageDate.getTime() >= threeDays;
                        } catch (ParseException ex) {
                            Game.log(Level.WARN, "Failed to parse last Version Check Message info: {0}", ex);
                        }
                    }
                    versionProp.setProperty("last-message", dateFormat.format(new Date()));
                    sendMessage = !latest.equals(lastSeenVersion) || timeElapsed;
                    fos = new FileOutputStream(versionFile);
                    versionProp.store(fos, "Information for update message");
                } catch (Exception ex) {
                    Game.log(Level.WARN, "Failed to retrieve last Version Check Message info: {0}", ex);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException ignored) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
            }

            versionCheckCompleted = true;
        }

    }

    public static void checkForNewVersion() {
        if (!RailcraftConfig.doUpdateCheck()) {
            return;
        }
        Thread versionCheckThread = new VersionCheckThread();
        versionCheckThread.start();
    }

    public static boolean isVersionCheckComplete() {
        return versionCheckCompleted;
    }

    public static boolean shouldSendMessage() {
        return sendMessage && hasUpdated;
    }

    public static String getLatestVersion() {
        return latest;
    }

}
