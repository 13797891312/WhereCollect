package com.gongwu.wherecollect.entity;
import java.io.Serializable;
/**
 * Function:
 * Date: 2017/10/25
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class BaseBean implements Serializable {
    protected String name;
    protected String code;
    protected int level;
    protected String _id;

    public BaseBean() {
    }

    public BaseBean(String name, String code, int level, String _id) {
        this.name = name;
        this.code = code;
        this.level = level;
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
