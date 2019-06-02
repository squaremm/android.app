package com.square.android.ui.fragment.fillProfileReferral

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.firebase.iid.FirebaseInstanceId
import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.ProfileInfo
import com.square.android.extensions.content
import com.square.android.extensions.hideKeyboard
import com.square.android.presentation.presenter.fillProfileReferral.FillProfileReferralPresenter
import com.square.android.presentation.view.fillProfileReferral.FillProfileReferralView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.TokenUtils
import com.square.android.utils.ValidationCallback
import kotlinx.android.synthetic.main.fragment_fill_profile_referral.*
import org.jetbrains.anko.bundleOf

private const val EXTRA_MODEL = "EXTRA_MODEL"

private const val POSITION_CONTENT = 0
private const val POSITION_PROGRESS = 1

private const val CODE_LENGTH = 4

class FillProfileReferralFragment : BaseFragment(), FillProfileReferralView, ValidationCallback<CharSequence> {

    override fun showData(profileInfo: ProfileInfo) {
     codeField.setText(profileInfo.referral)
    }

    override fun sendFcmToken() {
        if (presenter.repository.getUserInfo().id != 0L) {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity as Activity) { instanceIdResult ->
                val newToken = instanceIdResult.token
                TokenUtils.sendTokenToApi(App.INSTANCE, presenter.repository, newToken)
            }
        }
    }

    private var isValid: Boolean = false

    @InjectPresenter
    lateinit var presenter: FillProfileReferralPresenter

    @ProvidePresenter
    fun providePresenter() = FillProfileReferralPresenter(getModel())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fill_profile_referral, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeFields()

        fillReferralSkip.setOnClickListener {skipClick()}

        codeField.setMaxLength(CODE_LENGTH)
    }

    private fun skipClick(){
        if(!isValid){
            presenter.skipClicked()
        } else{
            codeReady()
        }
    }

    private fun codeReady(){
        val code = codeField.content

        presenter.confirmClicked(code)
    }

    override fun showProgress() {
        fillReferralFlipper.displayedChild = POSITION_PROGRESS
    }

    override fun hideProgress() {
        fillReferralFlipper.displayedChild = POSITION_CONTENT
    }

    override fun isValid(item: CharSequence) = item.length == CODE_LENGTH

    override fun validityChanged(isValid: Boolean) {
        if (isValid) {
            codeReady()
            codeField.hideKeyboard()
        }

        this.isValid = isValid

        fillReferralSkip.text = if(isValid) getString(R.string.confirm) else getString(R.string.skip)
    }

    private fun initializeFields() {
        addTextValidation(listOf(codeField), this)
    }

    private fun getModel() = arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: ProfileInfo): FillProfileReferralFragment {
            val fragment = FillProfileReferralFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }

    }

    override fun onStop() {
        val profileInfo = presenter.info
        profileInfo.referral = codeField.content
        presenter.keptImages = profileInfo.images
        profileInfo.images = null

        //this must be be 3 not 4
        presenter.saveState(profileInfo, 3)

        profileInfo.images = presenter.keptImages

        super.onStop()
    }
}
