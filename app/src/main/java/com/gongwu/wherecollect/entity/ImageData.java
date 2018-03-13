package com.gongwu.wherecollect.entity;
import java.io.Serializable;
public class ImageData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1842263344482536324L;
    private String url;
    private boolean isSelect;
    private int position;
    private String thumbnailPath;
    private String bigUri;
    private String name;
    private boolean isCamare;

    public boolean isCamare() {
        return isCamare;
    }

    public void setCamare(boolean camare) {
        isCamare = camare;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getBigUri() {
        return bigUri;
    }

    public void setBigUri(String bigUri) {
        this.bigUri = bigUri;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}