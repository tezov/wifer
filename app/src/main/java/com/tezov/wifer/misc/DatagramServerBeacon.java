package com.tezov.wifer.misc;

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

import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramBeacon;
import com.tezov.lib_java.debug.DebugString;

public class DatagramServerBeacon extends DatagramBeacon{
private Integer portServerFile = null;

@Override
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM_SERVER_BEACON;
}

@Override
public DatagramServerBeacon init(){
    super.init();
    portServerFile = null;
    return this;
}

public Integer getPortServerFile(){
    return portServerFile;
}
public DatagramServerBeacon setPortServerFile(Integer portServerFile){
    this.portServerFile = portServerFile;
    return this;
}

@Override
public boolean isIpv4Valid(){
    return super.isIpv4Valid() && isPortValid(portServerFile);
}

@Override
protected int getLength(){
    return super.getLength() + ByteBuffer.INT_SIZE();
}
@Override
protected ByteBuffer toByteBuffer(){
    ByteBuffer buffer = super.toByteBuffer();
    buffer.put(portServerFile);
    return buffer;
}
@Override
public boolean fromByteBuffer(ByteBuffer byteBuffer){
    if(super.fromByteBuffer(byteBuffer)){
        portServerFile = byteBuffer.getInt();
        return true;
    } else {
        return false;
    }
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("portServerFile", portServerFile);
    return data;
}

}
