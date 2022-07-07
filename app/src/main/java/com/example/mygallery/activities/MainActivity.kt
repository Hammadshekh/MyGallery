package com.example.mygallery.activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.camerax.CameraImageEngine
import com.example.camerax.SimpleCameraX
import com.example.camerax.listener.OnSimpleXPermissionDeniedListener
import com.example.camerax.listener.OnSimpleXPermissionDescriptionListener
import com.example.camerax.permissions.SimpleXPermissionUtil
import com.example.compress.*
import com.example.mygallery.R
import com.example.mygallery.adapter.GridImageAdapter
import com.example.mygallery.engine.GlideEngine
import com.example.mygallery.engine.ImageEngine
import com.example.mygallery.files.FullyGridLayoutManager
import com.example.mygallery.fragments.CustomPreviewFragment
import com.example.mygallery.listener.DragListener
import com.example.mygallery.utils.ImageLoaderUtils
import com.example.selector.PictureSelectorPreviewFragment
import com.example.selector.animations.AnimationType
import com.example.selector.basic.*
import com.example.selector.config.*
import com.example.selector.decoration.GridSpacingItemDecoration
import com.example.selector.dialog.RemindDialog
import com.example.selector.engine.*
import com.example.selector.entity.LocalMediaFolder
import com.example.selector.entity.MediaExtraInfo
import com.example.selector.interfaces.*
import com.example.selector.interfaces.VideoPlayerEngine
import com.example.selector.language.LanguageConfig
import com.example.selector.loader.SandboxFileLoader
import com.example.selector.permissions.PermissionConfig
import com.example.selector.style.*
import com.example.selector.utils.*
import com.example.selector.widget.MediumBoldTextView
import com.example.ucrop.UCrop
import com.example.ucrop.UCropImageEngine
import com.luck.picture.lib.entity.LocalMedia
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*


/**
 * @author：luck
 * @data：2019/12/20 晚上 23:12
 * @描述: Demo
 */
class MainActivity() : AppCompatActivity(), IBridgePictureBehavior, View.OnClickListener,
    RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private var mAdapter: GridImageAdapter? = null
    private var maxSelectNum: Int = 9
    private var maxSelectVideoNum: Int = 1
    private var tv_select_num: TextView? = null
    private var tv_select_video_num: TextView? = null
    private var tv_original_tips: TextView? = null
    private var tvDeleteText: TextView? = null
    private var rgb_crop: RadioGroup? = null
    private var llSelectVideoSize: LinearLayout? = null
    private var aspect_ratio_x: Int = -1
    private var aspect_ratio_y: Int = -1
    private var cb_voice: CheckBox? = null
    private var cb_choose_mode: CheckBox? = null
    private var cb_isCamera: CheckBox? = null
    private var cb_isGif: CheckBox? = null
    private var cb_preview_img: CheckBox? = null
    private var cb_preview_video: CheckBox? = null
    private var cb_crop: CheckBox? = null
    private var cb_compress: CheckBox? = null
    private var cb_mode: CheckBox? = null
    private var cb_hide: CheckBox? = null
    private var cb_crop_circular: CheckBox? = null
    private var cb_styleCrop: CheckBox? = null
    private var cb_showCropGrid: CheckBox? = null
    private var cb_showCropFrame: CheckBox? = null
    private var cb_preview_audio: CheckBox? = null
    private var cb_original: CheckBox? = null
    private var cb_single_back: CheckBox? = null
    private var cb_custom_camera: CheckBox? = null
    private var cbPage: CheckBox? = null
    private var cbEnabledMask: CheckBox? = null
    private var cbEditor: CheckBox? = null
    private var cb_custom_sandbox: CheckBox? = null
    private var cb_only_dir: CheckBox? = null
    private var cb_preview_full: CheckBox? = null
    private var cb_preview_scale: CheckBox? = null
    private var cb_inject_layout: CheckBox? = null
    private var cb_time_axis: CheckBox? = null
    private var cb_WithImageVideo: CheckBox? = null
    private var cb_system_album: CheckBox? = null
    private var cb_fast_select: CheckBox? = null
    private var cb_skip_not_gif: CheckBox? = null
    private var cb_not_gif: CheckBox? = null
    private var cb_attach_camera_mode: CheckBox? = null
    private var cb_attach_system_mode: CheckBox? = null
    private var cb_camera_zoom: CheckBox? = null
    private var cb_camera_focus: CheckBox? = null
    private var cb_query_sort_order: CheckBox? = null
    private var cb_watermark: CheckBox? = null
    private var cb_custom_preview: CheckBox? = null
    private var cb_permission_desc: CheckBox? = null
    private var cb_video_thumbnails: CheckBox? = null
    private var cb_auto_video: CheckBox? = null
    private var cb_selected_anim: CheckBox? = null
    private var cb_video_resume: CheckBox? = null
    private var chooseMode: Int = SelectMimeType.ofAll()
    private var isHasLiftDelete: Boolean = false
    private var needScaleBig: Boolean = true
    private var needScaleSmall: Boolean = false
    private var language: Int = LanguageConfig.UNKNOWN_LANGUAGE
    private var x: Int = 0
    private var y: Int = 0
    private var animationMode: Int = AnimationType.DEFAULT_ANIMATION
    private var selectorStyle: PictureSelectorStyle? = null
    private val mData: MutableList<LocalMedia> = ArrayList()
    private var launcherResult: ActivityResultLauncher<Intent>? = null
    private var resultMode: Int = LAUNCHER_RESULT
    private var imageEngine: ImageEngine? = null
    lateinit var videoPlayerEngine: VideoPlayerEngine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectorStyle = PictureSelectorStyle()
        val minus: ImageView = findViewById(R.id.minus)
        val plus: ImageView = findViewById(R.id.plus)
        tv_select_num = findViewById(R.id.tv_select_num)
        val videoMinus: ImageView = findViewById(R.id.video_minus)
        val videoPlus: ImageView = findViewById(R.id.video_plus)
        tv_select_video_num = findViewById(R.id.tv_select_video_num)
        llSelectVideoSize = findViewById(R.id.ll_select_video_size)
        tvDeleteText = findViewById(R.id.tv_delete_text)
        tv_original_tips = findViewById(R.id.tv_original_tips)
        rgb_crop = findViewById(R.id.rgb_crop)
        cb_video_thumbnails = findViewById(R.id.cb_video_thumbnails)
        val rgb_video_player: RadioGroup = findViewById(R.id.rgb_video_player)
        val rgb_result: RadioGroup = findViewById(R.id.rgb_result)
        val rgb_style: RadioGroup = findViewById(R.id.rgb_style)
        val rgb_animation: RadioGroup = findViewById(R.id.rgb_animation)
        val rgb_list_anim: RadioGroup = findViewById(R.id.rgb_list_anim)
        val rgb_photo_mode: RadioGroup = findViewById(R.id.rgb_photo_mode)
        val rgb_language: RadioGroup = findViewById(R.id.rgb_language)
        val rgb_engine: RadioGroup = findViewById(R.id.rgb_engine)
        cb_voice = findViewById(R.id.cb_voice)
        cb_choose_mode = findViewById(R.id.cb_choose_mode)
        cb_video_resume = findViewById(R.id.cb_video_resume)
        cb_isCamera = findViewById(R.id.cb_isCamera)
        cb_isGif = findViewById(R.id.cb_isGif)
        cb_watermark = findViewById(R.id.cb_watermark)
        cb_WithImageVideo = findViewById(R.id.cbWithImageVideo)
        cb_system_album = findViewById(R.id.cb_system_album)
        cb_fast_select = findViewById(R.id.cb_fast_select)
        cb_preview_full = findViewById(R.id.cb_preview_full)
        cb_preview_scale = findViewById(R.id.cb_preview_scale)
        cb_inject_layout = findViewById(R.id.cb_inject_layout)
        cb_preview_img = findViewById(R.id.cb_preview_img)
        cb_camera_zoom = findViewById(R.id.cb_camera_zoom)
        cb_camera_focus = findViewById(R.id.cb_camera_focus)
        cb_query_sort_order = findViewById(R.id.cb_query_sort_order)
        cb_custom_preview = findViewById(R.id.cb_custom_preview)
        cb_permission_desc = findViewById(R.id.cb_permission_desc)
        cb_preview_video = findViewById(R.id.cb_preview_video)
        cb_auto_video = findViewById(R.id.cb_auto_video)
        cb_selected_anim = findViewById(R.id.cb_selected_anim)
        cb_time_axis = findViewById(R.id.cb_time_axis)
        cb_crop = findViewById(R.id.cb_crop)
        cbPage = findViewById(R.id.cbPage)
        cbEditor = findViewById(R.id.cb_editor)
        cbEnabledMask = findViewById(R.id.cbEnabledMask)
        cb_styleCrop = findViewById(R.id.cb_styleCrop)
        cb_compress = findViewById(R.id.cb_compress)
        cb_mode = findViewById(R.id.cb_mode)
        cb_custom_sandbox = findViewById(R.id.cb_custom_sandbox)
        cb_only_dir = findViewById(R.id.cb_only_dir)
        cb_showCropGrid = findViewById(R.id.cb_showCropGrid)
        cb_showCropFrame = findViewById(R.id.cb_showCropFrame)
        cb_preview_audio = findViewById(R.id.cb_preview_audio)
        cb_original = findViewById(R.id.cb_original)
        cb_single_back = findViewById(R.id.cb_single_back)
        cb_custom_camera = findViewById(R.id.cb_custom_camera)
        cb_hide = findViewById(R.id.cb_hide)
        cb_not_gif = findViewById(R.id.cb_not_gif)
        cb_skip_not_gif = findViewById(R.id.cb_skip_not_gif)
        cb_crop_circular = findViewById(R.id.cb_crop_circular)
        cb_attach_camera_mode = findViewById(R.id.cb_attach_camera_mode)
        cb_attach_system_mode = findViewById(R.id.cb_attach_system_mode)
        cb_mode.setOnCheckedChangeListener(this)
        rgb_crop.setOnCheckedChangeListener(this)
        cb_custom_camera.setOnCheckedChangeListener(this)
        rgb_result.setOnCheckedChangeListener(this)
        rgb_style.setOnCheckedChangeListener(this)
        rgb_animation.setOnCheckedChangeListener(this)
        rgb_list_anim.setOnCheckedChangeListener(this)
        rgb_photo_mode.setOnCheckedChangeListener(this)
        rgb_language.setOnCheckedChangeListener(this)
        rgb_video_player.setOnCheckedChangeListener(this)
        rgb_engine.setOnCheckedChangeListener(this)
        val mRecyclerView: RecyclerView = findViewById(R.id.recycler)
        val left_back: ImageView = findViewById(R.id.left_back)
        left_back.setOnClickListener(this)
        minus.setOnClickListener(this)
        plus.setOnClickListener(this)
        videoMinus.setOnClickListener(this)
        videoPlus.setOnClickListener(this)
        cb_crop.setOnCheckedChangeListener(this)
        cb_only_dir.setOnCheckedChangeListener(this)
        cb_custom_sandbox.setOnCheckedChangeListener(this)
        cb_crop_circular.setOnCheckedChangeListener(this)
        cb_attach_camera_mode.setOnCheckedChangeListener(this)
        cb_attach_system_mode.setOnCheckedChangeListener(this)
        cb_system_album.setOnCheckedChangeListener(this)
        cb_compress.setOnCheckedChangeListener(this)
        cb_not_gif.setOnCheckedChangeListener(this)
        cb_skip_not_gif.setOnCheckedChangeListener(this)
        tv_select_num.setText(ValueOf.toString(maxSelectNum))
        tv_select_video_num.setText(ValueOf.toString(maxSelectVideoNum))
        // 注册需要写在onCreate或Fragment onAttach里，否则会报java.lang.IllegalStateException异常
        launcherResult = createActivityResultLauncher()

//        List<LocalMedia> list = new ArrayList<>();
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://fdfs.test-kepu.weiyilewen.com/group1/M00/00/01/wKhkY2Iv936EMKWzAAAAAHuLNY8762.mp4"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx1.sinaimg.cn/mw2000/0073ozWdly1h0afogn4vij30u05keb29.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx3.sinaimg.cn/mw2000/0073ozWdly1h0afohdkygj30u05791kx.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx2.sinaimg.cn/mw2000/0073ozWdly1h0afoi70m2j30u05fq1kx.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx2.sinaimg.cn/mw2000/0073ozWdly1h0afoipj8xj30kw3kmwru.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://wx4.sinaimg.cn/mw2000/0073ozWdly1h0afoj5q8ij30u04gqkb1.jpg"));
//        list.add(LocalMedia.generateHttpAsLocalMedia("https://ww1.sinaimg.cn/bmiddle/bcd10523ly1g96mg4sfhag20c806wu0x.gif"));
//        mData.addAll(list);
        val manager: FullyGridLayoutManager = FullyGridLayoutManager(
            this,
            4, GridLayoutManager.VERTICAL, false
        )
        mRecyclerView.layoutManager = manager
        val itemAnimator: ItemAnimator? = mRecyclerView.itemAnimator
        if (itemAnimator != null) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        mRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                4,
                DensityUtil.dip2px(this, 8f), false
            )
        )
        mAdapter = GridImageAdapter(context, mData)
        mAdapter.selectMax(maxSelectNum + maxSelectVideoNum)
        mRecyclerView.adapter = mAdapter
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList<Parcelable>("selectorList") != null) {
            mData.clear()
            mData.addAll((savedInstanceState.getParcelableArrayList("selectorList"))!!)
        }
        val systemHigh: String = " (仅支持部分api)"
        val systemTips: String = "使用系统图库$systemHigh"
        val startIndex: Int = systemTips.indexOf(systemHigh)
        val endOf: Int = startIndex + systemHigh.length
        val builder: SpannableStringBuilder = SpannableStringBuilder(systemTips)
        builder.setSpan(
            AbsoluteSizeSpan(DensityUtil.dip2px(context, 12)),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            ForegroundColorSpan(-0x340000),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        cb_system_album.setText(builder)
        val cameraHigh: String = " (默认fragment)"
        val cameraTips: String = "使用Activity承载Camera相机$cameraHigh"
        val startIndex2: Int = cameraTips.indexOf(cameraHigh)
        val endOf2: Int = startIndex2 + cameraHigh.length
        val builder2: SpannableStringBuilder = SpannableStringBuilder(cameraTips)
        builder2.setSpan(
            AbsoluteSizeSpan(DensityUtil.dip2px(context, 12)),
            startIndex2,
            endOf2,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        builder2.setSpan(
            ForegroundColorSpan(-0x340000),
            startIndex2,
            endOf2,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        cb_attach_camera_mode.setText(builder2)
        val systemAlbumHigh: String = " (默认fragment)"
        val systemAlbumTips: String = "使用Activity承载系统相册$systemAlbumHigh"
        val startIndex3: Int = systemAlbumTips.indexOf(systemAlbumHigh)
        val endOf3: Int = startIndex3 + systemAlbumHigh.length
        val builder3: SpannableStringBuilder = SpannableStringBuilder(systemAlbumTips)
        builder3.setSpan(
            AbsoluteSizeSpan(DensityUtil.dip2px(context, 12)),
            startIndex3,
            endOf3,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        builder3.setSpan(
            ForegroundColorSpan(-0x340000),
            startIndex3,
            endOf3,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        cb_attach_system_mode.setText(builder3)
        cb_original.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            tv_original_tips.setVisibility(
                if (isChecked) View.VISIBLE else View.GONE
            )
        })
        cb_choose_mode.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            cb_single_back.setVisibility(if (isChecked) View.GONE else View.VISIBLE)
            cb_single_back.setChecked(!isChecked && cb_single_back.isChecked())
        })
        imageEngine = GlideEngine.createGlideEngine()
        mAdapter.setOnItemClickListener(object : OnItemClickListener() {
            override fun onItemClick(v: View?, position: Int) {
                // 预览图片、视频、音频
                PictureSelector.create(this@MainActivity)
                    .openPreview()
                    .setImageEngine(imageEngine)
                    .setVideoPlayerEngine(videoPlayerEngine)
                    .setSelectorUIStyle(selectorStyle)
                    .setLanguage(language)
                    .isAutoVideoPlay(cb_auto_video.isChecked())
                    .isLoopAutoVideoPlay(cb_auto_video.isChecked())
                    .isPreviewFullScreenMode(cb_preview_full.isChecked())
                    .isVideoPauseResumePlay(cb_video_resume.isChecked())
                    .isPreviewZoomEffect(
                        chooseMode != SelectMimeType.ofAudio() && cb_preview_scale.isChecked(),
                        mRecyclerView
                    )
                    .setAttachViewLifecycle(object : IBridgeViewLifecycle() {
                        fun onViewCreated(
                            fragment: Fragment?,
                            view: View?,
                            savedInstanceState: Bundle?
                        ) {
//                                PictureSelectorPreviewFragment previewFragment = (PictureSelectorPreviewFragment) fragment;
//                                MediumBoldTextView tvShare = view.findViewById(R.id.tv_share);
//                                tvShare.setVisibility(View.VISIBLE)
//                                previewFragment.addAminViews(tvShare);
//                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tvShare.getLayoutParams();
//                                layoutParams.topMargin = cb_preview_full.isChecked() ? DensityUtil.getStatusBarHeight(getContext()) : 0;
//                                tvShare.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        PicturePreviewAdapter previewAdapter = previewFragment.getAdapter();
//                                        ViewPager2 viewPager2 = previewFragment.getViewPager2();
//                                        LocalMedia media = previewAdapter.getItem(viewPager2.getCurrentItem());
//                                        ToastUtils.showToast(fragment.getContext(), "自定义分享事件:" + viewPager2.getCurrentItem());
//                                    }
//                                });
                        }

                        fun onDestroy(fragment: Fragment?) {
//                                if (cb_preview_full.isChecked()) {
//                                    // 如果是全屏预览模式且是startFragmentPreview预览，回到自己的界面时需要恢复一下自己的沉浸式状态
//                                    // 以下提供2种解决方案:
//                                    // 1.通过ImmersiveManager.immersiveAboveAPI23重新设置一下沉浸式
//                                    int statusBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    int navigationBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    ImmersiveManager.immersiveAboveAPI23(MainActivity.this,
//                                            true, true,
//                                            statusBarColor, navigationBarColor, false);
//                                    // 2.让自己的titleBar的高度加上一个状态栏高度且内容PaddingTop下沉一个状态栏的高度
//                                }
                        }
                    })
                    .setInjectLayoutResourceListener(object : OnInjectLayoutResourceListener() {
                        fun getLayoutResourceId(context: Context?, resourceSource: Int): Int {
                            return if (resourceSource == InjectResourceSource.PREVIEW_LAYOUT_RESOURCE) R.layout.ps_custom_fragment_preview else InjectResourceSource.DEFAULT_LAYOUT_RESOURCE
                        }
                    })
                    .setExternalPreviewEventListener(com.luck.pictureselector.MainActivity.MyExternalPreviewEventListener())
                    .setInjectActivityPreviewFragment(object : OnInjectActivityPreviewListener() {
                        fun onInjectPreviewFragment(): PictureSelectorPreviewFragment? {
                            return if (cb_custom_preview.isChecked()) CustomPreviewFragment.newInstance() else null
                        }
                    })
                    .startActivityPreview(position, true, mAdapter.getData())
            }

            fun openPicture() {
                val mode: Boolean = cb_mode.isChecked()
                if (mode) {
                    // 进入系统相册
                    if (cb_system_album.isChecked()) {
                        val systemGalleryMode: PictureSelectionSystemModel = PictureSelector.create(
                            context
                        )
                            .openSystemGallery(chooseMode)
                            .setSelectionMode(if (cb_choose_mode.isChecked()) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE)
                            .setCompressEngine(compressFileEngine)
                            .setCropEngine(cropFileEngine)
                            .setSkipCropMimeType(notSupportCrop)
                            .setAddBitmapWatermarkListener(addBitmapWatermarkListener)
                            .setVideoThumbnailListener(videoThumbnailEventListener)
                            .isOriginalControl(cb_original.isChecked())
                            .setPermissionDescriptionListener(permissionDescriptionListener)
                            .setSandboxFileEngine(com.luck.pictureselector.MainActivity.MeSandboxFileEngine())
                        forSystemResult(systemGalleryMode)
                    } else {
                        // 进入相册
                        val selectionModel: PictureSelectionModel = PictureSelector.create(context)
                            .openGallery(chooseMode)
                            .setSelectorUIStyle(selectorStyle)
                            .setImageEngine(imageEngine)
                            .setVideoPlayerEngine(videoPlayerEngine)
                            .setCropEngine(cropFileEngine)
                            .setCompressEngine(compressFileEngine)
                            .setSandboxFileEngine(com.luck.pictureselector.MainActivity.MeSandboxFileEngine())
                            .setCameraInterceptListener(customCameraEvent)
                            .setRecordAudioInterceptListener(com.luck.pictureselector.MainActivity.MeOnRecordAudioInterceptListener())
                            .setSelectLimitTipsListener(com.luck.pictureselector.MainActivity.MeOnSelectLimitTipsListener())
                            .setEditMediaInterceptListener(customEditMediaEvent)
                            .setPermissionDescriptionListener(permissionDescriptionListener)
                            .setPreviewInterceptListener(previewInterceptListener)
                            .setPermissionDeniedListener(permissionDeniedListener)
                            .setAddBitmapWatermarkListener(addBitmapWatermarkListener)
                            .setVideoThumbnailListener(videoThumbnailEventListener)
                            .isAutoVideoPlay(cb_auto_video.isChecked())
                            .isLoopAutoVideoPlay(cb_auto_video.isChecked())
                            .isPageSyncAlbumCount(true)
                            .setQueryFilterListener(object : OnQueryFilterListener() {
                                fun onFilter(media: LocalMedia?): Boolean {
                                    return false
                                }
                            }) //.setExtendLoaderEngine(getExtendLoaderEngine())
                            .setInjectLayoutResourceListener(injectLayoutResource)
                            .setSelectionMode(if (cb_choose_mode.isChecked()) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE)
                            .setLanguage(language)
                            .setQuerySortOrder(if (cb_query_sort_order.isChecked()) MediaStore.MediaColumns.DATE_MODIFIED + " ASC" else "")
                            .setOutputCameraDir(if (chooseMode == SelectMimeType.ofAudio()) sandboxAudioOutputPath else sandboxCameraOutputPath)
                            .setOutputAudioDir(if (chooseMode == SelectMimeType.ofAudio()) sandboxAudioOutputPath else sandboxCameraOutputPath)
                            .setQuerySandboxDir(if (chooseMode == SelectMimeType.ofAudio()) sandboxAudioOutputPath else sandboxCameraOutputPath)
                            .isDisplayTimeAxis(cb_time_axis.isChecked())
                            .isOnlyObtainSandboxDir(cb_only_dir.isChecked())
                            .isPageStrategy(cbPage.isChecked())
                            .isOriginalControl(cb_original.isChecked())
                            .isDisplayCamera(cb_isCamera.isChecked())
                            .isOpenClickSound(cb_voice.isChecked())
                            .setSkipCropMimeType(notSupportCrop)
                            .isFastSlidingSelect(cb_fast_select.isChecked()) //.setOutputCameraImageFileName("luck.jpeg")
                            //.setOutputCameraVideoFileName("luck.mp4")
                            .isWithSelectVideoImage(cb_WithImageVideo.isChecked())
                            .isPreviewFullScreenMode(cb_preview_full.isChecked())
                            .isVideoPauseResumePlay(cb_video_resume.isChecked())
                            .isPreviewZoomEffect(cb_preview_scale.isChecked())
                            .isPreviewImage(cb_preview_img.isChecked())
                            .isPreviewVideo(cb_preview_video.isChecked())
                            .isPreviewAudio(cb_preview_audio.isChecked())
                            .setGridItemSelectAnimListener(if (cb_selected_anim.isChecked()) object :
                                OnGridItemSelectAnimListener() {
                                fun onSelectItemAnim(view: View?, isSelected: Boolean) {
                                    val set: AnimatorSet = AnimatorSet()
                                    set.playTogether(
                                        ObjectAnimator.ofFloat(
                                            view,
                                            "scaleX",
                                            if (isSelected) 1f else 1.12f,
                                            if (isSelected) 1.12f else 1.0f
                                        ),
                                        ObjectAnimator.ofFloat(
                                            view,
                                            "scaleY",
                                            if (isSelected) 1f else 1.12f,
                                            if (isSelected) 1.12f else 1.0f
                                        )
                                    )
                                    set.duration = 350
                                    set.start()
                                }
                            } else null)
                            .setSelectAnimListener(if (cb_selected_anim.isChecked()) object :
                                OnSelectAnimListener() {
                                fun onSelectAnim(view: View): Long {
                                    val animation: Animation = AnimationUtils.loadAnimation(
                                        context, R.anim.ps_anim_modal_in
                                    )
                                    view.startAnimation(animation)
                                    return animation.duration
                                }
                            } else null) //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
                            .isMaxSelectEnabledMask(cbEnabledMask.isChecked())
                            .isDirectReturnSingle(cb_single_back.isChecked())
                            .setMaxSelectNum(maxSelectNum)
                            .setMaxVideoSelectNum(maxSelectVideoNum)
                            .setRecyclerAnimationMode(animationMode)
                            .isGif(cb_isGif.isChecked())
                            .setSelectedData(mAdapter.getData())
                        forSelectResult(selectionModel)
                    }
                } else {
                    // 单独拍照
                    val cameraModel: PictureSelectionCameraModel =
                        PictureSelector.create(this@MainActivity)
                            .openCamera(chooseMode)
                            .setCameraInterceptListener(customCameraEvent)
                            .setRecordAudioInterceptListener(com.luck.pictureselector.MainActivity.MeOnRecordAudioInterceptListener())
                            .setCropEngine(cropFileEngine)
                            .setCompressEngine(compressFileEngine)
                            .setAddBitmapWatermarkListener(addBitmapWatermarkListener)
                            .setVideoThumbnailListener(videoThumbnailEventListener)
                            .setLanguage(language)
                            .setSandboxFileEngine(com.luck.pictureselector.MainActivity.MeSandboxFileEngine())
                            .isOriginalControl(cb_original.isChecked())
                            .setPermissionDescriptionListener(permissionDescriptionListener)
                            .setOutputAudioDir(sandboxAudioOutputPath)
                            .setSelectedData(mAdapter.getData())
                    forOnlyCameraResult(cameraModel)
                }
            }
        })
        mAdapter.setItemLongClickListener(object : OnItemLongClickListener() {
            fun onItemLongClick(holder: RecyclerView.ViewHolder, position: Int, v: View?) {
                val itemViewType: Int = holder.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    mItemTouchHelper.startDrag(holder)
                }
            }
        })
        // 绑定拖拽事件
        mItemTouchHelper.attachToRecyclerView(mRecyclerView)
        // 清除缓存
//        clearCache();
    }

    private val notSupportCrop: Array<String>?
        private get() {
            if (cb_skip_not_gif!!.isChecked) {
                return arrayOf(PictureMimeType.ofGIF(), PictureMimeType.ofWEBP())
            }
            return null
        }
    private val mItemTouchHelper: ItemTouchHelper =
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val itemViewType: Int = viewHolder.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    viewHolder.itemView.alpha = 0.7f
                }
                return makeMovementFlags(
                    (ItemTouchHelper.DOWN or ItemTouchHelper.UP
                            or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT), 0
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                try {
                    //得到item原来的position
                    val fromPosition: Int = viewHolder.absoluteAdapterPosition
                    //得到目标position
                    val toPosition: Int = target.absoluteAdapterPosition
                    val itemViewType: Int = target.itemViewType
                    if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                Collections.swap(mAdapter.getData(), i, i + 1)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                Collections.swap(mAdapter.getData(), i, i - 1)
                            }
                        }
                        mAdapter.notifyItemMoved(fromPosition, toPosition)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return true
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dx: Float,
                dy: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemViewType: Int = viewHolder.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (needScaleBig) {
                        needScaleBig = false
                        val animatorSet: AnimatorSet = AnimatorSet()
                        animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0f, 1.1f),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0f, 1.1f)
                        )
                        animatorSet.duration = 50
                        animatorSet.interpolator = LinearInterpolator()
                        animatorSet.start()
                        animatorSet.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                needScaleSmall = true
                            }
                        })
                    }
                    val targetDy: Int = tvDeleteText!!.top - viewHolder.itemView.bottom
                    if (dy >= targetDy) {
                        //拖到删除处
                        mDragListener.deleteState(true)
                        if (isHasLiftDelete) {
                            //在删除处放手，则删除item
                            viewHolder.itemView.visibility = View.INVISIBLE
                            mAdapter.delete(viewHolder.absoluteAdapterPosition)
                            resetState()
                            return
                        }
                    } else {
                        //没有到删除处
                        if (View.INVISIBLE == viewHolder.itemView.visibility) {
                            //如果viewHolder不可见，则表示用户放手，重置删除区域状态
                            mDragListener.dragState(false)
                        }
                        mDragListener.deleteState(false)
                    }
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dx,
                        dy,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                val itemViewType: Int =
                    if (viewHolder != null) viewHolder.itemViewType else GridImageAdapter.TYPE_CAMERA
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
                        mDragListener.dragState(true)
                    }
                    super.onSelectedChanged(viewHolder, actionState)
                }
            }

            override fun getAnimationDuration(
                recyclerView: RecyclerView,
                animationType: Int,
                animateDx: Float,
                animateDy: Float
            ): Long {
                isHasLiftDelete = true
                return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                val itemViewType: Int = viewHolder.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    viewHolder.itemView.alpha = 1.0f
                    if (needScaleSmall) {
                        needScaleSmall = false
                        val animatorSet: AnimatorSet = AnimatorSet()
                        animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1f, 1.0f),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1f, 1.0f)
                        )
                        animatorSet.interpolator = LinearInterpolator()
                        animatorSet.duration = 50
                        animatorSet.start()
                        animatorSet.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                needScaleBig = true
                            }
                        })
                    }
                    super.clearView(recyclerView, viewHolder)
                    mAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                    resetState()
                }
            }
        })
    private val mDragListener: DragListener = object : DragListener() {
        fun deleteState(isDelete: Boolean) {
            if (isDelete) {
                if (!TextUtils.equals(
                        getString(R.string.app_let_go_drag_delete),
                        tvDeleteText!!.text
                    )
                ) {
                    tvDeleteText!!.text = getString(R.string.app_let_go_drag_delete)
                    tvDeleteText!!.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_dump_delete,
                        0,
                        0
                    )
                }
            } else {
                if (!TextUtils.equals(getString(R.string.app_drag_delete), tvDeleteText!!.text)) {
                    tvDeleteText!!.text = getString(R.string.app_drag_delete)
                    tvDeleteText!!.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_normal_delete,
                        0,
                        0
                    )
                }
            }
        }

        fun dragState(isStart: Boolean) {
            if (isStart) {
                if (tvDeleteText!!.alpha == 0f) {
                    val alphaAnimator: ObjectAnimator =
                        ObjectAnimator.ofFloat(tvDeleteText, "alpha", 0f, 1f)
                    alphaAnimator.interpolator = LinearInterpolator()
                    alphaAnimator.duration = 120
                    alphaAnimator.start()
                }
            } else {
                if (tvDeleteText!!.alpha == 1f) {
                    val alphaAnimator: ObjectAnimator =
                        ObjectAnimator.ofFloat(tvDeleteText, "alpha", 1f, 0f)
                    alphaAnimator.interpolator = LinearInterpolator()
                    alphaAnimator.duration = 120
                    alphaAnimator.start()
                }
            }
        }
    }

    private fun forSystemResult(model: PictureSelectionSystemModel) {
        if (cb_attach_system_mode!!.isChecked) {
            when (resultMode) {
                ACTIVITY_RESULT -> model.forSystemResultActivity(PictureConfig.REQUEST_CAMERA)
                CALLBACK_RESULT -> model.forSystemResultActivity(com.luck.pictureselector.MainActivity.MeOnResultCallbackListener())
                else -> model.forSystemResultActivity(launcherResult)
            }
        } else {
            if (resultMode == CALLBACK_RESULT) {
                model.forSystemResult(com.luck.pictureselector.MainActivity.MeOnResultCallbackListener())
            } else {
                model.forSystemResult()
            }
        }
    }

    private fun forSelectResult(model: PictureSelectionModel) {
        when (resultMode) {
            ACTIVITY_RESULT -> model.forResult(PictureConfig.CHOOSE_REQUEST)
            CALLBACK_RESULT -> model.forResult(com.luck.pictureselector.MainActivity.MeOnResultCallbackListener())
            else -> model.forResult(launcherResult)
        }
    }

    private fun forOnlyCameraResult(model: PictureSelectionCameraModel) {
        if (cb_attach_camera_mode!!.isChecked) {
            when (resultMode) {
                ACTIVITY_RESULT -> model.forResultActivity(PictureConfig.REQUEST_CAMERA)
                CALLBACK_RESULT -> model.forResultActivity(com.luck.pictureselector.MainActivity.MeOnResultCallbackListener())
                else -> model.forResultActivity(launcherResult)
            }
        } else {
            if (resultMode == CALLBACK_RESULT) {
                model.forResult(com.luck.pictureselector.MainActivity.MeOnResultCallbackListener())
            } else {
                model.forResult()
            }
        }
    }

    /**
     * 重置
     */
    private fun resetState() {
        isHasLiftDelete = false
        mDragListener.deleteState(false)
        mDragListener.dragState(false)
    }

    /**
     * 外部预览监听事件
     */
    private inner class MyExternalPreviewEventListener() :
        OnExternalPreviewEventListener {
        fun onPreviewDelete(position: Int) {
            mAdapter.remove(position)
            mAdapter.notifyItemRemoved(position)
        }

        fun onLongPressDownload(media: LocalMedia?): Boolean {
            return false
        }
    }

    /**
     * 选择结果
     */
    private inner class MeOnResultCallbackListener() :
        OnResultCallbackListener<LocalMedia?> {
        fun onResult(result: ArrayList<LocalMedia>) {
            analyticalSelectResults(result)
        }

        fun onCancel() {
            Log.i(TAG, "PictureSelector Cancel")
        }
    }

    /**
     * 压缩引擎
     *
     * @return
     */
    private val compressFileEngine: com.luck.pictureselector.MainActivity.ImageFileCompressEngine?
        private get() = if (cb_compress!!.isChecked) com.luck.pictureselector.MainActivity.ImageFileCompressEngine() else null

    /**
     * 压缩引擎
     *
     * @return
     */
    @get:Deprecated("")
    private val compressEngine: com.luck.pictureselector.MainActivity.ImageCompressEngine?
        private get() = if (cb_compress!!.isChecked) com.luck.pictureselector.MainActivity.ImageCompressEngine() else null

    /**
     * 裁剪引擎
     *
     * @return
     */
    private val cropFileEngine: com.luck.pictureselector.MainActivity.ImageFileCropEngine?
        private get() = if (cb_crop!!.isChecked) com.luck.pictureselector.MainActivity.ImageFileCropEngine() else null

    /**
     * 裁剪引擎
     *
     * @return
     */
    private val cropEngine: com.luck.pictureselector.MainActivity.ImageCropEngine?
        private get() = if (cb_crop!!.isChecked) com.luck.pictureselector.MainActivity.ImageCropEngine() else null

    /**
     * 自定义相机事件
     *
     * @return
     */
    private val customCameraEvent: OnCameraInterceptListener?
        private get() = if (cb_custom_camera!!.isChecked) com.luck.pictureselector.MainActivity.MeOnCameraInterceptListener() else null

    /**
     * 自定义数据加载器
     *
     * @return
     */
    private val extendLoaderEngine: ExtendLoaderEngine
        private get() = com.luck.pictureselector.MainActivity.MeExtendLoaderEngine()

    /**
     * 注入自定义布局
     *
     * @return
     */
    private val injectLayoutResource: OnInjectLayoutResourceListener?
        private get() = if (cb_inject_layout!!.isChecked) com.luck.pictureselector.MainActivity.MeOnInjectLayoutResourceListener() else null

    /**
     * 处理视频缩略图
     */
    private val videoThumbnailEventListener: OnVideoThumbnailEventListener?
        private get() = if (cb_video_thumbnails!!.isChecked) com.luck.pictureselector.MainActivity.MeOnVideoThumbnailEventListener(
            videoThumbnailDir
        ) else null

    /**
     * 处理视频缩略图
     */
    private class MeOnVideoThumbnailEventListener(private val targetPath: String) :
        OnVideoThumbnailEventListener {
        fun onVideoThumbnail(
            context: Context?,
            videoPath: String?,
            call: OnKeyValueResultCallbackListener?
        ) {
            Glide.with((context)!!).asBitmap().sizeMultiplier(0.6f).load(videoPath)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
                        resource.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                        var fos: FileOutputStream? = null
                        var result: String? = null
                        try {
                            val targetFile: File = File(
                                targetPath,
                                "thumbnails_" + System.currentTimeMillis() + ".jpg"
                            )
                            fos = FileOutputStream(targetFile)
                            fos.write(stream.toByteArray())
                            fos.flush()
                            result = targetFile.absolutePath
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            PictureFileUtils.close(fos)
                            PictureFileUtils.close(stream)
                        }
                        if (call != null) {
                            call.onCallback(videoPath, result)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        if (call != null) {
                            call.onCallback(videoPath, "")
                        }
                    }
                })
        }
    }

    /**
     * 给图片添加水印
     */
    private val addBitmapWatermarkListener: OnBitmapWatermarkEventListener?
        private get() {
            return if (cb_watermark!!.isChecked) com.luck.pictureselector.MainActivity.MeBitmapWatermarkEventListener(
                sandboxMarkDir
            ) else null
        }

    /**
     * 给图片添加水印
     */
    private class MeBitmapWatermarkEventListener(private val targetPath: String) :
        OnBitmapWatermarkEventListener {
        fun onAddBitmapWatermark(
            context: Context,
            srcPath: String?,
            mimeType: String?,
            call: OnKeyValueResultCallbackListener?
        ) {
            if (PictureMimeType.isHasHttp(srcPath) || PictureMimeType.isHasVideo(mimeType)) {
                // 网络图片和视频忽略，有需求的可自行扩展
                call.onCallback(srcPath, "")
            } else {
                // 暂时只以图片为例
                Glide.with(context).asBitmap().sizeMultiplier(0.6f).load(srcPath)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val stream: ByteArrayOutputStream = ByteArrayOutputStream()
                            val watermark: Bitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.ic_mark_win
                            )
                            val watermarkBitmap: Bitmap = ImageUtil.createWaterMaskRightTop(
                                context,
                                resource,
                                watermark,
                                15,
                                15
                            )
                            watermarkBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                            watermarkBitmap.recycle()
                            var fos: FileOutputStream? = null
                            var result: String? = null
                            try {
                                val targetFile: File = File(
                                    targetPath,
                                    DateUtils.getCreateFileName("Mark_").toString() + ".jpg"
                                )
                                fos = FileOutputStream(targetFile)
                                fos.write(stream.toByteArray())
                                fos.flush()
                                result = targetFile.absolutePath
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                PictureFileUtils.close(fos)
                                PictureFileUtils.close(stream)
                            }
                            if (call != null) {
                                call.onCallback(srcPath, result)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            if (call != null) {
                                call.onCallback(srcPath, "")
                            }
                        }
                    })
            }
        }
    }

    /**
     * 权限拒绝后回调
     *
     * @return
     */
    private val permissionDeniedListener: OnPermissionDeniedListener?
        private get() {
            return if (cb_permission_desc!!.isChecked) com.luck.pictureselector.MainActivity.MeOnPermissionDeniedListener() else null
        }

    /**
     * 权限拒绝后回调
     */
    private class MeOnPermissionDeniedListener() : OnPermissionDeniedListener {
        fun onDenied(
            fragment: Fragment, permissionArray: Array<String?>,
            requestCode: Int, call: OnCallbackListener<Boolean?>?
        ) {
            val tips: String
            if (TextUtils.equals(permissionArray.get(0), PermissionConfig.CAMERA.get(0))) {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能"
            } else if (TextUtils.equals(permissionArray.get(0), Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少录音权限\n访问您设备上的音频、媒体内容和文件"
            } else {
                tips = "缺少存储权限\n访问您设备上的照片、媒体内容和文件"
            }
            val dialog: RemindDialog = RemindDialog.buildDialog(fragment.context, tips)
            dialog.setButtonText("去设置")
            dialog.setButtonTextColor(-0x828201)
            dialog.setContentTextColor(-0xcccccd)
            dialog.setOnDialogClickListener(object : RemindDialog.OnDialogClickListener() {
                fun onClick(view: View?) {
                    PermissionUtil.goIntentSetting(fragment, true, requestCode)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }
    }

    /**
     * SimpleCameraX权限拒绝后回调
     *
     * @return
     */
    private val simpleXPermissionDeniedListener: OnSimpleXPermissionDeniedListener?
        private get() {
            return if (cb_permission_desc!!.isChecked) com.luck.pictureselector.MainActivity.MeOnSimpleXPermissionDeniedListener() else null
        }

    /**
     * SimpleCameraX添加权限说明
     */
    private class MeOnSimpleXPermissionDeniedListener() :
        OnSimpleXPermissionDeniedListener {
        fun onDenied(context: Context?, permission: String?, requestCode: Int) {
            val tips: String
            if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少麦克风权限\n可能会导致录视频无法采集声音"
            } else {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能"
            }
            val dialog: RemindDialog = RemindDialog.buildDialog(context, tips)
            dialog.setButtonText("去设置")
            dialog.setButtonTextColor(-0x828201)
            dialog.setContentTextColor(-0xcccccd)
            dialog.setOnDialogClickListener(object : RemindDialog.OnDialogClickListener() {
                fun onClick(view: View?) {
                    SimpleXPermissionUtil.goIntentSetting(context as Activity?, requestCode)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }
    }

    /**
     * SimpleCameraX权限说明
     *
     * @return
     */
    private val simpleXPermissionDescriptionListener: OnSimpleXPermissionDescriptionListener?
        private get() {
            return if (cb_permission_desc!!.isChecked) com.luck.pictureselector.MainActivity.MeOnSimpleXPermissionDescriptionListener() else null
        }

    /**
     * SimpleCameraX添加权限说明
     */
    private class MeOnSimpleXPermissionDescriptionListener() :
        OnSimpleXPermissionDescriptionListener {
        fun onPermissionDescription(context: Context?, viewGroup: ViewGroup, permission: String) {
            addPermissionDescription(true, viewGroup, arrayOf(permission))
        }

        fun onDismiss(viewGroup: ViewGroup) {
            removePermissionDescription(viewGroup)
        }
    }

    /**
     * 权限说明
     *
     * @return
     */
    private val permissionDescriptionListener: OnPermissionDescriptionListener?
        private get() {
            return if (cb_permission_desc!!.isChecked) com.luck.pictureselector.MainActivity.MeOnPermissionDescriptionListener() else null
        }

    /**
     * 添加权限说明
     */
    private class MeOnPermissionDescriptionListener() :
        OnPermissionDescriptionListener {
        fun onPermissionDescription(fragment: Fragment, permissionArray: Array<String>) {
            val rootView: View = fragment.requireView()
            if (rootView is ViewGroup) {
                addPermissionDescription(false, rootView, permissionArray)
            }
        }

        fun onDismiss(fragment: Fragment) {
            removePermissionDescription(fragment.requireView() as ViewGroup)
        }
    }

    /**
     * 自定义预览
     *
     * @return
     */
    private val previewInterceptListener: OnPreviewInterceptListener?
        private get() {
            return if (cb_custom_preview!!.isChecked) com.luck.pictureselector.MainActivity.MeOnPreviewInterceptListener() else null
        }

    /**
     * 自定义预览
     *
     * @return
     */
    private class MeOnPreviewInterceptListener() : OnPreviewInterceptListener {
        fun onPreview(
            context: Context?,
            position: Int,
            totalNum: Int,
            page: Int,
            currentBucketId: Long,
            currentAlbumName: String?,
            isShowCamera: Boolean,
            data: ArrayList<LocalMedia?>?,
            isBottomPreview: Boolean
        ) {
            val previewFragment: CustomPreviewFragment = CustomPreviewFragment.newInstance()
            previewFragment.setInternalPreviewData(
                isBottomPreview, currentAlbumName, isShowCamera,
                position, totalNum, page, currentBucketId, data
            )
            FragmentInjectManager.injectFragment(
                context as FragmentActivity?,
                CustomPreviewFragment.TAG,
                previewFragment
            )
        }
    }

    /**
     * 拦截自定义提示
     */
    private class MeOnSelectLimitTipsListener() : OnSelectLimitTipsListener {
        fun onSelectLimitTips(
            context: Context?,
            config: PictureSelectionConfig?,
            limitType: Int
        ): Boolean {
            if (limitType == SelectLimitType.SELECT_NOT_SUPPORT_SELECT_LIMIT) {
                ToastUtils.showToast(context, "暂不支持的选择类型")
                return true
            }
            return false
        }
    }

    /**
     * 注入自定义布局UI，前提是布局View id 和 根目录Layout必须一致
     */
    private class MeOnInjectLayoutResourceListener() :
        OnInjectLayoutResourceListener {
        fun getLayoutResourceId(context: Context?, resourceSource: Int): Int {
            when (resourceSource) {
                InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE -> return R.layout.ps_custom_fragment_selector
                InjectResourceSource.PREVIEW_LAYOUT_RESOURCE -> return R.layout.ps_custom_fragment_preview
                InjectResourceSource.MAIN_ITEM_IMAGE_LAYOUT_RESOURCE -> return R.layout.ps_custom_item_grid_image
                InjectResourceSource.MAIN_ITEM_VIDEO_LAYOUT_RESOURCE -> return R.layout.ps_custom_item_grid_video
                InjectResourceSource.MAIN_ITEM_AUDIO_LAYOUT_RESOURCE -> return R.layout.ps_custom_item_grid_audio
                InjectResourceSource.ALBUM_ITEM_LAYOUT_RESOURCE -> return R.layout.ps_custom_album_folder_item
                InjectResourceSource.PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE -> return R.layout.ps_custom_preview_image
                InjectResourceSource.PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE -> return R.layout.ps_custom_preview_video
                InjectResourceSource.PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE -> return R.layout.ps_custom_preview_gallery_item
                else -> return 0
            }
        }
    }

    /**
     * 自定义数据加载器
     */
    private inner class MeExtendLoaderEngine() : ExtendLoaderEngine {
        fun loadAllAlbumData(
            context: Context?,
            query: OnQueryAllAlbumListener<LocalMediaFolder?>
        ) {
            val folder: LocalMediaFolder = SandboxFileLoader
                .loadInAppSandboxFolderFile(context, sandboxPath)
            val folders: MutableList<LocalMediaFolder> = ArrayList<LocalMediaFolder>()
            folders.add(folder)
            query.onComplete(folders)
        }

        fun loadOnlyInAppDirAllMediaData(
            context: Context?,
            query: OnQueryAlbumListener<LocalMediaFolder?>
        ) {
            val folder: LocalMediaFolder = SandboxFileLoader
                .loadInAppSandboxFolderFile(context, sandboxPath)
            query.onComplete(folder)
        }

        fun loadFirstPageMediaData(
            context: Context?,
            bucketId: Long,
            page: Int,
            pageSize: Int,
            query: OnQueryDataResultListener<LocalMedia?>
        ) {
            val folder: LocalMediaFolder = SandboxFileLoader
                .loadInAppSandboxFolderFile(context, sandboxPath)
            query.onComplete(folder.getData(), false)
        }

        fun loadMoreMediaData(
            context: Context?,
            bucketId: Long,
            page: Int,
            limit: Int,
            pageSize: Int,
            query: OnQueryDataResultListener<LocalMedia?>?
        ) {
        }
    }

    /**
     * 自定义编辑事件
     *
     * @return
     */
    private val customEditMediaEvent: OnMediaEditInterceptListener?
        private get() {
            return if (cbEditor!!.isChecked) com.luck.pictureselector.MainActivity.MeOnMediaEditInterceptListener(
                sandboxPath, buildOptions()
            ) else null
        }

    /**
     * 自定义编辑
     */
    private class MeOnMediaEditInterceptListener(
        private val outputCropPath: String,
        options: UCrop.Options
    ) :
        OnMediaEditInterceptListener {
        private val options: UCrop.Options
        fun onStartMediaEdit(fragment: Fragment, currentLocalMedia: LocalMedia, requestCode: Int) {
            val currentEditPath: String = currentLocalMedia.availablePath
            val inputUri: Uri =
                if (PictureMimeType.isContent(currentEditPath)) Uri.parse(currentEditPath) else Uri.fromFile(
                    File(currentEditPath)
                )
            val destinationUri: Uri = Uri.fromFile(
                File(outputCropPath, DateUtils.getCreateFileName("CROP_").toString() + ".jpeg")
            )
            val uCrop: UCrop = UCrop.of(inputUri, destinationUri)
            options.setHideBottomControls(false)
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine() {
                fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with((context)!!).load(url).override(180, 180).into((imageView)!!)
                }

                fun loadImage(
                    context: Context?,
                    url: Uri?,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: OnCallbackListener<Bitmap?>?
                ) {
                    Glide.with((context)!!).asBitmap().load(url).override(maxWidth, maxHeight)
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                if (call != null) {
                                    call.onCall(resource)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                if (call != null) {
                                    call.onCall(null)
                                }
                            }
                        })
                }
            })
            uCrop.startEdit(fragment.requireActivity(), fragment, requestCode)
        }

        init {
            this.options = options
        }
    }

    /**
     * 录音回调事件
     */
    private class MeOnRecordAudioInterceptListener() :
        OnRecordAudioInterceptListener {
        fun onRecordAudio(fragment: Fragment, requestCode: Int) {
            val recordAudio: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
            if (PermissionChecker.isCheckSelfPermission(fragment.context, recordAudio)) {
                startRecordSoundAction(fragment, requestCode)
            } else {
                addPermissionDescription(false, fragment.requireView() as ViewGroup, recordAudio)
                PermissionChecker.getInstance().requestPermissions(
                    fragment,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    object : PermissionResultCallback() {
                        fun onGranted() {
                            removePermissionDescription(fragment.requireView() as ViewGroup)
                            startRecordSoundAction(fragment, requestCode)
                        }

                        fun onDenied() {
                            removePermissionDescription(fragment.requireView() as ViewGroup)
                        }
                    })
            }
        }
    }

    /**
     * 自定义拍照
     */
    private inner class MeOnCameraInterceptListener() : OnCameraInterceptListener {
        fun openCamera(fragment: Fragment, cameraMode: Int, requestCode: Int) {
            val camera: SimpleCameraX = SimpleCameraX.of()
            camera.isAutoRotation(true)
            camera.setCameraMode(cameraMode)
            camera.setVideoFrameRate(25)
            camera.setVideoBitRate(3 * 1024 * 1024)
            camera.isDisplayRecordChangeTime(true)
            camera.isManualFocusCameraPreview(cb_camera_focus!!.isChecked)
            camera.isZoomCameraPreview(cb_camera_zoom!!.isChecked)
            camera.setOutputPathDir(sandboxCameraOutputPath)
            camera.setPermissionDeniedListener(simpleXPermissionDeniedListener)
            camera.setPermissionDescriptionListener(simpleXPermissionDescriptionListener)
            camera.setImageEngine(object : CameraImageEngine() {
                fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
                    Glide.with((context)!!).load(url).into((imageView)!!)
                }
            })
            camera.start(fragment.requireActivity(), fragment, requestCode)
        }
    }

    /**
     * 自定义沙盒文件处理
     */
    private class MeSandboxFileEngine() : UriToFileTransformEngine {
        fun onUriToFileAsyncTransform(
            context: Context?,
            srcPath: String?,
            mineType: String?,
            call: OnKeyValueResultCallbackListener?
        ) {
            if (call != null) {
                call.onCallback(
                    srcPath,
                    SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                )
            }
        }
    }

    /**
     * 自定义裁剪
     */
    private inner class ImageFileCropEngine() : CropFileEngine {
        fun onStartCrop(
            fragment: Fragment,
            srcUri: Uri?,
            destinationUri: Uri?,
            dataSource: ArrayList<String?>?,
            requestCode: Int
        ) {
            val options: UCrop.Options = buildOptions()
            val uCrop: UCrop = UCrop.of(srcUri, destinationUri, dataSource)
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine() {
                fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with((context)!!).load(url).override(180, 180).into((imageView)!!)
                }

                fun loadImage(
                    context: Context?,
                    url: Uri?,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: OnCallbackListener<Bitmap?>?
                ) {
                    Glide.with((context)!!).asBitmap().load(url).override(maxWidth, maxHeight)
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                if (call != null) {
                                    call.onCall(resource)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                if (call != null) {
                                    call.onCall(null)
                                }
                            }
                        })
                }
            })
            uCrop.start(fragment.requireActivity(), fragment, requestCode)
        }
    }

    /**
     * 自定义裁剪
     */
    private inner class ImageCropEngine() : CropEngine {
        fun onStartCrop(
            fragment: Fragment, currentLocalMedia: LocalMedia,
            dataSource: ArrayList<LocalMedia>, requestCode: Int
        ) {
            val currentCropPath: String = currentLocalMedia.availablePath
            val inputUri: Uri
            if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(
                    currentCropPath
                )
            ) {
                inputUri = Uri.parse(currentCropPath)
            } else {
                inputUri = Uri.fromFile(File(currentCropPath))
            }
            val fileName: String = DateUtils.getCreateFileName("CROP_").toString() + ".jpg"
            val destinationUri: Uri = Uri.fromFile(
                File(
                    sandboxPath, fileName
                )
            )
            val options: UCrop.Options = buildOptions()
            val dataCropSource: ArrayList<String> = ArrayList()
            for (i in dataSource.indices) {
                val media: LocalMedia = dataSource.get(i)
                dataCropSource.add(media.availablePath)
            }
            val uCrop: UCrop = UCrop.of(inputUri, destinationUri, dataCropSource)
            //options.setMultipleCropAspectRatio(buildAspectRatios(dataSource.size()));
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine() {
                fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with((context)!!).load(url).override(180, 180).into((imageView)!!)
                }

                fun loadImage(
                    context: Context?,
                    url: Uri?,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: OnCallbackListener<Bitmap?>?
                ) {
                }
            })
            uCrop.start(fragment.requireActivity(), fragment, requestCode)
        }
    }

    /**
     * 多图裁剪时每张对应的裁剪比例
     *
     * @param dataSourceCount
     * @return
     */
    private fun buildAspectRatios(dataSourceCount: Int): Array<AspectRatio?> {
        val aspectRatios: Array<AspectRatio?> = arrayOfNulls<AspectRatio>(dataSourceCount)
        for (i in 0 until dataSourceCount) {
            if (i == 0) {
                aspectRatios.get(i) = AspectRatio("16:9", 16, 9)
            } else if (i == 1) {
                aspectRatios.get(i) = AspectRatio("3:2", 3, 2)
            } else {
                aspectRatios.get(i) = AspectRatio("原始比例", 0, 0)
            }
        }
        return aspectRatios
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private fun buildOptions(): UCrop.Options {
        val options: UCrop.Options = Options()
        options.setHideBottomControls(!cb_hide!!.isChecked)
        options.setFreeStyleCropEnabled(cb_styleCrop!!.isChecked)
        options.setShowCropFrame(cb_showCropFrame!!.isChecked)
        options.setShowCropGrid(cb_showCropGrid!!.isChecked)
        options.setCircleDimmedLayer(cb_crop_circular!!.isChecked)
        options.withAspectRatio(aspect_ratio_x, aspect_ratio_y)
        options.setCropOutputPathDir(sandboxPath)
        options.isCropDragSmoothToCenter(false)
        options.setSkipCropMimeType(notSupportCrop)
        options.isForbidCropGifWebp(cb_not_gif!!.isChecked)
        options.isForbidSkipMultipleCrop(false)
        options.setMaxScaleMultiplier(100)
        if (selectorStyle != null && selectorStyle.getSelectMainStyle().getStatusBarColor() !== 0) {
            val mainStyle: SelectMainStyle = selectorStyle.getSelectMainStyle()
            val isDarkStatusBarBlack: Boolean = mainStyle.isDarkStatusBarBlack()
            val statusBarColor: Int = mainStyle.getStatusBarColor()
            options.isDarkStatusBarBlack(isDarkStatusBarBlack)
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor)
                options.setToolbarColor(statusBarColor)
            } else {
                options.setStatusBarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
                options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            }
            val titleBarStyle: TitleBarStyle = selectorStyle.getTitleBarStyle()
            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor())
            } else {
                options.setToolbarWidgetColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.ps_color_white))
        }
        return options
    }

    /**
     * 自定义压缩
     */
    private class ImageFileCompressEngine() : CompressFileEngine {
        fun onStartCompress(
            context: Context?,
            source: ArrayList<Uri?>?,
            call: OnKeyValueResultCallbackListener?
        ) {
            Luban.with(context).load(source).ignoreBy(100)
                .setRenameListener(object : OnRenameListener() {
                    fun rename(filePath: String): String {
                        val indexOf: Int = filePath.lastIndexOf(".")
                        val postfix: String =
                            if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                        return DateUtils.getCreateFileName("CMP_").toString() + postfix
                    }
                }).setCompressListener(object : OnNewCompressListener() {
                    fun onStart() {}
                    fun onSuccess(source: String?, compressFile: File) {
                        if (call != null) {
                            call.onCallback(source, compressFile.absolutePath)
                        }
                    }

                    fun onError(source: String?, e: Throwable?) {
                        if (call != null) {
                            call.onCallback(source, null)
                        }
                    }
                }).launch()
        }
    }

    /**
     * 自定义压缩
     */
    @Deprecated("")
    private class ImageCompressEngine() : CompressEngine {
        fun onStartCompress(
            context: Context?, list: ArrayList<LocalMedia>,
            listener: OnCallbackListener<ArrayList<LocalMedia?>?>
        ) {
            // 自定义压缩
            val compress: MutableList<Uri> = ArrayList()
            for (i in list.indices) {
                val media: LocalMedia = list.get(i)
                val availablePath: String = media.availablePath
                val uri: Uri =
                    if (PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(
                            availablePath
                        )
                    ) Uri.parse(availablePath) else Uri.fromFile(
                        File(availablePath)
                    )
                compress.add(uri)
            }
            if (compress.size == 0) {
                listener.onCall(list)
                return
            }
            Luban.with(context)
                .load(compress)
                .ignoreBy(100)
                .filter(object : CompressionPredicate() {
                    fun apply(path: String?): Boolean {
                        return PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(
                            path
                        )
                    }
                })
                .setRenameListener(object : OnRenameListener() {
                    fun rename(filePath: String): String {
                        val indexOf: Int = filePath.lastIndexOf(".")
                        val postfix: String =
                            if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                        return DateUtils.getCreateFileName("CMP_").toString() + postfix
                    }
                })
                .setCompressListener(object : OnCompressListener() {
                    fun onStart() {}
                    fun onSuccess(index: Int, compressFile: File) {
                        val media: LocalMedia = list.get(index)
                        if (compressFile.exists() && !TextUtils.isEmpty(compressFile.absolutePath)) {
                            media.setCompressed(true)
                            media.compressPath = compressFile.absolutePath
                            media.sandboxPath =
                                if (SdkVersionUtils.isQ()) media.compressPath else null
                        }
                        if (index == list.size - 1) {
                            listener.onCall(list)
                        }
                    }

                    fun onError(index: Int, e: Throwable?) {
                        if (index != -1) {
                            val media: LocalMedia = list.get(index)
                            media.setCompressed(false)
                            media.compressPath = null
                            media.sandboxPath = null
                            if (index == list.size - 1) {
                                listener.onCall(list)
                            }
                        }
                    }
                }).launch()
        }
    }

    /**
     * 创建相机自定义输出目录
     *
     * @return
     */
    private val sandboxCameraOutputPath: String
        private get() {
            if (cb_custom_sandbox!!.isChecked) {
                val externalFilesDir: File? = context.getExternalFilesDir("")
                val customFile: File = File(externalFilesDir!!.absolutePath, "Sandbox")
                if (!customFile.exists()) {
                    customFile.mkdirs()
                }
                return customFile.absolutePath + File.separator
            } else {
                return ""
            }
        }

    /**
     * 创建音频自定义输出目录
     *
     * @return
     */
    private val sandboxAudioOutputPath: String
        private get() {
            if (cb_custom_sandbox!!.isChecked) {
                val externalFilesDir: File? = context.getExternalFilesDir("")
                val customFile: File = File(externalFilesDir!!.absolutePath, "Sound")
                if (!customFile.exists()) {
                    customFile.mkdirs()
                }
                return customFile.absolutePath + File.separator
            } else {
                return ""
            }
        }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private val sandboxPath: String
        private get() {
            val externalFilesDir: File? = context.getExternalFilesDir("")
            val customFile: File = File(externalFilesDir!!.absolutePath, "Sandbox")
            if (!customFile.exists()) {
                customFile.mkdirs()
            }
            return customFile.absolutePath + File.separator
        }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private val sandboxMarkDir: String
        private get() {
            val externalFilesDir: File? = context.getExternalFilesDir("")
            val customFile: File = File(externalFilesDir!!.absolutePath, "Mark")
            if (!customFile.exists()) {
                customFile.mkdirs()
            }
            return customFile.absolutePath + File.separator
        }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private val videoThumbnailDir: String
        private get() {
            val externalFilesDir: File? = context.getExternalFilesDir("")
            val customFile: File = File(externalFilesDir!!.absolutePath, "Thumbnail")
            if (!customFile.exists()) {
                customFile.mkdirs()
            }
            return customFile.absolutePath + File.separator
        }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.left_back -> finish()
            R.id.minus -> {
                if (maxSelectNum > 1) {
                    maxSelectNum--
                }
                tv_select_num!!.text = maxSelectNum.toString()
                mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum)
            }
            R.id.plus -> {
                maxSelectNum++
                tv_select_num!!.text = maxSelectNum.toString()
                mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum)
            }
            R.id.video_minus -> {
                if (maxSelectVideoNum > 1) {
                    maxSelectVideoNum--
                }
                tv_select_video_num!!.text = maxSelectVideoNum.toString()
                mAdapter.setSelectMax(maxSelectVideoNum + maxSelectNum)
            }
            R.id.video_plus -> {
                maxSelectVideoNum++
                tv_select_video_num!!.text = maxSelectVideoNum.toString()
                mAdapter.setSelectMax(maxSelectVideoNum + maxSelectNum)
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        when (checkedId) {
            R.id.rb_all -> {
                chooseMode = SelectMimeType.ofAll()
                cb_preview_img!!.isChecked = true
                cb_preview_video!!.isChecked = true
                cb_isGif!!.isChecked = false
                cb_preview_video!!.isChecked = true
                cb_preview_img!!.isChecked = true
                cb_preview_video!!.visibility = View.VISIBLE
                cb_preview_img!!.visibility = View.VISIBLE
                llSelectVideoSize!!.visibility = View.VISIBLE
                cb_compress!!.visibility = View.VISIBLE
                cb_crop!!.visibility = View.VISIBLE
                cb_isGif!!.visibility = View.VISIBLE
                cb_preview_audio!!.visibility = View.GONE
            }
            R.id.rb_image -> {
                llSelectVideoSize!!.visibility = View.GONE
                chooseMode = SelectMimeType.ofImage()
                cb_preview_img!!.isChecked = true
                cb_preview_video!!.isChecked = false
                cb_isGif!!.isChecked = false
                cb_preview_video!!.isChecked = false
                cb_preview_video!!.visibility = View.GONE
                cb_preview_img!!.isChecked = true
                cb_preview_audio!!.visibility = View.GONE
                cb_preview_img!!.visibility = View.VISIBLE
                cb_compress!!.visibility = View.VISIBLE
                cb_crop!!.visibility = View.VISIBLE
                cb_isGif!!.visibility = View.VISIBLE
            }
            R.id.rb_video -> {
                llSelectVideoSize!!.visibility = View.GONE
                chooseMode = SelectMimeType.ofVideo()
                cb_preview_img!!.isChecked = false
                cb_preview_video!!.isChecked = true
                cb_isGif!!.isChecked = false
                cb_isGif!!.visibility = View.GONE
                cb_preview_video!!.isChecked = true
                cb_preview_video!!.visibility = View.VISIBLE
                cb_preview_img!!.visibility = View.GONE
                cb_preview_img!!.isChecked = false
                cb_compress!!.visibility = View.GONE
                cb_preview_audio!!.visibility = View.GONE
                cb_crop!!.visibility = View.GONE
            }
            R.id.rb_audio -> {
                chooseMode = SelectMimeType.ofAudio()
                cb_preview_audio!!.visibility = View.VISIBLE
            }
            R.id.rb_glide -> imageEngine = GlideEngine.createGlideEngine()
            R.id.rb_picasso -> imageEngine = PicassoEngine.createPicassoEngine()
            R.id.rb_coil -> imageEngine = CoilEngine()
            R.id.rb_media_player -> videoPlayerEngine = null
            R.id.rb_exo_player -> videoPlayerEngine = ExoPlayerEngine()
            R.id.rb_ijk_player -> videoPlayerEngine = IjkPlayerEngine()
            R.id.rb_system -> language = LanguageConfig.SYSTEM_LANGUAGE
            R.id.rb_jpan -> language = LanguageConfig.JAPAN
            R.id.rb_tw -> language = LanguageConfig.TRADITIONAL_CHINESE
            R.id.rb_us -> language = LanguageConfig.ENGLISH
            R.id.rb_ka -> language = LanguageConfig.KOREA
            R.id.rb_de -> language = LanguageConfig.GERMANY
            R.id.rb_fr -> language = LanguageConfig.FRANCE
            R.id.rb_spanish -> language = LanguageConfig.SPANISH
            R.id.rb_portugal -> language = LanguageConfig.PORTUGAL
            R.id.rb_ar -> {
                language = LanguageConfig.AR
                language = LanguageConfig.RU
            }
            R.id.rb_ru -> language = LanguageConfig.RU
            R.id.rb_crop_default -> {
                aspect_ratio_x = -1
                aspect_ratio_y = -1
            }
            R.id.rb_crop_1to1 -> {
                aspect_ratio_x = 1
                aspect_ratio_y = 1
            }
            R.id.rb_crop_3to4 -> {
                aspect_ratio_x = 3
                aspect_ratio_y = 4
            }
            R.id.rb_crop_3to2 -> {
                aspect_ratio_x = 3
                aspect_ratio_y = 2
            }
            R.id.rb_crop_16to9 -> {
                aspect_ratio_x = 16
                aspect_ratio_y = 9
            }
            R.id.rb_launcher_result -> resultMode = 0
            R.id.rb_activity_result -> resultMode = 1
            R.id.rb_callback_result -> resultMode = 2
            R.id.rb_photo_default_animation -> {
                val defaultAnimationStyle: PictureWindowAnimationStyle =
                    PictureWindowAnimationStyle()
                defaultAnimationStyle.setActivityEnterAnimation(R.anim.ps_anim_enter)
                defaultAnimationStyle.setActivityExitAnimation(R.anim.ps_anim_exit)
                selectorStyle.setWindowAnimationStyle(defaultAnimationStyle)
            }
            R.id.rb_photo_up_animation -> {
                val animationStyle: PictureWindowAnimationStyle = PictureWindowAnimationStyle()
                animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
                animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
                selectorStyle.setWindowAnimationStyle(animationStyle)
            }
            R.id.rb_default_style -> selectorStyle = PictureSelectorStyle()
            R.id.rb_white_style -> {
                val whiteTitleBarStyle: TitleBarStyle = TitleBarStyle()
                whiteTitleBarStyle.setTitleBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
                whiteTitleBarStyle.setTitleDrawableRightResource(R.drawable.ic_orange_arrow_down)
                whiteTitleBarStyle.setTitleLeftBackResource(R.drawable.ps_ic_black_back)
                whiteTitleBarStyle.setTitleTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_black
                    )
                )
                whiteTitleBarStyle.setTitleCancelTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_53575e
                    )
                )
                whiteTitleBarStyle.setDisplayTitleBarLine(true)
                val whiteBottomNavBarStyle: BottomNavBarStyle = BottomNavBarStyle()
                whiteBottomNavBarStyle.setBottomNarBarBackgroundColor(Color.parseColor("#EEEEEE"))
                whiteBottomNavBarStyle.setBottomPreviewSelectTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_53575e
                    )
                )
                whiteBottomNavBarStyle.setBottomPreviewNormalTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_9b
                    )
                )
                whiteBottomNavBarStyle.setBottomPreviewSelectTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_fa632d
                    )
                )
                whiteBottomNavBarStyle.setCompleteCountTips(false)
                whiteBottomNavBarStyle.setBottomEditorTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_53575e
                    )
                )
                whiteBottomNavBarStyle.setBottomOriginalTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_53575e
                    )
                )
                val selectMainStyle: SelectMainStyle = SelectMainStyle()
                selectMainStyle.setStatusBarColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
                selectMainStyle.setDarkStatusBarBlack(true)
                selectMainStyle.setSelectNormalTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_9b
                    )
                )
                selectMainStyle.setSelectTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_fa632d
                    )
                )
                selectMainStyle.setPreviewSelectBackground(R.drawable.ps_demo_white_preview_selector)
                selectMainStyle.setSelectBackground(R.drawable.ps_checkbox_selector)
                selectMainStyle.setSelectText(getString(R.string.ps_done_front_num))
                selectMainStyle.setMainListBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
                selectorStyle.setTitleBarStyle(whiteTitleBarStyle)
                selectorStyle.setBottomBarStyle(whiteBottomNavBarStyle)
                selectorStyle.setSelectMainStyle(selectMainStyle)
            }
            R.id.rb_num_style -> {
                val blueTitleBarStyle: TitleBarStyle = TitleBarStyle()
                blueTitleBarStyle.setTitleBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_blue
                    )
                )
                val numberBlueBottomNavBarStyle: BottomNavBarStyle = BottomNavBarStyle()
                numberBlueBottomNavBarStyle.setBottomPreviewNormalTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_9b
                    )
                )
                numberBlueBottomNavBarStyle.setBottomPreviewSelectTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_blue
                    )
                )
                numberBlueBottomNavBarStyle.setBottomNarBarBackgroundColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_white
                    )
                )
                numberBlueBottomNavBarStyle.setBottomSelectNumResources(R.drawable.ps_demo_blue_num_selected)
                numberBlueBottomNavBarStyle.setBottomEditorTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_53575e
                    )
                )
                numberBlueBottomNavBarStyle.setBottomOriginalTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_53575e
                    )
                )
                val numberBlueSelectMainStyle: SelectMainStyle = SelectMainStyle()
                numberBlueSelectMainStyle.setStatusBarColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_blue
                    )
                )
                numberBlueSelectMainStyle.setSelectNumberStyle(true)
                numberBlueSelectMainStyle.setPreviewSelectNumberStyle(true)
                numberBlueSelectMainStyle.setSelectBackground(R.drawable.ps_demo_blue_num_selector)
                numberBlueSelectMainStyle.setMainListBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
                numberBlueSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_demo_preview_blue_num_selector)
                numberBlueSelectMainStyle.setSelectNormalTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_9b
                    )
                )
                numberBlueSelectMainStyle.setSelectTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_blue
                    )
                )
                numberBlueSelectMainStyle.setSelectText(getString(R.string.ps_completed))
                selectorStyle.setTitleBarStyle(blueTitleBarStyle)
                selectorStyle.setBottomBarStyle(numberBlueBottomNavBarStyle)
                selectorStyle.setSelectMainStyle(numberBlueSelectMainStyle)
            }
            R.id.rb_we_chat_style -> {
                // 主体风格
                val numberSelectMainStyle: SelectMainStyle = SelectMainStyle()
                numberSelectMainStyle.setSelectNumberStyle(true)
                numberSelectMainStyle.setPreviewSelectNumberStyle(false)
                numberSelectMainStyle.setPreviewDisplaySelectGallery(true)
                numberSelectMainStyle.setSelectBackground(R.drawable.ps_default_num_selector)
                numberSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_preview_checkbox_selector)
                numberSelectMainStyle.setSelectNormalBackgroundResources(R.drawable.ps_select_complete_normal_bg)
                numberSelectMainStyle.setSelectNormalTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_53575e
                    )
                )
                numberSelectMainStyle.setSelectNormalText(getString(R.string.ps_send))
                numberSelectMainStyle.setAdapterPreviewGalleryBackgroundResource(R.drawable.ps_preview_gallery_bg)
                numberSelectMainStyle.setAdapterPreviewGalleryItemSize(
                    DensityUtil.dip2px(
                        context,
                        52
                    )
                )
                numberSelectMainStyle.setPreviewSelectText(getString(R.string.ps_select))
                numberSelectMainStyle.setPreviewSelectTextSize(14)
                numberSelectMainStyle.setPreviewSelectTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
                numberSelectMainStyle.setPreviewSelectMarginRight(DensityUtil.dip2px(context, 6))
                numberSelectMainStyle.setSelectBackgroundResources(R.drawable.ps_select_complete_bg)
                numberSelectMainStyle.setSelectText(getString(R.string.ps_send_num))
                numberSelectMainStyle.setSelectTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_white
                    )
                )
                numberSelectMainStyle.setMainListBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.ps_color_black
                    )
                )
                numberSelectMainStyle.setCompleteSelectRelativeTop(true)
                numberSelectMainStyle.setPreviewSelectRelativeBottom(true)
                numberSelectMainStyle.setAdapterItemIncludeEdge(false)

                // 头部TitleBar 风格
                val numberTitleBarStyle: TitleBarStyle = TitleBarStyle()
                numberTitleBarStyle.setHideCancelButton(true)
                numberTitleBarStyle.setAlbumTitleRelativeLeft(true)
                if (cb_only_dir!!.isChecked) {
                    numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_demo_only_album_bg)
                } else {
                    numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_album_bg)
                }
                numberTitleBarStyle.setTitleDrawableRightResource(R.drawable.ps_ic_grey_arrow)
                numberTitleBarStyle.setPreviewTitleLeftBackResource(R.drawable.ps_ic_normal_back)

                // 底部NavBar 风格
                val numberBottomNavBarStyle: BottomNavBarStyle = BottomNavBarStyle()
                numberBottomNavBarStyle.setBottomPreviewNarBarBackgroundColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_half_grey
                    )
                )
                numberBottomNavBarStyle.setBottomPreviewNormalText(getString(R.string.ps_preview))
                numberBottomNavBarStyle.setBottomPreviewNormalTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_9b
                    )
                )
                numberBottomNavBarStyle.setBottomPreviewNormalTextSize(16)
                numberBottomNavBarStyle.setCompleteCountTips(false)
                numberBottomNavBarStyle.setBottomPreviewSelectText(getString(R.string.ps_preview_num))
                numberBottomNavBarStyle.setBottomPreviewSelectTextColor(
                    ContextCompat.getColor(
                        context, R.color.ps_color_white
                    )
                )
                selectorStyle.setTitleBarStyle(numberTitleBarStyle)
                selectorStyle.setBottomBarStyle(numberBottomNavBarStyle)
                selectorStyle.setSelectMainStyle(numberSelectMainStyle)
            }
            R.id.rb_default -> animationMode = AnimationType.DEFAULT_ANIMATION
            R.id.rb_alpha -> animationMode = AnimationType.ALPHA_IN_ANIMATION
            R.id.rb_slide_in -> animationMode = AnimationType.SLIDE_IN_BOTTOM_ANIMATION
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.cb_crop -> {
                rgb_crop!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_hide!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_crop_circular!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_styleCrop!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_showCropFrame!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_showCropGrid!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_skip_not_gif!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_not_gif!!.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
            R.id.cb_custom_sandbox -> cb_only_dir!!.isChecked = isChecked
            R.id.cb_only_dir -> cb_custom_sandbox!!.isChecked = isChecked
            R.id.cb_skip_not_gif -> {
                cb_not_gif!!.isChecked = false
                cb_skip_not_gif!!.isChecked = isChecked
            }
            R.id.cb_not_gif -> {
                cb_skip_not_gif!!.isChecked = false
                cb_not_gif!!.isChecked = isChecked
            }
            R.id.cb_mode -> cb_attach_camera_mode!!.visibility =
                if (isChecked) View.GONE else View.VISIBLE
            R.id.cb_system_album -> cb_attach_system_mode!!.visibility =
                if (isChecked) View.VISIBLE else View.GONE
            R.id.cb_custom_camera -> {
                cb_camera_zoom!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                cb_camera_focus!!.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (isChecked) {
                } else {
                    cb_camera_zoom!!.isChecked = false
                    cb_camera_focus!!.isChecked = false
                }
            }
            R.id.cb_crop_circular -> {
                if (isChecked) {
                    x = aspect_ratio_x
                    y = aspect_ratio_y
                    aspect_ratio_x = 1
                    aspect_ratio_y = 1
                } else {
                    aspect_ratio_x = x
                    aspect_ratio_y = y
                }
                rgb_crop!!.visibility = if (isChecked) View.GONE else View.VISIBLE
                if (isChecked) {
                    cb_showCropFrame!!.isChecked = false
                    cb_showCropGrid!!.isChecked = false
                } else {
                    cb_showCropFrame!!.isChecked = true
                    cb_showCropGrid!!.isChecked = true
                }
            }
        }
    }

    fun onSelectFinish(result: PictureCommonFragment.SelectorResult?) {
        if (result == null) {
            return
        }
        if (result.mResultCode === RESULT_OK) {
            val selectorResult: ArrayList<LocalMedia> =
                PictureSelector.obtainSelectorList(result.mResultData)
            analyticalSelectResults(selectorResult)
        } else if (result.mResultCode === RESULT_CANCELED) {
            Log.i(TAG, "onSelectFinish PictureSelector Cancel")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST || requestCode == PictureConfig.REQUEST_CAMERA) {
                val result: ArrayList<LocalMedia> = PictureSelector.obtainSelectorList(data)
                analyticalSelectResults(result)
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "onActivityResult PictureSelector Cancel")
        }
    }

    /**
     * 创建一个ActivityResultLauncher
     *
     * @return
     */
    private fun createActivityResultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    val resultCode: Int = result.resultCode
                    if (resultCode == RESULT_OK) {
                        val selectList: ArrayList<LocalMedia> =
                            PictureSelector.obtainSelectorList(result.data)
                        analyticalSelectResults(selectList)
                    } else if (resultCode == RESULT_CANCELED) {
                        Log.i(TAG, "onActivityResult PictureSelector Cancel")
                    }
                }
            })
    }

    /**
     * 处理选择结果
     *
     * @param result
     */
    private fun analyticalSelectResults(result: ArrayList<LocalMedia>) {
        for (media: LocalMedia in result) {
            if (media.width == 0 || media.height == 0) {
                if (PictureMimeType.isHasImage(media.mimeType)) {
                    val imageExtraInfo: MediaExtraInfo =
                        MediaUtils.getImageSize(context, media.path)
                    media.width = imageExtraInfo.getWidth()
                    media.height = imageExtraInfo.getHeight()
                } else if (PictureMimeType.isHasVideo(media.mimeType)) {
                    val videoExtraInfo: MediaExtraInfo =
                        MediaUtils.getVideoSize(context, media.path)
                    media.width = videoExtraInfo.getWidth()
                    media.height = videoExtraInfo.getHeight()
                }
            }
            Log.i(TAG, "文件名: " + media.fileName)
            Log.i(TAG, "是否压缩:" + media.isCompressed())
            Log.i(TAG, "压缩:" + media.compressPath)
            Log.i(TAG, "初始路径:" + media.path)
            Log.i(TAG, "绝对路径:" + media.realPath)
            Log.i(TAG, "是否裁剪:" + media.isCut())
            Log.i(TAG, "裁剪路径:" + media.cutPath)
            Log.i(TAG, "是否开启原图:" + media.isOriginal())
            Log.i(TAG, "原图路径:" + media.originalPath)
            Log.i(TAG, "沙盒路径:" + media.sandboxPath)
            Log.i(TAG, "水印路径:" + media.getWatermarkPath())
            Log.i(TAG, "视频缩略图:" + media.videoThumbnailPath)
            Log.i(TAG, "原始宽高: " + media.width + "x" + media.height)
            Log.i(TAG, "裁剪宽高: " + media.cropImageWidth + "x" + media.cropImageHeight)
            Log.i(TAG, "文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(media.size))
        }
        runOnUiThread(object : Runnable {
            override fun run() {
                val isMaxSize: Boolean = result.size == mAdapter.getSelectMax()
                val oldSize: Int = mAdapter.getData().size()
                mAdapter.notifyItemRangeRemoved(0, if (isMaxSize) oldSize + 1 else oldSize)
                mAdapter.getData().clear()
                mAdapter.getData().addAll(result)
                mAdapter.notifyItemRangeInserted(0, result.size)
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if ((mAdapter != null) && (mAdapter.getData() != null) && (mAdapter.getData().size() > 0)) {
            outState.putParcelableArrayList(
                "selectorList",
                mAdapter.getData()
            )
        }
    }

    val context: Context
        get() {
            return this
        }

    companion object {
        private val TAG: String = "PictureSelectorTag"
        private val TAG_EXPLAIN_VIEW: String = "TAG_EXPLAIN_VIEW"
        private val ACTIVITY_RESULT: Int = 1
        private val CALLBACK_RESULT: Int = 2
        private val LAUNCHER_RESULT: Int = 3

        /**
         * 添加权限说明
         *
         * @param viewGroup
         * @param permissionArray
         */
        private fun addPermissionDescription(
            isHasSimpleXCamera: Boolean,
            viewGroup: ViewGroup,
            permissionArray: Array<String>
        ) {
            val dp10: Int = DensityUtil.dip2px(viewGroup.context, 10)
            val dp15: Int = DensityUtil.dip2px(viewGroup.context, 15)
            val view: MediumBoldTextView = MediumBoldTextView(viewGroup.context)
            view.setTag(TAG_EXPLAIN_VIEW)
            view.setTextSize(14)
            view.setTextColor(Color.parseColor("#333333"))
            view.setPadding(dp10, dp15, dp10, dp15)
            val title: String
            val explain: String
            if (TextUtils.equals(permissionArray.get(0), PermissionConfig.CAMERA.get(0))) {
                title = "相机权限使用说明"
                explain = "相机权限使用说明\n用户app用于拍照/录视频"
            } else if (TextUtils.equals(permissionArray.get(0), Manifest.permission.RECORD_AUDIO)) {
                if (isHasSimpleXCamera) {
                    title = "麦克风权限使用说明"
                    explain = "麦克风权限使用说明\n用户app用于录视频时采集声音"
                } else {
                    title = "录音权限使用说明"
                    explain = "录音权限使用说明\n用户app用于采集声音"
                }
            } else {
                title = "存储权限使用说明"
                explain = "存储权限使用说明\n用户app写入/下载/保存/读取/修改/删除图片、视频、文件等信息"
            }
            val startIndex: Int = 0
            val endOf: Int = startIndex + title.length
            val builder: SpannableStringBuilder = SpannableStringBuilder(explain)
            builder.setSpan(
                AbsoluteSizeSpan(DensityUtil.dip2px(viewGroup.context, 16)),
                startIndex,
                endOf,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            builder.setSpan(
                ForegroundColorSpan(-0xcccccd),
                startIndex,
                endOf,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            view.setText(builder)
            view.setBackground(
                ContextCompat.getDrawable(
                    viewGroup.context,
                    R.drawable.ps_demo_permission_desc_bg
                )
            )
            if (isHasSimpleXCamera) {
                val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.topMargin = DensityUtil.getStatusBarHeight(viewGroup.context)
                layoutParams.leftMargin = dp10
                layoutParams.rightMargin = dp10
                viewGroup.addView(view, layoutParams)
            } else {
                val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.topToBottom = R.id.title_bar
                layoutParams.leftToLeft = ConstraintSet.PARENT_ID
                layoutParams.leftMargin = dp10
                layoutParams.rightMargin = dp10
                viewGroup.addView(view, layoutParams)
            }
        }

        /**
         * 移除权限说明
         *
         * @param viewGroup
         */
        private fun removePermissionDescription(viewGroup: ViewGroup) {
            val tagExplainView: View = viewGroup.findViewWithTag(TAG_EXPLAIN_VIEW)
            viewGroup.removeView(tagExplainView)
        }

        /**
         * 启动录音意图
         *
         * @param fragment
         * @param requestCode
         */
        private fun startRecordSoundAction(fragment: Fragment, requestCode: Int) {
            val recordAudioIntent: Intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            if (recordAudioIntent.resolveActivity(fragment.requireActivity().packageManager) != null) {
                fragment.startActivityForResult(recordAudioIntent, requestCode)
            } else {
                ToastUtils.showToast(
                    fragment.context,
                    "The system is missing a recording component"
                )
            }
        }
    }
}