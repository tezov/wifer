package com.tezov.wifer.activity.activityFilter;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.CLOSED;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.NOT_INITIALISED;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.PAUSED;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.RESTARTING;
import static com.tezov.lib_java_android.application.ApplicationSystem.State.STARTED;

import android.app.Activity;
import android.content.Intent;

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.util.UtilsIntent;
import com.tezov.wifer.activity.ActivityMain;
import com.tezov.wifer.application.ApplicationSystem;
import com.tezov.wifer.fragment.FragmentServer;
import com.tezov.wifer.navigation.NavigationArguments;
import com.tezov.wifer.navigation.NavigationHelper;

public class ActivityFilterDispatcher{
public final static String EXTRA_REQUEST = "EXTRA_REQUEST";
public final static String EXTRA_REQUEST_SEND = "EXTRA_REQUEST_SEND";
public final static String EXTRA_TARGET_FRAGMENT = "EXTRA_REQUEST_FRAGMENT";

private static Class<ActivityFilterDispatcher> myClass(){
    return ActivityFilterDispatcher.class;
}

private static boolean hasExtraRequest(Intent intent){
    return (intent != null) && intent.hasExtra(EXTRA_REQUEST);
}

public static void startActivityFrom(Activity activity, boolean redirectIntent){
    boolean isDone = false;
    ApplicationSystem application = (ApplicationSystem)activity.getApplication();
/*#-debug-> DebugLog.start().track(myClass(), redirectIntent ? "intent redirected / " + application.getState() : "intent not redirected / " + application.getState()).end(); <-debug-#*/
    if(application.getState() == NOT_INITIALISED){
        isDone = true;
        application.startMainActivity(activity, redirectIntent, true).observe(new ObserverValueE<>(myClass()){
            @Override
            public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                if(state != STARTED){
/*#-debug-> DebugException.start().log("invalid state " + state).end(); <-debug-#*/
                    System.exit(0);
                }
            }
            @Override
            public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
                System.exit(0);
            }
        });
    } else if(application.getState() == STARTED){
        isDone = true;
        application.reachCurrentActivity(activity, redirectIntent).observe(new ObserverValueE<>(myClass()){
            @Override
            public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                if(state != STARTED){
/*#-debug-> DebugException.start().log("invalid state " + state).end(); <-debug-#*/
                    System.exit(0);
                }
            }
            @Override
            public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
                System.exit(0);
            }
        });
    } else if(application.getState() == PAUSED){
        isDone = true;
        application.wakeUpMainActivity(activity).observe(new ObserverValueE<>(myClass()){
            @Override
            public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                if(state == STARTED){
                    ActivityFilterDispatcher.onNewIntent(AppContext.getActivity());
                } else {
/*#-debug-> DebugException.start().log("invalid state " + state).end(); <-debug-#*/
                    System.exit(0);
                }
            }
            @Override
            public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
                System.exit(0);
            }
        });
    } else if(application.getState() == RESTARTING){
        TaskValue<com.tezov.lib_java_android.application.ApplicationSystem.State>.Observable task = application.getTaskObservable();
        if(task != null){
            isDone = true;
            task.observe(new ObserverValueE<>(myClass()){
                @Override
                public void onComplete(com.tezov.lib_java_android.application.ApplicationSystem.State state){
                    if(state == STARTED){
                        ActivityFilterDispatcher.onNewIntent(AppContext.getActivity());
                    } else {
/*#-debug-> DebugException.start().log("invalid state " + state).end(); <-debug-#*/
                        System.exit(0);
                    }
                }
                @Override
                public void onException(com.tezov.lib_java_android.application.ApplicationSystem.State state, Throwable e){
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
                    System.exit(0);
                }
            });
        }
    }
    if(!isDone){
/*#-debug-> DebugException.start().log("application state " + application.getState() + " not handled").end(); <-debug-#*/
        activity.finish();
    }

}
public static void onNewIntent(android.app.Activity activity){
    if(activity instanceof ActivityMain){
        dispatchToFragment((ActivityMain)activity);
    } else {
        Intent intent = activity.getIntent();
        if(hasExtraRequest(intent)){
            Navigate.Back(new NavigationOption().setRedirectIntent(true));
        }
    }
}
public static boolean onPrepare(android.app.Activity activity){
    if(activity instanceof ActivityMain){
        return dispatchToFragment((ActivityMain)activity);
    } else {
        return false;
    }
}
private static boolean dispatchToFragment(ActivityMain activity){
    Intent intent = activity.getIntent();
    if(!hasExtraRequest(intent)){
        return false;
    }
    NavigationArguments arguments = NavigationArguments.create();
    String extraRequest = intent.getStringExtra(EXTRA_REQUEST);
    if(EXTRA_REQUEST_SEND.equals(extraRequest)){
        arguments.setUris(UtilsIntent.getUris(intent, false));
    } else {
        return false;
    }
    Class<? extends FragmentNavigable> targetFragment;
    String nameFragment = intent.getStringExtra(EXTRA_TARGET_FRAGMENT);
    if((nameFragment != null) && nameFragment.equals(FragmentServer.class.getSimpleName())){
        targetFragment = FragmentServer.class;
        arguments.setTarget(targetFragment);
    } else {
        return false;
    }
    Navigate.CloseDialogAll(new NavigationOption().setPostCancel_NavBack(true)).observe(new ObserverState(activity){
        @Override
        public void onComplete(){
            FragmentNavigable currentRef = Navigate.getCurrentFragmentRef();
            if(currentRef != null){
                Class<? extends FragmentNavigable> current = currentRef.getClass();
                if(current == targetFragment){
                    currentRef.onNewNavigationArguments(arguments);
                    return;
                }
            }
            NavigationHelper.DestinationKey.Is target = Navigate.identify(targetFragment);
            activity.getToolbarBottom().setChecked(target.getId());
            Navigate.To(target, arguments);
        }
    });
    return true;
}

}

