package com.square.android.ui.fragment.fillProfileSecond

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
import com.square.android.presentation.presenter.fillProfileSecond.FillProfileSecondPresenter
import com.square.android.presentation.view.fillProfileSecond.FillProfileSecondView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_fill_profile_2.*
import kotlinx.android.synthetic.main.profile_form_2.view.*
import org.jetbrains.anko.bundleOf

private const val EXTRA_MODEL = "EXTRA_MODEL"

class FillProfileSecondFragment: BaseFragment(), FillProfileSecondView {

    override fun showData(profileInfo: ProfileInfo) {
        form.formProfileAccount.setText(profileInfo.email)
        form.formProfileMotherAgency.setText(profileInfo.motherAgency)
//        form.formProfileCurrentAgency.setText(profileInfo.currentAgency)
    }

    companion object {

        @Suppress("DEPRECATION")
        fun newInstance(info: ProfileInfo): FillProfileSecondFragment {
            val fragment = FillProfileSecondFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: FillProfileSecondPresenter

    @ProvidePresenter
    fun providePresenter(): FillProfileSecondPresenter = FillProfileSecondPresenter(getModel())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fill_profile_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillProfile2Next.setOnClickListener {
            nextClicked()
        }

        fillProfile2Back.setOnClickListener { activity?.onBackPressed() }
    }

    fun isValid(item: CharSequence) = item.isNotEmpty()

    private fun nextClicked() {
        if(!isValid(form.formProfileAccount.content)){
            form.formProfileAccount.showCustomError(getString(R.string.username_error))
        }

        if(!isValid(form.formProfileMotherAgency.content)){
            form.formProfileMotherAgency.showCustomError(getString(R.string.mother_agency_error))
        }

//        if(!isValid(form.formProfileCurrentAgency.content)){
//            form.formProfileCurrentAgency.showCustomError(getString(R.string.current_agency_error))
//        }

        if(!form.formProfileAccount.errorShowing && !form.formProfileMotherAgency.errorShowing){
//        if(!form.formProfileAccount.errorShowing && !form.formProfileMotherAgency.errorShowing && !form.formProfileCurrentAgency.errorShowing){

            val account = form.formProfileAccount.content

            val motherAgency = form.formProfileMotherAgency.content

//            val currentAgency = form.formProfileCurrentAgency.content

            presenter.nextClicked(account = account,
                    motherAgency = motherAgency)

//            presenter.nextClicked(account = account,
//                    motherAgency = motherAgency,
//                    currentAgency = currentAgency)

            activity?.hideKeyboard()
        }
    }

    private fun getModel() = arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo

    override fun onStop() {
        val profileInfo = presenter.info

        if(isValid(form.formProfileAccount.content)){
           profileInfo.instagramName = form.formProfileAccount.content
        }

        if(isValid(form.formProfileMotherAgency.content)){
            profileInfo.motherAgency = form.formProfileMotherAgency.content
        }

//        if(isValid(form.formProfileCurrentAgency.content)){
//            profileInfo.currentAgency = form.formProfileCurrentAgency.content
//        }

        presenter.saveState(profileInfo, 2)

        super.onStop()
    }
}
