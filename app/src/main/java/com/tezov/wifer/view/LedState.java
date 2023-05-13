package com.tezov.wifer.view;

import com.tezov.lib_java.debug.DebugLog;
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

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.wifer.R;

public class LedState extends AppCompatButton{
final static private int STATE_EXTRA_SPACE = R.styleable.LedState.length;
private State state = State.NEUTRAL;

public LedState(android.content.Context context){
    super(context);
    init(context, null, 0);
}

public LedState(android.content.Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0);
}
public LedState(android.content.Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}
private LedState me(){
    return this;
}
private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr){
/*#-debug-> DebugTrack.start().create(this).end(); <-debug-#*/
}
@Override
protected void finalize() throws Throwable{
/*#-debug-> DebugTrack.start().destroy(this).end(); <-debug-#*/
    super.finalize();
}
@Override
protected int[] onCreateDrawableState(int extraSpace){
    int[] drawableState = super.onCreateDrawableState(extraSpace + STATE_EXTRA_SPACE);
    if(state != null){
        drawableState = mergeDrawableStates(drawableState, new int[]{state.styleValue});
    }
    return drawableState;
}
public void setState(State state){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            me().state = state;
            refreshDrawableState();
        }
    });
}
public State getState(){
    return state;
}
public enum State{
    NEUTRAL(R.attr.state_neutral), BUSY(R.attr.state_busy), SUCCEED(R.attr.state_succeed), FAILED(R.attr.state_failed);
    int styleValue;
    State(int value){
        this.styleValue = value;
    }
}

}
