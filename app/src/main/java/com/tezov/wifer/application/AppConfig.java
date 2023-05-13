package com.tezov.wifer.application;

import static com.tezov.lib_java.application.AppConfig.getInt;

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
import java.util.concurrent.TimeUnit;

import application.AppConfig_bt;

public abstract class AppConfig extends AppConfig_bt{
public final static long ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms = 250;
public final static int FILE_ID_LENGTH = 4;

public final static int AD_SUGGEST_PAID_VERSION_MODULO = getInt(AppConfigKey.AD_SUGGEST_PAID_VERSION_MODULO.getId());
public final static Boolean ADMOB_TRIAL_TIME_OVER = com.tezov.lib_java.application.AppConfig.getBoolean(AppConfigKey.ADMOB_TRIAL_TIME_OVER.getId());
public final static long ADMOB_TRIAL_TIME_BANNER_ms = TimeUnit.MILLISECONDS.convert(com.tezov.lib_java.application.AppConfig.getLong(
        AppConfigKey.ADMOB_TRIAL_TIME_BANNER_days.getId()), TimeUnit.DAYS);
public final static long ADMOB_TRIAL_TIME_INTERSTITIAL_ms = TimeUnit.MILLISECONDS.convert(com.tezov.lib_java.application.AppConfig.getLong(AppConfigKey.ADMOB_TRIAL_TIME_INTERSTITIAL_days.getId()), TimeUnit.DAYS);
public final static long ADMOB_INTERSTITIAL_DELAY_START_ms = TimeUnit.MILLISECONDS.convert(com.tezov.lib_java.application.AppConfig.getLong(AppConfigKey.ADMOB_INTERSTITIAL_DELAY_START_s.getId()), TimeUnit.SECONDS);
public final static long ADMOB_INTERSTITIAL_DELAY_CYCLIC_ms = TimeUnit.MILLISECONDS.convert(com.tezov.lib_java.application.AppConfig.getLong(AppConfigKey.ADMOB_INTERSTITIAL_DELAY_CYCLIC_mn.getId()), TimeUnit.MINUTES);



}
