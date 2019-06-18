package com.square.android.ui.fragment.winner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.winner.WinnerPresenter
import com.square.android.presentation.view.winner.WinnerView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_page_winner.*
import org.jetbrains.anko.dimen

class WinnerFragment(private val campaign: Campaign?): BaseFragment(), WinnerView {

    @InjectPresenter
    lateinit var presenter: WinnerPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_winner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        campaign?.userWinner?.mainImage?.let { winnerImage.loadImage(it, roundedCornersRadiusPx = context!!.dimen(R.dimen.value_4dp))}
    }

}