package com.square.android.ui.activity.main


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.presentation.presenter.main.MainPresenter
import com.square.android.presentation.view.main.MainView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.claimedRedemption.CLAIMED_OFFER_EXTRA_ID
import com.square.android.ui.activity.claimedRedemption.CLAIMED_REDEMPTION_EXTRA_ID
import com.square.android.ui.activity.claimedRedemption.ClaimedExtras
import com.square.android.ui.activity.claimedRedemption.ClaimedRedemptionActivity
import com.square.android.ui.activity.editProfile.EditProfileFragment
import com.square.android.ui.activity.placeDetail.PLACE_EXTRA_ID
import com.square.android.ui.activity.placeDetail.PlaceDetailActivity
import com.square.android.ui.fragment.profile.ProfileFragment
import com.square.android.ui.activity.selectOffer.OFFER_EXTRA_ID
import com.square.android.ui.activity.selectOffer.SelectOfferActivity
import com.square.android.ui.activity.start.StartActivity
import com.square.android.ui.fragment.map.MapFragment
import com.square.android.ui.fragment.places.PlacesFragment
import com.square.android.ui.fragment.redemptions.RedemptionsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.notifications_badge.*
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator


private const val REDEMPTIONS_POSITION = 2

class MainActivity : BaseActivity(), MainView, BottomNavigationView.OnNavigationItemSelectedListener {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun provideNavigator(): Navigator = MainNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpNavigation()
    }

    override fun checkInitial() {
        bottomNavigation.selectedItemId = R.id.action_places
    }


    override fun setActiveRedemptions(count: Int) {
        when (count) {
            0 -> notificationsBadge.visibility = View.GONE
            in 1..9 -> {
                notificationsBadge.text = count.toString()
                notificationsBadge.visibility = View.VISIBLE
            }
            else -> {
                notificationsBadge.setText(R.string.badge_big_count)
                notificationsBadge.visibility = View.VISIBLE
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val screenKey = when (item.itemId) {
            R.id.action_redemptions -> SCREENS.REDEMPTIONS
            R.id.action_profile -> SCREENS.PROFILE
            R.id.action_places -> SCREENS.PLACES
            R.id.action_map -> SCREENS.MAP
            else -> SCREENS.PROFILE
        }

        presenter.navigationClicked(screenKey)

        return true
    }

    private fun setUpNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(this)

        addBadgeView()
    }

    private fun addBadgeView() {
        val bottomNavigationMenuView = bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(REDEMPTIONS_POSITION)
        val itemView = v as BottomNavigationItemView

        val inflater = LayoutInflater.from(this)

        inflater.inflate(R.layout.notifications_badge, itemView, true)
    }

    private class MainNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.main_container) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.START ->
                        context.intentFor<StartActivity>()

                    SCREENS.SELECT_OFFER ->
                        context.intentFor<SelectOfferActivity>(OFFER_EXTRA_ID to data as Long)

                    SCREENS.PLACE_DETAIL ->
                        context.intentFor<PlaceDetailActivity>(PLACE_EXTRA_ID to data as Long)

                    SCREENS.CLAIMED_REDEMPTION -> {
                        val extras = data as ClaimedExtras

                        context.intentFor<ClaimedRedemptionActivity>(
                                CLAIMED_OFFER_EXTRA_ID to extras.offerId,
                                CLAIMED_REDEMPTION_EXTRA_ID to extras.redemptionId)
                    }

                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?) = when (screenKey) {
            SCREENS.MAP -> MapFragment()
            SCREENS.PLACES -> PlacesFragment()
            SCREENS.REDEMPTIONS -> RedemptionsFragment()
            SCREENS.PROFILE -> ProfileFragment()
            SCREENS.EDIT_PROFILE -> EditProfileFragment()
            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
        }
    }
}
