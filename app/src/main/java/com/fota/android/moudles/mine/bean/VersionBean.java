package com.fota.android.moudles.mine.bean;

import java.io.Serializable;

/**
 * 版本信息
 * isNewest
 * Boolean
 * 1
 * 1是最新，0不是最新
 * version
 * String
 * 1.1
 * 版本号
 * text
 * String
 * 最新上线
 * 版本的说明文案
 * isCompulsory
 * Boolean
 * 1
 * 是否强制更新，1强制，0不强制
 * title
 * String
 * 更新标题
 * url
 * String
 * 应用的下载地址
 */
public class VersionBean implements Serializable {
    private boolean isNewest;
    private String version;
    private String text;
    private boolean isCompulsory;
    private String title;
    private String url;

    public boolean isNewest() {
        return isNewest;
    }

    public void setNewest(boolean newest) {
        isNewest = newest;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompulsory() {
        return isCompulsory;
    }

    public void setCompulsory(boolean compulsory) {
        isCompulsory = compulsory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "isNewest=" + isNewest +
                ", version='" + version + '\'' +
                ", text='" + text + '\'' +
                ", isCompulsory=" + isCompulsory +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
