package com.square.android.ui.fragment.introPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import com.square.android.presentation.view.introPage.IntroPageView
import com.square.android.presentation.presenter.introPage.IntroPagePresenter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.data.pojo.IntroPage
import com.square.android.extensions.loadImage
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_intro_page.*
import org.jetbrains.anko.bundleOf

const val EXTRA_PAGE_KEY = "EXTRA_PAGE_KEY"

class IntroPageFragment : BaseFragment(), IntroPageView {
    companion object {

        @Suppress("DEPRECATION")
        fun newInstance(page: IntroPage): IntroPageFragment {
            val fragment = IntroPageFragment()

            val args: Bundle = bundleOf(EXTRA_PAGE_KEY to page)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: IntroPagePresenter

    var onClickListener: View.OnClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val page = getPage()

        page?.let {
            introPageContent.setText(it.contentRes)
            introPageTitle.setText(it.titleRes)

            introPageImage.loadImage(it.imageRes,true)
        }

        if (onClickListener == null) {
            selectOfferSubmit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            selectOfferSubmit.setPadding(0,0,0,0)
            selectOfferSubmit.text = getString(R.string.lets_start)
        }

        selectOfferSubmit.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(null)
            } else {
                presenter.nextClicked()
            }
        }
    }

    private fun getPage() : IntroPage? {
        return arguments?.getParcelable(EXTRA_PAGE_KEY) as? IntroPage
    }
}
