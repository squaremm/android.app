package com.square.android.ui.fragment.redemptions

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.square.android.R
import com.square.android.presentation.presenter.redemptions.RedemptionsPresenter
import com.square.android.presentation.view.redemptions.RedemptionsView
import com.square.android.ui.fragment.LocationFragment
import kotlinx.android.synthetic.main.fragment_redemptions.*

class RedemptionsFragment : LocationFragment(), RedemptionsView, RedemptionsAdapter.Handler {
    @InjectPresenter(type = PresenterType.GLOBAL, tag = "RedemptionsPresenter")
    lateinit var presenter: RedemptionsPresenter

    private var adapter : RedemptionsAdapter? = null

    override fun showData(ordered: List<Any>) {
        adapter = RedemptionsAdapter(ordered, this)

        redemptionsList.adapter = adapter
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showProgress() {
        redemptionsProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        redemptionsProgress.visibility = View.GONE
    }

    override fun claimClicked(position: Int) {
        presenter.claimClicked(position)
    }

    override fun claimedItemClicked(position: Int) {
        presenter.claimedInfoClicked(position)
    }

    override fun removeItem(position: Int) {
        adapter?.removeItem(position)
    }

    override fun cancelClicked(position: Int) {
        presenter.cancelClicked(position)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_redemptions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        redemptionsList.setHasFixedSize(true)
    }
}
