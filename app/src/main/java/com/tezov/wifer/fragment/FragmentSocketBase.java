package com.tezov.wifer.fragment;

import static com.tezov.wifer.application.AppConfig.ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.runnable.RunnableW;
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.ConnectivityHotSpotManager;
import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java.socket.UtilsSocket;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.wifer.R;
import com.tezov.wifer.misc.DatagramRegister;

import org.threeten.bp.LocalDate;

import java.net.InetAddress;

import fragment.FragmentBase_bt;

public abstract class FragmentSocketBase extends FragmentBase_bt{
protected final static long START_STOP_DELAY_ms = 400;
static{
    DatagramRegister.init();
}
private TextView lblConnectionType = null;

public static int getRandomPort(){
    return UtilsSocket.randomPortDynamic();
}

@Override
protected State newState(){
    return new State();
}

@Override
public State getState(){
    return (State)super.getState();
}

@Override
public State obtainState(){
    return (State)super.obtainState();
}

@Override
public Param getParam(){
    return (Param)super.getParam();
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    lblConnectionType = view.findViewById(R.id.lbl_connection_type);
    ConnectivityState.init();
    return view;
}

final public void restoreButtons(){
    restoreButtons(false);
}
final public void restoreButtons(boolean withDelay){
    PostToHandler.of(getView(), withDelay ? ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms : 0, new RunnableW(){
        @Override
        public void runSafe(){
            getState().stateView.restore();
        }
    });
}

final public boolean disableButtons(){
    StateView stateView = getState().stateView;
    if(stateView.lock()){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                stateView.clear();
                onDisabledButtons(stateView);
                stateView.unlock();
            }
        });
        return true;
    } else {
        return false;
    }
}
protected void onDisabledButtons(StateView stateView){

}

final public void enableButtons(){
    enableButtons(false);
}
final public void enableButtons(boolean withDelay){
    PostToHandler.of(getView(), withDelay ? ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms : 0, new RunnableW(){
        @Override
        public void runSafe(){
            onEnabledButtons();
        }
    });
}
protected void onEnabledButtons(){

}

@Override
public void onConnected(ConnectivityState state){
    super.onConnected(state);
    lblConnectionType.setText(state.text());
}
@Override
public void onDisConnected(){
    super.onDisConnected();
    lblConnectionType.setText(ConnectivityState.NO_NETWORK.text());

}

public InetAddress getAddressLocal(){
    InetAddress inetAddress = ConnectivityHotSpotManager.getIPv4Address();
    if(inetAddress == null){
        inetAddress = ConnectivityManager.getIPv4Address();
    }
    return inetAddress;
}

protected static class State extends FragmentBase_bt.State{
    protected StateView stateView;
    public State(){
        stateView = new StateView();
    }
    @Override
    protected Param newParam(){
        return new Param();
    }
}
public static class Param extends FragmentBase_bt.Param{


}

}
