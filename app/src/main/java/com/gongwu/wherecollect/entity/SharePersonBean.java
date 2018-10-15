package com.gongwu.wherecollect.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/10/12.
 */

public class SharePersonBean implements Serializable {
    private String avatar;
    private String id;
    private String uid;
    private String usid;
    private String search_user_id;
    private boolean valid;
    private String nickname;
    private List<SharedLocationBean> shared_locations;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsid() {
        return usid;
    }

    public void setUsid(String usid) {
        this.usid = usid;
    }

    public String getSearch_user_id() {
        return search_user_id;
    }

    public void setSearch_user_id(String search_user_id) {
        this.search_user_id = search_user_id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<SharedLocationBean> getShared_locations() {
        return shared_locations;
    }

    public void setShared_locations(List<SharedLocationBean> shared_locations) {
        this.shared_locations = shared_locations;
    }
}
