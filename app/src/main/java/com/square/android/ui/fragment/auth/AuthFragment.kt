package com.square.android.ui.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.presentation.presenter.auth.AuthPresenter
import com.square.android.presentation.view.auth.AuthView
import com.square.android.ui.dialogs.OAuthDialog
import com.square.android.ui.fragment.BaseFragment
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

        authButton.setOnClickListener { presenter.authCLicked() }
    }

    override fun showProgress() {
        authProgress.visibility = View.VISIBLE

        authButton.visibility = View.GONE
    }

    override fun hideProgress() {
        authProgress.visibility = View.GONE

        authButton.visibility = View.VISIBLE
    }

    override fun showAuthDialog(url: String, trigger: String) {
        dialog = OAuthDialog(url, trigger)

        dialog!!.show(activity!!,
                callback = { code ->
                    presenter.authDone(code)
                },
                onCancelListener = {
                    presenter.dialogCanceled()
                })
    }

    override fun onDestroy() {
        super.onDestroy()

        dialog?.dismiss()
    }
}
