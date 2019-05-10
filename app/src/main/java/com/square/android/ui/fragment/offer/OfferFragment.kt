package com.square.android.ui.fragment.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.OfferInfo
import com.square.android.presentation.presenter.offer.OfferPresenter
import com.square.android.presentation.view.offer.OfferView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_offer.*

class OfferFragment : BaseFragment(), OfferView {
    private var adapter: OfferAdapter? = null

    @InjectPresenter
    lateinit var presenter: OfferPresenter

    override fun showData(data: List<OfferInfo>) {
        adapter = OfferAdapter(data, null)

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
}
