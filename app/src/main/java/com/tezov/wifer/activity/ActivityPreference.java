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
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java_android.file.StorageTree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.tezov.wifer.application.SharePreferenceKey.FILE_NAME_SHARE_PREFERENCE;
import static com.tezov.wifer.application.SharePreferenceKey.SP_DESTINATION_DIRECTORY_STRING;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java_android.playStore.PlayStore;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.wrapperAnonymous.PreferenceOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.toolbar.Toolbar;
import com.tezov.lib_java_android.ui.toolbar.ToolbarBottom;
import com.tezov.wifer.R;
import com.tezov.wifer.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.wifer.application.AppInfo;
import com.tezov.wifer.application.Application;
import com.tezov.wifer.dialog.DialogSuggestAppRating;
import com.tezov.wifer.dialog.DialogSuggestBuyNoAds;
import com.tezov.wifer.navigation.ToolbarContent;
import com.tezov.wifer.navigation.ToolbarHeaderBuilder;

public class ActivityPreference extends com.tezov.lib_java_android.ui.activity.ActivityPreference{
private ToolbarContent toolbarContent = null;

@Override
protected int getLayoutId(){
    return R.layout.activity_preference;
}
@Override
protected int getPreferenceContainerId(){
    return R.id.container_fragment;
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
    AppDisplay.setOrientationPortrait(true);
    toolbarContent = new ToolbarContent(this);
    AppInfo.privacyPolicySetOnClickListener(this.getViewRoot(), null);
    AppInfo.contactSetOnClickListener(this.getViewRoot());
//    AppInfo.downloadComputerApplicationSetOnClickListener(this.getViewRoot());
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


@Override
protected FragmentPreference createFragmentPreference(){
    return new FragmentPreference(R.xml.preference);
}

public static class FragmentPreference extends com.tezov.lib_java_android.ui.activity.ActivityPreference.FragmentSharePreference{
    public FragmentPreference(){
    }
    FragmentPreference(int xmlId){
        super(xmlId);
    }
    @Override
    public String getSharedPreferencesName(){
        return FILE_NAME_SHARE_PREFERENCE;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = super.onCreateView(inflater, container, savedInstanceState);
        findPreference(R.id.pref_app_version).setSummary(AppContext.getResources().getString(R.string.application_version) + "/" + Build.VERSION.SDK_INT);
        findPreference(R.id.pref_app_share).setOnPreferenceClickListener(new PreferenceOnClickListenerW(){
            @Override
            public boolean onClicked(Preference preference){
                setOnClickListenerEnabled(preference, false);
                shareApp();
                return true;
            }
        });
        Preference preferenceAppRating = findPreference(R.id.pref_app_rating);
        if(!DialogSuggestAppRating.isAlreadyDone()){
            preferenceAppRating.setVisible(true);
            preferenceAppRating.setOnPreferenceClickListener(new PreferenceOnClickListenerW(){
                @Override
                public boolean onClicked(Preference preference){
                    setOnClickListenerEnabled(preference, false);
                    openDialogSuggestReview();
                    return true;
                }
            });
        }
        updatePref_DestinationFolder(sp, findPreference(R.id.pref_destination_directory));
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        updatePrefSkuNoAds(Application.isOwnedNoAds());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key, String keyDecoded){
        super.onSharedPreferenceChanged(sp, key, keyDecoded);
        if(SP_DESTINATION_DIRECTORY_STRING.equals(keyDecoded)){
            updatePref_DestinationFolder(sp, findPreference(key));
        }
    }
    private void updatePref_DestinationFolder(SharedPreferences sp, Preference pref){
        String value = sp.getString(SP_DESTINATION_DIRECTORY_STRING);
        if(value != null){
            StorageTree uriTree = StorageTree.fromLink(value);
            if(uriTree != null){
                if(uriTree.canWrite()){
                    value = uriTree.getDisplayPath();
                } else {
                    sp.remove(SP_DESTINATION_DIRECTORY_STRING);
                    value = null;
                }
            }
        }
        String summary;
        if(value == null){
            summary = AppContext.getResources().getString(R.string.pref_destination_directory_android_summary);
        } else {
            summary = AppContext.getResources().getString(R.string.pref_destination_directory_summary);
        }
        pref.setSummary(String.format(summary, value));
    }
    private void updatePrefSkuNoAds(boolean isOwnedNoAds){
        com.tezov.lib_java_android.application.SharedPreferences sp = Application.sharedPreferences();
        String keySkuNoAds = sp.encodeKey(AppContext.getResources().getIdentifierName(R.id.pref_sku_no_ads));
        Preference prefSkuNoAds = findPreference(keySkuNoAds);
        if(isOwnedNoAds){
            prefSkuNoAds.setTitle(R.string.pref_sku_no_ads_owned_title);
            prefSkuNoAds.setSummary(R.string.pref_sku_no_ads_owned_summary);
            prefSkuNoAds.setIcon(R.drawable.ic_confirm_outline_24dp);
            prefSkuNoAds.setEnabled(false);
        } else {
            prefSkuNoAds.setTitle(R.string.pref_sku_no_ads_buy_full_version_title);
            prefSkuNoAds.setSummary(R.string.pref_sku_no_ads_buy_full_version_summary);
            prefSkuNoAds.setIcon(R.drawable.ic_buy_24dp);
            prefSkuNoAds.setEnabled(true);
            prefSkuNoAds.setOnPreferenceClickListener(new PreferenceOnClickListenerW(){
                @Override
                public boolean onClicked(Preference preference){
                    setOnClickListenerEnabled(preference, false);
                    showSuggestBuyNoAds();
                    return true;
                }
            });
        }
    }
    private void showSuggestBuyNoAds(){
        DialogSuggestBuyNoAds.open(true).observe(new ObserverValueE<Boolean>(this){
            @Override
            public void onComplete(Boolean isOwned){
                if(isOwned){
                    PostToHandler.of(getView(), new RunnableW(){
                        @Override
                        public void runSafe(){
                            updatePrefSkuNoAds(true);
                        }
                    });
                }
                setOnClickListenerEnabled(R.id.pref_sku_no_ads, true);
            }
            @Override
            public void onException(Boolean isOwned, Throwable e){
                setOnClickListenerEnabled(R.id.pref_sku_no_ads, true);
                //DebugException.start().log((e)).end();

            }
        });
    }
    private void openDialogSuggestReview(){
        DialogSuggestAppRating.open().observe(new ObserverValueE<DialogSuggestAppRating>(this){
            @Override
            public void onComplete(DialogSuggestAppRating dialog){
                dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
                    @Override
                    public void onComplete(Event.Is is, Object object){
                        Preference preferenceAppRating = findPreference(R.id.pref_app_rating);
                        if(DialogSuggestAppRating.isAlreadyDone()){
                            preferenceAppRating.setVisible(false);
                        } else {
                            setOnClickListenerEnabled(preferenceAppRating, true);
                        }
                    }
                });
            }
            @Override
            public void onException(DialogSuggestAppRating dialog, Throwable e){
                setOnClickListenerEnabled(R.id.pref_app_rating, true);
            }
        });
    }
    private void shareApp(){
        PlayStore.shareLink().observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                setOnClickListenerEnabled(R.id.pref_app_share, true);
            }
            @Override
            public void onException(Throwable e){
                setOnClickListenerEnabled(R.id.pref_app_share, true);
            }
        });
    }

}

}
