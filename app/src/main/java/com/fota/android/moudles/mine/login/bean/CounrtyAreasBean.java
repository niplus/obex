package com.fota.android.moudles.mine.login.bean;
//"arets": [{
//        "name_en": "Angola",
//        "name_zh": "安哥拉",
//        "key": "AO",
//        "code": "244"
//        },

import com.tencent.mmkv.MMKV;

import java.io.Serializable;
import java.util.List;

/**
 * 国家列表
 */
public class CounrtyAreasBean implements Serializable {
    private List<Area> areas;

    public static class Area implements Serializable {
        private String name_en;
        private String name_zh;
        private String name_tw;
        private String key;
        private String code;
        private String letters;

        public String getName_en() {
            return name_en;
        }

        public void setName_en(String name_en) {
            this.name_en = name_en;
        }

        public String getName_zh() {
            switch (MMKV.defaultMMKV().decodeString("language", "")){
                case "en":
                    return name_en;
                case "tw":
                    return name_tw;
                default:
                    return name_zh;
            }

        }

        public void setName_zh(String name_zh) {
            this.name_zh = name_zh;
        }
        public void setName_tw(String name_tw) {
            this.name_tw = name_tw;
        }



        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLetters() {
            return letters;
        }

        public void setLetters(String letters) {
            this.letters = letters;
        }

        @Override
        public String toString() {
            return "Area{" +
                    "name_en='" + name_en + '\'' +
                    ", name_zh='" + name_zh + '\'' +
                    ", key='" + key + '\'' +
                    ", code='" + code + '\'' +
                    ", letters='" + letters + '\'' +
                    '}';
        }
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    @Override
    public String toString() {
        return "CounrtyAreasBean{" +
                "areas=" + areas +
                '}';
    }
}
