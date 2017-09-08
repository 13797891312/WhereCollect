package com.gongwu.wherecollect.entity;
public class ResponseResult {
    /**
     * head : http://192.168.42.151:8080/ImageServer/head/zhaojin.jpg
     * realName : null
     * identity : {"102":"学生"}
     * mobile : null
     * id : 16081615391225800001
     * userName : zhaojin
     * userId : 10002
     * email : null
     * token :
     */
    private String result;
    /**
     * data : {"head":"http://192.168.42.151:8080/ImageServer/head/zhaojin.jpg","realName":null,
     * "identity":{"102":"学生"},"mobile":null,"id":"16081615391225800001","userName":"zhaojin","userId":"10002",
     * "email":null,"token":""}
     * code : 2000
     * msg : 登录成功
     */
    private String code;
    private String msg;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
