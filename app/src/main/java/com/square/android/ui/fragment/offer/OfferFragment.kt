package com.square.android.ui.fragment.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.offer.OfferPresenter
import com.square.android.presentation.view.offer.OfferView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_offer.*

class OfferFragment(private val place: Place?) : BaseFragment(), OfferView, OfferAdapter.Handler {

    private var adapter: OfferAdapter? = null

    private var dialog: OfferDialog? = null

    @InjectPresenter
    lateinit var presenter: OfferPresenter

    override fun showData(data: List<OfferInfo>) {
        adapter = OfferAdapter(data, this)

        offerList.adapter = adapter

        offerList.addItemDecoration(MarginItemDecorator( offerList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),true,
                offerList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),
                offerList.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt()
        ))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offerList.setHasFixedSize(true)
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position, place)
    }

    override fun setSelectedItem(position: Int) {
        adapter?.setSelectedItem(position)
    }

    override fun showOfferDialog(offer: OfferInfo, place: Place?) {
        context?.let {
            dialog = OfferDialog(it)
            dialog!!.show(offer, place)
        }
    }
}
