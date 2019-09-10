package com.square.android.ui.fragment.claimedActions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.presentation.view.claimedActions.ClaimedActionsView
import com.square.android.presentation.presenter.claimedActions.ClaimedActionsPresenter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.data.pojo.*
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import com.square.android.ui.fragment.review.*
import kotlinx.android.synthetic.main.fragment_claimed_actions.*

class ClaimedActionsFragment : BaseFragment(), ClaimedActionsView, ReviewAdapter.Handler, ReviewDialog.Handler {

    @InjectPresenter
    lateinit var presenter: ClaimedActionsPresenter

    @ProvidePresenter
    fun providePresenter() = ClaimedActionsPresenter()

    private var adapter: ReviewAdapter? = null

    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_claimed_actions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionsList.setHasFixedSize(true)

        loadingDialog = LoadingDialog(activity!!)
    }

    override fun showData(data: Offer, actions: List<Offer.Action>) {
        adapter = ReviewAdapter(actions, this)
        actionsList.layoutManager = LinearLayoutManager(actionsList.context, RecyclerView.VERTICAL, false)
        actionsList.addItemDecoration(MarginItemDecorator(actionsList.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), vertical = true))
        actionsList.adapter = adapter
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun disableAction(position: Int) {
        adapter?.disableAction(position)
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }

    override fun showDialog(index: Int, action: Offer.Action,subActions: List<Offer.Action>, instaName: String, fbName: String ) {
        val dialog = ReviewDialog(index, action, subActions, instaName, fbName, this, false, true)
        dialog.show(fragmentManager, "")
    }

    override fun sendClicked(index: Int, photo: ByteArray) {
        presenter.addReview(index, photo)
    }

    override fun showProgress() {}

    override fun hideProgress() {}
}
