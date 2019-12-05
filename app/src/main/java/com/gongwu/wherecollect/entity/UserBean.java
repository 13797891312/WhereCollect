package com.gongwu.wherecollect.entity;

import android.text.TextUtils;

/**
 * Function:
 * Date: 2017/9/6
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class UserBean {
    /**
     * level : 0
     * score : {"total":0,"current":0}
     * category_update : false
     * status : 0
     * tag_count : 0
     * object_count : 0
     * location_count : 0
     * box_count : 0
     * updated_at : 2017-09-06
     * created_at : 2017-09-06
     * loginway : account
     * data_cleaner : false
     * weixin : {}
     * qq : {}
     * sina : {}
     * id : 59af56c04aef762e75c47524
     * gender : 女
     * avatar : http://7xroa4.com1.z0.glb.clouddn.com/default/shounaer_icon.png
     * nickname : 1B0157A
     * mail : 833244802@qq.com
     * birthday
     */
    private int level;
    /**
     * total : 0
     * current : 0
     */
    private ScoreBean score;
    private boolean category_update;
    private int status;
    private int tag_count;
    private int object_count;
    private int location_count;
    private int box_count;
    private String loginway = "";
    private boolean data_cleaner;
    private WeixinBean weixin;
    private QqBean qq;
    private SinaBean sina;
    private String id = "";
    private String _id = "";
    private String gender = "";
    private String avatar = "";
    private String nickname = "";
    private String mail = "";
    private String birthday = "";
    private String mobile = "";
    private String openid;
    private String usid;
    private String max_version;
    private String login_messag;

    private boolean isPassLogin;

    private boolean isTest;

    public String getUsid() {
        return usid;
    }

    public void setUsid(String usid) {
        this.usid = usid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public boolean isPassLogin() {
        return isPassLogin;
    }

    public void setPassLogin(boolean passLogin) {
        isPassLogin = passLogin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isTest() {
        return !TextUtils.isEmpty(_id);
    }

    public void setTestId(String id) {
        this._id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ScoreBean getScore() {
        return score;
    }

    public void setScore(ScoreBean score) {
        this.score = score;
    }

    public boolean isCategory_update() {
        return category_update;
    }

    public void setCategory_update(boolean category_update) {
        this.category_update = category_update;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public boolean getTest() {
        return isTest;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTag_count() {
        return tag_count;
    }

    public void setTag_count(int tag_count) {
        this.tag_count = tag_count;
    }

    public int getObject_count() {
        return object_count;
    }

    public void setObject_count(int object_count) {
        this.object_count = object_count;
    }

    public int getLocation_count() {
        return location_count;
    }

    public void setLocation_count(int location_count) {
        this.location_count = location_count;
    }

    public int getBox_count() {
        return box_count;
    }

    public void setBox_count(int box_count) {
        this.box_count = box_count;
    }

    public String getLoginway() {
        return loginway;
    }

    public void setLoginway(String loginway) {
        this.loginway = loginway;
    }

    public boolean isData_cleaner() {
        return data_cleaner;
    }

    public void setData_cleaner(boolean data_cleaner) {
        this.data_cleaner = data_cleaner;
    }

    public WeixinBean getWeixin() {
        return weixin;
    }

    public void setWeixin(WeixinBean weixin) {
        this.weixin = weixin;
    }

    public QqBean getQq() {
        return qq;
    }

    public void setQq(QqBean qq) {
        this.qq = qq;
    }

    public SinaBean getSina() {
        return sina;
    }

    public void setSina(SinaBean sina) {
        this.sina = sina;
    }

    public String getId() {
        if (TextUtils.isEmpty(id)) {
            return _id;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMax_version() {
        return max_version;
    }

    public void setMax_version(String max_version) {
        this.max_version = max_version;
    }

    public String getLogin_messag() {
        return login_messag;
    }

    public void setLogin_messag(String login_messag) {
        this.login_messag = login_messag;
    }

    public static class ScoreBean {
        private int total;
        private int current;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }
    }

    public static class WeixinBean {
        /**
         * nickname : 呜呜呜呜
         * unionid : o9XfKwflYsmdJZpci3Nh8KYSIKwg
         * openid : oM-4dxJ7Vz3gp6vIDN77_TdAugw8
         */
        private String nickname;
        private String unionid;
        private String openid;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }
    }

    public static class QqBean {
        /**
         * nickname : 赵进
         * openid : 4EE7BFC2FB9494EAACFC5FF3E409A41D
         */
        private String nickname;
        private String openid;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }
    }

    public static class SinaBean {
        private String nickname;
        private String openid;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }
    }
}
