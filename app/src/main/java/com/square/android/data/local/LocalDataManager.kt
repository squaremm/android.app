package com.square.android.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.square.android.GOOGLEBILLING.SUBSCRIPTION_PER_MONTH_NAME
import com.square.android.GOOGLEBILLING.SUBSCRIPTION_PER_WEEK_NAME
import com.square.android.data.pojo.UserInfo
import com.square.android.ui.base.tutorial.TutorialService

private const val KEY_DISPLAY_INTRO = "KEY_DISPLAY_INTRO"
private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
private const val KEY_FCM_TOKEN = "KEY_FCM_TOKEN"
private const val KEY_ID = "KEY_ID"
private const val KEY_AVATAR_URL = "KEY_AVATAR_URL"
private const val KEY_USER_NAME = "KEY_USER_NAME"
private const val KEY_USER_PAYMENT_REQUIRED = "KEY_USER_PAYMENT_REQUIRED"
private const val KEY_PROFILE_FILLED = "KEY_PROFILE_FILLED"
private const val KEY_LOGGED_IN = "KEY_LOGGED_IN"

private const val KEY_OAUTH_TOKEN = "KEY_OAUTH_TOKEN"

private const val KEY_SOCIAL_LINK = "KEY_SOCIAL_LINK"

private const val KEY_TUTORIAL = "KEY_TUTORIAL"

private const val KEY_ENTITLEMENT = "KEY_ENTITLEMENT"

private const val KEY_PROFILE_INFO = "KEY_PROFILE_INFO"
private const val KEY_FRAGMENT_NUMBER = "KEY_FRAGMENT_NUMBER"

private const val KEY_ALLOW_PUSH_NOTIFICATIONS = "KEY_ALLOW_PUSH_NOTIFICATIONS"

private const val KEY_ALLOW_GEOLOCATION = "KEY_ALLOW_GEOLOCATION"

private const val DISPLAY_INTRO_DEFAULT = true
private const val PROFILE_FILLED_DEFAULT = false
private const val LOGGED_IN_DEFAULT = false
private const val ID_DEFAULT = 0L

class LocalDataManager(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun shouldDisplayIntro(): Boolean {
        return preferences.getBoolean(KEY_DISPLAY_INTRO, DISPLAY_INTRO_DEFAULT)
    }

    fun setShouldDisplayIntro(shouldDisplay: Boolean) {
        preferences.edit()
                .putBoolean(KEY_DISPLAY_INTRO, shouldDisplay)
                .apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_LOGGED_IN, LOGGED_IN_DEFAULT)
    }

    fun getFcmToken() = preferences.getString(KEY_FCM_TOKEN, null)

    @SuppressLint("ApplySharedPref")
    fun setAuthToken(token: String) {
        preferences.edit()
                .putString(KEY_AUTH_TOKEN, token)
                .commit()
    }

    fun saveFcmToken(fcmToken: String?) {
        preferences.edit()
                .putString(KEY_FCM_TOKEN, fcmToken)
                .apply()
    }

    fun setLoggedIn(isLogged: Boolean) {
        preferences.edit()
                .putBoolean(KEY_LOGGED_IN, isLogged)
                .apply()
    }

    fun setUserName(name: String) {
        preferences.edit()
                .putString(KEY_USER_NAME, name)
                .apply()
    }

    fun setUserPaymentRequired(paymentRequired: Boolean) {
        preferences.edit()
                .putBoolean(KEY_USER_PAYMENT_REQUIRED, paymentRequired)
                .apply()
    }

    fun setSocialLink(link: String) {
        preferences.edit()
                .putString(KEY_SOCIAL_LINK, link)
                .apply()
    }

    fun getUserInfo(): UserInfo {
        return UserInfo(
                name = getUserName(),
                photo = getAvatarUrl(),
                id = getId(),
                socialLink = getSocialLink(),
                isPaymentRequired = getUserPaymentRequired()
        )
    }

    fun setId(id: Long) {
        preferences.edit()
                .putLong(KEY_ID, id)
                .apply()
    }

    fun getAuthToken(): String {
        val token= preferences.getString(KEY_AUTH_TOKEN, null)
                ?: throw IllegalArgumentException("No key is stored")

        Log.e("LOL", "TOKEN: " + token)

        return token
    }

    fun setAvatarUrl(url: String?) {
        preferences.edit()
                .putString(KEY_AVATAR_URL, url)
                .apply()
    }

    fun setProfileFilled(isFilled: Boolean) {
        preferences.edit()
                .putBoolean(KEY_PROFILE_FILLED, isFilled)
                .apply()
    }

    fun getProfileInfo(): String {
        return preferences.getString(KEY_PROFILE_INFO, null)
                ?: throw IllegalArgumentException("No key is stored")
    }

    fun getOauthToken(): String {
        return preferences.getString(KEY_OAUTH_TOKEN, "empty")
                ?: throw IllegalArgumentException("No key is stored")
    }

    fun setOauthToken(token: String) {
        preferences.edit()
                .putString(KEY_OAUTH_TOKEN, "Bearer $token")
                .apply()
    }

    fun getFragmentNumber(): Int {
        return preferences.getInt(KEY_FRAGMENT_NUMBER, 0)
    }

    fun setProfileInfo(profileInfo: String, fragmentNumber: Int) {
        preferences.edit()
                .putString(KEY_PROFILE_INFO, profileInfo)
                .apply()

        preferences.edit()
                .putInt(KEY_FRAGMENT_NUMBER, fragmentNumber)
                .apply()
    }

    fun isProfileFilled() = preferences.getBoolean(KEY_PROFILE_FILLED, PROFILE_FILLED_DEFAULT)

    fun isTokenPresent() = preferences.contains(KEY_AUTH_TOKEN)

    fun clearUserData() {
        preferences.edit()
                .remove(KEY_ID)
                .remove(KEY_PROFILE_FILLED)
                .remove(KEY_AUTH_TOKEN)
                .remove(KEY_LOGGED_IN)
                .remove(KEY_USER_NAME)
                .remove(KEY_AVATAR_URL)
                .remove(KEY_SOCIAL_LINK)
                .remove(KEY_USER_PAYMENT_REQUIRED)
                .apply()
    }

    fun getTutorialDontShowAgain(tutorialKey: TutorialService.TutorialKey) =
            preferences.getBoolean(KEY_TUTORIAL+tutorialKey.name, false)

    fun setTutorialDontShowAgain(tutorialKey: TutorialService.TutorialKey, dontShowAgain: Boolean){
        preferences.edit()
                .putBoolean(KEY_TUTORIAL+tutorialKey, dontShowAgain)
                .apply()
    }

    fun getUserEntitlement(entitlementId: String) =
            preferences.getBoolean(KEY_ENTITLEMENT+entitlementId, false)


    fun isUserPremium(): Boolean{
        return getUserEntitlement(SUBSCRIPTION_PER_WEEK_NAME) || getUserEntitlement(SUBSCRIPTION_PER_MONTH_NAME)

        //Check every subscriptionId in app
    }

    fun setUserEntitlement(entitlementId: String, active: Boolean){
        preferences.edit()
                .putBoolean(KEY_ENTITLEMENT+entitlementId, active)
                .apply()
    }

    fun clearUserEntitlements(){
        preferences.edit()
                .putBoolean(KEY_ENTITLEMENT+SUBSCRIPTION_PER_WEEK_NAME, false)
                .apply()
        preferences.edit()
                .putBoolean(KEY_ENTITLEMENT+SUBSCRIPTION_PER_MONTH_NAME, false)
                .apply()

        //Repeat with every subscriptionId in app
    }

    fun grantAllUserEntitlements(){
        preferences.edit()
                .putBoolean(KEY_ENTITLEMENT+SUBSCRIPTION_PER_WEEK_NAME, true)
                .apply()
        preferences.edit()
                .putBoolean(KEY_ENTITLEMENT+SUBSCRIPTION_PER_MONTH_NAME, true)
                .apply()

        //Repeat with every subscriptionId in app
    }

    private fun getUserName(): String {
        return preferences.getString(KEY_USER_NAME, "")
                ?: throw IllegalArgumentException("Name is not stored")
    }

    fun getUserPaymentRequired(): Boolean {
        return preferences.getBoolean(KEY_USER_PAYMENT_REQUIRED, true)
    }

    private fun getAvatarUrl(): String? {
        return preferences.getString(KEY_AVATAR_URL, null)
    }

    private fun getSocialLink(): String? {
        return preferences.getString(KEY_SOCIAL_LINK, null)
    }

    fun getId() = preferences.getLong(KEY_ID, ID_DEFAULT)

    fun getPushNotificationsAllowed(): Boolean {
        return preferences.getBoolean(KEY_ALLOW_PUSH_NOTIFICATIONS, false)
    }

    fun getGeolocationAllowed(): Boolean {
        return preferences.getBoolean(KEY_ALLOW_GEOLOCATION, false)
    }

    fun setPushNotificationsAllowed(allowed: Boolean) {
        preferences.edit()
                .putBoolean(KEY_ALLOW_PUSH_NOTIFICATIONS, allowed)
                .apply()
    }

    fun setGeolocationAllowed(allowed: Boolean) {
        preferences.edit()
                .putBoolean(KEY_ALLOW_GEOLOCATION, allowed)
                .apply()
    }
}