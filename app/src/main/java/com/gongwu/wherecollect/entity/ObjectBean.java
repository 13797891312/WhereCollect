package com.gongwu.wherecollect.entity;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.gongwu.wherecollect.activity.BaseViewActivity;
import com.gongwu.wherecollect.util.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Function:物品列表实体
 * Date: 2017/10/19
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ObjectBean implements Serializable {
    /**
     * _id : 59e7fb1d31391929e7ed83e7
     * search_tags :
     * object_count : 1
     * object_url : http://7xroa4.com1.z0.glb.clouddn.com/object/e4a6606a-f9cc-4ffa-b5a6-a4b6c63c2532.jpg
     * user_id : 59e7fb1c31391929e7ed839e
     * channel : 购买>网店>淘宝
     * color : 黑色
     * season : 春天、夏天、秋天
     * price_min : 0
     * price_max : 0
     * name : 猫脸鞋
     * __v : 0
     * deleted_at : null
     * updated_at : 2017-07-15T08:45:41.769Z
     * created_at : 2017-07-15
     * is_archive : false
     * is_count_user_obj : false
     * is_count_location : false
     * star : 0
     * coordinates : [{"x":0,"y":0,"_id":"5969d63552ee506d50124e06"}]
     * locations : [{"code":"AB419AFCAF652609FE1FB5C811D93341","level":1,"name":"鞋柜-杂物柜",
     * "_id":"5953c5fd9741c4c01e3dc1dc"},{"code":"B4B539FA7723D2FDF77C795317C0D5E1","level":0,"name":"客厅",
     * "_id":"595488c99741c4c01e3dcc8d"},{"code":"9C5F4EB16113FDD00A14126C752EF2D5","level":2,"name":"鞋柜-左",
     * "_id":"5968286652ee506d50122aff"}]
     * categories : [{"code":"71D0E0441487F9B5CF6BA6ACDEF8CC6A","level":0,"name":"衣装打扮",
     * "_id":"58e239928f2536ae3a933fad"},{"code":"FD78BE6DE691ECB1597C9C6630722803","level":1,"name":"鞋靴",
     * "_id":"58e23ad28f2536ae3a934485"},{"code":"1559A7D0013FF13D568D00F438A84873","level":2,"name":"鞋",
     * "_id":"58e23ad48f2536ae3a93448f"},{"code":"C406042E95B2FFA24CCA812050E023C6","level":3,"name":"平跟鞋",
     * "_id":"58e23ad78f2536ae3a93449d"}]
     */
    private boolean isSelect;//代表空间是否选择状态
    private String _id;
    private String id;
    private String search_tags;
    private int object_count;//3.2弃用
    private String object_url;
    private String image_url;
    private String background_url;
    private String user_id;
    private String channel;
    private List<String> channels;
    private String color;
    private List<String> colors;
    private String season;
    private int price_min;
    private int price_max;
    private String name;
    private int __v;
    private String deleted_at;
    private String updated_at;
    private String created_at;
    private boolean is_archive;
    private boolean is_count_user_obj;
    private boolean is_count_location;
    private int star;
    private String detail;
    private Point scale;
    private Point position;
    private String code;
    private ArrayList<ObjectBean> layers;
    private float ratio;
    private int recommend;//快速添加几个
    private boolean isOpen = true;//自己添加的参数，快速添加时用的，别处没用
    private boolean isLayer = false;//自己添加的参数，迁移隔层时用，别处没用
    private List<String> tags;
    private String price;
    private String buy_date;
    private String expire_date;
    private int count;//物品数量
    /**
     * x : 0
     * y : 0
     * _id : 5969d63552ee506d50124e06
     */
    private List<CoordinatesBean> coordinates;
    /**
     * code : AB419AFCAF652609FE1FB5C811D93341
     * level : 1
     * name : 鞋柜-杂物柜
     * _id : 5953c5fd9741c4c01e3dc1dc
     */
    private List<BaseBean> parents;
    /**
     * code : AB419AFCAF652609FE1FB5C811D93341
     * level : 1
     * name : 鞋柜-杂物柜
     * _id : 5953c5fd9741c4c01e3dc1dc
     */
    private List<BaseBean> locations;
    /**
     * code : 71D0E0441487F9B5CF6BA6ACDEF8CC6A
     * level : 0
     * name : 衣装打扮
     * _id : 58e239928f2536ae3a933fad
     */
    private List<BaseBean> categories;

    /**
     * 获取初始尺寸
     *
     * @param context
     * @return
     */
    public static float getInitSize(Context context) {
        return BaseViewActivity.getScreenWidth(((Activity) context)) / 4;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isLayer() {
        return isLayer;
    }

    public void setLayer(boolean layer) {
        isLayer = layer;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public List<BaseBean> getParents() {
        return parents;
    }

    public void setParents(List<BaseBean> parents) {
        this.parents = parents;
    }

    public String getBackground_url() {
        return TextUtils.isEmpty(background_url) ? image_url : background_url;
    }

    public void setBackground_url(String background_url) {
        this.background_url = background_url;
    }

    public float getRatio() {
        return Math.abs(ratio);
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public List<ObjectBean> getLayers() {
        return layers;
    }

    public void setLayers(List<ObjectBean> layers) {
        this.layers = (ArrayList<ObjectBean>) layers;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getScale() {
        return scale;
    }

    public void setScale(Point scale) {
        this.scale = scale;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getId() {
        return TextUtils.isEmpty(id) ? _id : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSearch_tags() {
        return search_tags;
    }

    public void setSearch_tags(String search_tags) {
        this.search_tags = search_tags;
    }

    public int getObject_count() {
        return object_count;
    }

    public void setObject_count(int object_count) {
        this.object_count = object_count;
    }

    public String getObject_url() {
        return object_url;
    }

    public void setObject_url(String object_url) {
        this.object_url = object_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPrice() {
        String str = TextUtils.isEmpty(price) ? price_max + "" : price;
        return str.replaceAll("元", "").replaceAll("CNY ", "");
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChannel() {
        if (!StringUtils.isEmpty(channels)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < channels.size(); i++) {
                sb.append(channels.get(i)).append(">");
            }
            sb.delete(sb.length() - 1, sb.length());
            return sb.toString();
        } else {
            return TextUtils.isEmpty(channel) ? "" : channel;
        }
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getColor() {
        if (!StringUtils.isEmpty(colors)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < colors.size(); i++) {
                sb.append(colors.get(i)).append("、");
            }
            sb.delete(sb.length() - 1, sb.length());
            return sb.toString();
        } else {
            return TextUtils.isEmpty(color) ? "" : color;
        }
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getPrice_min() {
        return price_min;
    }

    public void setPrice_min(int price_min) {
        this.price_min = price_min;
    }

    public int getPrice_max() {
        return price_max;
    }

    public void setPrice_max(int price_max) {
        this.price_max = price_max;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            return "未定义";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getUpdated_at() {
        return TextUtils.isEmpty(updated_at) ? created_at : updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isIs_archive() {
        return is_archive;
    }

    public void setIs_archive(boolean is_archive) {
        this.is_archive = is_archive;
    }

    public boolean isIs_count_user_obj() {
        return is_count_user_obj;
    }

    public void setIs_count_user_obj(boolean is_count_user_obj) {
        this.is_count_user_obj = is_count_user_obj;
    }

    public boolean isIs_count_location() {
        return is_count_location;
    }

    public void setIs_count_location(boolean is_count_location) {
        this.is_count_location = is_count_location;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CoordinatesBean> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinatesBean> coordinates) {
        this.coordinates = coordinates;
    }

    public List<BaseBean> getLocations() {
        return locations;
    }

    public void setLocations(List<BaseBean> locations) {
        this.locations = locations;
    }

    public List<BaseBean> getCategories() {
        return categories;
    }

    public void setCategories(List<BaseBean> categories) {
        this.categories = categories;
    }


    public String getBuy_date() {
        if (!TextUtils.isEmpty(buy_date) && buy_date.contains("T")) {
            return buy_date.split("T")[0];
        }
        return buy_date;
    }

    public void setBuy_date(String buy_date) {
        this.buy_date = buy_date;
    }

    public String getExpire_date() {
        if (!TextUtils.isEmpty(expire_date) && expire_date.contains("T")) {
            return expire_date.split("T")[0];
        }
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    /**
     * x坐标
     *
     * @param context
     * @return
     */
    public float getX(Context context) {
        if (position == null) {
            return 0;
        }
        return position.getX() * getInitSize(context);
    }

    /**
     * Y坐标
     *
     * @param context
     * @return
     */
    public float getY(Context context) {
        if (position == null) {
            return 0;
        }
        return position.getY() * getInitSize(context);
    }

    /**
     * 获取长度
     *
     * @param context
     * @return
     */
    public float getWidth(Context context) {
        if (scale == null) {
            return getInitSize(context);
        }
        return scale.getX() * getInitSize(context);
    }

    /**
     * 获取高度
     *
     * @param context
     * @return
     */
    public float getHeight(Context context) {
        if (scale == null) {
            return getInitSize(context);
        }
        return scale.getY() * getInitSize(context);
    }

    /**
     * 设置高度
     *
     * @param context
     * @param height
     */
    public void setHeight(Context context, int height) {
        if (scale == null) {
            scale = new Point();
        }
        scale.setY(height / getInitSize(context));
    }

    /**
     * 设置宽度
     *
     * @param context
     * @param width
     */
    public void setWidth(Context context, int width) {
        if (scale == null) {
            scale = new Point();
        }
        scale.setX(width / getInitSize(context));
    }

    /**
     * @param x
     * @param y
     */
    public void setPosition(Context context, float x, float y) {
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        x = x > BaseViewActivity.getScreenWidth(((Activity) context)) - getWidth(context) ? BaseViewActivity
                .getScreenWidth(((Activity) context)) - getWidth(context) : x;
        if (position == null) {
            position = new Point();
        }
        x = x / getInitSize(context);
        y = y / getInitSize(context);
        position.setX(x);
        position.setY(y);
    }

    public Point getLeftTopBasePoint() {
        return position;
    }

    public Point getRightTopBasePoint() {
        Point point = new Point();
        point.setX(position.getX() + (scale.getX() - 1));
        point.setY(position.getY());
        return point;
    }

    public Point getLeftBottomBasePoint() {
        Point point = new Point();
        point.setY(position.getY() + (scale.getY() - 1));
        point.setX(position.getX());
        return point;
    }

    public Point getRightBottomBasePoint() {
        Point point = new Point();
        point.setY(position.getY() + (scale.getY() - 1));
        point.setX(position.getX() + (scale.getX() - 1));
        return point;
    }

    public static class CoordinatesBean implements Serializable {
        private int x;
        private int y;
        private String _id;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }

    public static class Point implements Serializable {
        private float x;
        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    public boolean isEmpty() {
        if (star > 0) {
            return false;
        } else if (object_count > 0) {
            return false;
        } else if (!TextUtils.isEmpty(created_at)) {
            return false;
        } else if (!TextUtils.isEmpty(deleted_at)) {
            return false;
        } else if (categories != null && categories.size() > 0) {
            return false;
        } else if (!TextUtils.isEmpty(price)) {
            return false;
        } else if (!TextUtils.isEmpty(price)) {
            return false;
        } else if (colors != null && colors.size() > 0) {
            return false;
        } else if (!TextUtils.isEmpty(season)) {
            return false;
        } else if (channels != null && channels.size() > 0) {
            return false;
        } else if (!TextUtils.isEmpty(detail)) {
            return false;
        } else if (!TextUtils.isEmpty(channel)) {
            return false;
        } else if (!TextUtils.isEmpty(color)) {
            return false;
        } else {
            return true;
        }
    }

    public List<File> getObjectFiles() {
        File file = new File(object_url);
        List<File> files = new ArrayList<>();
        files.add(file);
        return files;
    }

    public File getObjectFile() {
        File file = new File(object_url);
        return file;
    }

}
