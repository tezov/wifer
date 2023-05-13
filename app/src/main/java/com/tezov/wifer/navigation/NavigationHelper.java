package com.tezov.wifer.navigation;

import com.tezov.lib_java.debug.DebugLog;
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

import static com.tezov.lib_java_android.ui.misc.TransitionManager.Name.FADE;
import static com.tezov.lib_java_android.ui.misc.TransitionManagerAnimation.Name.SLIDE_OVER_LEFT;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.ACTIVITY;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.DIALOG;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationManager;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorActivity;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorDialog;
import com.tezov.lib_java_android.ui.navigation.navigator.NavigatorFragment;
import com.tezov.wifer.R;
import com.tezov.wifer.activity.ActivityPreference;
import com.tezov.wifer.activity.ActivityPrivacyPolicy;
import com.tezov.wifer.fragment.FragmentClient;
import com.tezov.wifer.fragment.FragmentInfo;
import com.tezov.wifer.fragment.FragmentServer;

import activity.ActivityMain_bt;

public class NavigationHelper extends com.tezov.lib_java_android.ui.navigation.NavigationHelper{

public NavigationHelper(){
    init();
}

private void init(){
    DestinationManager destinationManager = getDestinationManager();

    NavigatorDialog navigatorDialog = new NavigatorDialog(DIALOG);
    addNavigator(navigatorDialog);

    NavigationOption option = new NavigationOption().setKeepInStack(false);
    NavigatorFragment navigatorFragment = new NavigatorFragment(FRAGMENT, R.id.container_fragment).doNotPopOutTheLastFragment(true);
    addNavigator(navigatorFragment);
    //MENU
    destinationManager.addDestination(null, ActivityMain_bt.class, FragmentInfo.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentServer.class, FragmentInfo.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentClient.class, FragmentInfo.class).setTransition(FADE).setOption(option);
    //SERVER
    destinationManager.addDestination(null, ActivityMain_bt.class, FragmentServer.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentInfo.class, FragmentServer.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentClient.class, FragmentServer.class).setTransition(FADE).setOption(option);
    //CLIENT
    destinationManager.addDestination(null, ActivityMain_bt.class, FragmentClient.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentInfo.class, FragmentClient.class).setTransition(FADE).setOption(option);
    destinationManager.addDestination(null, FragmentServer.class, FragmentClient.class).setTransition(FADE).setOption(option);

    NavigatorActivity navigatorActivity = new NavigatorActivity(ACTIVITY);
    //ACTIVITY ENTRY POINT
    addNavigator(navigatorActivity);
    // MAIN
    destinationManager.addDestination(null, null, ActivityMain_bt.class);
    // POLICY PRIVACY
    destinationManager.addDestination(null, null, ActivityPrivacyPolicy.class).setTransition(SLIDE_OVER_LEFT);
    // PREFERENCE
    destinationManager.addDestination(null, null, ActivityPreference.class).setTransition(SLIDE_OVER_LEFT);
}

@Override
final public NavigatorKey.Is getNavigatorKey(Class<? extends defNavigable> type){
    NavigatorKey.Is navigatorKey = super.getNavigatorKey(type);

    if(navigatorKey == null){
/*#-debug-> DebugException.start().explode(("this object does not have navigator key declared " + DebugTrack.getFullName(type))).end(); <-debug-#*/
    }

    return navigatorKey;
}

@Override
public NavigatorKey.Is getNavigatorKey(NavigatorManager.DestinationKey.Is destination){
    if(destination == DestinationKey.MAIN){
        return ACTIVITY;
    }
    if(destination == DestinationKey.PRIVACY_POLICY){
        return ACTIVITY;
    }
    if(destination == DestinationKey.PREFERENCE){
        return ACTIVITY;
    }

    if(destination == DestinationKey.INFO){
        return FRAGMENT;
    }
    if(destination == DestinationKey.CLIENT){
        return FRAGMENT;
    }
    if(destination == DestinationKey.SERVER){
        return FRAGMENT;
    }

/*#-debug-> DebugException.start().explode(("destination is not declared " + DebugTrack.getFullName(destination))).end(); <-debug-#*/

    return null;
}

@Override
public <I extends NavigatorManager.DestinationKey.Is> I identify(Class<? extends defNavigable> type){
    if(type == ActivityMain_bt.class){
        return (I)DestinationKey.MAIN;
    }
    if(type == ActivityPrivacyPolicy.class){
        return (I)DestinationKey.PRIVACY_POLICY;
    }
    if(type == ActivityPreference.class){
        return (I)DestinationKey.PREFERENCE;
    }

    if(type == FragmentInfo.class){
        return (I)DestinationKey.INFO;
    }
    if(type == FragmentServer.class){
        return (I)DestinationKey.SERVER;
    }
    if(type == FragmentClient.class){
        return (I)DestinationKey.CLIENT;
    }
/*#-debug-> DebugException.start().explode(("type is not declared " + DebugTrack.getFullName(type))).end(); <-debug-#*/
    return null;
}

public interface DestinationKey extends NavigatorManager.DestinationKey{
    NavigatorManager.DestinationKey.Is MAIN = new NavigatorManager.DestinationKey.Is("MAIN");
    NavigatorManager.DestinationKey.Is PRIVACY_POLICY = new NavigatorManager.DestinationKey.Is("PRIVACY_POLICY");
    NavigatorManager.DestinationKey.Is PREFERENCE = new NavigatorManager.DestinationKey.Is("PREFERENCE");
    Is INFO = new Is("INFO", R.id.mn_info);
    Is CLIENT = new Is("CLIENT", R.id.mn_client);
    Is SERVER = new Is("SERVER", R.id.mn_server);

    static Is find(String name){
        return Is.findTypeOf(Is.class, name);
    }

    class Is extends NavigatorManager.DestinationKey.Is{
        private int id;
        public Is(String name, int id){
            super(name);
            this.id = id;
        }
        public int getId(){
            return id;
        }

    }

}

}
