package com.tezov.wifer.application;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.wifer.R;

public class SharePreferenceKey extends com.tezov.lib_java_android.application.SharePreferenceKey{
public final static String FILE_NAME_SHARE_PREFERENCE = "SHARE_PREFERENCE";

public static final String SP_NAVIGATION_LAST_DESTINATION_STRING = "NAVIGATION_LAST_DESTINATION";
public static final String SP_SERVER_PORT_STRING = "SERVER_PORT";

public static final String SP_DESTINATION_DIRECTORY_STRING = AppContext.getResources().getIdentifierName(R.id.pref_destination_directory);
public static final String SP_SERVER_AUTO_SELECT_FILE_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_server_auto_select_file);
public static final String SP_SERVER_AUTO_START_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_server_auto_start);
public static final String SP_SERVER_AUTO_STOP_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_server_auto_stop);
public static final String SP_SERVER_AUTO_SEARCH_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_server_auto_search);
public static final String SP_OVERWRITE_FILE_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_overwrite_file);

public final static String SP_SUGGEST_BUY_PAID_VERSION_DO_NOT_SHOW_BOOL = "SUGGEST_BUY_PAID_VERSION_DO_NOT_SHOW";
public final static String SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT = "SUGGEST_BUY_PAID_VERSION_COUNTER";
public final static String SP_OWNED_NO_ADS_INT = "OWNED_NO_ADS";
public final static String SP_SUGGEST_APP_RATING_ALREADY_DONE_BOOL = "SUGGEST_APP_RATING_ALREADY_DONE";

}
