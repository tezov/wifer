package com.tezov.wifer.activity;

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

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tezov.wifer.application.ApplicationSystem;

public class ActivityLauncher extends AppCompatActivity{
@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    ApplicationSystem application = (ApplicationSystem)getApplication();
    if(!application.isClosing(this)){
        application.startMainActivity(this, false, false);
    }
}

}