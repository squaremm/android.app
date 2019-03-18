package com.square.android.ui.activity.profile


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.Profile
import com.square.android.extensions.loadImage
import com.square.android.extensions.setTextCarryingEmpty
import com.square.android.presentation.presenter.profile.ProfilePresenter
import com.square.android.presentation.view.profile.ProfileView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.editProfile.EditProfileActivity
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator


class ProfileActivity : BaseActivity(), ProfileView {
    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    override fun provideNavigator(): Navigator = ProfileNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        setSupportActionBar(profileToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        profileFade.layoutParams.height = calculateFadeHeight()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_settings -> {
                    presenter.openSettings()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }


    override fun showUser(user: Profile.User) {
        profileAvatar.loadImage(user.photo, placeholder = R.color.colorPrimary)

        profileName.text = getString(R.string.name_format, user.name, user.surname)
        profileAgency.text = user.currentAgency


        profileCity.setTextCarryingEmpty(user.city)

        profileLevel.text = user.level.toString()

        profileCoins.text = getString(R.string.credits_format, user.credits)

        profileNationality.text = user.nationality
        profileMotherAgency.text = user.motherAgency
        profileEmail.text = user.email
        profilePhone.text = user.phone

        profileInvite.setOnClickListener { share(user.referralCode) }
    }


    private fun share(referralCode: String) {
        val text = getString(R.string.shareContent, referralCode)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)

        startActivity(shareIntent)
    }

    private class ProfileNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.profileContainer) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.EDIT_PROFILE -> context.intentFor<EditProfileActivity>()
                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?): Fragment {
            throw IllegalArgumentException("Unknown screen key: $screenKey")
        }
    }
}
