package com.square.android.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import com.square.android.data.pojo.UserInfo

private const val KEY_DISPLAY_INTRO = "KEY_DISPLAY_INTRO"
private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
private const val KEY_ID = "KEY_ID"
private const val KEY_AVATAR_URL = "KEY_AVATAR_URL"
private const val KEY_USER_NAME = "KEY_USER_NAME"
private const val KEY_PROFILE_FILLED = "KEY_PROFILE_FILLED"
private const val KEY_LOGGED_IN = "KEY_LOGGED_IN"

private const val KEY_SOCIAL_LINK = "KEY_SOCIAL_LINK"

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

    @SuppressLint("ApplySharedPref")
    fun setAuthToken(token: String) {
        preferences.edit()
                .putString(KEY_AUTH_TOKEN, token)
                .commit()
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
                socialLink = getSocialLink()
        )
    }

    fun setId(id: Long) {
        preferences.edit()
                .putLong(KEY_ID, id)
                .apply()
    }

    fun getAuthToken(): String {
        return preferences.getString(KEY_AUTH_TOKEN, null)
                ?: throw IllegalArgumentException("No key is stored")
    }

    fun setAvatarUrl(url: String) {
        preferences.edit()
                .putString(KEY_AVATAR_URL, url)
                .apply()
    }

    fun setProfileFilled(isFilled: Boolean) {
        preferences.edit()
                .putBoolean(KEY_PROFILE_FILLED, isFilled)
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
                .apply()
    }

    private fun getUserName(): String {
        return preferences.getString(KEY_USER_NAME, "")
                ?: throw IllegalArgumentException("Name is not stored")
    }

    private fun getAvatarUrl(): String {
        return preferences.getString(KEY_AVATAR_URL, null)
                ?: throw IllegalArgumentException("Avatar is not stored")
    }

    private fun getSocialLink(): String {
        return preferences.getString(KEY_SOCIAL_LINK, null)
                ?: throw IllegalArgumentException("Social link is not stored")
    }

    private fun getId() = preferences.getLong(KEY_ID, ID_DEFAULT)
}