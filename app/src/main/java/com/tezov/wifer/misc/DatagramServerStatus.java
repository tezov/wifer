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

import com.tezov.lib_java.socket.prebuild.datagram.DatagramAnswer;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugString;

public class DatagramServerStatus extends DatagramAnswer{
public enum Type{
    INITIALIZING, READY, FILE_TO_SEND, BUSY,
}
private Type type = null;
private String fileId = null;

@Override
public DatagramRegister.Is myType(){
    return DatagramRegister.DATAGRAM_SERVER_STATUS;
}

@Override
public DatagramServerStatus init(){
    super.init();
    type = null;
    fileId = null;
    return this;
}

public Type getStatus(){
    return type;
}
public DatagramServerStatus setStatus(Type type){
    this.type = type;
    return this;
}
private String getStatusName(){
    return type != null ? type.name() : null;
}

public String getFileId(){
    return fileId;
}
public DatagramServerStatus setFileId(String statusId){
    this.fileId = statusId;
    return this;
}
@Override
protected int getLength(){
    return super.getLength() + ByteBuffer.STRING_SIZE(getStatusName()) + ByteBuffer.STRING_SIZE(fileId);
}
@Override
protected ByteBuffer toByteBuffer(){
    ByteBuffer buffer = super.toByteBuffer();
    buffer.put(getStatusName());
    buffer.put(fileId);
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
        fileId = byteBuffer.getString();
        return true;
    } else {
        return false;
    }
}

@Override
public DebugString toDebugString(){
    DebugString data = super.toDebugString();
    data.append("type", type);
    data.append("fileId", fileId);
    return data;
}

}
