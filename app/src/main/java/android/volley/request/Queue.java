package android.volley.request;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gongwu.wherecollect.application.MyApplication;

import java.util.concurrent.LinkedBlockingQueue;
/**
 * Created by zhaojin on 2016/4/25.
 */
public class Queue {
    private static int MAXQUEUE = 6;//最多3个队列
    private static LinkedBlockingQueue<RequestQueue> queue = new LinkedBlockingQueue(MAXQUEUE);

    public static RequestQueue getQueue(Context context) {
        RequestQueue rq = null;
        try {
            if (queue.size() < MAXQUEUE) {//如果还没有3个队列新建队列
                queue.offer(Volley.newRequestQueue(MyApplication.getContext()));
            }
            rq = queue.poll();//取出队列
            queue.offer(rq);//把队列放到尾部
        } catch (Exception e) {
            e.printStackTrace();
            return Volley.newRequestQueue(MyApplication.getContext());
        }
        return rq != null ? rq : Volley.newRequestQueue(MyApplication.getContext());
    }
}
