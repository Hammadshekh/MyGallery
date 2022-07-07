package com.example.mygallery.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.mygallery.adapter.CustomPreviewAdapter
import com.example.mygallery.adapter.PicturePreviewAdapter
import com.example.selector.PictureSelectorPreviewFragment
import com.example.selector.adapter.holder.BasePreviewHolder
import com.example.selector.adapter.holder.PreviewVideoHolder
import com.example.selector.magical.BuildRecycleItemViewParams
import com.example.selector.magical.MagicalView
import com.example.selector.magical.OnMagicalViewCallback
import com.example.selector.magical.ViewParams
import com.example.selector.utils.MediaUtils
import com.example.selector.widget.TitleBar
import com.luck.picture.lib.entity.LocalMedia

class CustomPreviewFragment : PictureSelectorPreviewFragment() {
    val fragmentTag: String
        get() = CustomPreviewFragment::class.java.simpleName

    override fun createAdapter(): PicturePreviewAdapter {
        return CustomPreviewAdapter()
    }

    companion object {
        fun newInstance(): CustomPreviewFragment {
            val fragment = CustomPreviewFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }
}
