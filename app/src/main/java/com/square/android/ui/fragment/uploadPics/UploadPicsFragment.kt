package com.square.android.ui.fragment.uploadPics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.uploadPics.UploadPicsPresenter
import com.square.android.presentation.view.uploadPics.UploadPicsView
import com.square.android.ui.activity.campaignDetails.*
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.places.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_upload_pics.*
import org.jetbrains.anko.bundleOf

class UploadPicsFragment: BaseFragment(), UploadPicsView, UploadPicsAdapter.Handler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(campaign: Campaign): UploadPicsFragment {
            val fragment = UploadPicsFragment()

            val args = bundleOf(EXTRA_CAMPAIGN to campaign)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: UploadPicsPresenter

    @ProvidePresenter
    fun providePresenter(): UploadPicsPresenter = UploadPicsPresenter(getCampaign())

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

        picsDaysValue1.text = presenter.campaign.daysToPicture.toString()
        picsDaysValue2.text = presenter.campaign.daysToInstagramPicture.toString()

        photosLeft = if (presenter.campaign.images.isNullOrEmpty()) CAMPAIGN_MAX_PHOTOS_VALUE else (CAMPAIGN_MAX_PHOTOS_VALUE - presenter.campaign.images!!.size)

        if(photosLeft < CAMPAIGN_MAX_PHOTOS_VALUE){

            if(photosLeft >= CAMPAIGN_MIN_PHOTOS_VALUE){
                picsSend.isEnabled = true

                picsNotEnough.visibility = View.INVISIBLE
            }
            images = presenter.campaign.images!!.map {it.url}.toMutableList()

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

    override fun reloadData(campaign: Campaign) {

        picsDaysValue1.text = presenter.campaign.daysToPicture.toString()
        picsDaysValue2.text = presenter.campaign.daysToInstagramPicture.toString()

        photosLeft = if (presenter.campaign.images.isNullOrEmpty()) CAMPAIGN_MAX_PHOTOS_VALUE else (CAMPAIGN_MAX_PHOTOS_VALUE - presenter.campaign.images!!.size)

        if(photosLeft < CAMPAIGN_MAX_PHOTOS_VALUE){

            picsSend.isEnabled = photosLeft >= CAMPAIGN_MIN_PHOTOS_VALUE
            picsNotEnough.visibility = if(photosLeft >= CAMPAIGN_MIN_PHOTOS_VALUE) View.INVISIBLE else View.VISIBLE

            images = campaign.images!!.map {it.url}.toMutableList()

            if(photosLeft > 0){
                images.add(null)
            }

        } else{
            picsSend.isEnabled = false
            picsNotEnough.visibility = View.VISIBLE
            images = mutableListOf(null)
        }

        adapter!!.assignImages(images)

        isLoading = false
    }

    override fun itemClicked(index: Int, isEmpty: Boolean) {
        if(!isLoading){
            if(isEmpty){
                (activity as CampaignDetailsActivity).navigateToAddPhoto()
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
        (activity as CampaignDetailsActivity).replaceToApproval()
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

    private fun getCampaign() = arguments?.getParcelable(EXTRA_CAMPAIGN) as Campaign
}