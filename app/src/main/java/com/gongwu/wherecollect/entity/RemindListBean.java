package com.gongwu.wherecollect.entity;

import java.io.Serializable;
import java.util.List;

public class RemindListBean implements Serializable {
    private List<RemindBean> reminds;
    private int doneCount;
    private int unDoneCount;
    private int outTimeCount;

    public List<RemindBean> getReminds() {
        return reminds;
    }

    public void setReminds(List<RemindBean> reminds) {
        this.reminds = reminds;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public String getDoneCountString() {
        return doneCount + "";
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public int getUnDoneCount() {
        return unDoneCount;
    }

    public String getUnDoneCountString() {
        return unDoneCount + "";
    }

    public void setUnDoneCount(int unDoneCount) {
        this.unDoneCount = unDoneCount;
    }

    public int getOutTimeCount() {
        return outTimeCount;
    }

    public void setOutTimeCount(int outTimeCount) {
        this.outTimeCount = outTimeCount;
    }
}
