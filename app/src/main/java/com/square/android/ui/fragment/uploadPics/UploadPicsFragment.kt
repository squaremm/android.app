package com.square.android.ui.fragment.uploadPics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Participation
import com.square.android.presentation.presenter.uploadPics.UploadPicsPresenter
import com.square.android.presentation.view.uploadPics.UploadPicsView
import com.square.android.ui.activity.participationDetails.EXTRA_PARTICIPATION
import com.square.android.ui.activity.participationDetails.PARTICIPATION_MAX_PHOTOS_VALUE
import com.square.android.ui.activity.participationDetails.ParticipationDetailsActivity
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.addPhoto.AddPhotoAdapter
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_upload_pics.*
import org.jetbrains.anko.bundleOf

class UploadPicsFragment: BaseFragment(), UploadPicsView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(participation: Participation): UploadPicsFragment {
            val fragment = UploadPicsFragment()

            val args = bundleOf(EXTRA_PARTICIPATION to participation)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: UploadPicsPresenter

    @ProvidePresenter
    fun providePresenter(): UploadPicsPresenter = UploadPicsPresenter(getParticipation())

    var photosLeft: Int = 0

    //TODO create new adapter with List<String>
    private var adapter: AddPhotoAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_pics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO fill the rest of widget's data

        photosLeft = if (presenter.participation.photos.isNullOrEmpty()) PARTICIPATION_MAX_PHOTOS_VALUE else (PARTICIPATION_MAX_PHOTOS_VALUE - presenter.participation.photos!!.size)


        //TODO create a new adapter similar to AddPhotoAdapter with List<String> - on notNull photo -> delete photo with asking if for sure, on null -> navigate to add_photo_fragment from ac
        //TODO copy photos from adapter.participation to new list
//        adapter = AddPhotoAdapter(imagesUri, this)
//        picsRv.layoutManager = GridLayoutManager(context, 3)
//        picsRv.adapter = adapter
//        picsRv.addItemDecoration(GridItemDecoration(3,picsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))

        picsSend.setOnClickListener {presenter.sendOverReview()}
    }

    override fun replaceToApproval() {
        (activity as ParticipationDetailsActivity).replaceToApproval()
    }

    override fun showProgress() {
        picsSend.visibility = View.GONE
        picsProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        picsProgress.visibility = View.GONE
        picsSend.visibility = View.VISIBLE
    }

    private fun getParticipation() = arguments?.getParcelable(EXTRA_PARTICIPATION) as Participation
}