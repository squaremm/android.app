package com.square.android.ui.fragment.fillProfileFirst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import com.square.android.R
import com.square.android.extensions.content
import com.square.android.presentation.presenter.fillProfileFirst.FillProfileFirstPresenter
import com.square.android.presentation.view.fillProfileFirst.FillProfileFirstView
import com.square.android.ui.dialogs.DatePickDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.ValidationCallback
import kotlinx.android.synthetic.main.fragment_fill_profile_1.*
import kotlinx.android.synthetic.main.profile_form_1.view.*

class FillProfileFirstFragment : BaseFragment(), FillProfileFirstView, ValidationCallback<CharSequence>, OnCountryPickerListener {

    @InjectPresenter
    lateinit var presenter: FillProfileFirstPresenter

    private var dialog: SelectGenderDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.square.android.R.layout.fragment_fill_profile_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form.formProfileBirth.setOnClickListener { showBirthDialog() }

        form.formProfileNationality.setOnClickListener { showCountryDialog() }

        form.formProfileGender.setOnClickListener { genderClicked() }

        fillProfileNext.setOnClickListener { nextClicked() }

        setUpValidation()
    }

    override fun displayNationality(country: Country) {
        form.formProfileNationality.text = country.name
    }

    private fun showCountryDialog() {
        activity?.let {
            CountryPicker.Builder().with(it)
                    .listener(this)
                    .build()
                    .showDialog(it)
        }
    }

    private fun setUpValidation() {
        val validateList = listOf(
                form.formProfileName, form.formProfileLastName,
                form.formProfileNationality, form.formProfileBirth,
                form.formProfileGender
        )

        addTextValidation(validateList, this)
    }

    override fun validityChanged(isValid: Boolean) {
        fillProfileNext.isEnabled = isValid
    }

    override fun onSelectCountry(country: Country) {
        presenter.countrySelected(country)
    }

    override fun isValid(item: CharSequence) = item.isNotEmpty()

    private fun nextClicked() {
        val name = form.formProfileName.content
        val surname = form.formProfileLastName.content

        presenter.nextClicked(name = name, surname = surname)
    }

    private fun genderClicked(){
        context?.let {

            //TODO: change SelectGenderDialog layout to something better looking
            dialog = SelectGenderDialog(it)
            dialog!!.show(){
                when(it){
                    1 -> presenter.genderSelected(getString(R.string.gender_female))
                    2 -> presenter.genderSelected(getString(R.string.gender_male))
                }
            }
        }
    }

    override fun displayGender(gender: String) {
        form.formProfileGender.text = gender
    }

    override fun showBirthday(birthday: String) {
        form.formProfileBirth.text = birthday
    }

    private fun showBirthDialog() {
        DatePickDialog(activity!!)
                .show { date: String ->
                    presenter.birthSelected(date)
                }
    }
}
