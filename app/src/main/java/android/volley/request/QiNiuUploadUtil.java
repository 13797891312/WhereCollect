package android.volley.request;
import android.app.Activity;
import android.content.Context;

import com.gongwu.wherecollect.application.MyApplication;
import com.gongwu.wherecollect.entity.ResponseResult;
import com.gongwu.wherecollect.util.FileUtil;
import com.gongwu.wherecollect.util.JsonUtils;
import com.gongwu.wherecollect.util.LogUtil;
import com.gongwu.wherecollect.util.StringUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * Function:七牛云资源上传
 * Date: 2017/11/7
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class QiNiuUploadUtil {
    List<File> mlist;
    Context context;
    List<String> urls = new ArrayList<>();
    UploadManager uploadManager;
    String path;

    public QiNiuUploadUtil(Context context, List<File> list, String path) {
        this.path = path;
        this.mlist = list;
        this.context = context;
        uploadManager = new UploadManager();
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < StringUtils.getListSize(mlist); i++) {
                    final File smallFile = FileUtil.getSmallFile(mlist.get(i));
                    final int j = i;
                    final String key = String.format("%s-%s%s", path, System.currentTimeMillis(), mlist.get(i)
                            .getName());
                    Map<String, String> map = new TreeMap<>();
                    map.put("uid", MyApplication.getUser(context).getId());
                    map.put("key", key);
                    PostListenner listenner = new PostListenner(context) {
                        @Override
                        protected void code2000(final ResponseResult r) {
                            super.code2000(r);
                            Map<String, Object> map = JsonUtils.mapFromJson(r.getResult());
                            String token = (String) map.get("token");
                            upLoad(key, token, smallFile);
                        }
                    };
                    HttpClient.getQiniuToken(context, map, listenner);
                }
            }
        }).start();
    }

    private void upLoad(String key, String token, File file) {
        uploadManager.put(file, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            //                                {"hash":"FlY2_jV4gKjYHz6fkDQ9qLSr67rb",
                            // "key":"urser-15100554679801510055460290.jpg"}
                            //http://7xroa4.com1.z0.glb.clouddn.com/
                            //http://cdn.shouner.com/
                            try {
                                String url = "http://cdn.shouner.com/" + res.getString("key");
                                urls.add(url);
                                if (urls.size() == mlist.size()) {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish(urls);
                                        }
                                    });
                                    FileUtil.deleteFolderFile(MyApplication.CACHEPATH, false);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            LogUtil.e("qiniu", "Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        LogUtil.e("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);
    }

    /**
     * 上传完成后的回调
     *
     * @param list
     */
    protected void finish(List<String> list) {
    }
}
