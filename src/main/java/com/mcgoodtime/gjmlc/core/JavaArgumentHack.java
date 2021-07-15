package com.mcgoodtime.gjmlc.core;

import com.redgrapefruit.openmodinstaller.util.ToBeImproved;

/**
 * A dirty hack to avoid Kotlin formatting when ${} isn't needed.
 */
@ToBeImproved
public class JavaArgumentHack {
    public static final String VERSION_NAME = "${version_name}";
    public static final String GAME_DIRECTORY = "${game_directory}";
    public static final String ASSETS_ROOT = "${assets_root}";
    public static final String ASSETS_INDEX_NAME = "${assets_index_name}";
    public static final String AUTH_UUID = "${auth_uuid}";
    public static final String AUTH_ACCESS_TOKEN = "${auth_access_token}";
    public static final String USER_PROPERTIES = "${user_properties}";
    public static final String USER_TYPE = "${user_type}";
}
