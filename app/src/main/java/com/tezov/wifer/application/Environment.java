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

import static com.tezov.wifer.application.SharePreferenceKey.SP_DESTINATION_DIRECTORY_STRING;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.StorageTree;
import com.tezov.lib_java_android.file.UriW;

public class Environment{
//USER
public final static String DIRECTORY_ROOT = AppContext.getApplicationName() + Directory.PATH_SEPARATOR;

//URI
public static UriW obtainPendingUri(String fileFullName){
    return obtainUri(fileFullName, new uriSupplier(){
        @Override
        public UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName){
            return uriTree.obtainUri(directory, fileFullName);
        }
        @Override
        public UriW obtainUriFromStorageMedia(String directory, String fileFullName){
            return StorageMedia.obtainPendingUri(directory, fileFullName);
        }
    });
}
public static UriW obtainUniquePendingUri(String fileFullName){
    return obtainUri(fileFullName, new uriSupplier(){
        @Override
        public UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName){
            return uriTree.obtainUniqueUri(directory, fileFullName);
        }
        @Override
        public UriW obtainUriFromStorageMedia(String directory, String fileFullName){
            return StorageMedia.obtainUniquePendingUri(directory, fileFullName);
        }
    });
}
public static UriW obtainClosestPendingUri(String fileFullName){
    return obtainUri(fileFullName, new uriSupplier(){
        @Override
        public UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName){
            return uriTree.obtainClosestUri(directory, fileFullName);
        }
        @Override
        public UriW obtainUriFromStorageMedia(String directory, String fileFullName){
            return StorageMedia.obtainClosestPendingUri(directory, fileFullName);
        }
    });
}
private static UriW obtainUri(String fileFullName, uriSupplier supplier){
    String directoryPath = DIRECTORY_ROOT;
    UriW uri = null;
    SharedPreferences sp = Application.sharedPreferences();
    String destinationFolder = sp.getString(SP_DESTINATION_DIRECTORY_STRING);
    if(destinationFolder != null){
        StorageTree uriTree = StorageTree.fromLink(destinationFolder);
        if(uriTree != null){
            if(uriTree.canWrite()){
                uri = supplier.obtainUriFromUriTree(uriTree, directoryPath, fileFullName);
            }
            else{
                sp.remove(SP_DESTINATION_DIRECTORY_STRING);
            }
        }
    }
    if(uri == null){
        directoryPath = StorageMedia.findBestDirectoryForFile(fileFullName) + Directory.PATH_SEPARATOR + directoryPath;
        uri = supplier.obtainUriFromStorageMedia(directoryPath, fileFullName);
    }
    return uri;
}
private interface uriSupplier{
    UriW obtainUriFromUriTree(StorageTree uriTree, String directory, String fileFullName);
    UriW obtainUriFromStorageMedia(String directory, String fileFullName);
}


}
