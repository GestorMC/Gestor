package com.mcgoodtime.gjmlc.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LibrariesManager {
    private static final List<String> missingLib = new ArrayList<>();
    private static final List<String> nativesLib = new ArrayList<>();

    protected static void checkLibraries() {

        if (GJMLC.verInfoObject.has("inheritsFrom")) {
            String parentText = GJMLC.loadVersionInfoFile(GJMLC.verInfoObject.get("inheritsFrom").getAsString());
            JsonObject parentVerInfoObj = JsonParser.parseString(parentText).getAsJsonObject();
            JsonArray parentLibArray = (JsonArray) parentVerInfoObj.get("libraries");
            check(parentLibArray);
        }
        check(GJMLC.libArray);

        System.err.println("Missing " + missingLib.size() + " Libraries");

        for (String s : missingLib) {
            System.err.println(s);
        }

        deleteFile(GJMLC.nativesPath);

        for (String aNativesList : nativesLib) {
            try {
                unZipFiles(new File(aNativesList), GJMLC.nativesPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void check(JsonArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject arrayObject = (JsonObject) jsonArray.get(i);
            String lib = arrayObject.get("name").getAsString();
            String a = lib.substring(0, lib.lastIndexOf(":")).replace(".", "/").replace(":", "/");
            String b = lib.substring(lib.lastIndexOf(":") + 1);
            String c = lib.substring(lib.indexOf(":") + 1).replace(":", "-");
            String libs = "./.minecraft/libraries/" + a + "/" + b + "/" + c + ".jar";
            File fileLib = new File(libs);
            if (!fileLib.exists()) {
                if (arrayObject.has("natives")) {
                    JsonObject nativesObject = (JsonObject) arrayObject.get("natives");
                    String natives = null;
                    if (SystemUtils.IS_OS_WINDOWS) {
                        natives = nativesObject.get("windows").getAsString().replace("${arch}", SystemUtils.OS_ARCH);
                    }
                    if (SystemUtils.IS_OS_LINUX) {
                        natives = nativesObject.get("linux").getAsString();
                    }
                    if (SystemUtils.IS_OS_MAC_OSX) {
                        natives = nativesObject.get("osx").getAsString();
                    }
                    String nLibs = "./.minecraft/libraries/" + a + "/" + b + "/" + c + "-" + natives + ".jar";
                    File nFileLib = new File(nLibs);
                    if (!nFileLib.exists()) {
                        missingLib.add(lib);
                    } else {
                        nativesLib.add(nLibs);
                    }
                } else {
                    missingLib.add(lib);
                }
            }
        }
    }

    private static void unZipFiles(File zipFile, String descDir) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (new File(outPath).isDirectory()) {
                continue;
            }

            System.out.println(outPath);

            OutputStream out = null;
            try {
                out = new FileOutputStream(outPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                if (out != null) {
                    try {
                        out.write(buf1, 0, len);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            in.close();
            if (out != null) {
                out.close();
            }
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] ff = file.listFiles();
            for (File aFf : ff) {
                deleteFile(aFf.getPath());
            }
        }
        file.delete();
    }
}