package com.fota.android.moudles.main.bean;

import java.io.Serializable;

//{"content":"content","gotoUrl":"fota:\/\/goto\/MessageCenter","title":"title"}
public class PushBean implements Serializable {
    private String content;
    private String gotoUrl;
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGotoUrl() {
        return gotoUrl;
    }

    public void setGotoUrl(String gotoUrl) {
        this.gotoUrl = gotoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "PushBean{" +
                "content='" + content + '\'' +
                ", gotoUrl='" + gotoUrl + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
