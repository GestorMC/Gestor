package com.mcgoodtime.gjmlc.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;


/**
 * Created by suhao on 2015-6-8-0008.
 *
 * @author suhao
 */
public class GJMLC {

    public static JsonObject verInfoObject;
    public static JsonArray libArray;

    private static String text;

    private static JsonParser parser = new JsonParser();

    protected static String versionPath = "./.minecraft/versions/";
    private static String versionInfoJson;
    protected static String nativesPath;

    private String version;
    private String parentVer;

    /*
     * Demo.
     */
    public static void main(String[] args) {
        GJMLC launcher = new GJMLC("1.8.9");
        launcher.checkLibs();
        launcher.launch("GJMLC", 1024, null);
    }

    public GJMLC(String version) {
        text = loadVersionInfoFile(version);
        verInfoObject = parser.parse(text).getAsJsonObject();
        libArray = (JsonArray) verInfoObject.get("libraries");
        nativesPath =  versionPath + version + "/" + version + "-" + "Natives" + "/";
        this.version = version;
    }

    /**
     * Before you launch, you must run the libraries checker.
     *
     * @param username Minecraft username.
     * @param maxMemory Java VM max use memory.
     */
    public void launch(String username, int maxMemory, String jvmArgs) {
        String id = getVersionInfo("id");
        String time = getVersionInfo("time");
        String releaseTime = getVersionInfo("releaseTime");
        String minecraftArguments = getVersionInfo("minecraftArguments");
        int minimumLauncherVersion = getVersionInfoAsInt(text, "minimumLauncherVersion");
        String mainClass = getVersionInfo("mainClass");
        String assets = getVersionInfo("assets");

        String libraries;
        Boolean isInherits;
        if (verInfoObject.has("inheritsFrom")) {
            libraries = getLibraries(text);

            parentVer = getVersionInfo("inheritsFrom");
            String parentText = loadVersionInfoFile(parentVer);
            JsonObject parentVerInfoObj = parser.parse(parentText).getAsJsonObject();
            JsonArray parentLibArray = (JsonArray) parentVerInfoObj.get("libraries");

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < parentLibArray.size(); i++) {
                JsonObject arrayObject = (JsonObject) parentLibArray.get(i);
                String lib = arrayObject.get("name").toString();
                String a = lib.substring(0, lib.lastIndexOf(":")).replace(".", "/").replace(":", "/");
                String b = lib.substring(lib.lastIndexOf(":") + 1);
                String c = lib.substring(lib.indexOf(":") + 1).replace(":", "-");
                String libs = "\"" + "./.minecraft/libraries/" + a + "/" + b + "/" + c + ".jar" + "\"" + ";";
                stringBuffer.append(libs);
            }
            isInherits = true;
            libraries = libraries + stringBuffer.toString();
        } else {
            isInherits = false;
            libraries = getLibraries(text);
        }

        if (jvmArgs == null) {
            jvmArgs = "";
        }

        tryToLaunch(libraries, minecraftArguments, mainClass, assets, username, maxMemory, jvmArgs, isInherits);
    }

    private void tryToLaunch(String libraries, String minecraftArguments,
                             String mainClass, String assets, String username, int maxMemory, String jvmArgs, boolean isInherits) {

        String classPath;
        if (isInherits) {
            classPath = versionPath + parentVer + "/" + parentVer + ".jar";
        } else {
            classPath = versionPath + version + "/" + version + ".jar";
        }

        String arg = minecraftArguments.replace("${auth_player_name}", username)
                .replace("${version_name}", version)
                .replace("${game_directory}", "./.minecraft")
                .replace("${assets_root}", "./.minecraft/assets")
                .replace("${assets_index_name}", assets)
                .replace("${auth_uuid}", "auth_uuid")
                .replace("${auth_access_token}", "auth_access_token")
                .replace("${user_properties}", "{}")
                .replace("${user_type}", "legacy");

        String cmd = "java -Xmx" + maxMemory + "M" + " " + "-XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:-UseAdaptiveSizePolicy" + " "
                + jvmArgs + "-Djava.library.path=" + nativesPath + " " + "-classpath" + " " + libraries + "\"" + classPath + "\"" + " " + mainClass + " " + arg;
        System.out.println(cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            readProcessOutput(process);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void readProcessOutput(Process process) {
        read(process.getInputStream(), System.out);
        read(process.getErrorStream(), System.err);
    }

    private static void read(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get Version Info From Loaded Json
     */
    private String getVersionInfo(String key) {
        return verInfoObject.get(key).getAsString();
    }

    /**
     * Get Version Info From Loaded Json
     */
    private int getVersionInfoAsInt(String text, String key) {
        return verInfoObject.get(key).getAsInt();
    }

    /**
     * Load version info from json file
     * @param version Launch Minecraft version.
     * @return Minecraft version info file text.
     */
    protected static String loadVersionInfoFile(String version) {
        versionInfoJson = versionPath + version + "/" + version + ".json";
        System.out.println("Version Info Json Path:" + versionInfoJson);

        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(versionInfoJson)));
            while( (line = br.readLine())!= null ){
                stringBuffer.append(line);
            }
            br.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    protected String  getLibraries(String text) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < libArray.size(); i++) {
            JsonObject arrayObject = (JsonObject) libArray.get(i);
            String lib = arrayObject.get("name").toString();
            String a = lib.substring(0, lib.lastIndexOf(":")).replace(".", "/").replace(":", "/");
            String b = lib.substring(lib.lastIndexOf(":") + 1);
            String c = lib.substring(lib.indexOf(":") + 1).replace(":", "-");
            String libs = "\"" + "./.minecraft/libraries/" + a + "/" + b + "/" + c + ".jar" + "\"" + ";";
            stringBuffer.append(libs);
        }
        return stringBuffer.toString();
    }

    public void checkLibs() {
        LibrariesManager.checkLibraries();
    }
}