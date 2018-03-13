package com.gongwu.wherecollect.entity;
import java.util.List;
/**
 * Function:
 * Date: 2018/1/23
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class QuickAddRequest {
    /**
     * furnitures : ["CD3F738A99C38393E5774754DD39B81D"]
     * code : EBDC8CB7C185ABDC35C527EE4B0F656E
     * name : 客厅
     */
    private String code;
    private String name;
    private List<String> furnitures;

    public QuickAddRequest(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFurnitures() {
        return furnitures;
    }

    public void setFurnitures(List<String> furnitures) {
        this.furnitures = furnitures;
    }
}
