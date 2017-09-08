package android.volley.request;
import android.content.Context;

import com.gongwu.wherecollect.BuildConfig;

import java.util.Map;
/**
 * Function:
 * Date: 2016/5/26
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class HttpClient {
    public static String url = BuildConfig.URL;

    /**
     * 登陆
     * https://www.shouner.com/users/login?version=2.2.100&systemName=iPhone%20OS&systemVersion=9.3
     * .1&model=iPhone&from=sne_app_ios
     */
    public static void login(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("users/login", params,
                listenner);
        Queue.getQueue(context).add(request);
    }
    //    https://www.shouner.com/users/register?version=2.2.100&systemName=iPhone%20OS&systemVersion=9.3
    // .1&model=iPhone&from=sne_app_ios

    /**
     * 注册
     */
    public static void register(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("users/register", params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 修改资料
     */
    public static void editInfo(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("users/edit", params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 修改密码
     */
    public static void changePWD(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("users/change-password", params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取验证码
     */
    public static void getCode(Context context, Map<String, String> params, PostListenner
            listenner, String phone) {
        CustomGetRequest request = new CustomGetRequest("users/verify-code/send/" + phone, params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 验证验证码
     */
    public static void signCode(Context context, Map<String, String> params, PostListenner
            listenner, String phone) {
        CustomPostRequest request = new CustomPostRequest("users/verify-code/validate/" + phone, params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 绑定手机号
     */
    public static void changePhone(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("users/set-phone", params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 忘记密码
     */
    public static void forgetPWD(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("users/set-password", params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 绑定第三方账号
     */
    public static void bindOther(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v120/bind-account", params,
                listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 意见反馈
     */
    public static void feedBack(Context context, Map<String, String> params, PostListenner
            listenner) {
        CustomPostRequest request = new CustomPostRequest("users/feedback", params,
                listenner);
        Queue.getQueue(context).add(request);
    }
}

