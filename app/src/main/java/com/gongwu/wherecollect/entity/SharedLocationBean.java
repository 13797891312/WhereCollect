package com.gongwu.wherecollect.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/10/15.
 */

public class SharedLocationBean implements Serializable {
    private String id;
    private String code;
    private String user_id;
    private String name;
    private boolean is_shared;
    private SharePersonBean user;
    private List<SharePersonBean> shared_users;
    private int level;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean is_shared() {
        return is_shared;
    }

    public void setIs_shared(boolean is_shared) {
        this.is_shared = is_shared;
    }

    public SharePersonBean getUser() {
        return user;
    }

    public void setUser(SharePersonBean user) {
        this.user = user;
    }

    public List<SharePersonBean> getShared_users() {
        return shared_users;
    }

    public void setShared_users(List<SharePersonBean> shared_users) {
        this.shared_users = shared_users;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
