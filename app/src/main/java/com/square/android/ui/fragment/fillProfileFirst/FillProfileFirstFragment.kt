package com.square.android.ui.fragment.fillProfileFirst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import com.square.android.extensions.content
import com.square.android.presentation.presenter.fillProfileFirst.FillProfileFirstPresenter
import com.square.android.presentation.view.fillProfileFirst.FillProfileFirstView
import com.square.android.ui.dialogs.DatePickDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.ValidationCallback
import kotlinx.android.synthetic.main.fragment_fill_profile_1.*
import kotlinx.android.synthetic.main.profile_form_1.view.*


private const val GENDER_MALE = "male"
private const val GENDER_FEMALE = "female"

class FillProfileFirstFragment : BaseFragment(), FillProfileFirstView, ValidationCallback<CharSequence>, OnCountryPickerListener {
    @InjectPresenter
    lateinit var presenter: FillProfileFirstPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.square.android.R.layout.fragment_fill_profile_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form.formProfileBirth.setOnClickListener { showBirthDialog() }

        form.formProfileNationality.setOnClickListener { showCountryDialog() }

        fillProfileNext.setOnClickListener { nextClicked() }

        setUpValidation()
    }

    override fun displayNationality(country: Country) {
        form.formProfileNationality.text = country.name

        form.formFlag.visibility = View.VISIBLE
        form.formFlag.setImageResource(country.flag)
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
                form.formProfileName, form.formProfileSurname,
                form.formProfileBirth, form.formProfileNationality
        )

        addTextValidation(validateList, this)
    }

    override fun validityChanged(isValid: Boolean) {
        val visibility = if (isValid) View.VISIBLE else View.INVISIBLE

        fillProfileNext.visibility = visibility
    }

    override fun onSelectCountry(country: Country) {
        presenter.countrySelected(country)
    }

    override fun isValid(item: CharSequence) = item.isNotEmpty()

    private fun nextClicked() {
        val name = form.formProfileName.content
        val surname = form.formProfileSurname.content

        val gender = getGender()

        presenter.nextClicked(name = name,
                surname = surname,
                gender = gender)
    }

    private fun getGender() = when (form.formProfileGenderGroup.checkedRadioButtonId) {
        com.square.android.R.id.formProfileGenderFemale -> GENDER_FEMALE
        else -> GENDER_MALE
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
