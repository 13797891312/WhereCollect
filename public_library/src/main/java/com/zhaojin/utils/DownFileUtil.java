package com.zhaojin.utils;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public abstract class DownFileUtil {
    DownLoadReceiver receiver;
    private Context con;
    private String url;
    private String title;
    private String name;
    private long tag_id;
    private String path;
    private DownloadManager m_downLoadManager;
    private Handler hdHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            downChange(msg.arg1, msg.arg2);
        }

        ;
    };

    /**
     * @param con   上下文
     * @param url   下载地址
     * @param name  保存的文件名
     * @param path  保存路径，如果直接填""表示SD卡根目录，前面不用加"sdcard/",
     *              比如想存在sd卡的file文件夹或者file文件夹下的wanzhuan文件夹下分别填写
     *              "file","file/wanzhuan"
     *              ,注意：如果写成"file"，file文件夹不存在会自动创建，但是如果写成"file/wanzhuan"
     *              这样当file文件夹不存在时会报异常，如果file存在，wanzhuan不存在也会自动创建
     * @param title 下载显示的标题
     */
    public DownFileUtil(Context con, String url, String name, String path,
                        String title) {
        // TODO Auto-generated constructor stub
        this.con = con;
        this.url = url;
        this.name = name;
        this.title = title;
        this.path = path;
        /*** 初始化下载类 ****/
        m_downLoadManager = (DownloadManager) con
                .getSystemService(Context.DOWNLOAD_SERVICE);
        // 注册广播接收器
        receiver = new DownLoadReceiver();
        con.getApplicationContext().registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        /**
         * 下载改变监听
         */
        con.getContentResolver().registerContentObserver(
                Uri.parse("content://downloads/my_downloads"), true,
                new ContentObserver(hdHandler) {
                    @Override
                    public void onChange(boolean selfChange) {
                        // TODO Auto-generated method stub
                        super.onChange(selfChange);
                        int[] bytesAndStatus = getBytesAndStatus(tag_id);
                        if (bytesAndStatus[0] == -1 || bytesAndStatus[1] == -1) {
                            hdHandler.sendMessage(hdHandler.obtainMessage(0, 0,
                                    -1));
                        } else {
                            hdHandler.sendMessage(hdHandler.obtainMessage(0,
                                    bytesAndStatus[0], bytesAndStatus[1]));
                        }
                    }
                });
    }

    /**
     * @param apkName 安装包的包名，格式为com.xxxx.xxxx;
     * @return
     */
    public static boolean isInstall(String apkName, Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(apkName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    /**
     * @param appPackageName 包名
     * @param context
     */
    public static void startAPP(String appPackageName, Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                appPackageName);
        context.startActivity(intent);
    }

    /**
     * @param path    完整路径格式 mnt/sdcard/***.apk
     * @param context
     */
    public static void insertAPK(String path, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * @param downloadId 下载ID
     * @return 返回包含当前下载大小和总大小的数组
     */
    public int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query()
                .setFilterById(downloadId);
        Cursor c = null;
        try {
            c = m_downLoadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c
                        .getInt(c
                                .getColumnIndexOrThrow(DownloadManager
                                        .COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c
                        .getInt(c
                                .getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

    /***
     * 开始下载
     **/
    public void downFile() {
        Uri uri = Uri.parse(url);
        Request request = new Request(uri);
        request.setTitle(title);
        request.setDescription("正在下载");
        // 设置下载图片保存的位置和文件名
        request.setDestinationInExternalPublicDir(path, name);
        request.setAllowedNetworkTypes(Request.NETWORK_WIFI
                | Request.NETWORK_MOBILE);
        tag_id = m_downLoadManager.enqueue(request);
    }

    /***
     * 下载完毕监听
     **/
    public abstract void downOK();

    /**
     * 下载进度监听
     */
    public abstract void downChange(long current, long max);

    /**
     * 销毁资源
     */
    public void onDestroy() {
        con.unregisterReceiver(receiver);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTag_id() {
        return tag_id;
    }

    public void setTag_id(long tag_id) {
        this.tag_id = tag_id;
    }
    /***
     * 下载完毕广播接收
     ***/
    public class DownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction() == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                long id = intent
                        .getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (tag_id == id) {
                    downOK();
                }
            }
        }
    }
}