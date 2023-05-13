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
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.tezov.wifer.R;
import com.tezov.wifer.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.wifer.navigation.ToolbarContent;
import com.tezov.wifer.navigation.ToolbarHeaderBuilder;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.ui.activity.ActivityToolbar;
import com.tezov.lib_java_android.ui.component.plain.WebViewHtmlResource;
import com.tezov.lib_java_android.ui.toolbar.Toolbar;
import com.tezov.lib_java_android.ui.toolbar.ToolbarBottom;


public class ActivityPrivacyPolicy extends ActivityToolbar{
private ToolbarContent toolbarContent = null;

@Override
protected int getLayoutId(){
    return R.layout.tpl_activity_tbc_tbb;
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
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    AppDisplay.setOrientationUser(true);
    toolbarContent = new ToolbarContent(this);
    WebViewHtmlResource webView = new WebViewHtmlResource(this);
    webView.setRawFileId(R.raw.privacy_policy);
    webView.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    FrameLayout frame = findViewById(R.id.container_fragment);
    frame.addView(webView);
    webView.loadData();
}
@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    setToolbarTittle(R.string.activity_privacy_policy_title);
}
protected <DATA> void setToolbarTittle(DATA data){
    ToolbarContent toolbarContent = getToolbarContent();
    if(data == null){
        toolbarContent.setToolBarView(null);
    } else {
        ToolbarHeaderBuilder header = new ToolbarHeaderBuilder().setData(data);
        toolbarContent.setToolBarView(header.build(getToolbar()));
    }
}
@Override
protected boolean onCreateMenu(){
    Toolbar toolbar = getToolbar();
    toolbar.setVisibility(View.VISIBLE);
    ToolbarBottom toolbarBottom = getToolbarBottom();
    toolbarBottom.setVisibility(View.GONE);
    return true;
}

}
