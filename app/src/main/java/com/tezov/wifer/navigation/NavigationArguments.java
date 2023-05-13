package com.tezov.wifer.navigation;

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
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.type.collection.Arguments;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationManager;

import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.ui.navigation.defNavigable;

import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.CREATE;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.GET;
import static com.tezov.lib_java_android.ui.navigation.NavigationArguments.How.OBTAIN;

public class NavigationArguments extends com.tezov.lib_java_android.ui.navigation.NavigationArguments{

protected NavigationArguments(defNavigable ref, Arguments<com.tezov.lib_java_android.ui.navigation.NavigationArguments.ArgumentKey.Is> arguments){
    super(ref, arguments);
}

public static NavigationArguments create(){
    DestinationManager destinationManager = Application.navigationHelper().getDestinationManager();
    return new NavigationArguments(null, destinationManager.arguments(null, CREATE));
}
public static NavigationArguments obtain(defNavigable ref){
    DestinationManager destinationManager = Application.navigationHelper().getDestinationManager();
    return new NavigationArguments(ref, destinationManager.arguments(ref, OBTAIN));
}
public static NavigationArguments get(defNavigable ref){
    DestinationManager destinationManager = Application.navigationHelper().getDestinationManager();
    return new NavigationArguments(ref, destinationManager.arguments(ref, GET));
}
public static NavigationArguments wrap(Arguments<NavigationArguments.ArgumentKey.Is> arguments){
    return new NavigationArguments(null, arguments);
}

public NavigationArguments setTarget(Class<? extends defNavigable> target){
    this.put(ArgumentKey.TARGET, target);
    return this;
}
public boolean isTargetMe(Class<? extends defNavigable> target){
    return exist() && get(ArgumentKey.TARGET) == target;
}
public boolean isTargetDialog(){
    return exist() && Reflection.isInstanceOf(getTarget(), DialogNavigable.class);
}
public Class<? extends defNavigable> getTarget(){
    return this.get(ArgumentKey.TARGET);
}

public ListOrObject<UriW> getUris(){
    return this.get(ArgumentKey.URIS);
}
public NavigationArguments setUris(ListOrObject<UriW> uris){
    this.put(ArgumentKey.URIS, uris);
    return this;
}

public interface ArgumentKey extends com.tezov.lib_java_android.ui.navigation.NavigationArguments.ArgumentKey{
    Is URIS = new Is("URIS");
    Is TARGET = new Is("TARGET");

}

}
