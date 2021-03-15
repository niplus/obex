package com.fota.android.common.bean.home;

import java.io.Serializable;

/**
 * 轮播图
 */
//{
//        "pictureStoregedUrl": "http://pic1.win4000.com/pic/d/8d/9c5cc844bb.jpg",
//        "hyperlink": "http://www.win4000.com/meinv158787.html"
//        }
public class BannerBean implements Serializable {
        private String pictureStoregedUrl;
        private String hyperlink;

        public String getPictureStoregedUrl() {
            return pictureStoregedUrl;
        }

        public void setPictureStoregedUrl(String pictureStoregedUrl) {
            this.pictureStoregedUrl = pictureStoregedUrl;
        }

        public String getHyperlink() {
            return hyperlink;
        }

        public void setHyperlink(String hyperlink) {
            this.hyperlink = hyperlink;
        }

        @Override
        public String toString() {
            return "Banner{" +
                    "pictureStoregedUrl='" + pictureStoregedUrl + '\'' +
                    ", hyperlink='" + hyperlink + '\'' +
                    '}';
        }

}
