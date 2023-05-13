package com.tezov.wifer.activity;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java_android.ui.layout.FrameFlipperLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.activity.ActivityToolbar;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.toolbar.Toolbar;
import com.tezov.lib_java_android.ui.toolbar.ToolbarBottom;
import com.tezov.wifer.R;
import com.tezov.wifer.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.lib_java_android.ads.adMax.AdMaxInterstitialCyclicLoadwState;
import com.tezov.wifer.application.Application;
import com.tezov.wifer.dialog.DialogSuggestBuyNoAds;
import com.tezov.wifer.navigation.NavigationHelper;
import com.tezov.wifer.navigation.ToolbarContent;

import static com.tezov.lib_java_android.application.Application.sharedPreferences;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.DestinationKey;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;
import static com.tezov.wifer.application.AppConfig.ADMOB_INTERSTITIAL_DELAY_CYCLIC_ms;
import static com.tezov.wifer.application.AppConfig.ADMOB_INTERSTITIAL_DELAY_START_ms;
import static com.tezov.wifer.application.Application.SKU_NO_ADS;
import static com.tezov.wifer.application.SharePreferenceKey.SP_NAVIGATION_LAST_DESTINATION_STRING;
import static com.tezov.wifer.navigation.NavigationHelper.DestinationKey.CLIENT;
import static com.tezov.wifer.navigation.NavigationHelper.DestinationKey.INFO;
import static com.tezov.wifer.navigation.NavigationHelper.DestinationKey.PREFERENCE;
import static com.tezov.wifer.navigation.NavigationHelper.DestinationKey.SERVER;

import com.tezov.lib_java_android.ads.adMax.AdMaxBanner;
import com.tezov.lib_java_android.ads.adMax.AdMaxInstance;

public abstract class ActivityMain extends ActivityToolbar{
private ToolbarContent toolbarContent = null;
private ConnectivityManager.QueryHelper queryConnection = null;

@Override
protected State newState(){
    return new State();
}
@Override
public State getState(){
    return super.getState();
}
@Override
public State obtainState(){
    return super.obtainState();
}

@Override
protected int getLayoutId(){
    return R.layout.activity_main;
}

public ToolbarContent getToolbarContent(){
    return toolbarContent;
}

@Override
protected void onNewIntent(Intent sourceIntent){
    super.onNewIntent(sourceIntent);
    ActivityFilterDispatcher.onNewIntent(this);
}

@Override
protected void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    AppDisplay.setOrientationPortrait(true);
    toolbarContent = new ToolbarContent(this);
    queryConnection = new ConnectivityManager.QueryHelper(Handler.MAIN()){
        @Override
        public void onConnected(ConnectivityManager.State state){
            getState().resumeAdmob();
        }
        @Override
        public void onDisConnected(ConnectivityManager.State state){
            getState().pauseAdmob();
        }
    }.addGoogleSocketTest();
    ConnectivityManager connectivity = Application.connectivityManager();
    connectivity.addQuery(queryConnection);
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    boolean adMobPending = false;
    if(!hasBeenReconstructed){
        DestinationKey.Is source = Navigate.getSource(this);
        if(source == null){
            if(!ActivityFilterDispatcher.onPrepare(this)){
                NavigationHelper.DestinationKey.Is destination = null;
                SharedPreferences sp = sharedPreferences();
                String destinationString = sp.getString(SP_NAVIGATION_LAST_DESTINATION_STRING);
                if(destinationString != null){
                    destination = NavigationHelper.DestinationKey.find(destinationString);
                }
                if(destination == null){
                    destination = INFO;
                }
                Navigate.observe(new ObserverEvent<NavigatorManager.NavigatorKey.Is, NavigatorManager.Event>(this, FRAGMENT){
                    int id;
                    @Override
                    public void onComplete(NavigatorManager.NavigatorKey.Is navigator, NavigatorManager.Event event){
                        if(event == NavigatorManager.Event.ON_NAVIGATE_TO){
                            unsubscribe();
                            getToolbarBottom().setChecked(id);
                        }
                    }
                    ObserverEvent<NavigatorManager.NavigatorKey.Is, NavigatorManager.Event> init(int id){
                        this.id = id;
                        return this;
                    }

                }.init(destination.getId()));
                Navigate.To(destination);
            }
            adMobPending = true;
            mustShowSuggestBuy().observe(new ObserverValue<Boolean>(this){
                @Override
                public void onComplete(Boolean mustShow){
                    if(mustShow){
                        showSuggestBuy();
                    } else {
                        showAdMob();
                    }
                }
            });
        }
    }
    if(!adMobPending){
        showAdMob();
    }
}

private void showAdMob(){
    if(queryConnection != null){
        if(Application.isOwnedNoAds()){
            queryConnection.enable(false);
            obtainState().destroyAdmob();
        } else {
            obtainState().createAdmob();
            queryConnection.enable(true);
        }
    }
}
private TaskValue<Boolean>.Observable mustShowSuggestBuy(){
    if(Application.isOwnedNoAds()){
        return TaskValue.Complete(false);
    }
    TaskValue<Boolean> task = new TaskValue<>();
    DialogSuggestBuyNoAds.isOwned(SKU_NO_ADS).observe(new ObserverValueE<Boolean>(this){
        @Override
        public void onComplete(Boolean isOwned){
            task.notifyComplete(!isOwned && DialogSuggestBuyNoAds.isTrialTimeInterstitialOver() && DialogSuggestBuyNoAds.canShow());
        }
        @Override
        public void onException(Boolean isOwned, Throwable e){
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
            task.notifyComplete(false);
        }
    });
    return task.getObservable();
}
private void showSuggestBuy(){
    DialogSuggestBuyNoAds.open(false).observe(new ObserverValueE<Boolean>(this){
        @Override
        public void onComplete(Boolean bought){
            showAdMob();
        }
        @Override
        public void onException(Boolean isOwned, Throwable e){

//DebugException.start().log((e)).end();

            onComplete(false);
        }
    });
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    if(hasBeenRestarted && !Application.isOwnedNoAds()){
        queryConnection.enable(true);
    }
}
@Override
protected void onPause(){
    super.onPause();
    queryConnection.enable(false);
}

@Override
protected boolean onCreateMenu(){
    Toolbar toolbar = getToolbar();
    toolbar.setVisibility(View.VISIBLE);
    toolbar.inflateMenu(R.menu.toolbar);
    ToolbarBottom toolbarBottom = getToolbarBottom();
    toolbarBottom.setVisibility(View.VISIBLE);
    toolbarBottom.inflateMenu(R.menu.toolbar_bottom);
    toolbarBottom.getBehavior().enableScrollDrag(false);
    return true;
}

@Override
public boolean onMenuItemSelected(Type uiType, Object object){
    if(uiType == Type.TOOLBAR){
        MenuItem menuItem = (MenuItem)object;
        if(menuItem.getItemId() == R.id.mn_setting){
            Navigate.To(PREFERENCE);
        }
        return true;
    }
    if(uiType == Type.TOOLBAR_BOTTOM){
        SharedPreferences sp = sharedPreferences();
        MenuItem menuItem = (MenuItem)object;
        if(menuItem.getItemId() == R.id.mn_server){
            sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, SERVER.name());
            Navigate.To(SERVER);
        } else if(menuItem.getItemId() == R.id.mn_client){
            sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, CLIENT.name());
            Navigate.To(CLIENT);
        } else if(menuItem.getItemId() == R.id.mn_info){
            sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, INFO.name());
            Navigate.To(INFO);
        }
        return true;
    }
    return false;
}

@Override
protected void onDestroy(){
    if(queryConnection != null){
        queryConnection.remove();
        queryConnection = null;
    }
    super.onDestroy();
}

@Override
public void removedFromStack(){
    if(hasState()){
        getState().destroyAdmob();
    }
    super.removedFromStack();
}

public TaskState.Observable showInterstitial(){
    if(hasState()){
        return getState().showInterstitial();
    } else {
        return TaskState.Exception("not available");
    }
}
public void setBannerVisible(boolean flag){
    if(hasState()){
        getState().setBannerVisible(flag);
    }
}
public static class State extends ActivityToolbar.State{
    private AdMaxBanner banner = null;
    private AdMaxInterstitialCyclicLoadwState interstitial = null;
    public ActivityMain getActivity(){
        return getOwner();
    }
    @Override
    public void attach(Object owner){
        super.attach(owner);
        attach((ActivityMain)owner);
    }
    private void attach(ActivityMain activity){

    }
    private TaskState.Observable showInterstitial(){
        if((interstitial != null) && interstitial.isReadyToBeShown()){
            return interstitial.show();
        } else {
            return TaskState.Exception("not available");
        }
    }
    private void createAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob == null){
            return;
        }
        admob.post(new RunnableW(){
            @Override
            public void runSafe(){
                if(DialogSuggestBuyNoAds.isTrialTimeBannerOver() && (banner == null)){
                    FrameFlipperLayout banner_container = getActivity().findViewById(R.id.ad_view_banner_container);
                    View bannerDefaultView = banner_container.findViewById(R.id.ad_view_banner_default);
                    bannerDefaultView.setOnClickListener(new ViewOnClickListenerW(){
                        @Override
                        public void onClicked(View v){
                            if(ConnectivityManager.isConnected()){
                                DialogSuggestBuyNoAds.open(true).observe(new ObserverValueE<Boolean>(this){
                                    @Override
                                    public void onComplete(Boolean isOwned){
                                        if(isOwned){
                                            getActivity().queryConnection.enable(false);
                                            destroyAdmob();
                                        }
                                    }
                                    @Override
                                    public void onException(Boolean isOwned, Throwable e){
//                                        DebugException.pop().produce(e).log().pop();
                                    }
                                });
                            }
                        }
                    });
                    PostToHandler.of(banner_container, new RunnableW(){
                        @Override
                        public void runSafe(){
                            banner_container.setVisibility(View.VISIBLE);
                        }
                    });
                    banner = new AdMaxBanner(AppContext.getResources().getString(R.string.ad_banner_id));
                }
                if(DialogSuggestBuyNoAds.isTrialTimeInterstitialOver() && (interstitial == null)){
                    interstitial = new AdMaxInterstitialCyclicLoadwState(AppContext.getResources().getString(R.string.ad_interstitial_id), ADMOB_INTERSTITIAL_DELAY_CYCLIC_ms).mute(true);
                }
            }
        });
    }
    private void destroyAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob != null){
            admob.clearPendings();
        }
        if(interstitial != null){
            interstitial.destroy();
            interstitial = null;
        }
        if(banner != null){
            banner.destroy();
            banner = null;
            View view = getActivity().findViewById(R.id.ad_view_banner_container);
            PostToHandler.of(view, new RunnableW(){
                @Override
                public void runSafe(){
                    view.setVisibility(View.GONE);
                }
            });
        }
    }
    private void resumeAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob == null){
            return;
        }
        admob.post(new RunnableW(){
            @Override
            public void runSafe(){
                if(interstitial != null){
                    if(!interstitial.isStarted()){
                        interstitial.start(ADMOB_INTERSTITIAL_DELAY_START_ms);
                    } else {
                        interstitial.resume();
                    }
                }
                if(banner != null){
                    banner.setContainer(getActivity().findViewById(R.id.ad_view_banner_container)).resume().observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){

                        }
                        @Override
                        public void onException(Throwable e){
/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/
                        }
                    });
                }
            }
        });
    }
    private void pauseAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob != null){
            admob.clearPendings();
        }
        admob.clearPendings();
        if(interstitial != null){
            interstitial.pause();
        }
        if(banner != null){
            banner.pause().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){

                }
                @Override
                public void onException(Throwable e){

/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/

                }
            });
        }
    }
    private void setBannerVisible(boolean flag){
        if(banner != null){
            getActivity().findViewById(R.id.ad_view_banner_container).setVisibility(flag ? View.VISIBLE : View.GONE);
        }
    }

}

}
