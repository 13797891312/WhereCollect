package com.gongwu.wherecollect.entity;
import java.util.ArrayList;
import java.util.List;
/**
 * Function:
 * Date: 2017/10/28
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class FilterCategoryBean {
    /**
     * sub : [{"query":"{\"color\":{\"$regex\":\"粉色\"}}","object_count":3,"code":"d6387d0a7d0f41039ed8f61622263f40",
     * "name":"粉色"},{"query":"{\"color\":{\"$regex\":\"棕色\"}}","object_count":1,
     * "code":"1ca887f62ba3511730d49a2db7cb73dd","name":"棕色"},{"query":"{\"color\":{\"$regex\":\"红色\"}}",
     * "object_count":2,"code":"6c12331a2b140ba0905f817204679b28","name":"红色"},
     * {"query":"{\"color\":{\"$regex\":\"黄色\"}}","object_count":2,"code":"3470f003a30fe74ff47aa06f739d9aae",
     * "name":"黄色"},{"query":"{\"color\":{\"$regex\":\"白色\"}}","object_count":3,
     * "code":"7b83a8d44389a1e33aec910f70813c5f","name":"白色"},{"query":"{\"color\":{\"$regex\":\"黑色\"}}",
     * "object_count":5,"code":"ee01c47838055275a6077a539cf5832a","name":"黑色"},
     * {"query":"{\"color\":{\"$regex\":\"蓝色\"}}","object_count":2,"code":"8ea3177c981b16d1e4b1a46309be702e",
     * "name":"蓝色"},{"query":"{\"color\":{\"$regex\":\"紫色\"}}","object_count":2,
     * "code":"58cb33f96fa52187dc65f50e372fb61c","name":"紫色"},{"query":"{\"color\":{\"$regex\":\"灰色\"}}",
     * "object_count":1,"code":"64e1e1cbe1ca8e88ef3a838a3e7b57d6","name":"灰色"}]
     * icon : http://7xroa4.com1.z0.glb.clouddn.com/a1ba55801f6897078f269ce83ce37ed0.png
     * query : {"$or":[{"color":{"$regex":"黑色"}},{"color":{"$regex":"白色"}},{"color":{"$regex":"红色"}},
     * {"color":{"$regex":"黄色"}},{"color":{"$regex":"绿色"}},{"color":{"$regex":"蓝色"}},{"color":{"$regex":"紫色"}},
     * {"color":{"$regex":"金色"}},{"color":{"$regex":"银色"}},{"color":{"$regex":"棕色"}},{"color":{"$regex":"橙色"}},
     * {"color":{"$regex":"粉色"}},{"color":{"$regex":"灰色"}},{"color":{"$regex":"米色"}},{"color":{"$regex":"驼色"}},
     * {"color":{"$regex":"香槟色"}},{"color":{"$regex":"卡其色"}}]}
     * user_id : 59f4162c26288e16ce92d73c
     * code : 6A1F44716BC0DF540BBCB03951B89D8B
     * name : 颜色
     */
    private String icon;
    private String query;
    private String user_id;
    private String code;
    private String name;
    private List<SubBean> selectSubs = new ArrayList<>();//选择的子节点
    /**
     * query : {"color":{"$regex":"粉色"}}
     * object_count : 3
     * code : d6387d0a7d0f41039ed8f61622263f40
     * name : 粉色
     */
    private List<SubBean> sub;

    public List<SubBean> getSelectSubs() {
        return selectSubs;
    }

    public void setSelectSubs(List<SubBean> selectSubs) {
        this.selectSubs = selectSubs;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubBean> getSub() {
        return sub;
    }

    public void setSub(List<SubBean> sub) {
        this.sub = sub;
    }

    public static class SubBean {
        private String query;
        private int object_count;
        private String code;
        private String name;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public int getObject_count() {
            return object_count;
        }

        public void setObject_count(int object_count) {
            this.object_count = object_count;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
