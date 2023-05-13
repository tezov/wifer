package com.tezov.wifer.dialog;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.debug.DebugException;

import static com.tezov.wifer.application.AppConfig.ADMOB_TRIAL_TIME_BANNER_ms;
import static com.tezov.wifer.application.AppConfig.ADMOB_TRIAL_TIME_INTERSTITIAL_ms;
import static com.tezov.wifer.application.AppConfig.ADMOB_TRIAL_TIME_OVER;
import static com.tezov.wifer.application.AppConfig.AD_SUGGEST_PAID_VERSION_MODULO;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT;
import static com.tezov.wifer.application.SharePreferenceKey.SP_SUGGEST_BUY_PAID_VERSION_DO_NOT_SHOW_BOOL;
import static com.tezov.lib_java.type.defEnum.Event.ON_CANCEL;
import static com.tezov.lib_java.type.defEnum.Event.ON_CONFIRM;

import com.android.billingclient.api.Purchase;
import com.tezov.wifer.R;
import com.tezov.wifer.application.AppInfo;
import com.tezov.wifer.application.Application;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.playStore.PlayStoreBilling;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalWebview;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.view.status.StatusParam;

public class DialogSuggestBuyNoAds extends DialogModalWebview{

public static TaskValue<Boolean>.Observable open(boolean checkBoxDoNotShowAgainHide){
    TaskValue<Boolean> task = new TaskValue<>();
    State state = new State();
    Param param = state.obtainParam();
    param.setConfirmButtonText(R.string.btn_buy);
    param.setCancelButtonText(R.string.btn_maybe_later);
    if(!checkBoxDoNotShowAgainHide){
        param.setCheckBoxText(R.string.chk_do_not_show_again);
        param.setChecked(getDoNotShowValue());
        resetNotShowCounter();
    }
    param.setRawFileId(R.raw.suggest_buy_no_ads);
    Navigate.To(DialogSuggestBuyNoAds.class, state).observe(new ObserverValueE<DialogSuggestBuyNoAds>(myClass()){
        @Override
        public void onComplete(DialogSuggestBuyNoAds dialog){
            dialog.observe(new ObserverEvent<Event.Is, Object>(myClass(), ON_CANCEL){
                @Override
                public void onComplete(Event.Is event, Object object){
                    task.notifyComplete(false);
                }
            });
            dialog.observe(new ObserverEvent<Event.Is, Object>(myClass(), ON_CONFIRM){
                @Override
                public void onComplete(Event.Is event, Object object){
                    task.notifyComplete(true);
                }
            });
        }
        @Override
        public void onException(DialogSuggestBuyNoAds dialog, Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}

public static boolean getDoNotShowValue(){
    return getDoNotShowValue(Application.sharedPreferences());
}
private static void setDoNotShowValue(boolean flag){
    SharedPreferences sp = Application.sharedPreferences();
    sp.put(SP_SUGGEST_BUY_PAID_VERSION_DO_NOT_SHOW_BOOL, flag);
}
private static boolean getDoNotShowValue(SharedPreferences sp){
    return Compare.isTrue(sp.getBoolean(SP_SUGGEST_BUY_PAID_VERSION_DO_NOT_SHOW_BOOL));
}
public static void resetNotShowCounter(){
    SharedPreferences sp = Application.sharedPreferences();
    sp.put(SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT, 0);
}
public static void incNotShowCounter(){
    SharedPreferences sp = Application.sharedPreferences();
    Integer adPaidVersionCounter = sp.getInt(SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT);
    if(adPaidVersionCounter == null){
        sp.put(SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT, 1);
    } else {
        sp.put(SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT, ++adPaidVersionCounter);
    }
}
public static boolean canShow(){
    SharedPreferences sp = Application.sharedPreferences();
    if(getDoNotShowValue(sp)){
        return false;
    } else {
        boolean result = getNotShowCounter(sp) >= AD_SUGGEST_PAID_VERSION_MODULO;
        if(!result){
            incNotShowCounter();
        }
        return result;
    }
}
private static int getNotShowCounter(SharedPreferences sp){
    Integer adPaidVersionCounter = sp.getInt(SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT);
    if(adPaidVersionCounter == null){
        sp.put(SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT, AD_SUGGEST_PAID_VERSION_MODULO);
        adPaidVersionCounter = AD_SUGGEST_PAID_VERSION_MODULO;
    }
    return adPaidVersionCounter;
}

public static boolean isTrialTimeInterstitialOver(){
    long timestamp = AppInfo.getInstalledTimestamp();
    boolean result = (timestamp + ADMOB_TRIAL_TIME_INTERSTITIAL_ms) < Clock.MilliSecond.now();
    result = Compare.isTrue(ADMOB_TRIAL_TIME_OVER) || ((ADMOB_TRIAL_TIME_OVER == null) && result);
    return result;
}
public static boolean isTrialTimeBannerOver(){
    long timestamp = AppInfo.getInstalledTimestamp();
    boolean result = (timestamp + ADMOB_TRIAL_TIME_BANNER_ms) < Clock.MilliSecond.now();
    result = Compare.isTrue(ADMOB_TRIAL_TIME_OVER) || ((ADMOB_TRIAL_TIME_OVER == null) && result);
    return result;
}
private static Class<DialogSuggestBuyNoAds> myClass(){
    return DialogSuggestBuyNoAds.class;
}
public static TaskValue<Boolean>.Observable isOwned(String SKU){
    TaskValue<Boolean> task = new TaskValue<>();
    PlayStoreBilling billing = new PlayStoreBilling();
    RunnableGroup gr = new RunnableGroup(myClass()).name("isOwned");
    int LBL_DISCONNECT = gr.label();
    int KEY_OWNED = gr.key();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            billing.connect().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("connect"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            billing.isOwned(SKU, false).observe(new ObserverValueE<Boolean>(this){
                @Override
                public void onComplete(Boolean owned){
                    put(KEY_OWNED, owned);
                    if(owned){
                        skipUntilLabel(LBL_DISCONNECT);
                    } else {
                        next();
                    }
                }
                @Override
                public void onException(Boolean owned, Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("check if owned"));
    gr.add(new RunnableGroup.Action(LBL_DISCONNECT){
        @Override
        public void runSafe(){
            billing.disconnect().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    done();
                }
                @Override
                public void onException(Throwable e){
                    done();
                }
            });
        }
    }.name("disconnect"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                Application.setOwnedNoAds(false);
                task.notifyException(null, e);
            } else {
                boolean result = Compare.isTrue(get(KEY_OWNED));
                Application.setOwnedNoAds(result);
                task.notifyComplete(result);
            }
        }
    });
    gr.start();
    return task.getObservable();
}

@Override
protected void onConfirm(){
    setCancelable(false);
    buy().observe(new ObserverStateE(this){
        @Override
        public void onComplete(){
            close();
            postConfirm();
        }
        @Override
        public void onException(Throwable e){
/*#-debug-> DebugException.start().log(e).end(); <-debug-#*/
            close();
            postCancel();
        }
    });
}
@Override
protected void onCheckBoxChange(boolean flag){
    setDoNotShowValue(flag);
}
public TaskState.Observable buy(){
    TaskState task = new TaskState();
    String SKU = Application.SKU_NO_ADS;
    PlayStoreBilling billing = new PlayStoreBilling();
    RunnableGroup gr = new RunnableGroup(myClass()).name("buy");
    int LBL_DISCONNECT = gr.label();
    int KEY_PURCHASE = gr.key();
    int KEY_OWNED = gr.key();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            billing.connect().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("connect"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            billing.buy(SKU, false).observe(new ObserverValueE<Purchase>(this){
                @Override
                public void onComplete(Purchase purchase){
                    put(KEY_OWNED, true);
                    put(KEY_PURCHASE, purchase);
                    next();
                }
                @Override
                public void onException(Purchase purchase, Throwable e){
                    AppInfo.toast(R.string.lbl_billing_error, StatusParam.DELAY_INFO_LONG_ms, StatusParam.Color.FAILED, true);
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("buy"));
    gr.add(new RunnableGroup.Action(LBL_DISCONNECT){
        @Override
        public void runSafe(){
            billing.disconnect().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    done();
                }
                @Override
                public void onException(Throwable e){
                    done();
                }
            });
        }
    }.name("disconnect"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                Application.setOwnedNoAds(false);
                task.notifyException(e);
            } else if(Compare.isFalseOrNull(get(KEY_OWNED))){
                Application.setOwnedNoAds(false);
                task.notifyComplete();
            } else {
                Purchase purchase = get(KEY_PURCHASE);
                if(purchase != null){
                    //NOW ?
                }
                Application.setOwnedNoAds(true);
                task.notifyComplete();
            }
        }
    });
    gr.start();
    return task.getObservable();
}


}