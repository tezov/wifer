package com.tezov.wifer.fragment;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.task.TaskState;

import static com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction.IconAction.ACTION;
import static com.tezov.wifer.application.SharePreferenceKey.SP_OVERWRITE_FILE_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_SEARCH_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_START_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_STOP_BOOL;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_PORT_STRING;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.data.validator.ValidatorIpv4;
import com.tezov.lib_java.data.validator.ValidatorPortDynamic;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.file.UtilsFile;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.socket.TcpServer;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramAnswer;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramBeacon;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramRequest;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.component.plain.TextViewScrollable;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.view.ProgressBarTransfer;
import com.tezov.lib_java_android.util.UtilsTextWatcher;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.wifer.R;
import com.tezov.wifer.activity.ActivityMain;
import com.tezov.wifer.application.AppInfo;
import com.tezov.wifer.application.Environment;
import com.tezov.wifer.application.WakeLock;
import com.tezov.wifer.misc.DatagramServerBeacon;
import com.tezov.wifer.misc.DatagramServerRequest;
import com.tezov.wifer.misc.DatagramServerStatus;
import com.tezov.wifer.view.LedState;

import java.net.InetAddress;

public class FragmentClient extends FragmentSocketBase{
private final static int DELAY_REQUEST_STATUS_ms = 5000;
private final static int DELAY_REQUEST_STATUS_RANDOM_ms = 1000;

private FormEditText frmServerAddress = null;
private FormEditText frmServerPort = null;

private TextViewScrollable lblFileName = null;
private TextViewScrollable lblFolderName = null;

private TextView lblSepFile = null;
private ProgressBarTransfer progressBarTransfer = null;

private ButtonMultiIconMaterial btnServerSearchStart = null;

private LedState stepConnectionType = null;
private LedState stepServerSearch = null;
private LedState stepTransferResult = null;

private ButtonIconMaterial btnOpenFile = null;

@Override
protected State newState(){
    return new State();
}
@Override
public State getState(){
    return (State)super.getState();
}
@Override
public State obtainState(){
    return (State)super.obtainState();
}
@Override
public Param getParam(){
    return (Param)super.getParam();
}

@Override
protected int getLayoutId(){
    return R.layout.fragment_client;
}
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    progressBarTransfer = new ProgressBarTransfer(view.findViewById(R.id.container_bar_progress));
    progressBarTransfer.setSeparator(AppContext.getResources().getString(R.string.transfer_sep));
    progressBarTransfer.setMax(Long.parseLong(AppContext.getResources().getString(R.string.transfer_max)));
    progressBarTransfer.setUnit(AppContext.getResources().getString(R.string.transfer_unit));

    frmServerAddress = view.findViewById(R.id.frm_server_address);
    frmServerAddress.setValidator(new ValidatorIpv4());
    frmServerAddress.addTextChangedListener(new UtilsTextWatcher.IPv4());

    frmServerPort = view.findViewById(R.id.frm_server_port);
    frmServerPort.setValidator(new ValidatorPortDynamic());
    frmServerPort.addTextChangedListener(new UtilsTextWatcher.PortDynamic());
    frmServerPort.link(new FormEditText.EntryString(){
        @Override
        public boolean setValue(String value){
            if(value == null){
                command(ACTION);
            } else {
                SharedPreferences sp = Application.sharedPreferences();
                sp.put(SP_SERVER_PORT_STRING, value);
            }
            return true;
        }
        @Override
        public String getValue(){
            SharedPreferences sp = Application.sharedPreferences();
            String value = sp.getString(SP_SERVER_PORT_STRING);
            if(value == null){
                value = String.valueOf(getRandomPort());
                sp.put(SP_SERVER_PORT_STRING, value);
            }
            return value;
        }
        @Override
        public boolean command(Object o){
            if(o == ACTION){
                frmServerPort.setText(String.valueOf(getRandomPort()));
                return true;
            }
            else {
                return false;
            }
        }
    });

    lblFileName = view.findViewById(R.id.lbl_file_name);
    lblFolderName = view.findViewById(R.id.lbl_folder_name);
    lblSepFile = view.findViewById(R.id.lbl_sep_file);

    btnServerSearchStart = view.findViewById(R.id.btn_server_search_start);
    btnServerSearchStart.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                if(btnServerSearchStart.getIndex() == 0){
                    serverSearchStart();
                } else {
                    serverSearchAbort();
                }
            }
        }
    });

    stepConnectionType = view.findViewById(R.id.stp_connection_type);
    stepServerSearch = view.findViewById(R.id.stp_server_search);
    stepTransferResult = view.findViewById(R.id.stp_transfer_result);

    btnOpenFile = view.findViewById(R.id.btn_open_file);
    btnOpenFile.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                open();
            }
        }
    });

    return view;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    setStep(Step.IDLE);
}

public void setUri(UriW uri){
    if(uri != null){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                String fileFullName = uri.getFullName();
                lblFileName.setText(fileFullName);
                lblFileName.moveToEnd();
                Pair<String, String> p = UtilsFile.splitToPathAndFileName(uri.getDisplayPath());
                if(p != null){
                    lblFolderName.setText(p.first);
                }
            }
        });

    }
    else {
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                lblFileName.setText(null);
                lblFolderName.setText(null);
            }
        });
    }
    getState().setFileUri(uri);
}

@Override
public void onConnected(FragmentBase.ConnectivityState state){
    super.onConnected(state);
    if(disableButtons()){
        RunnableGroup gr = new RunnableGroup(this).name("onConnected");
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                SharedPreferences sp = Application.sharedPreferences();
                boolean mustRestartServer = getState().messenger.isStarted() || Compare.isTrue(sp.getBoolean(SP_SERVER_AUTO_SEARCH_BOOL));
                putValue(mustRestartServer);
                if(getState().messenger.isStarted()){
                    serverSearchAbort().observe(new ObserverState(this){
                        @Override
                        public void onComplete(){
                            next();
                        }
                    });
                }
                else{
                    next();
                }
            }
        }.name("server stop"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                if(state == ConnectivityState.WIFI){
                    stepConnectionType.setState(LedState.State.SUCCEED);
                }
                else if(state == ConnectivityState.DATA){
                    stepConnectionType.setState(LedState.State.FAILED);
                    putValue(false);
                }
                else if(state == ConnectivityState.HOTSPOT){
                    stepConnectionType.setState(LedState.State.SUCCEED);
                }
                else if(state == ConnectivityState.UNKNOWN){
                    stepConnectionType.setState(LedState.State.NEUTRAL);
                }
                else {
/*#-debug-> DebugException.start().unknown("state", state).end(); <-debug-#*/
                }
                next();
            }
        }.name("update state"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                boolean mustRestartServer = getValue();
                if(mustRestartServer && (btnServerSearchStart.getIndex() == 0) && canSearchServer()){
                    PostToHandler.of(getView(), new RunnableW(){
                        @Override
                        public void runSafe(){
                            btnServerSearchStart.performClick();
                        }
                    });
                }
                else {
                    enableButtons();
                }
                next();
            }
        }.name("restart"));
        gr.start();
    }
}
@Override
public void onDisConnected(){
    super.onDisConnected();
    if(getView() != null){
        stepConnectionType.setState(LedState.State.FAILED);
    }
    if(getState().messenger.isStarted()){
        serverSearchAbort();
    }
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    setToolbarTittle(R.string.frg_client_title);
}

@Override
protected void onDisabledButtons(StateView stateView){
    stateView.clear()
            .enableNot(btnOpenFile)
            .enableNot(frmServerPort)
            .clickableIconNot(frmServerPort)
            .enableNot(btnServerSearchStart);
}
@Override
protected void onEnabledButtons(){
    btnOpenFile.setEnabled(getState().fileUri != null);
    frmServerPort.setEnabled(true);
    frmServerPort.setClickableIcon(true);
    btnServerSearchStart.setEnabled(getNetworkState() != ConnectivityState.DATA);
}

@Override
public void onDestroy(){
    if(getState().messenger.isStarted()){
        serverSearchAbort();
    }
    super.onDestroy();
}
private void setStep(Step step){
    View view = getView();
    if(view != null){
        PostToHandler.of(view, new RunnableW(){
            @Override
            public void runSafe(){
                Step previousStep = getState().currentStep;
                getState().currentStep = step;
                switch(step){
                    case IDLE:
                        stepIdle(previousStep);
                        break;

                    case SEARCH_START:
                        stepSearchStart(previousStep);
                        break;
                    case SEARCH_STARTED:
                        stepSearchStarted(previousStep);
                        break;
                    case SEARCH_SUCCEED:
                        stepSearchSucceed(previousStep);
                        break;
                    case SEARCH_FAILED:
                        stepSearchFailed(previousStep);
                        break;

                    case RUNNING_EXCEPTION:
                        stepRunningException(previousStep);
                        break;

                    case SEARCH_ABORT:
                        stepSearchAbort(previousStep);
                        break;
                    case SEARCH_ABORTED:
                        stepSearchAborted(previousStep);
                        break;

                    case TRANSFER_FILE_STARTED:
                        stepTransferStarted(previousStep);
                        break;
                    case TRANSFER_FILE_SUCCEED:
                        stepTransferSucceed(previousStep);
                        break;

                    case TRANSFER_FILE_FAILED:
                        stepTransferFailed(previousStep);
                        break;

                    case TRANSFER_FILE_ABORTED:
                        stepTransferAborted(previousStep);
                        break;
                }
            }
        });
    }
}
private void stepIdle(Step previousStep){
    stepServerSearch.setState(LedState.State.NEUTRAL);
    frmServerAddress.setText(null);
    btnServerSearchStart.setIndex(0);
    setUri(null);
    stepTransferResult.setState(LedState.State.NEUTRAL);
    lblSepFile.setText(R.string.sep_file);
    progressBarTransfer.setTitle(R.string.txt_result_progress_not_started);
    progressBarTransfer.setCurrent(0);
    enableButtons();
}
private void stepSearchStart(Step previousStep){
    if(previousStep == Step.SEARCH_START){
        return;
    }
    stepServerSearch.setState(LedState.State.BUSY);
    frmServerPort.setEnabled(false);
    frmServerPort.setClickableIcon(false);
    frmServerAddress.setText(null);
}
private void stepSearchStarted(Step previousStep){
    if(previousStep == Step.SEARCH_STARTED){
        return;
    }
    btnServerSearchStart.setIndex(1);

    if(getState().stateView.isLocked()){
/*#-debug-> DebugException.start().log("state view is already locked..").end(); <-debug-#*/
    }
    getState().stateView.lock();
    PostToHandler.of(getView(), START_STOP_DELAY_ms, new RunnableW(){
        @Override
        public void runSafe(){
            btnServerSearchStart.setEnabled(true);
            getState().stateView.unlock();
        }
    });
}
private void stepSearchSucceed(Step previousStep){
    if(previousStep == Step.SEARCH_SUCCEED){
        return;
    }
    stepServerSearch.setState(LedState.State.SUCCEED);
    frmServerAddress.setText(getState().messenger.getAddressRemote().getHostAddress());
    frmServerPort.setText(String.valueOf(getState().messenger.getPortRemote()));
}
private void stepSearchFailed(Step previousStep){
    if(previousStep == Step.SEARCH_FAILED){
        return;
    }
    stepServerSearch.setState(LedState.State.FAILED);
    btnServerSearchStart.setIndex(0);
    enableButtons();
}
private void stepRunningException(Step previousStep){
    setStep(Step.IDLE);
}
private void stepSearchAbort(Step previousStep){
    if(previousStep == Step.SEARCH_ABORT){
        return;
    }
    stepServerSearch.setState(LedState.State.BUSY);
}
private void stepSearchAborted(Step previousStep){
    if(previousStep == Step.SEARCH_ABORTED){
        return;
    }
    stepServerSearch.setState(LedState.State.NEUTRAL);
    btnServerSearchStart.setIndex(0);
    enableButtons();
}

public void request(DatagramServerRequest.Type type){
    DatagramServerRequest request = new DatagramServerRequest().init();
    request.setRequest(type);
    getState().messenger.sendRequest(request);
}
public void processAnswerStatus(DatagramServerStatus answer){
    DatagramServerStatus.Type type = answer.getStatus();
    if((type == DatagramServerStatus.Type.FILE_TO_SEND) && !Compare.equals(getState().fileId, answer.getFileId())){
        receiveFileStart(answer.getFileId());
    }
}
public void processRequestFailed(Throwable e, DatagramRequest request){
    if(request instanceof DatagramServerRequest){
        DatagramServerRequest requestServer = (DatagramServerRequest)request;
        switch(requestServer.getRequest()){
            case STATUS:{
                getState().messenger.requestStatusStop();
                if(canSearchServer()){
                    getState().messenger.startBeacon(null, getState().messenger.getPortLocal());
                }
            }
            break;
        }
    } else {
/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/
    }
}

private void stepTransferStarted(Step previousStep){
    if(previousStep == Step.TRANSFER_FILE_STARTED){
        return;
    }
    stepTransferResult.setState(LedState.State.BUSY);
    lblSepFile.setText(R.string.sep_file_receiving);
    progressBarTransfer.setTitle(R.string.txt_result_progressing);
    progressBarTransfer.setCurrent(0);
    setUri(null);
    btnOpenFile.setEnabled(false);

}
private void stepTransferAborted(Step previousStep){
    if(previousStep == Step.TRANSFER_FILE_ABORTED){
        return;
    }
    stepTransferResult.setState(LedState.State.FAILED);
    lblSepFile.setText(R.string.sep_file);
    progressBarTransfer.setTitle(R.string.txt_result_progress_aborted);
}
private void stepTransferFailed(Step previousStep){
    if(previousStep == Step.TRANSFER_FILE_FAILED){
        return;
    }
    stepTransferResult.setState(LedState.State.FAILED);
    lblSepFile.setText(R.string.sep_file);
    progressBarTransfer.setTitle(R.string.txt_result_progress_failed);
}
private void stepTransferSucceed(Step previousStep){
    if(previousStep == Step.TRANSFER_FILE_SUCCEED){
        return;
    }
    ActivityMain activityMain = (ActivityMain)getActivity();
    activityMain.showInterstitial().observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            stepTransferResult.setState(LedState.State.SUCCEED);
            lblSepFile.setText(R.string.sep_file_received);
            progressBarTransfer.setTitle(R.string.txt_result_progress_done);
            btnOpenFile.setEnabled(true);
        }
        @Override
        public void onException(Throwable e){
            onComplete();
        }
    });
}

private boolean canSearchServer(){
    FocusCemetery.request();
    return frmServerPort.isValid();
}
private void serverSearchStart(){
    if(!canSearchServer()){
        frmServerPort.showError();
        restoreButtons();
        return;
    }
    setStep(Step.SEARCH_START);
    int port = Integer.parseInt(frmServerPort.getValue());
    getState().messenger.start(getAddressLocal(), port, true).observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            getState().messenger.startBeacon(null, port);
            setStep(Step.SEARCH_STARTED);
        }
        @Override
        public void onException(Throwable e){
/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/
            setStep(Step.SEARCH_FAILED);
        }
    });
}
private TaskState.Observable serverSearchAbort(){
    setStep(Step.SEARCH_ABORT);
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("serverSearchAbort");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            getState().messenger.stop().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    }.name("stop messenger"));
    if(getState().client.isConnected()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                getState().client.disconnect().observe(new ObserverState(this){
                    @Override
                    public void onComplete(){
                        if(getState().fileUri != null){
                            getState().fileUri.delete();
                            getState().fileUri = null;
                        }
                        setStep(Step.TRANSFER_FILE_ABORTED);
                        next();
                    }
                });
            }
        }.name("disconnect client"));
    }
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            setStep(Step.SEARCH_ABORTED);
            task.notifyComplete();
        }
    });
    gr.start();
    return task.getObservable();
}

private boolean canReceiveFile(){
    FocusCemetery.request();
    return frmServerPort.isValid() && frmServerAddress.isValid();
}
private void receiveFileStart(String fileId){
    getState().messenger.requestStatusStop();
    setStep(Step.TRANSFER_FILE_STARTED);
    RunnableGroup gr = new RunnableGroup(this).name("onTransferStart");
    int LABEL_DISCONNECT = gr.label();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(canReceiveFile()){
                next();
            }
            else {
                PostToHandler.of(getView(), new RunnableW(){
                    @Override
                    public void runSafe(){
                        if(!frmServerAddress.isValid()){
                            frmServerAddress.showError();
                        }
                        if(!frmServerPort.isValid()){
                            frmServerPort.showError();
                        }
                    }
                });
                putException("can not receive files");
                done();
            }
        }
    }.name("can receive file?"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            getState().fileId = fileId;
            String address = frmServerAddress.getValue();
            int port = getState().messenger.getPortServerFile();
            getState().client.connect(address, port).observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("connect client"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(StorageMedia.PERMISSION_CHECK_WRITE()){
                next();
            } else {
                StorageMedia.PERMISSION_REQUEST_WRITE(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        skipUntilLabel(LABEL_DISCONNECT);
                    }
                });
            }
        }
    }.name("check permission write"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            setStep(Step.TRANSFER_FILE_STARTED);
            TcpServer.SocketClient socket = getState().client.getSocket();
            socket.post(new RunnableW(){
                @Override
                public void runSafe(){
                    String fileFullName = socket.readNextString();
                    if(fileFullName == null){
                        putException("Failed name is null");
                        done();
                        return;
                    }
                    SharedPreferences sp = Application.sharedPreferences();
                    Boolean overwriteFile = sp.getBoolean(SP_OVERWRITE_FILE_BOOL);
                    UriW uri;
                    if(Compare.isTrue(overwriteFile)){
                        uri = Environment.obtainClosestPendingUri(fileFullName);
                    } else {
                        uri = Environment.obtainUniquePendingUri(fileFullName);
                    }
                    if(uri == null){
                        putException("uri is null");
                        skipUntilLabel(LABEL_DISCONNECT);
                        return;
                    }
                    setUri(uri);
                    next();
                }
            });
        }
    }.name("receive file name"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TcpServer.SocketClient socket = getState().client.getSocket();
            socket.post(new RunnableW(){
                UtilsStream.OutputStreamProgressCheckCrc out = null;
                @Override
                public void runSafe() throws Throwable{
                    out = new UtilsStream.OutputStreamProgressCheckCrc(getState().fileUri.getOutputStream()){
                        @Override
                        protected void init(){
                            setStreamLinker(new UtilsStream.StreamLinkerSocketProgress(this));
                        }
                        @Override
                        public void onProgress(int current, int max){
                            int value = (int)(((float)current / (float)max) * 100f);
                            progressBarTransfer.setCurrent(value);
                        }
                    };
                    WakeLock.acquire(null, WakeLock.Type.CLIENT);
                    if(!socket.readNextStream(out)){
                        throw new Throwable("Failed to read file");
                    } else {
                        WakeLock.release();
                        getState().fileUri.pending(false);
                        progressBarTransfer.setCurrent(100);
                        next();
                    }
                }
                @Override
                public void onException(Throwable e){
                    WakeLock.release();
                    UtilsStream.close(out);
                    if(getState().fileUri != null){
                        getState().fileUri.delete();
                        getState().fileUri = null;
                    }
                    putException(e);
                    skipUntilLabel(LABEL_DISCONNECT);
                }
            });
        }
    }.name("receive file"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TcpServer.SocketClient socket = getState().client.getSocket();
            socket.post(new RunnableW(){
                @Override
                public void runSafe(){
                    if(socket.writeNext(true)){
                        socket.flush();
                        next();
                    }
                    else{
                        skipUntilLabel(LABEL_DISCONNECT);
                    }
                }
            });
        }
    }.name("confirm reception done"));
    gr.add(new RunnableGroup.Action(LABEL_DISCONNECT){
        @Override
        public void runSafe(){
            getState().client.disconnect().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    if(getException() == null){
                        next();
                    }
                    else{
                        done();
                    }
                }
            });
        }
    }.name("disconnect client"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            SharedPreferences sp = Application.sharedPreferences();
            if(Compare.isTrue(sp.getBoolean(SP_SERVER_AUTO_STOP_BOOL))){
                putValue(true);
                serverSearchAbort().observe(new ObserverState(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                });
            }
            else {
                next();
            }
        }
    }.name("stop server"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                getState().fileId = null;
            }
            boolean stoppedWhenDone = Compare.isTrue((Boolean)getValue());
            boolean hasBeenInterrupted = !stoppedWhenDone && (getState().currentStep != Step.TRANSFER_FILE_STARTED);
            if(!hasBeenInterrupted){
                if(e == null){
                    if(getState().messenger.isStarted()){
                        getState().messenger.requestStatusStart();
                    }
                    setStep(Step.TRANSFER_FILE_SUCCEED);
                } else {
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
                    if(getState().messenger.isStarted() && canSearchServer()){
                        getState().messenger.startBeacon(null, getState().messenger.getPortLocal());
                    }
                    setStep(Step.TRANSFER_FILE_FAILED);
                }
            }
        }
    }.name("disconnect client"));
    gr.start();
}

private void open(){
    AppInfo.open(getState().fileUri).observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            restoreButtons(true);
        }
        @Override
        public void onException(Throwable e){
            restoreButtons(true);
        }
    });
}

private enum Step{
    IDLE, SEARCH_START, SEARCH_STARTED, SEARCH_SUCCEED, SEARCH_FAILED, RUNNING_EXCEPTION, SEARCH_ABORT, SEARCH_ABORTED, TRANSFER_FILE_STARTED, TRANSFER_FILE_SUCCEED,
    TRANSFER_FILE_FAILED, TRANSFER_FILE_ABORTED;
}

protected static class State extends FragmentSocketBase.State{
    FragmentClient.SocketMessenger messenger;
    FragmentClient.SocketClient client;
    Step currentStep = null;
    String fileId = null;
    UriW fileUri = null;
    State(){
        setParam(newParam());
        messenger = new FragmentClient.SocketMessenger();
        client = new FragmentClient.SocketClient();
    }
    @Override
    public void attach(Object owner){
        super.attach(owner);
        client.attach(getOwner());
        messenger.attach(getOwner());
    }
    @Override
    protected Param newParam(){
        return new Param();
    }
    public void setFileUri(UriW fileUri){
        this.fileUri = fileUri;
    }

}
public static class Param extends FragmentSocketBase.Param{}

private static class SocketClient extends com.tezov.lib_java.socket.prebuild.SocketClient{
    WR<FragmentClient> frWR = null;
    void attach(FragmentClient fr){
        frWR = WR.newInstance(fr);
    }
    FragmentClient fragment(){
        return frWR.get();
    }
    @Override
    protected void onRunningException(Throwable e){
        super.onRunningException(e);

/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/

        fragment().setStep(Step.RUNNING_EXCEPTION);
    }
}
private static class SocketMessenger extends com.tezov.lib_java.socket.prebuild.SocketMessenger{
    WR<FragmentClient> frWR = null;
    Integer portServerFile = null;
    RunnableTimeOut requestStatusRunnable = null;
    public SocketMessenger(){
        setBroadcastAddressSupplier(ConnectivityManager::getBroadcastIPv4Address);
    }

    public void attach(FragmentClient fr){
        frWR = WR.newInstance(fr);
    }
    public FragmentClient fragment(){
        return frWR.get();
    }

    private void requestStatusStart(){
        if(requestStatusRunnable == null){
            requestStatusRunnable = new RunnableTimeOut(this, 2000){
                @Override
                public <R extends RunnableTimeOut> R start(){
                    setDelay(DELAY_REQUEST_STATUS_ms + AppRandomNumber.nextInt(DELAY_REQUEST_STATUS_RANDOM_ms));
                    return super.start();
                }
                @Override
                public void onTimeOut(){
                    start();
                }
                @Override
                public void onStart(){
                    fragment().request(DatagramServerRequest.Type.STATUS);
                }
            };
            requestStatusRunnable.start();
        }
    }
    public void requestStatusStop(){
        if(requestStatusRunnable != null){
            requestStatusRunnable.cancel();
            requestStatusRunnable = null;
        }
    }

    @Override
    public TaskState.Observable stop(){
        requestStatusStop();
        return super.stop();
    }
    public int getPortServerFile(){
        if(portServerFile == null){
            return TcpServer.NULL_PORT;
        } else {
            return portServerFile;
        }
    }
    @Override
    protected void onRunningException(Throwable e){
        super.onRunningException(e);
/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/
        fragment().setStep(Step.RUNNING_EXCEPTION);
    }
    @Override
    protected boolean acceptBeaconAnswer(DatagramBeacon beacon){
        return (beacon instanceof DatagramServerBeacon) && beacon.isIpv4Valid();
    }
    @Override
    protected DatagramBeacon buildBeaconRequest(){
        DatagramBeacon beacon =  new DatagramServerBeacon().init();
        return beacon;
    }
    @Override
    protected void onReceivedBeaconAnswer(DatagramBeacon beacon){
        DatagramServerBeacon serverBeacon = (DatagramServerBeacon)beacon;
        try{
            InetAddress addressRemote = InetAddress.getByName(serverBeacon.getOwnerAddress());
            setAddressRemote(addressRemote);
            setPortRemote(serverBeacon.getOwnerPort());
            portServerFile = serverBeacon.getPortServerFile();
            stopBeacon();
            fragment().setStep(Step.SEARCH_SUCCEED);
            requestStatusStart();
        }
        catch(Throwable e){
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
        }
    }
    @Override
    protected boolean acceptAnswer(DatagramAnswer answer, DatagramRequest request){
        return answer instanceof DatagramServerStatus;
    }
    @Override
    protected void onReceivedAnswer(DatagramAnswer answer, DatagramRequest request){
        fragment().processAnswerStatus((DatagramServerStatus)answer);
    }
    @Override
    protected void onAnswerToRequestFailed(Throwable e, int remotePort, DatagramRequest request){
        fragment().processRequestFailed(e, request);
    }

}

}
