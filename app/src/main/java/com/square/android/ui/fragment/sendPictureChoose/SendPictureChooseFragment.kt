package com.square.android.ui.fragment.sendPictureChoose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.SendPictureType
import com.square.android.presentation.presenter.sendPictureChoose.SendPictureChoosePresenter
import com.square.android.presentation.view.sendPictureChoose.SendPictureChooseView
import com.square.android.ui.activity.sendPicture.INDEX_EXTRA
import com.square.android.ui.activity.sendPicture.SendPictureActivity
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_send_picture_choose.*
import org.jetbrains.anko.bundleOf

class SendPictureChooseFragment: BaseFragment(), SendPictureChooseView, SendPictureAdapter.Handler{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(index: Int): SendPictureChooseFragment {
            val fragment = SendPictureChooseFragment()

            val args = bundleOf(INDEX_EXTRA to index)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SendPictureChoosePresenter

    @ProvidePresenter
    fun providePresenter(): SendPictureChoosePresenter = SendPictureChoosePresenter(getIndex())

    private var adapter: SendPictureAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_picture_choose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SendPictureActivity).changeTitle(0)
        (activity as SendPictureActivity).lastFragment = true
    }

    override fun showData(items: List<SendPictureType>) {
        adapter = SendPictureAdapter(items, this)

        pictureChooseRv.layoutManager = GridLayoutManager(context, 2)
        pictureChooseRv.adapter = adapter
        pictureChooseRv.addItemDecoration(GridItemDecoration(2, pictureChooseRv.context.resources.getDimension(R.dimen.value_12dp).toInt(), false))
    }

    override fun itemClicked(index: Int) {
        presenter.typeSelected(index)
    }

    private fun getIndex() = arguments?.getInt(INDEX_EXTRA, 0) ?: 0
}

