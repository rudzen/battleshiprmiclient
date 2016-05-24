/*
 * The MIT License
 *
 * Copyright 2016 rudz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package utility;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Support class with various useful nuggets.
 *
 * @author rudz
 */
public final class Statics {

    public static volatile boolean yourTurn;

    public static volatile boolean isLoggedIn;
    public static volatile boolean gameInProgress;

    /* properties */
    public static String lastUser;
    public static String lastPassword;
    public static String lastRegistry = null;

    public static final String PROPERTIES = "./BattleshipRMIClient.properties";

    /* time format */
    public static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    /* build time constant */
    public static final Date buildDate = getClassBuildTime();

    /**
     * To get a simple formatted time string used for listings
     *
     * @return The current time as string
     */
    public static String getTimeString() {
        final Calendar cal = Calendar.getInstance();
        return SDF.format(cal.getTime());
    }

    /**
     * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
     * by : GGB667 at http://stackoverflow.com/a/22404140
     *
     * @return The date if it can be determined, or null if not.
     */
    private static Date getClassBuildTime() {
        Date d = null;
        Class<?> currentClass = new Object() {
        }.getClass().getEnclosingClass();
        URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
        if (resource != null) {
            String path;
            switch (resource.getProtocol()) {
                case "file":
                    try {
                        d = new Date(new File(resource.toURI()).lastModified());
                    } catch (URISyntaxException ignored) {
                    }
                    break;
                case "jar":
                    path = resource.getPath();
                    d = new Date(new File(path.substring(5, path.indexOf("!"))).lastModified());
                    break;
                case "zip":
                    path = resource.getPath();
                    File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
                    //long jfodLastModifiedLong = jarFileOnDisk.lastModified ();
                    //Date jfodLasModifiedDate = new Date(jfodLastModifiedLong);
                    try (JarFile jf = new JarFile(jarFileOnDisk)) {
                        ZipEntry ze = jf.getEntry(path.substring(path.indexOf("!") + 2));//Skip the ! and the /
                        long zeTimeLong = ze.getTime();
                        Date zeTimeDate = new Date(zeTimeLong);
                        d = zeTimeDate;
                    } catch (IOException | RuntimeException ignored) {
                    }
                    break;
                default:
                    break;
            }
        }
        return d;
    }

    public static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (preferIPv6) {
                            continue;
                        }
                        return addr;
                    }
                    if (addr instanceof Inet6Address) {
                        if (preferIpv4) {
                            continue;
                        }
                        return addr;
                    }
                }
            }
        }
        return null;
    }
}
