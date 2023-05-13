package com.tezov.wifer.activity.activityFilter;

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
import static com.tezov.wifer.activity.activityFilter.ActivityFilterDispatcher.EXTRA_REQUEST;
import static com.tezov.wifer.activity.activityFilter.ActivityFilterDispatcher.EXTRA_REQUEST_SEND;
import static com.tezov.wifer.activity.activityFilter.ActivityFilterDispatcher.EXTRA_TARGET_FRAGMENT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.util.UtilsIntent;
import com.tezov.wifer.fragment.FragmentServer;

public class ActivityActionSend extends AppCompatActivity{
@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    onNewAction();
}

private void onNewAction(){
    Intent intent = getIntent();
    ListOrObject<UriW> uris = UtilsIntent.getUris(intent, false);
    if(uris.size() > 1){
        Log.d(DebugLog.TAG, "multiple uri ignored");
    }
    UriW uri = uris.get();
    UtilsIntent.retainUriPermission(intent, uri);
    intent.setFlags(0);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    intent.putExtra(EXTRA_REQUEST, EXTRA_REQUEST_SEND);
    intent.putExtra(EXTRA_TARGET_FRAGMENT, FragmentServer.class.getSimpleName());
    ActivityFilterDispatcher.startActivityFrom(this, true);
}

}
