package com.square.android.ui.fragment.approval

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Participation
import com.square.android.presentation.presenter.approval.ApprovalPresenter
import com.square.android.presentation.view.approval.ApprovalView
import com.square.android.ui.activity.participationDetails.EXTRA_PARTICIPATION
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.entries.SquareImagesAdapter
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_approval.*
import org.jetbrains.anko.bundleOf

class ApprovalFragment: BaseFragment(), ApprovalView, SquareImagesAdapter.Handler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(participation: Participation): ApprovalFragment {
            val fragment = ApprovalFragment()

            val args = bundleOf(EXTRA_PARTICIPATION to participation)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ApprovalPresenter

    @ProvidePresenter
    fun providePresenter(): ApprovalPresenter = ApprovalPresenter(getParticipation())

    private var adapter: SquareImagesAdapter? = null

    private var dialog: PhotoDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_approval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!presenter.participation.photos.isNullOrEmpty()){

            adapter = SquareImagesAdapter(presenter.participation.photos!!.map {it.url}, this)

            approvalRv.layoutManager = GridLayoutManager(context, 3)
            approvalRv.adapter = adapter
            approvalRv.addItemDecoration(GridItemDecoration(3,approvalRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
        }

        when(presenter.participation.status){
            0 ->{
                approvalStatusCircle.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(approvalStatusCircle.context, R.color.status_yellow))
                approvalStatusText.setTextColor(ContextCompat.getColor(approvalStatusText.context, R.color.status_yellow))
                approvalStatusText.text = approvalStatusText.context.getString(R.string.status_waiting)
            }
            1 ->{
                approvalStatusCircle.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(approvalStatusCircle.context, R.color.status_orange))
                approvalStatusText.setTextColor(ContextCompat.getColor(approvalStatusText.context, R.color.status_orange))
                approvalStatusText.text = approvalStatusText.context.getString(R.string.status_under_review)
            }
            2 ->{
                approvalStatusCircle.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(approvalStatusCircle.context, R.color.status_green))
                approvalStatusText.setTextColor(ContextCompat.getColor(approvalStatusText.context, R.color.status_green))
                approvalStatusText.text = approvalStatusText.context.getString(R.string.status_approved)
            }
            3 ->{
                approvalStatusCircle.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(approvalStatusCircle.context, R.color.status_red))
                approvalStatusText.setTextColor(ContextCompat.getColor(approvalStatusText.context, R.color.status_red))
                approvalStatusText.text = approvalStatusText.context.getString(R.string.status_rejected)
            }
        }

    }

    override fun itemClicked(url: String) {
        context?.let {
            dialog = PhotoDialog(it)
            dialog!!.show(url)
        }
    }

    private fun getParticipation() = arguments?.getParcelable(EXTRA_PARTICIPATION) as Participation
}