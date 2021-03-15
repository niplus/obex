package com.fota.android.moudles.mine.identity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.exception.ApiException;
import com.fota.android.commonlib.http.rx.CommonSubscriber;
import com.fota.android.commonlib.http.rx.CommonTransformer;
import com.fota.android.commonlib.http.rx.NothingTransformer;
import com.fota.android.commonlib.utils.L;
import com.fota.android.commonlib.utils.NetworkUtil;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.MvpFragment;
import com.fota.android.core.base.SimpleFragmentActivity;
import com.fota.android.core.event.Event;
import com.fota.android.databinding.FragmentIdentityBinding;
import com.fota.android.http.Http;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.moudles.mine.identity.imageloader.GlideImageLoader;
import com.fota.android.moudles.mine.login.bean.CounrtyAreasBean;
import com.fota.android.utils.KeyBoardUtils;
import com.fota.android.utils.StringFormatUtils;
import com.google.gson.JsonObject;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 身份认证
 */
public class IdentityFragment extends MvpFragment<IdentityPresenter> implements IIdentityView, View.OnClickListener {

    FragmentIdentityBinding mBinding;
    //    ImagePicker imagePicker = new ImagePicker();
    PopupWindow pop;
    ArrayList<ImageItem> images = null;
    private int RESULT_CODE = 1000;
    private int TYPE_FRONT = 0;
    private int TYPE_BACK = 1;
    private int TYPE = TYPE_FRONT;

    private String path_front = "";
    private String path_back = "";
    private String path_front_url = "";//服务端存储地址
    private String path_back_url = "";
    CounrtyAreasBean.Area countryArea = null;
    private int checkStatus = 0;


    @Override
    protected IdentityPresenter createPresenter() {
        return new IdentityPresenter(this);
    }

    @Override
    protected View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_identity, container, false);
        mBinding.setView(this);
        return mBinding.getRoot();
    }

    @Override
    protected void onInitView(View view) {
        super.onInitView(view);
        getMindeMsg();
        if (checkStatus == 1 || checkStatus == 4) {
            mBinding.llChecking.setVisibility(View.VISIBLE);
            mBinding.llMain.setVisibility(View.GONE);
        } else if (checkStatus == 3) {
            mBinding.llCheckfail.setVisibility(View.VISIBLE);
            mBinding.llMain.setVisibility(View.GONE);
        } else if (checkStatus == 2) {//已完成
            showToast(R.string.safesetting_ident_over);
            finish();
        }
        mBinding.imvFront.setOnClickListener(this);
        mBinding.imvBack.setOnClickListener(this);
        mBinding.btnSubmit.setOnClickListener(this);
        mBinding.tvCountry.setOnClickListener(this);
        mBinding.imvDelFront.setOnClickListener(this);
        mBinding.imvDelBack.setOnClickListener(this);
        mBinding.tvRecheck.setOnClickListener(this);
        mBinding.rlFullname.setOnClickListener(this);
        mBinding.rlId.setOnClickListener(this);
        initPicker();
        countryArea = new CounrtyAreasBean.Area();
        countryArea.setCode("86");
        countryArea.setName_en("China");
        countryArea.setName_zh("中国");
        countryArea.setKey("CN");
        mBinding.tvCountry.setText(countryArea.getName_zh());
        bindValid(mBinding.btnSubmit, mBinding.edtFullname, mBinding.edtName, mBinding.edtSurname, mBinding.edtIdNo);
        valid();
        edtFocus(mBinding.edtFullname, mBinding.viewFullname);
        edtFocus(mBinding.edtSurname, mBinding.viewShortname);
        edtFocus(mBinding.edtName, mBinding.viewName);
        edtFocus(mBinding.edtIdNo, mBinding.viewId);
        if (AppConfigs.getTheme() == 0) {
            mBinding.tvTip.setTextColor(getColor(R.color.googletips_black));
        } else {
            mBinding.tvTip.setTextColor(getColor(R.color.idtips_white));
        }
    }

    @Override
    protected void onInitData(Bundle bundle) {
        super.onInitData(bundle);
//        证件审核状态 0-未审核 1-审核中 2-审核通过 3-审核失败
        checkStatus = bundle.getInt("cardCheckStatus", 0);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_front:
                TYPE = TYPE_FRONT;
                dealCheckPic();
                break;
            case R.id.imv_back:
                TYPE = TYPE_BACK;
                dealCheckPic();
                break;
            case R.id.btn_submit:
                submitClick();
                break;
            case R.id.tv_country:
                Bundle bundle = new Bundle();
                bundle.putString("from", "identity");
                SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.CheckCountryFragment, bundle);
                break;
            case R.id.tv_camera:
                Intent intent = new Intent(mContext, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                startActivityForResult(intent, RESULT_CODE);
                pop.dismiss();
                break;
            case R.id.tv_gallery:
                Intent intent2 = new Intent(mContext, ImageGridActivity.class);
                intent2.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                //ImagePicker.getInstance().setSelectedImages(images);
                startActivityForResult(intent2, RESULT_CODE);
                pop.dismiss();
                break;
            case R.id.tv_cancel:
                pop.dismiss();
                break;
            case R.id.rl_popup:
                pop.dismiss();
                break;
            case R.id.imv_del_back:
                mBinding.imvBack.setImageDrawable(mContext.getResources().getDrawable(Pub.getThemeResource(mContext, R.attr.id_back)));
                path_back_url = "";
                mBinding.imvDelBack.setVisibility(View.GONE);
                mBinding.imvBack.setClickable(true);
                mBinding.tvBack.setVisibility(View.VISIBLE);
                valid();
                break;
            case R.id.imv_del_front:
                mBinding.imvFront.setImageDrawable(mContext.getResources().getDrawable(Pub.getThemeResource(mContext, R.attr.id_front)));
                path_front_url = "";
                mBinding.imvDelFront.setVisibility(View.GONE);
                mBinding.imvFront.setClickable(true);
                mBinding.tvFront.setVisibility(View.VISIBLE);
                valid();
                break;
            case R.id.tv_recheck:
                mBinding.llMain.setVisibility(View.VISIBLE);
                mBinding.llCheckfail.setVisibility(View.GONE);
                break;
            case R.id.rl_fullname:
                KeyBoardUtils.showSoftInputFromWindow(mContext, mBinding.edtFullname);
                break;
            case R.id.rl_id:
                KeyBoardUtils.showSoftInputFromWindow(mContext, mBinding.edtIdNo);
                break;
        }

    }

    private void submitClick() {
        String fullname = mBinding.edtFullname.getText().toString().trim();
        String firstname = mBinding.edtSurname.getText().toString().trim();
        String name = mBinding.edtName.getText().toString().trim();
        String idno = mBinding.edtIdNo.getText().toString().replace(" ", "");
        firstname = "CN".equals(countryArea.getKey()) ? fullname : firstname;
        name = "CN".equals(countryArea.getKey()) ? "" : name;
//        if (TextUtils.isEmpty(path_front_url)) {
//            showToast(R.string.identity_front_null);
//            return;
//        }
//        if ("CN".equals(countryArea.getKey()) && TextUtils.isEmpty(path_back_url)) {
//            showToast(R.string.identity_back_null);
//            return;
//        }
        if ("CN".equals(countryArea.getKey()) && !StringFormatUtils.isIDCard(idno)) {
            showToast(R.string.identity_idcard_error);
            return;
        }

        getcheckIdent(countryArea.getKey(), firstname, idno, path_front_url, path_back_url, name);

    }

    @Override
    protected String setAppTitle() {
        return mContext.getResources().getString(R.string.identity_title);
    }

    /**
     * 处理选择图片
     */
    private void dealCheckPic() {
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_piccheck, null);
        pop = new PopupWindow(popView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.showAtLocation(popView, Gravity.BOTTOM | Gravity.LEFT, 0, 0);

        TextView tv_camera = popView.findViewById(R.id.tv_camera);
        TextView tv_gallery = popView.findViewById(R.id.tv_gallery);
        TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
        RelativeLayout rl_popup = popView.findViewById(R.id.rl_popup);
        tv_camera.setOnClickListener(this);
        tv_gallery.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rl_popup.setOnClickListener(this);

    }

    private void initPicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
//        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
        imagePicker.setMultiMode(false);//单选模式
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == RESULT_CODE) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                MyAdapter adapter = new MyAdapter(images);
//                gridView.setAdapter(adapter);
                if (images.size() > 0) {
                    if (TYPE == TYPE_FRONT) {
                        if (TextUtils.isEmpty(images.get(0).path))
                            return;
                        ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, images.get(0).path, mBinding.imvFront, 0, 0);
                        path_front = images.get(0).path;
                        Luban.with(mContext)
                                .load(path_front)
                                .ignoreBy(1000)
                                .setTargetDir(getPath())
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        file.getAbsolutePath();
                                        // TODO 压缩成功后调用，返回压缩后的图片文件
                                        uploadPic(file.getAbsolutePath(), path_front);

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // TODO 当压缩过程出现问题时调用
                                    }
                                }).launch();

                    } else if (TYPE == TYPE_BACK) {
                        if (TextUtils.isEmpty(images.get(0).path))
                            return;
                        ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, images.get(0).path, mBinding.imvBack, 0, 0);
                        path_back = images.get(0).path;
                        Luban.with(mContext)
                                .load(path_back)
                                .ignoreBy(1000)
                                .setTargetDir(getPath())
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {
                                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        file.getAbsolutePath();
                                        // TODO 压缩成功后调用，返回压缩后的图片文件
                                        uploadPic(file.getAbsolutePath(), path_back);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // TODO 当压缩过程出现问题时调用
                                    }
                                }).launch();
                    }
                }
            } else {
                showToast("no data");
            }
        }
    }

    /**
     * 上传图片
     *
     * @param file       上传的文件
     * @param path_index 标记是正面还是反面
     */
    private void uploadPic(final String file, final String path_index) {
        if (path_index.equals(path_front)) {
            mBinding.imvDelFront.setVisibility(View.GONE);
            mBinding.llLoadingFront.setVisibility(View.VISIBLE);
            mBinding.tvFront.setVisibility(View.GONE);
        } else if (path_index.equals(path_back)) {
            mBinding.imvDelBack.setVisibility(View.GONE);
            mBinding.llLoadingBack.setVisibility(View.VISIBLE);
            mBinding.tvBack.setVisibility(View.GONE);
        }

        List<File> fileList = new ArrayList<>();
        fileList.add(new File(file));
        List<MultipartBody.Part> partList = NetworkUtil.files2Parts("file", new String[]{file});

        Http.getHttpService().uploadIdPic(partList)
                .compose(new CommonTransformer<String>())
                .subscribe(new CommonSubscriber<String>(this) {
                    @Override
                    public void onNext(String path) {
                        L.a("uploadIdPic = " + path.toString());
//                        showToast("uploadIdPic  suc");
                        if (path_index.equals(path_front)) {
                            if (!TextUtils.isEmpty(path)) {
                                path_front_url = path;
                            }
                            mBinding.imvDelFront.setVisibility(View.VISIBLE);
                            mBinding.llLoadingFront.setVisibility(View.GONE);
                            mBinding.imvFront.setClickable(false);
                        } else if (path_index.equals(path_back)) {
                            if (!TextUtils.isEmpty(path)) {
                                path_back_url = path;
                            }
                            mBinding.imvDelBack.setVisibility(View.VISIBLE);
                            mBinding.llLoadingBack.setVisibility(View.GONE);
                            mBinding.imvBack.setClickable(false);
                        }
                        valid();


                    }

                    @Override
                    protected boolean showLoading() {
                        return false;
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("reset", "uploadIdPic fail fail ---" + e.toString());
                        if (path_index.equals(path_front)) {
                            mBinding.imvDelFront.setVisibility(View.GONE);
                            mBinding.tvFront.setVisibility(View.VISIBLE);
                            mBinding.llLoadingFront.setVisibility(View.GONE);
                            mBinding.imvFront.setImageDrawable(mContext.getResources().getDrawable(Pub.getThemeResource(mContext, R.attr.id_front)));

                        } else if (path_index.equals(path_back)) {
                            mBinding.imvDelBack.setVisibility(View.GONE);
                            mBinding.llLoadingBack.setVisibility(View.GONE);
                            mBinding.tvBack.setVisibility(View.VISIBLE);
                            mBinding.imvBack.setImageDrawable(mContext.getResources().getDrawable(Pub.getThemeResource(mContext, R.attr.id_back)));

                        }


                    }
                });

    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }


    /**
     * nation
     * String
     * 用户国籍
     * firstName
     * String
     * 用户的姓
     * idCard
     * String
     * 证件号
     * idCardFrontUrl
     * String
     * 上传的证件正面照片的保存路径
     * idCardBackUrl
     * String
     * 上传的证件反面照片的保存路径
     * lastName
     * String
     * 名
     * cardType
     * String
     * 证件类型。1 为身份证，2为护照。
     */
    private void getcheckIdent(String nation, String firstName, String idCard, String idCardFrontUrl, String idCardBackUrl, String lastName) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nation", nation);
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("idCard", idCard);
        jsonObject.addProperty("idCardFrontUrl", idCardFrontUrl);
        jsonObject.addProperty("idCardBackUrl", idCardBackUrl);
        jsonObject.addProperty("lastName", lastName);
        if ("CN".equals(countryArea.getKey())) {
            jsonObject.addProperty("cardType", "1");
        } else {
            jsonObject.addProperty("cardType", "2");
        }
        Http.getHttpService().identityCheck(jsonObject)
                .compose(new NothingTransformer<BaseHttpEntity>())
                .subscribe(new CommonSubscriber<BaseHttpEntity>(this) {
                    @Override
                    public void onNext(BaseHttpEntity loginBean) {
                        L.a("getcheckIdent suc ---" + loginBean.toString());
//                        showToast(R.string.identity_send_suc);
//                        finish();
                        mBinding.llChecking.setVisibility(View.VISIBLE);
                        mBinding.llMain.setVisibility(View.GONE);

                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                        L.a("getcheckIdent fail ---");

                    }
                });

    }

    @Override
    public boolean eventEnable() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @Override
    public void onEventPosting(Event event) {
        switch (event._id) {
            case R.id.event_identity_countrycheck:
                CounrtyAreasBean.Area a = event.getParam(CounrtyAreasBean.Area.class);
                if (a != null) {
                    countryArea = a;
                }
                if ("CN".equals(countryArea.getKey())) {
                    mBinding.rlSurandname.setVisibility(View.GONE);
                    mBinding.llFullname.setVisibility(View.VISIBLE);
                    mBinding.edtFullname.setVisibility(View.VISIBLE);
                    mBinding.edtName.setVisibility(View.GONE);
                    mBinding.edtSurname.setVisibility(View.GONE);
                    mBinding.tvFront.setText(R.string.identity_front_msg_id);
                    mBinding.tvBack.setText(R.string.identity_back_msg_id);
                    mBinding.rlBackParent.setVisibility(View.VISIBLE);
                    mBinding.tvId.setText(R.string.identity_typename_id);
                    mBinding.tvUploadmsg.setText(R.string.identity_pic_id);
                    mBinding.tvFront.setText(R.string.identity_front_msg_id);
                } else {
                    mBinding.rlSurandname.setVisibility(View.VISIBLE);
                    mBinding.llFullname.setVisibility(View.GONE);
                    mBinding.edtFullname.setVisibility(View.GONE);
                    mBinding.edtName.setVisibility(View.VISIBLE);
                    mBinding.edtSurname.setVisibility(View.VISIBLE);
                    mBinding.tvFront.setText(R.string.identity_front_msg_id);
//                    mBinding.tvBack.setText(R.string.identity_back_msg_huzhao);
                    mBinding.rlBackParent.setVisibility(View.GONE);
                    mBinding.tvId.setText(R.string.identity_typename_passport);
                    mBinding.tvUploadmsg.setText(R.string.identity_pic_passport);
                    mBinding.tvFront.setText(R.string.identity_front_msg_huzhao);

                }
                mBinding.imvBack.setImageDrawable(mContext.getResources().getDrawable(Pub.getThemeResource(mContext, R.attr.id_back)));
                path_back_url = "";
                mBinding.imvDelBack.setVisibility(View.GONE);
                mBinding.imvBack.setClickable(true);
                mBinding.tvBack.setVisibility(View.VISIBLE);
                mBinding.imvFront.setImageDrawable(mContext.getResources().getDrawable(Pub.getThemeResource(mContext, R.attr.id_front)));
                path_front_url = "";
                mBinding.imvDelFront.setVisibility(View.GONE);
                mBinding.imvFront.setClickable(true);
                mBinding.tvFront.setVisibility(View.VISIBLE);
                valid();

                bindValid(mBinding.btnSubmit, mBinding.edtFullname, mBinding.edtName, mBinding.edtSurname, mBinding.edtIdNo);
                valid();
                mBinding.tvCountry.setText(countryArea.getName_zh());
                break;
        }
    }

    @Override
    protected boolean viewGroupFocused() {
        return false;
    }

    /**
     * 获取我的数据
     */
    public void getMindeMsg() {
        Http.getHttpService().getMineData()
                .compose(new CommonTransformer<MineBean>())
                .subscribe(new CommonSubscriber<MineBean>(FotaApplication.getInstance()) {
                    @Override
                    public void onNext(MineBean mineBean) {
                        if (getView() == null) {
                            return;
                        }

                        if (mineBean != null && mineBean.getUserSecurity() != null) {
                            if (mineBean.getUserSecurity().getCardCheckStatus() == 1 || mineBean.getUserSecurity().getCardCheckStatus() == 4) {
                                mBinding.llChecking.setVisibility(View.VISIBLE);
                                mBinding.llMain.setVisibility(View.GONE);
                            } else if (mineBean.getUserSecurity().getCardCheckStatus() == 3) {
                                mBinding.llCheckfail.setVisibility(View.VISIBLE);
                                mBinding.llMain.setVisibility(View.GONE);
                            } else if (mineBean.getUserSecurity().getCardCheckStatus() == 2) {//已完成
                                showToast(R.string.safesetting_ident_over);
                                finish();
                            }
                        }
                    }

                    @Override
                    protected void onError(ApiException e) {
                        super.onError(e);
                    }

                });
    }

    /**
     * 用户补充认证
     *
     * @return
     */
    @Override
    protected boolean customerValid() {
        boolean unable = false;
        if ("CN".equals(countryArea.getKey())) {
            unable = (!TextUtils.isEmpty(path_front_url)) && (!TextUtils.isEmpty(path_back_url));
        } else {
            unable = (!TextUtils.isEmpty(path_front_url));
        }
        return unable;
    }


}
