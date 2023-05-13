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

import com.tezov.lib_java.socket.prebuild.datagram.DatagramRequest;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugString;

public class DatagramServerRequest extends DatagramRequest{
public enum Type{
    STATUS,
}
private Type type = null;

@Override
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM_SERVER_REQUEST;
}

@Override
public DatagramServerRequest init(){
    super.init();
    type = null;
    return this;
}

public Type getRequest(){
    return type;
}
public DatagramServerRequest setRequest(Type type){
    this.type = type;
    return this;
}

private String getRequestName(){
    return type != null ? type.name() : null;
}

@Override
protected int getLength(){
    return super.getLength() + ByteBuffer.STRING_SIZE(getRequestName());
}
@Override
protected ByteBuffer toByteBuffer(){
    ByteBuffer buffer = super.toByteBuffer();
    buffer.put(getRequestName());
    return buffer;
}
@Override
public boolean fromByteBuffer(ByteBuffer byteBuffer){
    if(super.fromByteBuffer(byteBuffer)){
        String name = byteBuffer.getString();
        if(name == null){
            type = null;
        } else {
            type = Type.valueOf(name);
        }
        return true;
    } else {
        return false;
    }
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("type", type);
    return data;
}

}
