package com.square.android.ui.fragment.fillProfileSecond

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
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
private const val COUNTRY_DEFAULT_ISO = "US"

class FillProfileSecondFragment : BaseFragment(), FillProfileSecondView, OnCountryPickerListener {
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

    private lateinit var countryPicker: CountryPicker

    @ProvidePresenter
    fun providePresenter(): FillProfileSecondPresenter = FillProfileSecondPresenter(getModel())

    override fun showDialInfo(country: Country) {
        form.formDialCode.text = country.dialCode
        form.formDialFlag.setImageResource(country.flag)
    }

    override fun onSelectCountry(country: Country) {
        presenter.countrySelected(country)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fill_profile_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form.formDialPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        form.formDialPhoneNumber.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                form.formDialPhoneLl.setBackgroundResource(R.drawable.input_field_enabled)
            } else{
                form.formDialPhoneLl.setBackgroundResource(R.drawable.input_field_disabled)
            }
         }

        fillProfile2Next.setOnClickListener {
            nextClicked()
        }

        form.formDialCountryLl.setOnClickListener {showCountryDialog()}

        showDefaultDialInfo()
    }


    fun isValid(item: CharSequence) = item.isNotEmpty()

    private fun showDefaultDialInfo() {

        //TODO: change CountryPicker dialog design
        countryPicker = CountryPicker.Builder().with(activity!!)
                .listener(this)
                .build()

        val country = countryPicker.getCountryByISO(COUNTRY_DEFAULT_ISO)

        showDialInfo(country)
    }

    private fun showCountryDialog() {
        activity?.let {
            countryPicker.showDialog(it)
        }
    }

    private fun nextClicked() {
        if(!isValid(form.formProfileAccount.content)){
            form.formProfileAccount.showCustomError(getString(R.string.username_error))
        }

        if(!isValid(form.formDialPhoneNumber.content)){
            form.formDialPhoneNumber.showCustomError(getString(R.string.phone_error))
        }

        if(!isValid(form.formProfileMotherAgency.content)){
            form.formProfileMotherAgency.showCustomError(getString(R.string.mother_agency_error))
        }

        if(!isValid(form.formProfileCurrentAgency.content)){
            form.formProfileCurrentAgency.showCustomError(getString(R.string.current_agency_error))
        }

        if(!form.formProfileAccount.errorShowing && !form.formDialPhoneNumber.errorShowing && !form.formProfileMotherAgency.errorShowing && !form.formProfileCurrentAgency.errorShowing){

            val account = form.formProfileAccount.content

            val phone = "${form.formDialCode.content} ${form.formDialPhoneNumber.content}"

            val motherAgency = form.formProfileMotherAgency.content

            val currentAgency = form.formProfileCurrentAgency.content

            presenter.nextClicked(account = account,
                    phone = phone,
                    motherAgency = motherAgency,
                    currentAgency = currentAgency)

            activity?.hideKeyboard()
        }
    }

    private fun getModel(): ProfileInfo {
        return arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo
    }
}
