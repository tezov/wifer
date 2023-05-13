package com.tezov.wifer.fragment;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.ConnectivityHotSpotManager;
import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppResources;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.ui.fragment.FragmentMenu;
import com.tezov.wifer.R;
import com.tezov.wifer.activity.ActivityMain;
import com.tezov.wifer.application.Application;
import com.tezov.wifer.navigation.ToolbarContent;
import com.tezov.wifer.navigation.ToolbarHeaderBuilder;

import java.util.List;

public abstract class FragmentBase extends FragmentMenu{
private ConnectivityManager.QueryHelper queryConnection = null;
private ConnectivityHotSpotManager.Query queryHotSpotConnection = null;
private ConnectivityState networkState = ConnectivityState.NO_NETWORK;

private FragmentBase me(){
    return this;
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
public Param getParam(){
    return super.getParam();
}

protected int getToolbarBottomMenuResourceId(){
    return AppResources.NULL_ID;
}

protected <DATA> void setToolbarTittle(DATA data){
    getParam().setTitleData(data);
    ActivityMain activity = (ActivityMain)getActivity();
    ToolbarContent toolbarContent = activity.getToolbarContent();
    if(data == null){
        toolbarContent.setToolBarView(null);
    }
    else {
        ToolbarHeaderBuilder header = new ToolbarHeaderBuilder().setData(data);
        toolbarContent.setToolBarView(header.build(activity.getToolbar()));
    }
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    ConnectivityManager connectivity = Application.connectivityManager();
    ConnectivityHotSpotManager connectivityHotSpot = Application.connectivityHotSpotManager();
    queryConnection = new ConnectivityManager.QueryHelper(Handler.MAIN()){
        @Override
        public void onConnected(ConnectivityManager.State state){
            if(!connectivityHotSpot.isActive()){
                me().onConnected(ConnectivityState.find(connectivity.getState()));
            }
        }
        @Override
        public void onDisConnected(ConnectivityManager.State state){
            if(!connectivityHotSpot.isActive()){
                me().onDisConnected();
            }
        }
    };
    connectivity.addQuery(queryConnection);
    queryHotSpotConnection = new ConnectivityHotSpotManager.Query(Handler.MAIN()){
        @Override
        public void onActive(){
            me().onConnected(ConnectivityState.HOTSPOT);
        }
        @Override
        public void onInactive(){
            ConnectivityState state = ConnectivityState.find(connectivity.getState());
            if(state == ConnectivityState.NO_NETWORK){
                me().onDisConnected();
            } else {
                me().onConnected(state);
            }
        }
    };
    connectivityHotSpot.addQuery(queryHotSpotConnection);
    return view;
}
@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    if(!hasState()){
        obtainParam();
    }
    queryConnection.enable(true);
    queryHotSpotConnection.enable(true);
    ConnectivityManager connectivity = Application.connectivityManager();
    ConnectivityHotSpotManager connectivityHotSpot = Application.connectivityHotSpotManager();
    if(!connectivityHotSpot.isActive() && (ConnectivityState.find(connectivity.getState()) == ConnectivityState.NO_NETWORK)){
        onDisConnected();
    }
}
public boolean hasActiveNetwork(){
    return networkState != ConnectivityState.NO_NETWORK;
}
public ConnectivityState getNetworkState(){
    return networkState;
}

public void onConnected(ConnectivityState state){
    networkState = state;
}
public void onDisConnected(){
    networkState = ConnectivityState.NO_NETWORK;
}

@Override
public void onDestroy(){
    if(queryConnection != null){
        queryConnection.enable(false);
        queryConnection.remove();
        queryConnection = null;
    }
    if(queryHotSpotConnection != null){
        queryHotSpotConnection.enable(false);
        queryHotSpotConnection.remove();
        queryHotSpotConnection = null;
    }
    super.onDestroy();
}

public enum ConnectivityState{
    NO_NETWORK, WIFI, DATA, HOTSPOT, UNKNOWN;
    String text = name(); //DEFAULT VALUES IF NO XML ATTRIBUTES
    static void init(){
        List<String> texts = AppContext.getResources().getStrings(R.array.cny_state);
        if(texts == null){
            return;
        }
        ConnectivityState[] messages = values();

        if(texts.size() != messages.length){
/*#-debug-> DebugException.start().log(("texts size " + texts.size() + " and enum size " + messages.length + " mismatch")).end(); <-debug-#*/
        }

        for(int end = messages.length, i = 0; i < end; i++){
            messages[i].text = texts.get(i);
        }
    }
    public static ConnectivityState find(ConnectivityManager.State state){
        switch(state){
            case DISCONNECTED:
            case AIR_PLANE:
                return NO_NETWORK;
            case TYPE_WIFI:
                return WIFI;
            case TYPE_DATA:
                return DATA;
            default:
                return UNKNOWN;
        }
    }
    public String text(){
        return text;
    }
}

public abstract static class State extends FragmentMenu.State{
    @Override
    public Param getParam(){
        return (Param)super.getParam();
    }
}
public abstract static class Param extends FragmentMenu.Param{
    public Object titleData = null;
    public <DATA> DATA getTitleData(){
        return (DATA)titleData;
    }
    public <DATA> Param setTitleData(DATA data){
        this.titleData = data;
        return this;
    }
}

}
