package com.gongwu.wherecollect.entity;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
/**
 * Function:
 * Date: 2017/11/8
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class BookBean implements Serializable{
    /**
     * title : 移动app测试的22条军规
     * pic : https://img3.doubanio.com/lpic/s28270425.jpg
     * price : 49
     * author_intro : 黄勇，现任ThoughtWorks中国区QA
     * Lead。先后任职于博彦科技，普华永道GTS和ThoughtWorks；作为资深质量分析师，先后工作在Oracle，微软，普华永道，澳洲电信等多家公司的大型项目上。
     * summary : 本书全面讲解了移动App测试的技术、技巧、工具、案例和测试用例，全书共分23章，主要内容为：移动App
     * 的特性，关注多任务和意外情况处理，避免手势冲突，关注用户体验，设计通知和消息展示，支持操作系统特性，及时显示和同步消息，支持多种文件格式，支持多语言和地区设置，重点测试高内存占用的功能、降低流量和电量消耗，确保成功集成和调用第三方App，尽量不使用非标准控件，iOS 8升级所引入的新特性，Android 5.0升级所引入的新特性，自动化和探索性测试，自动化测试中模拟器的使用，用户界面自动化测试的常见工具，性能和安全性测试，使用Log定位问题，充分使用持续集成、持续部署，以及微信App测试综合案例分析等核心技术。
     本书适合软件的测试初学者、测试从业人员及程序员阅读，也可以作为大专院校相关专业师生的学习用书，以及培训学校的教材。
     * isbnCode : 9787115394965
     * category : {"code":"B52642DEA13A35513B29B049C5A4F4F7","name":"图书","level":0}
     */
    private String title;
    private String pic;
    private String price;
    private String author_intro;
    private String summary;
    private String isbnCode;
    private File imageFile;
    private String channel;
    /**
     * code : B52642DEA13A35513B29B049C5A4F4F7
     * name : 图书
     * level : 0
     */
    private BaseBean category;

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPrice() {
//        if(TextUtils.isEmpty(price)){
//            return 0;
//        }else{
//            try {
//                return Float.parseFloat(price.split("元")[0]);
//            }catch (Exception e){
//                e.toString();
//                return 0;
//            }
//        }
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAuthor_intro() {
        return author_intro;
    }

    public void setAuthor_intro(String author_intro) {
        this.author_intro = author_intro;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIsbnCode() {
        return isbnCode;
    }

    public void setIsbnCode(String isbnCode) {
        this.isbnCode = isbnCode;
    }

    public BaseBean getCategory() {
        return category;
    }

    public void setCategory(BaseBean category) {
        this.category = category;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
