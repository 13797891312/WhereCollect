package com.gongwu.wherecollect.util;

import com.gongwu.wherecollect.entity.BookBean;
import com.gongwu.wherecollect.entity.LocationBean;
import com.gongwu.wherecollect.entity.MessageBean;
import com.gongwu.wherecollect.entity.ObjectBean;
import com.gongwu.wherecollect.entity.SharePersonBean;
import com.gongwu.wherecollect.entity.ShareUserBean;
import com.gongwu.wherecollect.entity.UserBean;

import java.util.List;

/**
 * Function:
 * Date: 2017/12/15
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class EventBusMsg {
    public static String SPACE_EDIT = "space_edit";//空间编辑后的消息
    public static String OBJECT_CHANGE = "object_change";//需要刷新首页物品列表的消息
    public static String OBJECT_FITLER = "object_fitler";//筛选变动
    public static String ACTIVITY_FINISH = "activity_finish";
    public static String REFRESH_GOODS = "refresh_goods";
    public static String MESSAGE_LIST = "message_list";

    /**
     * 更换账号
     */
    public static class ChangeUser {
        UserBean user;
    }

    /**
     * 账号资料改变更新UI
     */
    public static class ChangeUserInfo {
    }

    /**
     * 添加家具后的消息体
     */
    public static class AddFurnitureMsg {
        public ObjectBean objectBean;
    }

    /**
     * 位置变动，某一页位置需要重新获取网络数据刷新
     */
    public static class EditLocationMsg {
        public int position;
        public boolean hasFurnitureChanged = true;//家具是否有变动要刷新
        public boolean hasObjectChanged = false;//物品总览是否有变动
        public boolean onlyNotifyUi = false;//是否只是刷新界面，不重新请求网络
        public ObjectBean changeBean;//可以为空，不为空代表改变的家具

        public EditLocationMsg(int position) {
            this.position = position;
        }
    }

    /**
     * 编辑家具
     */
    public static class editFurnitureMsg {
        public List<ObjectBean> layers;
        public String name;
        public float shape;
        public String imageUrl;
        public String background_url;

        public editFurnitureMsg(String name, float shape, String imageUrl, String background_url) {
            this.name = name;
            this.shape = shape;
            this.imageUrl = imageUrl;
            this.background_url = background_url;
        }

        public void setLayous(List<ObjectBean> layers) {
            this.layers = layers;
        }
    }

    /**
     * 导入物品
     */
    public static class ImportObject {
        public int position;

        public ImportObject(int position) {
            this.position = position;
        }
    }

    /**
     * 导入物品
     */
    public static class RecordChange {
        public int isSave;//1为暂存，2为取消暂存

        public RecordChange() {
        }
    }

    /**
     * 快速添加了空间和家具后需要网络刷新首页空间数据
     */
    public static class RequestSpace {
    }

    /**
     * 快速添加了空间和家具后位置查看页刷新后要通知编辑页刷新
     */
    public static class RequestSpaceEdit {
    }

    /**
     * 获取到物品总览后给详情页发消息
     */
    public static class getLocationObjectsMsg {
        public int position;//哪一页
        public List<ObjectBean> objectList;

        public getLocationObjectsMsg(int position, List<ObjectBean> objectList) {
            this.position = position;
            this.objectList = objectList;
        }
    }

    /**
     * 编辑位置页面切换，位置通知位置查看页变动，
     */
    public static class EditLocationPositionChangeMsg {
        public int position;//哪一页

        public EditLocationPositionChangeMsg(int position) {
            this.position = position;
        }
    }

    /**
     * 导入购买商品成功
     */
    public static class ImportFromBugSucces {
        public BookBean bean;

        public ImportFromBugSucces(BookBean bean) {
            this.bean = bean;
        }
    }

    /**
     * 是否呼吸查看
     */
    public static class GoodsIsCloseBreathLook {
        public boolean isCloseBreathLook;

        public GoodsIsCloseBreathLook(boolean isCloseBreathLook) {
            this.isCloseBreathLook = isCloseBreathLook;
        }
    }

    /**
     * 删除物品
     */
    public static class DeleteGoodsMsg {
        public String goodsId;

        public DeleteGoodsMsg(String goodsId) {
            this.goodsId = goodsId;
        }
    }

    /**
     * 获取msglist
     */
    public static class GetMessageList {
        public MessageBean messageBean;

        public GetMessageList(MessageBean messageBean) {
            this.messageBean = messageBean;
        }
    }

    public static class showShareImgList {
        public LocationBean shareUser;

        public showShareImgList(LocationBean shareUser) {
            this.shareUser = shareUser;
        }
    }

    public static class updateShareMsg {
    }

    public static class startService {
    }
    
    public static class stopService {
    }
}
