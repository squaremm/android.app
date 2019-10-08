package com.square.android.ui.activity.passEligible

import android.os.Parcelable
import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.item_feature.*

@Parcelize
@JsonClass(generateAdapter = true)
class Feature(var text: String = "", var secondaryText: String = "") : Parcelable

class FeatureAdapter(data: List<Feature>) :
        BaseAdapter<Feature, FeatureAdapter.Holder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_feature

    override fun instantiateHolder(view: View): Holder {
        return Holder(view)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: Holder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, position)
    }

    override fun bindHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }

    class Holder(view: View) : BaseAdapter.BaseHolder<Feature>(view) {
        override fun bind(item: Feature, vararg extras: Any?) {
            itemFeatureText.text = item.text
            itemFeatureTextSecondary.text = item.secondaryText
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}