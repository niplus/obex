package com.fota.android.moudles.mine.identity

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fota.android.R
import com.fota.android.app.ConstantsPage
import com.fota.android.app.FotaApplication
import com.fota.android.commonlib.base.AppConfigs
import com.fota.android.commonlib.http.BaseHttpEntity
import com.fota.android.commonlib.http.exception.ApiException
import com.fota.android.commonlib.http.rx.CommonSubscriber
import com.fota.android.commonlib.http.rx.CommonTransformer
import com.fota.android.commonlib.http.rx.NothingTransformer
import com.fota.android.commonlib.utils.L
import com.fota.android.commonlib.utils.Pub
import com.fota.android.core.base.MvpFragment
import com.fota.android.core.base.SimpleFragmentActivity
import com.fota.android.core.event.Event
import com.fota.android.databinding.FragmentIdentityBinding
import com.fota.android.http.Http
import com.fota.android.moudles.mine.bean.MineBean
import com.fota.android.moudles.mine.identity.imageloader.GlideImageLoader
import com.fota.android.moudles.mine.login.bean.CounrtyAreasBean
import com.fota.android.utils.GlideEngine
import com.fota.android.utils.KeyBoardUtils
import com.fota.android.utils.StringFormatUtils
import com.fota.android.utils.saveBitmap2File
import com.google.gson.JsonObject
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.lzy.imagepicker.ImagePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

/**
 * 身份认证
 */
class IdentityFragment : MvpFragment<IdentityPresenter?>(), IIdentityView, View.OnClickListener {
    var mBinding: FragmentIdentityBinding? = null

    //    ImagePicker imagePicker = new ImagePicker();
    var pop: PopupWindow? = null
    private val RESULT_CODE = 1000
    private val TYPE_FRONT = 0
    private val TYPE_BACK = 1
    private val TYPE_HOLD = 2
    private var TYPE = TYPE_FRONT
    private val path_front = ""
    private val path_back = ""
    private val path_hold = ""
    private var path_front_url = "" //服务端存储地址
    private var path_back_url = ""
    private var path_hold_url =""
    var countryArea: CounrtyAreasBean.Area? = null
    private var checkStatus = 0
    override fun createPresenter(): IdentityPresenter {
        return IdentityPresenter(this)
    }

    override fun onCreateFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_identity, container, false)
        mBinding!!.view = this
        return mBinding!!.root
    }

    override fun onInitView(view: View) {
        super.onInitView(view)
        mindeMsg
        if (checkStatus == 1 || checkStatus == 4) {
            mBinding!!.llChecking.visibility = View.VISIBLE
            mBinding!!.llMain.visibility = View.GONE
        } else if (checkStatus == 3) {
            mBinding!!.llCheckfail.visibility = View.VISIBLE
            mBinding!!.llMain.visibility = View.GONE
        } else if (checkStatus == 2) { //已完成
            showToast(R.string.safesetting_ident_over)
            finish()
        }
        mBinding!!.imvFront.setOnClickListener(this)
        mBinding!!.imvBack.setOnClickListener(this)
        mBinding!!.imvHold.setOnClickListener(this)
        mBinding!!.btnSubmit.setOnClickListener(this)
        mBinding!!.tvCountry.setOnClickListener(this)
        mBinding!!.imvDelFront.setOnClickListener(this)
        mBinding!!.imvDelBack.setOnClickListener(this)
        mBinding!!.tvRecheck.setOnClickListener(this)
        mBinding!!.rlFullname.setOnClickListener(this)
        mBinding!!.rlId.setOnClickListener(this)
        initPicker()
        countryArea = CounrtyAreasBean.Area()
        countryArea!!.code = "86"
        countryArea!!.name_en = "China"
        countryArea!!.name_zh = "中国"
        countryArea!!.setName_tw("中國")
        countryArea!!.key = "CN"
        mBinding!!.tvCountry.text = countryArea!!.name_zh
        bindValid(
            mBinding!!.btnSubmit,
            mBinding!!.edtFullname,
            mBinding!!.edtName,
            mBinding!!.edtSurname,
            mBinding!!.edtIdNo
        )
        valid()
        edtFocus(mBinding!!.edtFullname, mBinding!!.viewFullname)
        edtFocus(mBinding!!.edtSurname, mBinding!!.viewShortname)
        edtFocus(mBinding!!.edtName, mBinding!!.viewName)
        edtFocus(mBinding!!.edtIdNo, mBinding!!.viewId)
        if (AppConfigs.getTheme() == 0) {
            mBinding!!.tvTip.setTextColor(getColor(R.color.googletips_black))
        } else {
            mBinding!!.tvTip.setTextColor(getColor(R.color.idtips_white))
        }
    }

    override fun onInitData(bundle: Bundle) {
        super.onInitData(bundle)
        //        证件审核状态 0-未审核 1-审核中 2-审核通过 3-审核失败
        checkStatus = bundle.getInt("cardCheckStatus", 0)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imv_front -> {
                TYPE = TYPE_FRONT
                dealCheckPic()
            }
            R.id.imv_back -> {
                TYPE = TYPE_BACK
                dealCheckPic()
            }
            R.id.imv_hold -> {
                TYPE = TYPE_HOLD
                dealCheckPic()
            }
            R.id.btn_submit -> submitClick()
            R.id.tv_country -> {
                val bundle = Bundle()
                bundle.putString("from", "identity")
                SimpleFragmentActivity.gotoFragmentActivity(
                    context,
                    ConstantsPage.CheckCountryFragment,
                    bundle
                )
            }
            R.id.tv_camera -> {
                EasyPhotos.createCamera(this) //                        .setFileProviderAuthority("com.fota.android.fileprovider")
                    .start(object : SelectCallback() {
                        override fun onResult(
                            photos: ArrayList<Photo>,
                            paths: ArrayList<String>,
                            isOriginal: Boolean
                        ) {
                            onPictureSelect(photos[0].path)
                        }
                    })
                pop!!.dismiss()
            }
            R.id.tv_gallery -> {
                EasyPhotos.createAlbum(this, false, GlideEngine.getInstance())
                    .start(object : SelectCallback() {
                        override fun onResult(
                            photos: ArrayList<Photo>,
                            paths: ArrayList<String>,
                            isOriginal: Boolean
                        ) {
                            onPictureSelect(photos[0].path)
                        }
                    })
                pop!!.dismiss()
            }
            R.id.tv_cancel -> pop!!.dismiss()
            R.id.rl_popup -> pop!!.dismiss()
            R.id.imv_del_back -> {
                mBinding!!.imvBack.setImageDrawable(
                    mContext.resources.getDrawable(
                        Pub.getThemeResource(
                            mContext,
                            R.attr.id_back
                        )
                    )
                )
                path_back_url = ""
                mBinding!!.imvDelBack.visibility = View.GONE
                mBinding!!.imvBack.isClickable = true
                mBinding!!.tvBack.visibility = View.VISIBLE
                valid()
            }
            R.id.imv_del_front -> {
                mBinding!!.imvFront.setImageDrawable(
                    mContext.resources.getDrawable(
                        Pub.getThemeResource(
                            mContext,
                            R.attr.id_front
                        )
                    )
                )
                path_front_url = ""
                mBinding!!.imvDelFront.visibility = View.GONE
                mBinding!!.imvFront.isClickable = true
                mBinding!!.tvFront.visibility = View.VISIBLE
                valid()
            }
            R.id.tv_recheck -> {
                mBinding!!.llMain.visibility = View.VISIBLE
                mBinding!!.llCheckfail.visibility = View.GONE
            }
            R.id.rl_fullname -> KeyBoardUtils.showSoftInputFromWindow(
                mContext,
                mBinding!!.edtFullname
            )
            R.id.rl_id -> KeyBoardUtils.showSoftInputFromWindow(mContext, mBinding!!.edtIdNo)
        }
    }

    private fun submitClick() {
        val fullname = mBinding!!.edtFullname.text.toString().trim { it <= ' ' }
        var firstname = mBinding!!.edtSurname.text.toString().trim { it <= ' ' }
        var name = mBinding!!.edtName.text.toString().trim { it <= ' ' }
        val idno = mBinding!!.edtIdNo.text.toString().replace(" ", "")
        firstname = if ("CN" == countryArea!!.key) fullname else firstname
        name = if ("CN" == countryArea!!.key) "" else name
        //        if (TextUtils.isEmpty(path_front_url)) {
//            showToast(R.string.identity_front_null);
//            return;
//        }
//        if ("CN".equals(countryArea.getKey()) && TextUtils.isEmpty(path_back_url)) {
//            showToast(R.string.identity_back_null);
//            return;
//        }
        if ("CN" == countryArea!!.key && !StringFormatUtils.isIDCard(idno)) {
            showToast(R.string.identity_idcard_error)
            return
        }
        getcheckIdent(countryArea!!.key, firstname, idno, path_front_url, path_back_url, path_hold_url, name)
    }

    override fun setAppTitle(): String {
        return mContext.resources.getString(R.string.identity_title)
    }

    /**
     * 处理选择图片
     */
    private fun dealCheckPic() {
        val popView = LayoutInflater.from(activity).inflate(R.layout.popup_piccheck, null)
        pop = PopupWindow(
            popView,
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        pop!!.isFocusable = true
        pop!!.showAtLocation(popView, Gravity.BOTTOM or Gravity.LEFT, 0, 0)
        val tv_camera = popView.findViewById<TextView>(R.id.tv_camera)
        val tv_gallery = popView.findViewById<TextView>(R.id.tv_gallery)
        val tv_cancel = popView.findViewById<TextView>(R.id.tv_cancel)
        val rl_popup = popView.findViewById<RelativeLayout>(R.id.rl_popup)
        tv_camera.setOnClickListener(this)
        tv_gallery.setOnClickListener(this)
        tv_cancel.setOnClickListener(this)
        rl_popup.setOnClickListener(this)
    }

    private fun initPicker() {
        val imagePicker = ImagePicker.getInstance()
        imagePicker.imageLoader = GlideImageLoader() //设置图片加载器
        imagePicker.isShowCamera = false //显示拍照按钮
        imagePicker.isCrop = false //允许裁剪（单选才有效）
        imagePicker.isSaveRectangle = true //是否按矩形区域保存
        imagePicker.selectLimit = 1 //选中数量限制
        //        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.outPutX = 1000 //保存文件的宽度。单位像素
        imagePicker.outPutY = 1000 //保存文件的高度。单位像素
        imagePicker.isMultiMode = false //单选模式
    }

    fun onPictureSelect(path: String?) {
        if (TYPE == TYPE_FRONT) {
            Glide.with(requireContext()).load(path).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val path = requireContext().saveBitmap2File(
                        "front.jpg",
                        (resource as BitmapDrawable).bitmap
                    )
                    uploadPic(File(path), path_front, true)
                    return false
                }
            }).into(mBinding!!.imvFront)
        } else if (TYPE == TYPE_BACK) {
            Glide.with(requireContext()).load(path).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val path = requireContext().saveBitmap2File(
                        "back.jpg",
                        (resource as BitmapDrawable).bitmap
                    )
                    uploadPic(File(path), path_back, false)
                    return false
                }
            }).into(mBinding!!.imvBack)
        } else if (TYPE == TYPE_HOLD) {
            Glide.with(requireContext()).load(path).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val path = requireContext().saveBitmap2File(
                        "hold.jpg",
                        (resource as BitmapDrawable).bitmap
                    )
                    uploadPic(File(path), path_hold, false)
                    return false
                }
            }).into(mBinding!!.imvHold)
        }
    }

    /**
     * 上传图片
     *
     * @param file       上传的文件
     * @param path_index 标记是正面还是反面
     */
    private fun uploadPic(file: File, path_index: String, isFront: Boolean) {
        if (isFront) {
            mBinding!!.imvDelFront.visibility = View.GONE
            mBinding!!.llLoadingFront.visibility = View.VISIBLE
            mBinding!!.tvFront.visibility = View.GONE
        } else {
            mBinding!!.imvDelBack.visibility = View.GONE
            mBinding!!.llLoadingBack.visibility = View.VISIBLE
            mBinding!!.tvBack.visibility = View.GONE
        }
        val fileList: MutableList<File> = ArrayList()
        fileList.add(file)
        val partList = files2Parts("file", file)
        Http.getHttpService().uploadIdPic(partList)
            .compose(CommonTransformer())
            .subscribe(object : CommonSubscriber<String?>(this) {
                override fun onNext(path: String?) {

                    when(TYPE){
                        TYPE_FRONT ->{
                            if (!TextUtils.isEmpty(path)) {
                                path_front_url = path!!
                            }
                            mBinding!!.imvDelFront.visibility = View.VISIBLE
                            mBinding!!.llLoadingFront.visibility = View.GONE
                            mBinding!!.imvFront.isClickable = false
                        }
                        TYPE_BACK ->{
                            if (!TextUtils.isEmpty(path)) {
                                path_back_url = path!!
                            }
                            mBinding!!.imvDelBack.visibility = View.VISIBLE
                            mBinding!!.llLoadingBack.visibility = View.GONE
                            mBinding!!.imvBack.isClickable = false
                        }
                        TYPE_HOLD ->{
                            if (!TextUtils.isEmpty(path)) {
                                path_hold_url = path!!
                            }
                            mBinding!!.imvDelBack.visibility = View.VISIBLE
                            mBinding!!.llLoadingBack.visibility = View.GONE
                            mBinding!!.imvBack.isClickable = false
                        }
                    }
                    valid()
                }

                override fun showLoading(): Boolean {
                    return false
                }

                override fun onError(e: ApiException) {
                    super.onError(e)
                    if (isFront) {
                        mBinding!!.imvDelFront.visibility = View.GONE
                        mBinding!!.tvFront.visibility = View.VISIBLE
                        mBinding!!.llLoadingFront.visibility = View.GONE
                        mBinding!!.imvFront.setImageDrawable(
                            mContext.resources.getDrawable(
                                Pub.getThemeResource(
                                    mContext,
                                    R.attr.id_front
                                )
                            )
                        )
                    } else {
                        mBinding!!.imvDelBack.visibility = View.GONE
                        mBinding!!.llLoadingBack.visibility = View.GONE
                        mBinding!!.tvBack.visibility = View.VISIBLE
                        mBinding!!.imvBack.setImageDrawable(
                            mContext.resources.getDrawable(
                                Pub.getThemeResource(
                                    mContext,
                                    R.attr.id_back
                                )
                            )
                        )
                    }
                }
            })
    }

    private val path: String
        private get() {
            val path = Environment.getExternalStorageDirectory().toString() + "/Luban/image/"
            val file = File(path)
            return if (file.mkdirs()) {
                path
            } else path
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
    private fun getcheckIdent(
        nation: String,
        firstName: String,
        idCard: String,
        idCardFrontUrl: String,
        idCardBackUrl: String,
        idCardHoldUrl: String,
        lastName: String
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("nation", nation)
        jsonObject.addProperty("firstName", firstName)
        jsonObject.addProperty("idCard", idCard)
        jsonObject.addProperty("idCardFrontUrl", idCardFrontUrl)
        jsonObject.addProperty("idCardBackUrl", idCardBackUrl)
        jsonObject.addProperty("cardPictureHandUrl", idCardHoldUrl)
        jsonObject.addProperty("lastName", lastName)
        if ("CN" == countryArea!!.key) {
            jsonObject.addProperty("cardType", "1")
        } else {
            jsonObject.addProperty("cardType", "2")
        }
        Http.getHttpService().identityCheck(jsonObject)
            .compose(NothingTransformer<BaseHttpEntity>())
            .subscribe(object : CommonSubscriber<BaseHttpEntity?>(this) {
                override fun onNext(loginBean: BaseHttpEntity?) {
                    L.a("getcheckIdent suc ---$loginBean")
                    //                        showToast(R.string.identity_send_suc);
//                        finish();
                    mBinding!!.llChecking.visibility = View.VISIBLE
                    mBinding!!.llMain.visibility = View.GONE
                }

                override fun onError(e: ApiException) {
                    super.onError(e)
                    L.a("getcheckIdent fail ---")
                }
            })
    }

    override fun eventEnable(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    override fun onEventPosting(event: Event) {
        when (event._id) {
            R.id.event_identity_countrycheck -> {
                val a = event.getParam(
                    CounrtyAreasBean.Area::class.java
                )
                if (a != null) {
                    countryArea = a
                }
                if ("CN" == countryArea!!.key) {
                    mBinding!!.rlSurandname.visibility = View.GONE
                    mBinding!!.llFullname.visibility = View.VISIBLE
                    mBinding!!.edtFullname.visibility = View.VISIBLE
                    mBinding!!.edtName.visibility = View.GONE
                    mBinding!!.edtSurname.visibility = View.GONE
                    mBinding!!.tvFront.setText(R.string.identity_front_msg_id)
                    mBinding!!.tvBack.setText(R.string.identity_back_msg_id)
                    mBinding!!.rlBackParent.visibility = View.VISIBLE
                    mBinding!!.tvId.setText(R.string.identity_typename_id)
                    mBinding!!.tvUploadmsg.setText(R.string.identity_pic_id)
                    mBinding!!.tvFront.setText(R.string.identity_front_msg_id)
                } else {
                    mBinding!!.rlSurandname.visibility = View.VISIBLE
                    mBinding!!.llFullname.visibility = View.GONE
                    mBinding!!.edtFullname.visibility = View.GONE
                    mBinding!!.edtName.visibility = View.VISIBLE
                    mBinding!!.edtSurname.visibility = View.VISIBLE
                    mBinding!!.tvFront.setText(R.string.identity_front_msg_id)
                    //                    mBinding.tvBack.setText(R.string.identity_back_msg_huzhao);
                    mBinding!!.rlBackParent.visibility = View.GONE
                    mBinding!!.tvId.setText(R.string.identity_typename_passport)
                    mBinding!!.tvUploadmsg.setText(R.string.identity_pic_passport)
                    mBinding!!.tvFront.setText(R.string.identity_front_msg_huzhao)
                }
                mBinding!!.imvBack.setImageDrawable(
                    mContext.resources.getDrawable(
                        Pub.getThemeResource(
                            mContext,
                            R.attr.id_back
                        )
                    )
                )
                path_back_url = ""
                mBinding!!.imvDelBack.visibility = View.GONE
                mBinding!!.imvBack.isClickable = true
                mBinding!!.tvBack.visibility = View.VISIBLE
                mBinding!!.imvFront.setImageDrawable(
                    mContext.resources.getDrawable(
                        Pub.getThemeResource(
                            mContext,
                            R.attr.id_front
                        )
                    )
                )
                path_front_url = ""
                mBinding!!.imvDelFront.visibility = View.GONE
                mBinding!!.imvFront.isClickable = true
                mBinding!!.tvFront.visibility = View.VISIBLE

                mBinding!!.imvHold.setImageDrawable(
                    mContext.resources.getDrawable(
                        Pub.getThemeResource(
                            mContext,
                            R.mipmap.img_hold
                        )
                    )
                )
                path_hold_url = ""
                mBinding!!.imvDelHold.visibility = View.GONE
                mBinding!!.imvHold.isClickable = true
                mBinding!!.tvHold.visibility = View.VISIBLE

                valid()
                bindValid(
                    mBinding!!.btnSubmit,
                    mBinding!!.edtFullname,
                    mBinding!!.edtName,
                    mBinding!!.edtSurname,
                    mBinding!!.edtIdNo
                )
                valid()
                mBinding!!.tvCountry.text = countryArea!!.name_zh
            }
        }
    }

    override fun viewGroupFocused(): Boolean {
        return false
    }//已完成

    /**
     * 获取我的数据
     */
    val mindeMsg: Unit
        get() {
            Http.getHttpService().mineData
                .compose(CommonTransformer())
                .subscribe(object : CommonSubscriber<MineBean?>(FotaApplication.getInstance()) {
                    override fun onNext(mineBean: MineBean?) {
                        if (view == null) {
                            return
                        }
                        if (mineBean != null && mineBean.userSecurity != null) {
                            if (mineBean.userSecurity.cardCheckStatus == 1 || mineBean.userSecurity.cardCheckStatus == 4) {
                                mBinding!!.llChecking.visibility = View.VISIBLE
                                mBinding!!.llMain.visibility = View.GONE
                            } else if (mineBean.userSecurity.cardCheckStatus == 3) {
                                mBinding!!.llCheckfail.visibility = View.VISIBLE
                                mBinding!!.llMain.visibility = View.GONE
                            } else if (mineBean.userSecurity.cardCheckStatus == 2) { //已完成
                                showToast(R.string.safesetting_ident_over)
                                finish()
                            }
                        }
                    }

                    override fun onError(e: ApiException) {
                        super.onError(e)
                    }
                })
        }

    /**
     * 用户补充认证
     *
     * @return
     */
    override fun customerValid(): Boolean {
        var unable = false
        unable = if ("CN" == countryArea!!.key) {
            !TextUtils.isEmpty(path_front_url) && !TextUtils.isEmpty(
                path_back_url
            )
        } else {
            !TextUtils.isEmpty(path_front_url)
        }
        return unable
    }

    companion object {
        fun files2Parts(key: String?, file: File): List<MultipartBody.Part?> {
            val parts: MutableList<MultipartBody.Part?> = mutableListOf()
            val requestBody = RequestBody.create(MediaType.parse("images/jpeg"), file)
            val part = MultipartBody.Part.createFormData(key, file.name, requestBody)
            parts.add(part)
            return parts
        }
    }
}