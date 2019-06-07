package com.square.android.ui.fragment.addPhoto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.data.pojo.Participation
import com.square.android.extensions.toBytes
import com.square.android.presentation.presenter.addPhoto.AddPhotoPresenter
import com.square.android.presentation.view.addPhoto.AddPhotoView
import com.square.android.ui.activity.participationDetails.EXTRA_PARTICIPATION
import com.square.android.ui.activity.participationDetails.PARTICIPATION_MAX_PHOTOS_VALUE
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.places.GridItemDecoration
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.fragment_add_photo.*
import org.jetbrains.anko.bundleOf

class AddPhotoFragment: BaseFragment(), AddPhotoView, PermissionsListener, AddPhotoAdapter.Handler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(participation: Participation): AddPhotoFragment {
            val fragment = AddPhotoFragment()

            val args = bundleOf(EXTRA_PARTICIPATION to participation)
            fragment.arguments = args

            return fragment
        }
    }

    private val permissionsManager by lazy {
        PermissionsManager(this)
    }

    @InjectPresenter
    lateinit var presenter: AddPhotoPresenter

    @ProvidePresenter
    fun providePresenter(): AddPhotoPresenter = AddPhotoPresenter(getParticipation())

    var imagesUri: MutableList<Uri?> = mutableListOf(null)

    var photosLeft: Int = 0

    private var adapter: AddPhotoAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosLeft = if (presenter.participation.photos.isNullOrEmpty()) PARTICIPATION_MAX_PHOTOS_VALUE else (PARTICIPATION_MAX_PHOTOS_VALUE - presenter.participation.photos!!.size)

        adapter = AddPhotoAdapter(imagesUri, this)
        addPhotoRv.layoutManager = GridLayoutManager(context, 3)
        addPhotoRv.adapter = adapter
        addPhotoRv.addItemDecoration(GridItemDecoration(3,addPhotoRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))

        addPhotoUpload.setOnClickListener {uploadPhotos()}
    }

    private fun uploadPhotos(){
        val uploadList: MutableList<ByteArray?> = mutableListOf()

        for(uri in imagesUri.filterNotNull()){
            uploadList.add(uri.toBytes(context!!))
        }

        presenter.uploadPhotos(uploadList.filterNotNull().toList())
    }

    override fun itemClicked(index: Int, isEmpty: Boolean) {
        if(isEmpty){
            openPhotoPicker()
        } else{
            deleteImage(index)
        }
    }

    private fun deleteImage(index: Int){
        imagesUri.removeAt(index)
        adapter!!.notifyItemRemoved(index)

        if(imagesUri.size == photosLeft && imagesUri.filterNotNull().isEmpty()){
            imagesUri.add(null)
            adapter!!.notifyItemInserted(imagesUri.size - 1)
        }
    }

    override fun showProgress() {
        addPhotoUpload.visibility = View.GONE
        addPhotoProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        addPhotoProgress.visibility = View.GONE
        addPhotoUpload.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val uri: Uri? = when (requestCode) {
            IMAGE_PICKER_RC -> data?.data
            else -> data?.extras?.get("data") as Uri? ?: FileUtils.getOutputFileUri(context!!)
        }

        uri?.let {assignPhoto(it)}
    }

    private fun assignPhoto(uri: Uri){
        imagesUri[imagesUri.size - 1] = uri
        adapter!!.notifyItemChanged(imagesUri.size - 1)

        if (imagesUri.filterNotNull().size < photosLeft) {
            imagesUri.add(null)
            adapter!!.notifyItemInserted(imagesUri.size - 1)
        }

        if(!addPhotoUpload.isEnabled) addPhotoUpload.isEnabled = true
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            FileUtils.startImagePicker(activity!!)
        }
    }

    private fun openPhotoPicker() {
        if (PermissionsManager.areCameraPermissionsGranted(context)) {
            FileUtils.startImagePicker(activity!!)
        } else {
            permissionsManager.requestCameraPermissions(activity)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    private fun getParticipation() = arguments?.getParcelable(EXTRA_PARTICIPATION) as Participation
}