package com.square.android.ui.fragment.fillProfileReferral

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.ProfileInfo
import com.square.android.extensions.content
import com.square.android.extensions.hideKeyboard
import com.square.android.presentation.presenter.fillProfileReferral.FillProfileReferralPresenter
import com.square.android.presentation.view.fillProfileReferral.FillProfileReferralView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.ValidationCallback
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_fill_profile_referral.*
import org.jetbrains.anko.bundleOf

private const val EXTRA_MODEL = "EXTRA_MODEL"

private const val POSITION_CONTENT = 0
private const val POSITION_PROGRESS = 1

private const val CODE_LENGTH = 4

class FillProfileReferralFragment : BaseFragment(), FillProfileReferralView, ValidationCallback<CharSequence> {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: ProfileInfo): FillProfileReferralFragment {
            val fragment = FillProfileReferralFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }

    }

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

        fillReferralSkip.setOnClickListener { presenter.skipClicked() }

        fillReferralSubmit.setOnClickListener { codeReady() }

        codeField.setMaxLength(CODE_LENGTH)
    }

    private fun codeReady() {
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
        val confirmVisibility = if (isValid) View.VISIBLE else View.INVISIBLE

        if (isValid) {
            codeReady()
            codeField.hideKeyboard()
        }

        fillReferralSubmit.visibility = confirmVisibility
    }

    private fun initializeFields() {
        addTextValidation(listOf(codeField), this)
    }


    private fun getModel(): ProfileInfo {
        return arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo
    }

    override fun showPendingUser() {
        pending_splash_image.visibility = View.VISIBLE
    }

}
