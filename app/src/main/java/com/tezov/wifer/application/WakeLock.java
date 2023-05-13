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

import static android.content.Context.POWER_SERVICE;

import android.os.PowerManager;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;

import java.util.concurrent.TimeUnit;

public class WakeLock{
final private static String WAVE_LOCK_TAG = "wifer:alarm";
final private static long WAVE_LOCK_TIMEOUT = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
final private static long WAVE_LOCK_TIMEOUT_OFFSET_ms = 100;
private static RunnableTimeOut runnableTimeOut = null;

private static Class<WakeLock> myClass(){
    return WakeLock.class;
}
public enum Type{
    CLIENT, SERVER
}
public static void acquire(Integer size, Type type){
    if(runnableTimeOut == null){
        //TODO determine Max normal time according file size
        runnableTimeOut = new RunnableTimeOut(myClass(), WAVE_LOCK_TIMEOUT-WAVE_LOCK_TIMEOUT_OFFSET_ms){
            private PowerManager.WakeLock wl = null;
            @Override
            public void onTimeOut(){
                onComplete();
                start();
            }
            @Override
            public void onStart(){
                PowerManager pm = AppContext.getSystemService(POWER_SERVICE);
                wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAVE_LOCK_TAG);
                wl.acquire(WAVE_LOCK_TIMEOUT);
            }
            @Override
            public void onComplete(){
                wl.release();
                wl = null;
            }
        };
        runnableTimeOut.start();
    }
}
public static void release(){
    if(runnableTimeOut != null){
        runnableTimeOut.completed();
        runnableTimeOut = null;
    }
}
}

