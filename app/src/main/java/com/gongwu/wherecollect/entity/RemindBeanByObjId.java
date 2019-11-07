package com.gongwu.wherecollect.entity;


public class RemindBeanByObjId {
    private boolean timeout;
    private String _id;
    private String title;
    private String tips_time;
    private int repeat;
    private int first;
    private int done;
    private String associated_object_url;
    private String associated_object_id;
    private String description;

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTips_time() {
        return tips_time;
    }

    public void setTips_time(String tips_time) {
        this.tips_time = tips_time;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public String getAssociated_object_url() {
        return associated_object_url;
    }

    public void setAssociated_object_url(String associated_object_url) {
        this.associated_object_url = associated_object_url;
    }

    public String getAssociated_object_id() {
        return associated_object_id;
    }

    public void setAssociated_object_id(String associated_object_id) {
        this.associated_object_id = associated_object_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
