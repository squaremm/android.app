package com.square.android.ui.activity.editProfile


import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.listeners.OnCountryPickerListener
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.Profile
import com.square.android.data.pojo.ProfileInfo
import com.square.android.extensions.*
import com.square.android.presentation.presenter.editProfile.EditProfilePresenter
import com.square.android.presentation.view.editProfile.EditProfileView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.start.StartActivity
import com.square.android.ui.dialogs.DatePickDialog
import com.square.android.utils.ValidationCallback
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.profile_form_1.*
import kotlinx.android.synthetic.main.profile_form_1.view.*
import kotlinx.android.synthetic.main.profile_form_2.view.*
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator

private const val GENDER_MALE = "male"
private const val GENDER_FEMALE = "female"

class EditProfileActivity : BaseActivity(), EditProfileView, ValidationCallback<CharSequence>, OnCountryPickerListener {
    private var isDoneShown = false

    @InjectPresenter
    lateinit var presenter: EditProfilePresenter

    lateinit var countryPicker: CountryPicker

    override fun provideNavigator(): Navigator = EditProfileNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setSupportActionBar(editProfileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editProfileFade.layoutParams.height = calculateFadeHeight()

        formFirst.formProfileNationality.setOnClickListener { showCountryDialog() }

        formFirst.formProfileBirth.setOnClickListener { showBirthDialog() }

        editProfileLogout.setOnClickListener { presenter.logout() }

        countryPicker = CountryPicker.Builder().with(this)
                .listener(this)
                .build()

        setUpValidation()
    }

    override fun showProgress() {
        editProfileProgress.visibility = View.VISIBLE
        editProfileContent.visibility = View.INVISIBLE
    }

    override fun onSelectCountry(country: Country) {
        presenter.countrySelected(country)
    }

    override fun hideProgress() {
        editProfileProgress.visibility = View.INVISIBLE
        editProfileContent.visibility = View.VISIBLE
    }

    override fun showBirthday(date: String) {
        formFirst.formProfileBirth.text = date
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val doneItem = menu.findItem(R.id.action_done)

        doneItem.isVisible = isDoneShown

        return super.onPrepareOptionsMenu(menu)
    }

    override fun isValid(item: CharSequence) = item.isNotEmpty()

    override fun validityChanged(isValid: Boolean) {
        isDoneShown = isValid

        invalidateOptionsMenu()
    }

    override fun displayNationality(country: Country?) {
        if (country != null) {
            formFirst.formProfileNationality.text = country.name

            formFirst.formFlag.visibility = View.VISIBLE
            formFirst.formFlag.setImageResource(country.flag)

        } else {
            formFirst.formProfileNationality.clearText()
            formFirst.formFlag.visibility = View.GONE
        }
    }

    override fun showData(user: Profile.User) {
        editProfileAvatar.loadImage(user.photo, placeholder = R.color.colorPrimary)

        formFirst.formProfileName.content = user.name
        formFirst.formProfileSurname.content = user.surname
        formFirst.formProfileBirth.text = user.birthDate

        setNationality(user.nationality)

        formSecond.formProfileCurrentAgency.content = user.currentAgency
        formSecond.formProfileMotherAgency.content = user.motherAgency
        formSecond.formProfileEmail.content = user.email
        formSecond.formProfilePhone.content = user.phone

        formSecond.formDialCode.visibility = View.GONE // TODO replace
        formSecond.formDialFlag.visibility = View.GONE

        val genderButton = determineGenderButtonId(user.gender)
        genderButton.isChecked = true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_done -> {
                    presenter.save(collectInfo())
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun setNationality(nationality: String) {
        val country = getCountryByName(nationality)

        displayNationality(country)
    }

    private fun determineGenderButtonId(gender: String) =
            if (gender == GENDER_MALE) {
                formProfileGenderMale
            } else {
                formProfileGenderFemale
            }


    private fun setUpValidation() {
        val validateList = listOf(
                formFirst.formProfileName, formFirst.formProfileSurname,
                formFirst.formProfileNationality,

                formSecond.formProfileEmail, formSecond.formProfilePhone,
                formSecond.formProfileMotherAgency, formSecond.formProfileCurrentAgency
        )

        addTextValidation(validateList, this)
    }

    private fun getCountryByName(name: String): Country? {
        return countryPicker.getCountryByName(name)
    }

    private fun showCountryDialog() {
        CountryPicker.Builder().with(this)
                .listener(this)
                .build()
                .showDialog(this)
    }

    private fun showBirthDialog() {
        DatePickDialog(this)
                .show { date: String ->
                    presenter.birthSelected(date)
                }
    }

    private fun collectInfo(): ProfileInfo {
        val name = formFirst.formProfileName.content
        val surname = formFirst.formProfileSurname.content

        val gender = getGender()

        val birthday = formFirst.formProfileBirth.content

        val nationality = formFirst.formProfileNationality.content

        val email = formSecond.formProfileEmail.content
        val phone = formSecond.formProfilePhone.content
        val motherAgency = formSecond.formProfileMotherAgency.content
        val currentAgency = formSecond.formProfileCurrentAgency.content

        return ProfileInfo(
                name = name,
                surname = surname,
                gender = gender,
                email = email,
                phone = phone,
                motherAgency = motherAgency,
                nationality = nationality,
                currentAgency = currentAgency,
                birthDate = birthday
        )
    }

    private fun getGender() = when (formFirst.formProfileGenderGroup.checkedRadioButtonId) {
        R.id.formProfileGenderFemale -> GENDER_FEMALE
        else -> GENDER_MALE
    }

    private class EditProfileNavigator(activity: FragmentActivity) : AppNavigator(activity, 0) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.START -> context.intentFor<StartActivity>().withClearingStack()
                    else -> null
                }

        override fun createFragment(screenKey: String?, data: Any?): Fragment? {
            return null
        }
    }
}
