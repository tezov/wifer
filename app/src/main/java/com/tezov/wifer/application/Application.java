package com.tezov.wifer.application;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation;

import android.content.Context;
import android.content.Intent;

import com.tezov.lib_java_android.application.AppInfo;
import com.tezov.lib_java_android.application.ApplicationSystem;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java_android.application.ConnectivityHotSpotManager;
import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.wifer.R;
import com.tezov.wifer.navigation.NavigationHelper;

import java.util.List;

import static com.tezov.lib_java_android.application.SharePreferenceKey.makeKey;
import static com.tezov.wifer.application.SharePreferenceKey.SP_NAVIGATION_LAST_DESTINATION_STRING;
import static com.tezov.wifer.application.SharePreferenceKey.SP_OVERWRITE_FILE_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_OWNED_NO_ADS_INT;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_SEARCH_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_SELECT_FILE_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_START_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_STOP_BOOL;
import static com.tezov.wifer.navigation.NavigationHelper.DestinationKey.INFO;

import com.tezov.lib_java_android.ads.adMax.AdMaxInstance;

public class Application extends com.tezov.lib_java_android.application.Application{
public final static String SKU_NO_ADS = com.tezov.lib_java_android.application.AppContext.getResources().getString(R.string.billing_sku_no_ads);

protected static ConnectivityHotSpotManager connectivityHotSpotManager = null;
protected static AdMaxInstance adMob = null;

public static void onMainActivityStart(ApplicationSystem app, Intent source, boolean isRestarted){
    com.tezov.lib_java_android.application.Application.onMainActivityStart(app, source, isRestarted);
    if(!isRestarted){
        Context context = app.getApplicationContext();
        if(state == null){
            state = new State();
        }
        state.onMainActivityStart(app, source);
        transitionManager = new TransitionManagerAnimation();
        navigationHelper = new NavigationHelper();
        sharedPreferences(AppConfig.newSharedPreferencesEncrypted());
        if(AppInfo.isFirstLaunch()){
            setDefaultSharePreference();
        }
        connectivityManager = new ConnectivityManager();
        connectivityHotSpotManager = new ConnectivityHotSpotManager();
        adMob = new AdMaxInstance().onMainActivityStart(context);
    }
}
public static void onApplicationPause(ApplicationSystem app){
    com.tezov.lib_java_android.application.Application.onApplicationPause(app);
}
public static void onApplicationClose(ApplicationSystem app){
    if(adMob != null){
        adMob.clearPendings();
        adMob = null;
    }
    if(connectivityManager != null){
        connectivityManager.unregisterReceiver(true);
        connectivityManager = null;
    }
    if(connectivityHotSpotManager != null){
        connectivityHotSpotManager.unregisterReceiver(true);
        connectivityHotSpotManager = null;
    }
    sharedPreferences = null;
    navigationHelper = null;
    transitionManager = null;
    if(state != null){
        state.onApplicationClose(app);
    }
    com.tezov.lib_java_android.application.Application.onApplicationClose(app);
}

private static void setDefaultSharePreference(){
    SharedPreferences sp = sharedPreferences();
    sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, INFO.name());
    sp.put(SP_OVERWRITE_FILE_BOOL, false);
    sp.put(SP_SERVER_AUTO_SELECT_FILE_BOOL, false);
    sp.put(SP_SERVER_AUTO_START_BOOL, false);
    sp.put(SP_SERVER_AUTO_STOP_BOOL, false);
    sp.put(SP_SERVER_AUTO_SEARCH_BOOL, false);
    sp.put(SP_OVERWRITE_FILE_BOOL, false);
}

public static ConnectivityHotSpotManager connectivityHotSpotManager(){
    return connectivityHotSpotManager;
}
public static AdMaxInstance adMob(){
    return adMob;
}
public static boolean isOwnedNoAds(){
    SharedPreferences sp = Application.sharedPreferences();
    return Compare.isTrue(sp.getBoolean(makeKey(SP_OWNED_NO_ADS_INT, getState().sessionUid().toHexString())));
}
public static void setOwnedNoAds(boolean flag){
    SharedPreferences sp = Application.sharedPreferences();
    List<String> previous = sp.findKeyStartWith(SP_OWNED_NO_ADS_INT);
    if(previous != null){
        for(String key: previous){
            sp.remove(key);
        }
    }
    sp.put(makeKey(SP_OWNED_NO_ADS_INT, getState().sessionUid().toHexString()), flag);
}

}
