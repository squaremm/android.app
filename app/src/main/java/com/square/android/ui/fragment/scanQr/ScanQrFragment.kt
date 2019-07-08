package com.square.android.ui.fragment.scanQr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.scanQr.ScanQrPresenter
import com.square.android.presentation.view.scanQr.ScanQrView
import com.square.android.ui.fragment.BaseFragment
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.fragment_scan_qr.*
import io.fotoapparat.preview.Frame

class ScanQrFragment: BaseFragment(), ScanQrView {

    @InjectPresenter
    lateinit var presenter: ScanQrPresenter

    @ProvidePresenter
    fun providePresenter() = ScanQrPresenter()

    private var fotoapparat: Fotoapparat? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fotoapparat = Fotoapparat(context = activity!!,
                view = scanQrCamera,
                scaleType = ScaleType.CenterCrop,
                lensPosition = back(),
                cameraConfiguration = CameraConfiguration(frameProcessor = this::handleFrame))

        fotoapparat!!.start()
    }

    private fun handleFrame(frame: Frame){

        println("EEEE frame: "+ frame.toString())
    }

    override fun showProgress() {
        scanQrProgress.visibility = View.VISIBLE

        presenter.scanning = true
    }

    override fun hideProgress() {
        scanQrProgress.visibility = View.GONE

        presenter.scanning = false
    }

    override fun onStart() {
        super.onStart()
        fotoapparat?.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
    }
}
