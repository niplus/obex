package com.fota.android.moudles.main;

import com.fota.android.commonlib.utils.UrlParse;

import java.io.Serializable;

public class BottomMenuItem implements Serializable {

    private int position;
    private String code;
    private String nameZh;
    private String url;
    private String iconUrlBlack;
    private String selectIconUrlBlack;
    private String remarks;
    private String nameEn;
    private String iconUrlWhite;
    private String selectIconUrlWhite;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getPath() {
        return UrlParse.getUrlHostAndPath(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrlBlack() {
        return iconUrlBlack;
    }

    public void setIconUrlBlack(String iconUrlBlack) {
        this.iconUrlBlack = iconUrlBlack;
    }

    public String getSelectIconUrlBlack() {
        return selectIconUrlBlack;
    }

    public void setSelectIconUrlBlack(String selectIconUrlBlack) {
        this.selectIconUrlBlack = selectIconUrlBlack;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getIconUrlWhite() {
        return iconUrlWhite;
    }

    public void setIconUrlWhite(String iconUrlWhite) {
        this.iconUrlWhite = iconUrlWhite;
    }

    public String getSelectIconUrlWhite() {
        return selectIconUrlWhite;
    }

    public void setSelectIconUrlWhite(String selectIconUrlWhite) {
        this.selectIconUrlWhite = selectIconUrlWhite;
    }
}
