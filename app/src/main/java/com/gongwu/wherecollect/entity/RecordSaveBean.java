package com.gongwu.wherecollect.entity;
/**
 * Function:室迹暂存类
 * Date: 2018/1/22
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class RecordSaveBean {
    String file1;
    String file2;
    String memo;

    public RecordSaveBean(String file1, String file2, String memo) {
        this.file1 = file1;
        this.file2 = file2;
        this.memo = memo;
    }

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
