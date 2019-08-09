package com.gongwu.wherecollect.entity;


public class RemindDetailsBean {
    private String _id;
    private String title;
    private String description;
    private long tips_time;
    private int first;
    private int repeat;
    private int done;
    private ObjectBean associated_object;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTips_time() {
        return tips_time;
    }

    public void setTips_time(long tips_time) {
        this.tips_time = tips_time;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public ObjectBean getAssociated_object() {
        return associated_object;
    }

    public void setAssociated_object(ObjectBean associated_object) {
        this.associated_object = associated_object;
    }
}
