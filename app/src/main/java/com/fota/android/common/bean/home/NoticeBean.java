package com.fota.android.common.bean.home;

import java.io.Serializable;

/**
 * 公告列表
 */
public class NoticeBean implements Serializable {
    private String textTitle;
    private String hyperlink;

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    @Override
    public String toString() {
        return "NoticeCenterBean{" +
                "textTitle='" + textTitle + '\'' +
                ", hyperlink='" + hyperlink + '\'' +
                '}';
    }
}
