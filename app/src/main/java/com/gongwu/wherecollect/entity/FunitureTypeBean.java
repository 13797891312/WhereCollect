package com.gongwu.wherecollect.entity;
/**
 * Function:
 * Date: 2018/1/17
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class FunitureTypeBean {
    /**
     * _id : 5a5b65db8397ce18c40b41f0
     * name : 宜家风格
     * __v : 0
     * updated_at : 2018-01-14T14:14:51.092Z
     * created_at : 2018-01-14T14:14:51.092Z
     * level : 0
     */
    private String _id;
    private String name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
