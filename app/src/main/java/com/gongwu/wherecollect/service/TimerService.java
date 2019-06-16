package com.gongwu.wherecollect.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.volley.request.HttpClient;
import android.volley.request.PostListenner;
import android.widget.Toast;

import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.MessageBean;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.EventBusMsg;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimerService extends Service {
    private boolean pushthread = false;
    private static final int time = 10000;

    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d("TimerService", "onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("TimerService", "onStartCommand");
        if (intent!=null&&!TextUtils.isEmpty(intent.getStringExtra("flags"))&&
                intent.getStringExtra("flags").equals("3")) {
            //判断当系统版本大于20，即超过Android5.0时，我们采用线程循环的方式请求。
            //当小于5.0时的系统则采用定时唤醒服务的方式执行循环
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion > 20) {
                getPushThread();
            } else {
                getHttp();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //循环请求的线程
    public void getPushThread() {
        pushthread = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pushthread) {
                    try {
                        Thread.sleep(time);
                        getHttp();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //请求网络获取数据
    private void getHttp() {
        if (MyApplication.getUser(this) == null) return;
        Map<String, String> map = new TreeMap<>();
        map.put("uid", MyApplication.getUser(this).getId());
        map.put("type", "0");
        PostListenner listenner = new PostListenner(this) {
            @Override
            protected void code2000(final ResponseResult r) {
                super.code2000(r);
                List<MessageBean> temp = JsonUtils.listFromJsonWithSubKey(r.getResult(), MessageBean.class, "items");
                if (temp != null && temp.size() > 0) {
                    EventBusMsg.GetMessageList msg = new EventBusMsg.GetMessageList(temp.get(0));
                    EventBus.getDefault().post(msg);
                    EventBus.getDefault().post(EventBusMsg.MESSAGE_LIST);
                }
            }

            @Override
            protected void onFinish() {
                super.onFinish();
            }
        };
        HttpClient.getMessageList(this, map, listenner);
    }

    @Override
    public void onDestroy() {
        pushthread = false;
        Log.d("TimerService", "onDestroy");
        super.onDestroy();
    }

    //启动服务和定时器
    public static void getConnet(Context mContext) {
        try {
            Intent intent = new Intent(mContext, TimerService.class);
            intent.putExtra("flags", "3");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                //一般的启动服务的方式
                mContext.startService(intent);
            } else {
                //定时唤醒服务的启动方式
                PendingIntent pIntent = PendingIntent.getService(mContext, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) mContext
                        .getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(), time, pIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //停止由AlarmManager启动的循环
    public static void stop(Context mContext) {
        Intent intent = new Intent(mContext, TimerService.class);
        PendingIntent pIntent = PendingIntent.getService(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }
}
