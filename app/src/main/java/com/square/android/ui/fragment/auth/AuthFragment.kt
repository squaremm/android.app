package com.square.android.ui.fragment.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.square.android.App
import com.square.android.R
import com.square.android.data.pojo.AuthData
import com.square.android.extensions.clearText
import com.square.android.extensions.content
import com.square.android.presentation.presenter.auth.AuthPresenter
import com.square.android.presentation.view.auth.AuthView
import com.square.android.ui.dialogs.OAuthDialog
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.TokenUtils
import kotlinx.android.synthetic.main.fragment_auth.*


class AuthFragment : BaseFragment(), AuthView {

    @InjectPresenter
    lateinit var presenter: AuthPresenter

    private var dialog: OAuthDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRegisterButton.setOnClickListener {
            hideInitialButtons()
            showRegisterFields()
        }
        authButton.setOnClickListener {
            hideInitialButtons()
            showLoginFields()
        }

        tv_already_user.setOnClickListener { showLoginFields() }
        tv_forgot_password.setOnClickListener { showForgotFields() }
        tv_not_a_user.setOnClickListener { showRegisterFields() }
        tv_want_to_login.setOnClickListener { showLoginFields() }

        pending_text_2.movementMethod = LinkMovementMethod.getInstance()
        read_acceptation_policy.movementMethod = LinkMovementMethod.getInstance()
        pending_button_video.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_tutorial)))
            startActivity(browserIntent)
        }

        do_action_button.setOnClickListener {
            val authData = AuthData(et_email.content, et_password.content, et_confirm_password.content)
            presenter.actionClicked(authData)
        }
    }

    override fun hideInitialButtons() {
        authRegisterButton.visibility = View.GONE
        authButton.visibility = View.GONE
        tv_or.visibility = View.GONE
        container_register_login_data.visibility = View.VISIBLE
    }

    override fun showProgress() {
        authProgress.visibility = View.VISIBLE

        container_register_login_data.visibility = View.GONE
    }

    override fun hideProgress() {
        authProgress.visibility = View.GONE

        container_register_login_data.visibility = View.VISIBLE
    }

    override fun showPendingUser() {
        pending_splash.visibility = View.VISIBLE
    }

    override fun showLoginFields() {
        presenter.loginAction()
        do_action_button.text = getString(R.string.login)

        tv_not_a_user.visibility = View.VISIBLE
        tv_forgot_password.visibility = View.VISIBLE
        et_password.visibility = View.VISIBLE
        tv_want_to_login.visibility = View.GONE
        tv_already_user.visibility = View.GONE
        et_confirm_password.visibility = View.GONE

        et_password.clearText()
        et_confirm_password.clearText()
    }

    override fun showRegisterFields() {
        presenter.registerAction()
        do_action_button.text = getString(R.string.register)

        tv_not_a_user.visibility = View.GONE
        tv_forgot_password.visibility = View.GONE
        tv_want_to_login.visibility = View.GONE
        tv_already_user.visibility = View.VISIBLE
        et_password.visibility = View.VISIBLE
        et_confirm_password.visibility = View.VISIBLE

        et_password.clearText()
        et_confirm_password.clearText()
    }

    override fun showForgotFields() {
        presenter.resetPasswordAction()
        do_action_button.text = getString(R.string.send_password)

        tv_want_to_login.visibility = View.VISIBLE
        tv_not_a_user.visibility = View.GONE
        tv_forgot_password.visibility = View.GONE
        tv_already_user.visibility = View.GONE
        et_password.visibility = View.GONE
        et_confirm_password.visibility = View.GONE

        et_password.clearText()
        et_confirm_password.clearText()
    }

    override fun sendFcmToken() {
        if (presenter.repository.getUserInfo().id != 0L) {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity as Activity) { instanceIdResult ->
                val newToken = instanceIdResult.token
                TokenUtils.sendTokenToApi(App.INSTANCE, presenter.repository, newToken)
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            presenter.checkUserPending()
        }
    }

    override fun showUserPending() {
        pending_splash.visibility = View.VISIBLE
    }

    override fun hideUserPending() {
        pending_splash.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

        dialog?.dismiss()
    }
}
