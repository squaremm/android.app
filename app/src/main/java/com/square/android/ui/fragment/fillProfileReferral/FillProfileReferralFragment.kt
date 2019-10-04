package com.square.android.ui.fragment.fillProfileReferral

import android.app.Activity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.TokenUtils
import com.square.android.utils.ValidationCallback
import kotlinx.android.synthetic.main.fragment_fill_profile_referral.*
import org.jetbrains.anko.bundleOf

private const val EXTRA_MODEL = "EXTRA_MODEL"

private const val CODE_LENGTH = 4

class FillProfileReferralFragment : BaseFragment(), FillProfileReferralView, ValidationCallback<CharSequence> {

    // Commented because we're not saving data from previous fragment too(images)
    override fun showData(profileInfo: ProfileInfo) {
//     codeField.setText(profileInfo.referral)
    }

    override fun sendFcmToken() {
        if (presenter.repository.getUserInfo().id != 0L) {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity as Activity) { instanceIdResult ->
                val newToken = instanceIdResult.token
                TokenUtils.sendTokenToApi(App.INSTANCE, presenter.repository, newToken)
            }
        }
    }

    @InjectPresenter
    lateinit var presenter: FillProfileReferralPresenter

    @ProvidePresenter
    fun providePresenter() = FillProfileReferralPresenter(getModel())

    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fill_profile_referral, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity!!)

        initializeFields()

        fillReferralSkip.setOnClickListener { presenter.skipClicked() }

        fillReferralBtnConfirm.setOnClickListener { presenter.confirmClicked(codeField.content) }

        fillProfile4Back.setOnClickListener { activity?.onBackPressed() }

        codeField.setMaxLength(CODE_LENGTH)

        val ss = SpannableString(getString(R.string.fill_referral_title))
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, android.R.color.black)), ss.length - 12 , ss.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        fillProfileReferralLabel.text = ss
    }

    override fun isValid(item: CharSequence) = item.length == CODE_LENGTH

    override fun validityChanged(isValid: Boolean) {
        if (isValid) {
            codeField.hideKeyboard()
        }

        fillReferralBtnConfirm.isEnabled = isValid
    }

    private fun initializeFields() {
        addTextValidation(listOf(codeField), this)
    }

    override fun showProgress() {
        loadingDialog?.show()
    }

    override fun hideProgress() {
        loadingDialog?.dismiss()
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

    // Commented because we're not saving data from previous fragment too(images)
//    override fun onStop() {
//        val profileInfo = presenter.info
//        profileInfo.referral = codeField.content
//
//        presenter.saveState(profileInfo, 4)
//
//        super.onStop()
//    }

}
