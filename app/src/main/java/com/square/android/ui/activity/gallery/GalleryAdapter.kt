package com.square.android.ui.activity.gallery

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.Photo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter

const val ITEM_GALLERY_VT = 0
const val ITEM_GALLERY_ADD_VT = 1

class GalleryAdapter(override var data: List<Photo>,
                     private val handler: Handler) :
        BaseAdapter<Photo, GalleryAdapter.PhotoHolder>(data) {

    override fun getLayoutId(viewType: Int) = when (viewType) {
        ITEM_GALLERY_VT -> R.layout.item_gallery
        ITEM_GALLERY_ADD_VT -> R.layout.item_gallery_add
        else -> R.layout.item_gallery
    }

    override fun instantiateHolder(view: View): PhotoHolder {
        return PhotoHolder(view, handler)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: PhotoHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val item = data[position]

    }

    override fun getViewType(position: Int)
            = if (!data[position].id.isEmpty()) ITEM_GALLERY_VT else ITEM_GALLERY_ADD_VT

    override fun bindHolder(holder: PhotoHolder, position: Int) {
        val item = data[position]
        holder.bind(item)


        val context = holder.containerView.context

        holder.image.setOnClickListener {
            if (!item.id.isEmpty()) {
                val dialog: MaterialDialog = MaterialDialog.Builder(context)
                        .title(R.string.set_image_as_main)
                        .content(R.string.set_image_as_main_info)
                        .contentColorRes(android.R.color.black)
                        .itemsColor(ContextCompat.getColor(context, R.color.nice_pink))
                        .positiveText(R.string.ok_lowercase)
                        .negativeText(R.string.cancel)
                        .cancelable(true)
                        .onPositive { dialog, action ->
                            dialog.cancel()
                            handler.itemClicked(item)
                        }
                        .onNegative { dialog, action ->
                            dialog.cancel()
                        }
                        .build()

                dialog.show()
            } else {
                handler.launchPhotoPicker()
            }
        }

        holder.delete.setOnClickListener {
            val dialog: MaterialDialog = MaterialDialog.Builder(context)
                    .title(R.string.delete_image)
                    .content(R.string.remove_item_content)
                    .contentColorRes(android.R.color.black)
                    .itemsColor( ContextCompat.getColor(context, R.color.nice_pink))
                    .positiveText(R.string.ok_lowercase)
                    .negativeText(R.string.cancel)
                    .cancelable(true)
                    .onPositive { dialog, action ->
                        dialog.cancel()
                        handler.deleteClicked(item)
                    }
                    .onNegative { dialog, action ->
                        dialog.cancel()
                    }
                    .build()

            dialog.show()
        }

        if (!item.id.isEmpty()) {
            holder.image.loadImage(item.url, roundedCornersRadiusPx = 20)
            holder.background?.visibility = if (item.isMainImage) View.VISIBLE else View.GONE
        }

    }

    class PhotoHolder(val view: View,
                      private val handler: Handler) : BaseAdapter.BaseHolder<Photo>(view) {

        lateinit var image: ImageView
        lateinit var delete: AppCompatImageView
        var background: ImageView? = null

        override fun bind(item: Photo, vararg extras: Any?) {
            image = view.findViewById(R.id.image)
            delete = view.findViewById(R.id.delete)
            background = view.findViewById(R.id.background)
        }

    }

    interface Handler {
        fun itemClicked(item: Photo)

        fun deleteClicked(item: Photo)

        fun launchPhotoPicker()
    }
}