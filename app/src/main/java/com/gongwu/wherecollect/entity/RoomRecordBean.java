package com.gongwu.wherecollect.entity;
import java.io.Serializable;
/**
 * Function:
 * Date: 2018/1/22
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class RoomRecordBean implements Serializable{
    /**
     * __v : 0
     * name : 6
     * snapshot_url : http://7xroa4.com1.z0.glb.clouddn.com/roomrecord/-15165921050471516592104817snowp.jpg
     * image_url : http://7xroa4.com1.z0.glb.clouddn.com/roomrecord/-15165921050471516592104726.jpg
     * change_tex : 说两句
     * user_id : 5a5f6c3ffa2a404424117f35
     * _id : 5a655bfd098a9a0abaf8aeaa
     * created_at : 2018-01-22 11:35:25
     * view_count : 0
     */
    private String name;
    private String snapshot_url;
    private String image_url;
    private String change_tex;
    private String created_at;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnapshot_url() {
        return snapshot_url;
    }

    public void setSnapshot_url(String snapshot_url) {
        this.snapshot_url = snapshot_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getChange_tex() {
        return change_tex;
    }

    public void setChange_tex(String change_tex) {
        this.change_tex = change_tex;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
