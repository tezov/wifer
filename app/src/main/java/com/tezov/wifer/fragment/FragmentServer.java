package com.tezov.wifer.fragment;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.async.notifier.task.TaskState;

import static com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction.IconAction.CLEAR;
import static com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction.IconAction.ACTION;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SERVER_AUTO_SELECT_FILE_BOOL;
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

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.data.validator.ValidatorIpv4;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;
import com.tezov.lib_java.data.validator.ValidatorPortDynamic;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.socket.TcpServer;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramAnswer;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramBeacon;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramRequest;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java_android.file.StorageMedia;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.view.ProgressBarTransfer;
import com.tezov.lib_java_android.util.UtilsTextWatcher;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.wifer.R;
import com.tezov.wifer.activity.ActivityMain;
import com.tezov.wifer.application.AppConfig;
import com.tezov.wifer.application.WakeLock;
import com.tezov.wifer.misc.DatagramServerBeacon;
import com.tezov.wifer.misc.DatagramServerRequest;
import com.tezov.wifer.misc.DatagramServerStatus;
import com.tezov.wifer.navigation.NavigationArguments;
import com.tezov.wifer.view.LedState;

public class FragmentServer extends FragmentSocketBase{

private FormEditText frmServerAddress = null;
private FormEditText frmServerPort = null;
private FormEditText frmFileName = null;

private ButtonMultiIconMaterial btnServerStart = null;
private ButtonMultiIconMaterial btnSelectFile = null;

private TextView lblSepFile = null;
private ProgressBarTransfer progressBarTransfer = null;

private LedState stepConnectionType = null;
private LedState stepServerStart = null;
private LedState stepTransferResult = null;

private FragmentServer me(){
    return this;
}

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
    return R.layout.fragment_server;
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
            SharedPreferences sp = Application.sharedPreferences();
            sp.put(SP_SERVER_PORT_STRING, value);
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
            else{
                return false;
            }
        }
    });

    btnSelectFile = view.findViewById(R.id.btn_select_file);
    btnSelectFile.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                selectFile();
            }
        }
    });

    lblSepFile = view.findViewById(R.id.lbl_sep_file);
    frmFileName = view.findViewById(R.id.lbl_file_name);
    frmFileName.setValidator(new ValidatorNotEmpty<>());
    frmFileName.link(new FormEditText.EntryString(){
        @Override
        public <T> void onSetValue(Class<T> type){
            if(getValue() == null){
                btnSelectFile.setIndex(0);
            }
            else{
                btnSelectFile.setIndex(1);
            }
        }
        @Override
        public boolean command(Object o){
            if(o == CLEAR){
                return setUri(null);
            }
            else{
                return false;
            }
        }
    });
    frmFileName.addOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                selectFile();
            }
        }
    });
    frmFileName.setText(null);

    btnServerStart = view.findViewById(R.id.btn_server_start);
    btnServerStart.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                if(btnServerStart.getIndex() == 0){
                    serverStart();
                }
                else {
                    serverStop();
                }
            }
        }
    });

    stepConnectionType = view.findViewById(R.id.stp_connection_type);
    stepServerStart = view.findViewById(R.id.stp_server_start);
    stepTransferResult = view.findViewById(R.id.stp_transfer_result);
    return view;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    setStep(Step.IDLE);
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            if(!onNewNavigationArguments(NavigationArguments.get(me()))){
                SharedPreferences sp = Application.sharedPreferences();
                if(Compare.isTrue(sp.getBoolean(SP_SERVER_AUTO_SELECT_FILE_BOOL))){
                    btnSelectFile.performClick();
                }
            }
        }
    });
}

@Override
public boolean onNewNavigationArguments(com.tezov.lib_java_android.ui.navigation.NavigationArguments arg){
    NavigationArguments arguments = (NavigationArguments)arg;
    if(arguments.isTargetMe(getClass())){
        if(arguments.exist()){
            ListOrObject<UriW> uris = arguments.getUris();
            if(setUri(uris.get())){
                stepTransferResult.setState(LedState.State.NEUTRAL);
                progressBarTransfer.setTitle(R.string.txt_result_progress_not_started);
                progressBarTransfer.setCurrent(0);
            }
        }
    }
    return super.onNewNavigationArguments(arg);
}

public boolean setUri(UriW uri){
    if((getState().currentStep != Step.TRANSFER_FILE_STARTED)){
        if(uri != null){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    String fileFullName = uri.getFullName();
                    frmFileName.setText(fileFullName);
                    frmFileName.moveToEnd();
                }
            });
        }
        else {
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    frmFileName.setText(null);
                }
            });
        }
        getState().setFileUri(uri);
        return true;
    }
    else {
/*#-debug-> DebugException.start().log(("Transfer in progress... can't set another uri now")).end(); <-debug-#*/
        return false;
    }
}

@Override
public void onConnected(ConnectivityState state){
    super.onConnected(state);
    if(disableButtons()){
        RunnableGroup gr = new RunnableGroup(this).name("onConnected");
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                SharedPreferences sp = Application.sharedPreferences();
                boolean mustRestartServer = getState().messenger.isStarted() || Compare.isTrue(sp.getBoolean(SP_SERVER_AUTO_START_BOOL));
                putValue(mustRestartServer);
                if(getState().messenger.isStarted()){
                    serverStop().observe(new ObserverState(this){
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
                enableButtons();
                boolean mustRestartServer = getValue();
                if(mustRestartServer && (btnServerStart.getIndex() == 0) && canStartServer()){
                    PostToHandler.of(getView(), new RunnableW(){
                        @Override
                        public void runSafe(){
                            btnServerStart.performClick();
                        }
                    });
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
        disableButtons();
        serverStop();
    }
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    setToolbarTittle(R.string.frg_server_title);
}

@Override
protected void onDisabledButtons(StateView stateView){
    stateView.clear()
        .enableNot(btnSelectFile)
        .clickableNot(frmFileName)
        .clickableIconNot(frmFileName)
        .enableNot(frmServerPort)
        .clickableIconNot(frmServerPort)
        .enableNot(btnServerStart);
}
@Override
protected void onEnabledButtons(){
    btnSelectFile.setEnabled(true);
    frmFileName.setClickable(true);
    frmFileName.setClickableIcon(true);
    frmServerPort.setEnabled(true);
    frmServerPort.setClickableIcon(true);
    btnServerStart.setEnabled(getNetworkState() != ConnectivityState.DATA);
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
                    case SERVER_START:
                        stepServerStart(previousStep);
                        break;
                    case SERVER_STARTED:
                        stepServerStarted(previousStep);
                        break;
                    case SERVER_START_FAILED:
                        stepServerStartFailed(previousStep);
                        break;

                    case RUNNING_EXCEPTION:
                        stepRunningException(previousStep);
                        break;

                    case SERVER_STOP:
                        stepServerStop(previousStep);
                        break;
                    case SERVER_STOPPED:
                        stepServerStopped(previousStep);
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
                }
            }
        });
    }
}
private void stepIdle(Step previousStep){
    stepServerStart.setState(LedState.State.NEUTRAL);
    frmServerAddress.setText(null);
    btnServerStart.setIndex(0);
    lblSepFile.setText(R.string.sep_file_to_send);
    stepTransferResult.setState(LedState.State.NEUTRAL);
    progressBarTransfer.setTitle(R.string.txt_result_progress_not_started);
    progressBarTransfer.setCurrent(0);
    enableButtons();
}

private void stepServerStart(Step previousStep){
    if(previousStep == Step.SERVER_START){
        return;
    }
    stepServerStart.setState(LedState.State.BUSY);
    stepTransferResult.setState(LedState.State.NEUTRAL);
    progressBarTransfer.setTitle(R.string.txt_result_progress_not_started);
    progressBarTransfer.setCurrent(0);
    frmServerAddress.setText(null);
}
private void stepServerStarted(Step previousStep){
    if(previousStep == Step.SERVER_STARTED){
        return;
    }
    stepServerStart.setState(LedState.State.SUCCEED);
    frmServerAddress.setText(getState().messenger.getAddressLocal());
    Integer localPort = getState().messenger.getPortLocal();
    if(!Compare.equals(frmServerPort.getText(), String.valueOf(localPort))){
        frmServerPort.setText(String.valueOf(localPort));
    }
    btnServerStart.setIndex(1);
    btnSelectFile.setEnabled(true);
    frmFileName.setClickable(true);
    frmFileName.setClickableIcon(true);

    if(getState().stateView.isLocked()){
/*#-debug-> DebugException.start().log("state view is already locked..").end(); <-debug-#*/
    }
    getState().stateView.lock();
    PostToHandler.of(getView(), START_STOP_DELAY_ms, new RunnableW(){
        @Override
        public void runSafe(){
            btnServerStart.setEnabled(true);
            getState().stateView.unlock();
        }
    });
}
private void stepServerStartFailed(Step previousStep){
    if(previousStep == Step.SERVER_START_FAILED){
        return;
    }
    stepServerStart.setState(LedState.State.FAILED);
    frmServerAddress.setText(null);
    enableButtons();
}

private void stepRunningException(Step previousStep){
    setStep(Step.IDLE);
}

private void stepServerStop(Step previousStep){
    if(previousStep == Step.SERVER_STOP){
        return;
    }
    stepServerStart.setState(LedState.State.BUSY);
}
private void stepServerStopped(Step previousStep){
    if(previousStep == Step.SERVER_STOPPED){
        return;
    }
    stepServerStart.setState(LedState.State.NEUTRAL);
    frmServerAddress.setText(null);
    btnServerStart.setIndex(0);
    enableButtons();
}

private void stepTransferStarted(Step previousStep){
    if(previousStep == Step.TRANSFER_FILE_STARTED){
        return;
    }
    stepTransferResult.setState(LedState.State.BUSY);
    btnSelectFile.setEnabled(false);
    frmFileName.setClickable(false);
    frmFileName.setClickableIcon(false);
    progressBarTransfer.setTitle(R.string.txt_result_progressing);
    lblSepFile.setText(R.string.sep_file_sending);
    progressBarTransfer.setCurrent(0);
}
private void stepTransferSucceed(Step previousStep){
    if(previousStep == Step.TRANSFER_FILE_SUCCEED){
        return;
    }
    stepTransferResult.setState(LedState.State.SUCCEED);
    btnSelectFile.setEnabled(true);
    frmFileName.setClickable(true);
    frmFileName.setClickableIcon(true);
    progressBarTransfer.setTitle(R.string.txt_result_progress_done);
    lblSepFile.setText(R.string.sep_file_to_send);
}
private void stepTransferFailed(Step previousStep){
    if(previousStep == Step.TRANSFER_FILE_FAILED){
        return;
    }
    stepTransferResult.setState(LedState.State.FAILED);
    btnSelectFile.setEnabled(true);
    frmFileName.setClickable(true);
    frmFileName.setClickableIcon(true);
    progressBarTransfer.setTitle(R.string.txt_result_progress_failed);
    lblSepFile.setText(R.string.sep_file_to_send);
}

private boolean canStartServer(){
    FocusCemetery.request();
    return frmServerPort.isValid();
}
private void serverStart(){
    if(!canStartServer()){
        frmServerPort.showError();
        restoreButtons();
        return;
    }
    setStep(Step.SERVER_START);
    Integer port = Integer.parseInt(frmServerPort.getValue());
    RunnableGroup gr = new RunnableGroup("serverStart");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            getState().messenger.start(getAddressLocal(), port, true).observe(new ObserverStateE(this){
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
    }.name("start messenger"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            getState().server.start(getAddressLocal(), null).observe(new ObserverStateE(this){
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
    }.name("start server"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e == null){
                setStep(Step.SERVER_STARTED);
            } else {
                setStep(Step.SERVER_START_FAILED);
            }
        }
    });
    gr.start();
}
private TaskState.Observable serverStop(){
    setStep(Step.SERVER_STOP);
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("serverStop");
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
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            boolean aborted = getState().server.getSocket() != null;
            getState().server.socketClose();
            getState().server.stop().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    if(aborted){
                        setStep(Step.TRANSFER_FILE_FAILED);
                    }
                    next();
                }
            });
        }
    }.name("stop server"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            setStep(Step.SERVER_STOPPED);
            task.notifyComplete();
        }
    });
    gr.start();
    return task.getObservable();
}

private DatagramAnswer processRequest(DatagramServerRequest request){
    DatagramAnswer answer = null;
    DatagramServerRequest.Type type = request.getRequest();
    if(type != null){
        switch(type){
            case STATUS:{
                answer = buildAnswerStatus(request);
            }
            break;
        }
    }
    return answer;
}
private DatagramAnswer buildAnswerStatus(DatagramServerRequest request){
    DatagramServerStatus answer = new DatagramServerStatus().init();
    answer.setAccepted(true);
    SocketServer server = getState().server;
    if(!server.isStarted()){
        answer.setStatus(DatagramServerStatus.Type.INITIALIZING);
    } else if(server.getSocket() != null){
        answer.setStatus(DatagramServerStatus.Type.BUSY);
    } else if(canSendFile()){
        answer.setStatus(DatagramServerStatus.Type.FILE_TO_SEND);
        answer.setFileId(getState().fileId);
    } else {
        answer.setStatus(DatagramServerStatus.Type.READY);
    }
    return answer;
}

private void selectFile(){
    StorageMedia.openDocument(false).observe(new ObserverValueE<ListOrObject<UriW>>(this){
        @Override
        public void onComplete(ListOrObject<UriW> uris){
            if(uris.size() > 1){
/*#-debug-> DebugException.start().log("multiple uri ignored").end(); <-debug-#*/
            }
            if(setUri(uris.get())){
                stepTransferResult.setState(LedState.State.NEUTRAL);
                progressBarTransfer.setTitle(R.string.txt_result_progress_not_started);
                progressBarTransfer.setCurrent(0);
                SharedPreferences sp = Application.sharedPreferences();
                if(getState().messenger.isStarted()){
                    DatagramAnswer answer = buildAnswerStatus(null);
                    getState().messenger.sendAnswer(answer);
                    restoreButtons();
                }
                else if(hasActiveNetwork() && Compare.isTrue(sp.getBoolean(SP_SERVER_AUTO_START_BOOL))){
                    restoreButtons();
                    if(btnServerStart.isEnabled() && canStartServer()){
                        PostToHandler.of(getView(), new RunnableW(){
                            @Override
                            public void runSafe(){
                                btnServerStart.performClick();
                            }
                        });
                    }
                }
                else{
                    restoreButtons();
                }
            }
            else{
                restoreButtons();
            }
        }
        @Override
        public void onException(ListOrObject<UriW> uris, Throwable e){
            restoreButtons();
        }
    });
}
private boolean canSendFile(){
    FocusCemetery.request();
    return frmFileName.isValid();
}
private void onSendStart(){
    setStep(Step.TRANSFER_FILE_STARTED);
    RunnableGroup gr = new RunnableGroup(this).name("onTransferStart");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(canSendFile()){
                next();
            }
            else {
                PostToHandler.of(frmFileName, new RunnableW(){
                    @Override
                    public void runSafe(){
                        frmFileName.showError();
                    }
                });
                putException("can not send file");
                done();
            }
        }
    }.name("can send file?"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TcpServer.SocketClient socket = getState().server.getSocket();
            if(socket == null){
                putException("socket is null");
                done();
                return;
            }
            socket.setUncaughtExceptionHandler(new UncaughtExceptionHandlerW(){
                @Override
                public void uncaughtException(@NonNull Thread t, @NonNull Throwable e){
                    putException(new Throwable(e));
                    done();
                }
            });
            next();
        }
    }.name("prepare socket"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TcpServer.SocketClient socket = getState().server.getSocket();
            if(socket == null){
                putException("socket is null");
                done();
                return;
            }
            socket.post(new RunnableW(){
                @Override
                public void runSafe(){
                    String fileName = getState().fileUri.getFullName();
                    if(!socket.writeNext(fileName) || !socket.flush()){
                        putException("failed to write");
                        done();
                    } else {
                        next();
                    }
                }
            });
        }
    }.name("send file name"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TcpServer.SocketClient socket = getState().server.getSocket();
            if(socket == null){
                putException("socket is null");
                done();
                return;
            }
            socket.post(new RunnableW(){
                UtilsStream.InputStreamProgressAppendCrc in = null;
                @Override
                public void runSafe() throws Throwable{
                    in = new UtilsStream.InputStreamProgressAppendCrc(getState().fileUri.getInputStream()){
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
                    WakeLock.acquire(in.available(), WakeLock.Type.SERVER);
                    if(!socket.writeNext(in)){
                        throw new Throwable("failed to write");
                    } else {
                        WakeLock.release();
                        progressBarTransfer.setCurrent(100);
                        next();
                    }
                }
                @Override
                public void onException(Throwable e){
                    WakeLock.release();
                    UtilsStream.close(in);
                    putException(e);
                    done();
                }
            });
        }
    }.name("send file"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TcpServer.SocketClient socket = getState().server.getSocket();
            if(socket == null){
                putException("socket is null");
                done();
                return;
            }
            socket.post(new RunnableW(){
                @Override
                public void runSafe(){
                    Boolean confirmation = socket.readNextBoolean();
                    if(Compare.isTrue(confirmation)){
                        next();
                    }
                    else {
                        putException("confirmation failed");
                        done();
                    }
                }
            });
        }
    }.name("wait confirmation reception done"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            TcpServer.SocketClient socket = getState().server.getSocket();
            if(socket == null){
                putException("socket is null");
                done();
                return;
            }
            if(getException() == null){
                getState().server.socketNullify();
            }
            SharedPreferences sp = Application.sharedPreferences();
            if((getException() == null) && Compare.isTrue(sp.getBoolean(SP_SERVER_AUTO_STOP_BOOL))){
                putValue(true);
                serverStop().observe(new ObserverState(this){
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
            boolean stoppedWhenDone = Compare.isTrue((Boolean)getValue());
            boolean hasBeenInterrupted = !stoppedWhenDone && (getState().currentStep != Step.TRANSFER_FILE_STARTED);
            if(!hasBeenInterrupted){
                if(getException() == null){
                    setStep(Step.TRANSFER_FILE_SUCCEED);
                } else {
                    getState().server.socketClose();
                    setStep(Step.TRANSFER_FILE_FAILED);
                }
            }
        }
    });
    gr.start();
}

private enum Step{
    IDLE, SERVER_START, SERVER_STARTED, SERVER_START_FAILED, RUNNING_EXCEPTION, SERVER_STOP, SERVER_STOPPED, TRANSFER_FILE_STARTED, TRANSFER_FILE_SUCCEED, TRANSFER_FILE_FAILED
}
private static class State extends FragmentSocketBase.State{
    FragmentServer.SocketMessenger messenger;
    FragmentServer.SocketServer server;
    Step currentStep = null;
    UriW fileUri = null;
    String fileId = null;
    State(){
        messenger = new FragmentServer.SocketMessenger();
        server = new FragmentServer.SocketServer();
    }
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public void attach(Object owner){
        super.attach(owner);
        messenger.attach(getOwner());
        server.attach(getOwner());
    }
    public void setFileUri(UriW fileUri){
        if(fileUri != null){
            this.fileUri = fileUri;
            this.fileId = UtilsString.randomHex(AppConfig.FILE_ID_LENGTH);
        } else {
            this.fileUri = null;
            this.fileId = null;
        }
    }
}
public static class Param extends FragmentSocketBase.Param{}
private static class SocketServer extends com.tezov.lib_java.socket.prebuild.SocketServer{
    TcpServer.SocketClient socket = null;
    WR<FragmentServer> frWR = null;
    public void attach(FragmentServer fr){
        frWR = WR.newInstance(fr);
    }
    public FragmentServer fragment(){
        return frWR.get();
    }
    public void socketNullify(){
        if(socket != null){
            socket.setUncaughtExceptionHandler(null);
            socket = null;
        }
    }
    public void socketClose(){
        if(socket != null){
            socket.setUncaughtExceptionHandler(null);
            socket.close();
            socket = null;
        }
    }
    public TcpServer.SocketClient getSocket(){
        return socket;
    }
    @Override
    protected void onRunningException(Throwable e){
        super.onRunningException(e);
/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/
        fragment().setStep(Step.RUNNING_EXCEPTION);
    }
    @Override
    protected void onClientAccepted(TcpServer.SocketClient socket){
        this.socket = socket;
        fragment().onSendStart();
    }
    @Override
    protected boolean acceptAnswer(TcpServer.SocketClient socket, DatagramRequest request, DatagramAnswer answer){
        return (this.socket == null) && super.acceptAnswer(socket, request, answer);
    }

}
private static class SocketMessenger extends com.tezov.lib_java.socket.prebuild.SocketMessenger{
    private WR<FragmentServer> frWR = null;
    public SocketMessenger(){
        setBroadcastAddressSupplier(ConnectivityManager::getBroadcastIPv4Address);
    }
    public void attach(FragmentServer fr){
        frWR = WR.newInstance(fr);
    }
    public FragmentServer fragment(){
        return frWR.get();
    }
    @Override
    protected void onRunningException(Throwable e){
        super.onRunningException(e);
/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/
        fragment().setStep(Step.RUNNING_EXCEPTION);
    }
    @Override
    protected boolean acceptBeaconRequest(DatagramBeacon beacon){
        return (beacon instanceof DatagramServerBeacon);
    }
    @Override
    protected DatagramBeacon buildBeaconAnswer(){
        DatagramServerBeacon beacon = new DatagramServerBeacon().init();
        beacon.setPortServerFile(fragment().getState().server.getPortLocal());
        return beacon;
    }
    @Override
    protected boolean acceptRequest(DatagramRequest request){
        return request instanceof DatagramServerRequest;
    }
    @Override
    protected DatagramAnswer onReceivedRequest(DatagramRequest request){
        DatagramServerRequest requestServer = (DatagramServerRequest)request;
        return fragment().processRequest(requestServer);
    }
    @Override
    protected void onAnswerToRequestFailed(Throwable e, int remotePort, DatagramRequest request){
/*#-debug-> DebugException.start().log((e)).end(); <-debug-#*/
    }

}


}
