package com.square.android.ui.fragment.scanQr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.presentation.presenter.scanQr.ScanQrPresenter
import com.square.android.presentation.view.scanQr.ScanQrView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.PermissionsManager
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.fragment_scan_qr.*
import io.fotoapparat.preview.Frame
import io.fotoapparat.exception.camera.CameraException
import io.fotoapparat.error.CameraErrorListener
import io.fotoapparat.preview.FrameProcessor
import org.jetbrains.annotations.NotNull
import android.graphics.Bitmap
import android.content.Context
import android.media.ThumbnailUtils
import android.os.Handler
import android.os.Looper
import android.renderscript.*
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.square.android.ui.dialogs.LoadingDialog

class ScanQrFragment: BaseFragment(), ScanQrView, PermissionsListener {

    @InjectPresenter
    lateinit var presenter: ScanQrPresenter

    @ProvidePresenter
    fun providePresenter() = ScanQrPresenter()

    private val permissionsManager by lazy {
        PermissionsManager(this)
    }

    private var fotoapparat: Fotoapparat? = null

    private var codeObtained = false

    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PermissionsManager.areCameraPermissionsGranted(activity!!)) {
            setup()
        } else {
            permissionsManager.requestCameraPermissions(this)
        }

        loadingDialog = LoadingDialog(activity!!)
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            setup()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {}

    private fun setup() {
        fotoapparat = Fotoapparat
                .with(activity!!)
                .into(scanQrCamera)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(back())
                .focusMode(firstAvailable(
                        continuousFocusPicture(),
                        autoFocus(),
                        fixed()
                ))
                .frameProcessor(object : FrameProcessor {
                    override fun process(frame: Frame) {
                        if (!codeObtained) {
                            codeObtained = true
                            processFrame(frame)
                        }
                    }
                })
                .cameraErrorCallback(object : CameraErrorListener {
                    override fun onError(@NotNull e: CameraException) {

                    }
                })
                .build()

        startFotoapparat()
    }

    override fun startFotoapparat() {
        codeObtained = false
        fotoapparat!!.start()
    }

    private fun processFrame(frame: Frame) {
        try {
            var bitmap = Bitmap.createBitmap(frame.size.width, frame.size.height, Bitmap.Config.ARGB_8888)
            val bmData = renderScriptNV21ToRGBA8888(
                    activity!!, frame.size.width, frame.size.height, frame.image)
            bmData.copyTo(bitmap)

            bitmap = ThumbnailUtils.extractThumbnail(bitmap, scanQrCamera.measuredWidth, scanQrCamera.measuredHeight)

            val codeString: String? = scanQRImage(bitmap)

            codeString?.let {
                fotoapparat!!.stop()

                println("EEEE qr code: $it")

                presenter.scanQr(it)
            } ?: run { codeObtained = false }

        } catch (e: Exception) {
            codeObtained = false
        }
    }

    private fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null

        val intArray = IntArray(bMap.width * bMap.height)
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

        val source = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
        } catch (e: Exception) {
        }

        return contents
    }

    private fun renderScriptNV21ToRGBA8888(context: Context, width: Int, height: Int, nv21: ByteArray): Allocation {
        val rs = RenderScript.create(context)
        val yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

        val yuvType = Type.Builder(rs, Element.U8(rs)).setX(nv21.size)
        val `in` = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)

        val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height)
        val out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)

        `in`.copyFrom(nv21)

        yuvToRgbIntrinsic.setInput(`in`)
        yuvToRgbIntrinsic.forEach(out)
        return out
    }

    override fun showProgress() {
        Handler(Looper.getMainLooper()).post {
            loadingDialog?.show()
        }
    }

    override fun hideProgress() {
        Handler(Looper.getMainLooper()).post {
            loadingDialog?.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        startFotoapparat()
    }

    override fun onPause() {
        super.onPause()
        fotoapparat?.stop()
    }

}
