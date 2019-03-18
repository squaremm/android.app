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
import com.square.android.utils.ValidationCallback
import kotlinx.android.synthetic.main.fragment_fill_profile_2.*
import kotlinx.android.synthetic.main.profile_form_2.view.*
import org.jetbrains.anko.bundleOf

private const val EXTRA_MODEL = "EXTRA_MODEL"

private const val COUNTRY_DEFAULT_ISO = "US"

class FillProfileSecondFragment : BaseFragment(), FillProfileSecondView, ValidationCallback<CharSequence>, OnCountryPickerListener {
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

        form.formProfilePhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        fillProfileNext.setOnClickListener {
            nextClicked()
        }

        form.formDialCode.setOnClickListener { showCountryDialog() }

        setUpValidation()

        showDefaultDialInfo()
    }

    override fun validityChanged(isValid: Boolean) {
        val visibility = if (isValid) View.VISIBLE else View.INVISIBLE

        fillProfileNext.visibility = visibility
    }

    override fun isValid(item: CharSequence) = item.isNotEmpty()

    private fun showDefaultDialInfo() {
        countryPicker = CountryPicker.Builder().with(activity!!)
                .listener(this)
                .build()

        val country = countryPicker.getCountryByISO(COUNTRY_DEFAULT_ISO)

        showDialInfo(country)
    }

    private fun setUpValidation() {
        val validateList = listOf(
                form.formProfileEmail, form.formProfilePhone,
                form.formProfileMotherAgency, form.formProfileCurrentAgency
        )

        addTextValidation(validateList, this)
    }

    private fun showCountryDialog() {
        activity?.let {
            countryPicker.showDialog(it)
        }
    }

    private fun nextClicked() {
        val email = form.formProfileEmail.content

        val phone = "${form.formDialCode.content} ${form.formProfilePhone.content}"

        val motherAgency = form.formProfileMotherAgency.content

        val currentAgency = form.formProfileCurrentAgency.content

        presenter.nextClicked(email = email,
                phone = phone,
                motherAgency = motherAgency,
                currentAgency = currentAgency)

        activity?.hideKeyboard()
    }

    private fun getModel(): ProfileInfo {
        return arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo
    }
}
