package com.tezov.wifer.application;

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
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.util.UtilsIntent;
import com.tezov.wifer.R;

import static com.tezov.wifer.navigation.NavigationHelper.DestinationKey.PRIVACY_POLICY;

public final class AppInfo extends com.tezov.lib_java_android.application.AppInfo{

public static void privacyPolicySetOnClickListener(View view, Boolean keepInStack){
    TextView lblPrivacyPolicy = view.findViewById(R.id.lbl_privacy_policy);
    lblPrivacyPolicy.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            NavigationOption option = null;
            if(keepInStack != null){
                option = new NavigationOption().setKeepInStack_NavTo(keepInStack);
            }
            Navigate.To(PRIVACY_POLICY, option);
        }
    });
}
public static void contactSetOnClickListener(View view){
    TextView lblContact = view.findViewById(R.id.lbl_contact);
    lblContact.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            String target = AppContext.getResources().getString(R.string.app_email);
            String subject = "Contact from " + AppContext.getResources().getString(R.string.app_name);
            subject += "_" + AppContext.getResources().getString(R.string.application_version) + "/" + Build.VERSION.SDK_INT;
            UtilsIntent.emailTo(target, subject, null);
        }
    });
}

public static TaskState.Observable open(UriW uri){
    if(uri == null){
        return TaskState.Exception("uri out is null");
    } else {
        return uri.open();
    }
}

//public static void downloadComputerApplicationSetOnClickListener(View view){
//    TextView lblApplicationPC = view.findViewById(R.id.lbl_application_pc);
//    lblApplicationPC.setOnClickListener(new ViewOnClickListenerW(){
//        @Override
//        public void onClicked(View v){
//            UtilsIntent.openLink("http://www.tezov.com/wifer/application_pc/");
//        }
//    });
//}


}
