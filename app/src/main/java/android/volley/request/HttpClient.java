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
    public static void login(Context context, Map<String, String> params, String versionName, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/login?version=" + versionName, params, listenner);
        Queue.getQueue(context).add(request);
    }
    //    https://www.shouner.com/users/register?version=2.2.100&systemName=iPhone%20OS&systemVersion=9.3
    // .1&model=iPhone&from=sne_app_ios

    /**
     * 注册
     */
    public static void register(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/register", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 注册测试账号
     */
    public static void registerTest(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/register-test", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 注销测试账号
     */
    public static void logoutTest(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/logout-test", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 修改资料
     */
    public static void editInfo(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/edit", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 修改密码
     */
    public static void changePWD(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/change-password", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取验证码
     */
    public static void getCode(Context context, Map<String, String> params, PostListenner listenner, String phone) {
        CustomGetRequest request = new CustomGetRequest("users/verify-code/send/" + phone, params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 验证验证码
     */
    public static void signCode(Context context, Map<String, String> params, PostListenner listenner, String phone) {
        CustomPostRequest request = new CustomPostRequest("users/verify-code/validate/" + phone, params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 绑定手机号
     */
    public static void changePhone(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/set-phone", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 忘记密码
     */
    public static void forgetPWD(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/set-password", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 绑定第三方账号
     */
    public static void bindOther(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v120/bind-account", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 意见反馈
     */
    public static void feedBack(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/feedback", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 物品列表
     */
    public static void getGoodsList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v230/object-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取没有归位的物品列表
     */
    public static void getNoLocationGoodsList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/object/no-position-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 删除物品
     */
    public static void deleteGoods(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/object-del", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取颜色列表
     */
    public static void getColors(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v230/recommend/color", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取分类列表
     */
    public static void getChannel(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v230/recommend/channel", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取分类列表搜索
     */
    public static void getSearchChannel(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v230/channel/list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取分类列表
     */
    public static void getChannelList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v230/channel/list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取分类列表
     */
    public static void getCategoryList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/category-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 添加分类
     */
    public static void getAddChannel(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v120/channel/add", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 搜索分类
     */
    public static void getSearchFenlei(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/category-search", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 添加分类
     */
    public static void getAddFenlei(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/category-add", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 添加物品
     */
    public static void getAddObject(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/object-add", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 封存物品
     */
    public static void fengcun(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v210/object-archive", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取筛选条件列表
     */
    public static void getFilterList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomGetRequest request = new CustomGetRequest("api/app/v230/filter-category-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 物品搜索
     */
    public static void searchObject(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/object/search", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取七牛TOKEN
     */
    public static void getQiniuToken(Context context, Map<String, String> params, PostListenner listenner) {
        CustomGetRequest request = new CustomGetRequest("qiniu-token", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 批量添加
     */
    public static void addObjects(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v320/add-objects", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 通过图书码获取图书信息
     */
    public static void getBookInfo(Context context, Map<String, String> params, PostListenner listenner) {
        CustomGetRequest request = new CustomGetRequest("api/app/v120/import-object/isbn", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 通过网购商品导入
     */
    public static void getTaobaoInfo(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v120/import-object/taobao", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取位置最外层列表
     */
    public static void getLocationList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/location-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取家具列表
     */
    public static void getFurnitureList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/furniture-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 删除空间
     */
    public static void deleteLocation(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/delete", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 添加空间
     */
    public static void addLocation(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/location-add", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 换位置空间
     */
    public static void updataSpacePosition(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v120/update-location-list-position", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 空间改名
     */
    public static void editSpace(Context context, Map<String, String> params, PostListenner listenner, String code) {
        CustomPostRequest request = new CustomPostRequest(String.format("users/location/%s/edit", code), params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 移动家具位置
     */
    public static void movePosition(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/location/set/position", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 移动物品归属
     * location_code
     * object_id
     * position
     * scale
     * uid
     */
    public static void moveLocation(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/object/move", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 导入物品
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void importObjects(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/import-objects", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 新建家具
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void createFurniture(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/add", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 移出物品
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void removeObjectFromFurnitrue(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/remove-object", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 编辑家具
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void updataFurniture(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/update", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 编辑家具合并隔层
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void mergeObject(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/union", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 编辑家具隔层拆分
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void splitObject(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/split", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 家具移动位置
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void moveFurniture(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/move", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取物品总览
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void getObjectListWithSpaceCode(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/object-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取系统家具类型，用来搜索条件
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void getFurnitureType(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/furniture/type/list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取系统家具列表
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void getSysfurniturelist(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/furniture/list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取家具内部详情
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void getFurnitureDetail(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/furniture/detail", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取家具内部详情
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void savaRoomRecord(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/bigevent/add", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取家具内部详情
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void getRecordList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/bigevent/list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取快速添加空间数据
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void getQuickData(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/recommend-list", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 快速添加提交
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void commitQuickAdd(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/quick-add", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 迁移隔层
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void moveLayer(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v300/location/move-layer", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 批量添加
     *
     * @param context
     * @param params
     * @param listenner
     */
    public static void addMoreGoods(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v320/add-objects", params, listenner);
        Queue.getQueue(context).add(request);
    }
    //############################################3.3接口##################################################

    /**
     * 用户详情
     */
    public static void getUserInfo(Context context, String uid, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("users/user-info/" + uid, params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取历史共享人列表
     */
    public static void getAddSharePersonOldList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v330/search-user-history", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 消息中心列表
     */
    public static void getMessageList(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v330/messages", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取共享人列表
     */
    public static void getAllSharedUsers(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v330/a/get-all-shared-users", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 获取共享空间列表
     */
    public static void getAllSharedLocations(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v330/a/get-all-shared-locations", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 扫描用户二维码获取信息
     */
    public static void getUserCodeInfo(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v330/user-info-by-id", params, listenner);
        Queue.getQueue(context).add(request);
    }

    /**
     * 与已建立连接的用户直接共享空间
     */
    public static void shareOldUserLocation(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v330/a/share-location-2-user", params, listenner);
        Queue.getQueue(context).add(request);

        /**
         * 邀请用户共享空间
         */
    }

    public static void dealWithShareRequest(Context context, String url, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest(url.substring(1, url.length()), params, listenner);
        Queue.getQueue(context).add(request);
    }


    /**
     * 与共享用户断开连接
     */
    public static void closeShareUser(Context context, Map<String, String> params, PostListenner listenner) {
        CustomPostRequest request = new CustomPostRequest("api/app/v330/a/discontinue", params, listenner);
        Queue.getQueue(context).add(request);
    }
}
