package com.gongwu.wherecollect.entity;
import android.text.TextUtils;

import com.gongwu.wherecollect.util.StringUtils;

import java.util.List;
/**
 * Function:
 * Date: 2017/10/24
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class ChannelBean extends BaseBean {
    /**
     * _id : 58e35fa82af3cc8846b94fab
     * name : 淘宝
     * level : 2
     * category_code : E99809D2E48031B38BC7872ED038ED40
     * user_id : system_channel
     * code : AAC853356DC24C4061B2BB76EB94E52A
     * __v : 0
     * deleted_at : null
     * updated_at : 2017-04-04T08:56:08.789Z
     * created_at : 2017-04-04T08:56:08.789Z
     * properties : []
     * parents : [{"name":"购买","code":"72544B13939EB68B9A56072C475E605D","level":0,"_id":"58e35fa82af3cc8846b94fa8"},
     * {"name":"网店","code":"E99809D2E48031B38BC7872ED038ED40","level":1,"_id":"58e35fa82af3cc8846b94faa"}]
     * disabled : false
     * black_list : []
     * white_list : []
     * is_user : false
     * object_count : 2818
     * tag_count : 0
     * weight : 0
     */
    private String category_code;
    private String user_id;
    private int __v;
    private String updated_at;
    private String created_at;
    private boolean disabled;
    private boolean is_user;
    private int object_count;
    private int tag_count;
    private int weight;
    /**
     * name : 购买
     * code : 72544B13939EB68B9A56072C475E605D
     * level : 0
     * _id : 58e35fa82af3cc8846b94fa8
     */
    private List<BaseBean> parents;
    private List<ChannelBean> childBeans;
    private List<ChannelBean> parentBeans;
    private ChannelBean parentsBean;

    public ChannelBean getParentsBean() {
        return parentsBean;
    }

    public void setParentsBean(ChannelBean parentsBean) {
        this.parentsBean = parentsBean;
    }

    public List<ChannelBean> getParentBeans() {
        return parentBeans;
    }

    public void setParentBeans(List<ChannelBean> parentBeans) {
        this.parentBeans = parentBeans;
    }

    public List<ChannelBean> getChildBeans() {
        return childBeans;
    }

    public void setChildBeans(List<ChannelBean> childBeans) {
        this.childBeans = childBeans;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCategory_code() {
        return category_code;
    }

    public void setCategory_code(String category_code) {
        this.category_code = category_code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public String getUpdated_at() {
        return updated_at;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isIs_user() {
        return is_user;
    }

    public void setIs_user(boolean is_user) {
        this.is_user = is_user;
    }

    public int getObject_count() {
        return object_count;
    }

    public void setObject_count(int object_count) {
        this.object_count = object_count;
    }

    public int getTag_count() {
        return tag_count;
    }

    public void setTag_count(int tag_count) {
        this.tag_count = tag_count;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<BaseBean> getParents() {
        return parents;
    }

    public void setParents(List<BaseBean> parents) {
        this.parents = parents;
    }

    /**
     * 返回归属字符串
     *
     * @return
     */
    public String getParentsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(getParents()); i++) {
            sb.append(getParents().get(i).getName());
            if (i != getParents().size() - 1) {
                sb.append(">");
            }
        }
        if (TextUtils.isEmpty(sb.toString())) {
            return "自定义";
        }
        return sb.toString();
    }

    /**
     * 转字符串
     *
     * @return
     */
    public String getString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < StringUtils.getListSize(getParents()); i++) {
            sb.append(getParents().get(i).getName());
            sb.append(">");
        }
        sb.append(getName());
        return sb.toString();
    }
}
