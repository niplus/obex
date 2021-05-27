package com.fota.android.moudles

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.fota.android.R
import com.fota.android.commonlib.utils.CommonUtils
import com.fota.android.commonlib.utils.Pub
import com.fota.android.commonlib.utils.UIUtil
import com.fota.android.core.mvvmbase.BaseFragment
import com.fota.android.databinding.FragmentCommissionBinding
import com.fota.android.databinding.ItemInvitePostBinding
import com.fota.android.utils.ZXingUtils
import com.fota.android.utils.saveBitmap2Public
import com.fota.android.widget.dialog.BottomDialog
import com.fota.android.widget.recyclerview.SmartRefreshLayoutUtils
import com.ndl.lib_common.base.BaseAdapter
import com.ndl.lib_common.base.MyViewHolder
import com.ndl.lib_common.utils.showSnackMsg
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs

class CommissionFragment : BaseFragment<FragmentCommissionBinding, InviteViewModel>() {

    var qrBitmap: Bitmap? = null
    val postData = mutableListOf<Int>(R.mipmap.img_post1, R.mipmap.img_post2, R.mipmap.img_post3)

    var clipManager : ClipboardManager? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_commission
    }

    override fun initData() {
        viewModel.inviteRecordLiveData.observe(this, Observer {
            qrBitmap = ZXingUtils.Create2DCode(
                    it.inviteUrl, UIUtil.dip2px(context, 200.0), UIUtil.dip2px(
                    context,
                    200.0
            )
            )

            dataBinding.refreshLayout.finishRefresh()
        })
        viewModel.getInviteInfo()
        viewModel.getInviteRecord()

        clipManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun initComp() {
        dataBinding.viewModel = viewModel
        dataBinding.apply {
            cdInvite.setOnClickListener {
//                val dialog = BottomDialog(requireContext())
//                dialog.setContentView(R.layout.dialog_platform_share)
//                dialog.findViewById<View>(R.id.iv_copy_link).setOnClickListener {
//                    val myClip = ClipData.newPlainText("text", this@CommissionFragment.viewModel.inviteRecordLiveData.value!!.inviteUrl) //str
//                    clipManager?.setPrimaryClip(myClip)
//                    showSnackMsg(CommonUtils.getResouceString(context, R.string.copy_success))
//                    dialog.dismiss()
//                }
//                dialog.findViewById<View>(R.id.iv_close).setOnClickListener {
//                    dialog.dismiss()
//                }
//                dialog.show()

                val myClip = ClipData.newPlainText("text", this@CommissionFragment.viewModel.inviteRecordLiveData.value!!.inviteUrl) //str
                clipManager?.setPrimaryClip(myClip)
                showSnackMsg(CommonUtils.getResouceString(context, R.string.copy_success))
            }

            cdPost.setOnClickListener {
                val dialog = BottomDialog(requireContext())
                dialog.setContentView(R.layout.dialog_invite_post1)
                val attr = dialog.window!!.attributes
                attr.height = ViewGroup.LayoutParams.MATCH_PARENT
                dialog.window!!.attributes =attr

//                dialog.findViewById<TextView>(R.id.tv_invite_code).text = getString(R.string.invitation_code) + this@CommissionFragment.viewModel.inviteInfo.get()!!.inviteCode
                dialog.findViewById<View>(R.id.iv_close).setOnClickListener {
                    dialog.dismiss()
                }
//                dialog.findViewById<ImageView>(R.id.iv_qrcode).setImageBitmap(qrBitmap)

                val vpInvite = dialog.findViewById<ViewPager2>(R.id.vp_post)
                vpInvite.adapter = object : BaseAdapter<ItemInvitePostBinding, Int>(postData, R.layout.item_invite_post, 0){
                    override fun onBindViewHolder(
                        holder: MyViewHolder<ItemInvitePostBinding>,
                        position: Int
                    ) {
                        super.onBindViewHolder(holder, position)
                        holder.dataBinding.apply {
                            ivPost.setImageResource(data[position])
                            ivQrcode.setImageBitmap(qrBitmap)
                            tvInviteCode.text = getString(R.string.invitation_code) + this@CommissionFragment.viewModel.inviteInfo.get()!!.inviteCode
                        }
                    }
                }
                val recyclerView = vpInvite.getChildAt(0) as RecyclerView
                recyclerView.setPadding(150, 0, 150, 0)
                recyclerView.clipToPadding = false
                vpInvite.setPageTransformer { page, position ->
                    val scale = 0.8f.coerceAtLeast(1 - abs(position))
                    page.scaleX = scale
                    page.scaleY = scale
                }


                dialog.findViewById<View>(R.id.iv_save).setOnClickListener {
                    val container = recyclerView.getChildAt(vpInvite.currentItem)
                    GlobalScope.launch {
                        val bitmap: Bitmap? = getBitmap(container)
                        requireActivity().saveBitmap2Public("test.jpg", bitmap!!){
                            MainScope().launch {
                                if (it) {
                                    showSnackMsg(getString(R.string.save_pic_success))
                                    dialog.dismiss()
                                }else{
                                    showSnackMsg(getString(R.string.save_pic_failed))
                                }
                            }
                        }
                    }
                }
                dialog.show()
            }

            cvQrcode.setOnClickListener {
                val dialog = Dialog(requireContext())
                dialog.window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))
                dialog.setContentView(R.layout.dialog_invite_qrcode)
                dialog.findViewById<ImageView>(R.id.iv_qrcode).setImageBitmap(qrBitmap)
                dialog.findViewById<View>(R.id.iv_close).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            tvCopy.setOnClickListener {
                val myClip = ClipData.newPlainText("text", tvInviteCode.text) //str
                clipManager?.setPrimaryClip(myClip)
                showSnackMsg(CommonUtils.getResouceString(context, R.string.copy_success))
            }

            initHeader(refreshLayout, context)
            refreshLayout.setOnRefreshListener {
                this@CommissionFragment.viewModel.getInviteInfo()
                this@CommissionFragment.viewModel.getInviteRecord()
            }
        }
    }



    override fun createViewModel(): InviteViewModel {
        return ViewModelProvider(requireActivity()).get(InviteViewModel::class.java)
    }

    /**
     * 获取Bitmap
     *
     * @param view view
     * @return Bitmap
     */
    fun getBitmap(view: View): Bitmap? {
        val width = view.width
        val height = view.height

        // draw(canvas)获取Bitmap方法
        val bitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


}

fun initHeader(refreshLayout: SmartRefreshLayout?, context: Context?) {
    if (refreshLayout == null || context == null) {
        return
    }
    val header = com.fota.android.widget.ClassicsHeader(context)
    header.setAccentColor(Pub.getColor(context, R.attr.font_color))
    header.setPrimaryColor(Pub.getColor(context, R.attr.bg_color))
    refreshLayout.setRefreshHeader(header)
}