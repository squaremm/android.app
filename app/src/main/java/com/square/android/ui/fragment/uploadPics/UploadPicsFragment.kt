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
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_upload_pics.*
import org.jetbrains.anko.bundleOf

class UploadPicsFragment: BaseFragment(), UploadPicsView, UploadPicsAdapter.Handler {

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

    private var isLoading: Boolean = false

    var images: MutableList<String?> = mutableListOf()

    private var adapter: UploadPicsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_pics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        picsDaysLeft.text = presenter.participation.daysLeft.toString()

        photosLeft = if (presenter.participation.photos.isNullOrEmpty()) PARTICIPATION_MAX_PHOTOS_VALUE else (PARTICIPATION_MAX_PHOTOS_VALUE - presenter.participation.photos!!.size)

        if(photosLeft < PARTICIPATION_MAX_PHOTOS_VALUE){
            picsSend.isEnabled = true

            images = presenter.participation.photos!!.map {it.url}.toMutableList()

            if(photosLeft > 0){
                images.add(null)
            }

        } else{
            images = mutableListOf(null)
        }

        adapter = UploadPicsAdapter(images, this)
        picsRv.layoutManager = GridLayoutManager(context, 3)
        picsRv.adapter = adapter
        picsRv.addItemDecoration(GridItemDecoration(3, picsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))

        picsSend.setOnClickListener {presenter.sendOverReview()}
    }

    override fun reloadData(participation: Participation) {
        picsDaysLeft.text = participation.daysLeft.toString()

        photosLeft = if (participation.photos.isNullOrEmpty()) PARTICIPATION_MAX_PHOTOS_VALUE else (PARTICIPATION_MAX_PHOTOS_VALUE - participation.photos!!.size)

        if(photosLeft < PARTICIPATION_MAX_PHOTOS_VALUE){
            picsSend.isEnabled = true

            images = participation.photos!!.map {it.url}.toMutableList()

            if(photosLeft > 0){
                images.add(null)
            }

        } else{
            picsSend.isEnabled = false
            images = mutableListOf(null)
        }

        adapter!!.assignImages(images)

        isLoading = false
    }

    override fun itemClicked(index: Int, isEmpty: Boolean) {
        if(!isLoading){
            if(isEmpty){
                (activity as ParticipationDetailsActivity).navigateToAddPhoto()
            } else{

                //TODO ask if user is sure to delete this photo, if yes - fire code below
                deletePhoto(index)
            }
        }
    }

    private fun deletePhoto(index: Int){
        presenter.removePhoto(index)
    }

    override fun replaceToApproval() {
        (activity as ParticipationDetailsActivity).replaceToApproval()
    }

    override fun showProgress() {
        isLoading = true
        picsSend.visibility = View.GONE
        picsProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        isLoading = false
        picsProgress.visibility = View.GONE
        picsSend.visibility = View.VISIBLE
    }

    private fun getParticipation() = arguments?.getParcelable(EXTRA_PARTICIPATION) as Participation
}