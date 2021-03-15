package com.fota.android.utils.apputils;

import android.content.Context;

import com.fota.android.R;
import com.fota.android.utils.UserLoginUtil;

import java.util.HashMap;
import java.util.Map;

import cn.udesk.config.UdeskConfig;
import udesk.core.UdeskConst;

public class CustomerServiceUtils {

    public static UdeskConfig.Builder getUdeskConfigBuild(String sdktoken, Context context) {
        Map<String, String> info = new HashMap<String, String>();
        info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN, sdktoken);
        //以下信息是可选
        info.put(UdeskConst.UdeskUserInfo.NICK_NAME, "-");
        info.put(UdeskConst.UdeskUserInfo.EMAIL, UserLoginUtil.getEmail());
        info.put(UdeskConst.UdeskUserInfo.CELLPHONE, UserLoginUtil.getPhone());
        info.put(UdeskConst.UdeskUserInfo.DESCRIPTION, "-");
        UdeskConfig.Builder builder = new UdeskConfig.Builder();
        builder.setUdeskTitlebarBgResId(R.color.main_color_white) //设置标题栏TitleBar的背景色
                .setUdeskTitlebarTextLeftRightResId(R.color.udesk_color_navi_text1) //设置标题栏TitleBar，左右两侧文字的颜色
                .setUdeskIMLeftTextColorResId(R.color.udesk_color_im_text_left1) //设置IM界面，左侧文字的字体颜色
                .setUdeskIMRightTextColorResId(R.color.udesk_color_im_text_right1) // 设置IM界面，右侧文字的字体颜色
                .setUdeskIMAgentNickNameColorResId(R.color.udesk_color_im_left_nickname1) //设置IM界面，左侧客服昵称文字的字体颜色
                .setUdeskIMTimeTextColorResId(R.color.udesk_color_im_time_text1) // 设置IM界面，时间文字的字体颜色
                .setUdeskIMTipTextColorResId(R.color.udesk_color_im_tip_text1) //设置IM界面，提示语文字的字体颜色，比如客服转移
                .setUdeskbackArrowIconResId(R.drawable.udesk_titlebar_back) // 设置返回箭头图标资源id
                .setUdeskCommityBgResId(R.color.udesk_color_im_commondity_bg1) //咨询商品item的背景颜色
                .setUdeskCommityTitleColorResId(R.color.udesk_color_im_commondity_title1) // 商品介绍Title的字样颜色
                .setUdeskCommitysubtitleColorResId(R.color.udesk_color_im_commondity_subtitle1)// 商品咨询页面中，商品介绍子Title的字样颜色
                .setUdeskCommityLinkColorResId(R.color.udesk_color_im_commondity_link1) //商品咨询页面中，发送链接的字样颜色
                .setUserSDkPush(true) // 配置 是否使用推送服务  true 表示使用  false表示不使用
                .setOnlyUseRobot(false)//配置是否只使用机器人功能 只使用机器人功能,只使用机器人功能;  其它功能不使用。
                .setUdeskQuenuMode(false ? UdeskConfig.UdeskQuenuFlag.FORCE_QUIT : UdeskConfig.UdeskQuenuFlag.Mark)  //  配置放弃排队的策略
                .setUseVoice(true) // 是否使用录音功能  true表示使用 false表示不使用
                .setUsephoto(true) //是否使用发送图片的功能  true表示使用 false表示不使用
                .setUsecamera(true) //是否使用拍照的功能  true表示使用 false表示不使用
                .setUsefile(true) //是否使用上传文件功能  true表示使用 false表示不使用
                .setUseEmotion(true) //是否使用表情 true表示使用 false表示不使用
                .setUseMore(true) // 是否使用更多控件 展示出更多功能选项 true表示使用 false表示不使用
                .setUseNavigationSurvy(true) //设置是否使用导航UI中的满意度评价UI rue表示使用 false表示不使用
                .setUseSmallVideo(false)  //设置是否需要小视频的功能 rue表示使用 false表示不使用
                .setScaleImg(false) //上传图片是否使用原图 还是缩率图
                .setScaleMax(1024) // 缩放图 设置最大值，如果超出则压缩，否则不压缩
                .setOrientation(UdeskConfig.OrientationValue.portrait) //设置默认屏幕显示习惯
                .setUserForm(true) //在没有请求到管理员在后端对sdk使用配置下，在默认的情况下，是否需要表单留言，true需要， false 不需要
        ;

        return builder;
    }
}
