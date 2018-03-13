package com.gongwu.wherecollect.entity;
import org.litepal.crud.DataSupport;
/**
 * Function:
 * Date: 2017/9/12
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class SerchBean extends DataSupport {
    private String title;
    private long upDateTime;//更新时间戳

    public long getUpDateTime() {
        return upDateTime;
    }

    public void setUpDateTime(long upDateTime) {
        this.upDateTime = upDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
