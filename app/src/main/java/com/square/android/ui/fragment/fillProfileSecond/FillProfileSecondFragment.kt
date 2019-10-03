package com.square.android.ui.fragment.fillProfileSecond

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.ProfileInfo
import com.square.android.extensions.content
import com.square.android.extensions.hideKeyboard
import com.square.android.presentation.presenter.fillProfileSecond.FillProfileSecondPresenter
import com.square.android.presentation.view.fillProfileSecond.FillProfileSecondView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_fill_profile_2.*
import kotlinx.android.synthetic.main.profile_form_2.view.*
import org.jetbrains.anko.bundleOf

private const val EXTRA_MODEL = "EXTRA_MODEL"

class FillProfileSecondFragment: BaseFragment(), FillProfileSecondView {

    override fun showData(profileInfo: ProfileInfo) {
        form.formProfileMotherAgency.setText(profileInfo.motherAgency)

        if(!TextUtils.isEmpty(profileInfo.city1)){
            form.formProfileCity1.text = profileInfo.city1
        }
        form.formProfileAgency1.setText(profileInfo.agency1)

        if(!TextUtils.isEmpty(profileInfo.city2)){
            form.formProfileCity2.text = profileInfo.city2
        }
        form.formProfileAgency2.setText(profileInfo.agency2)

        if(!TextUtils.isEmpty(profileInfo.city3)){
            form.formProfileCity3.text = profileInfo.city3
        }
        form.formProfileAgency3.setText(profileInfo.agency3)
    }

    companion object {

        @Suppress("DEPRECATION")
        fun newInstance(info: ProfileInfo): FillProfileSecondFragment {
            val fragment = FillProfileSecondFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }
    }

    //TODO add agencies and their cities to FragmentEditProfile, it's Presenter and View

    @InjectPresenter
    lateinit var presenter: FillProfileSecondPresenter

    @ProvidePresenter
    fun providePresenter(): FillProfileSecondPresenter = FillProfileSecondPresenter(getModel())

    private var dialog: SelectCityDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fill_profile_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillProfile2Next.setOnClickListener {
            nextClicked()
        }

        fillProfile2Back.setOnClickListener { activity?.onBackPressed() }

        form.formProfileAgency2.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(form.formProfileAgency2Error.visibility == View.VISIBLE){
                    form.formProfileAgency2Error.visibility = View.GONE
                }
            }
        })

        form.formProfileAgency3.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(form.formProfileAgency3Error.visibility == View.VISIBLE){
                    form.formProfileAgency3Error.visibility = View.GONE
                }
            }
        })

        form.formProfileCity1.setOnClickListener {
            context?.let {
                dialog = SelectCityDialog(it, presenter.cities){ city: String ->

                    if(!TextUtils.isEmpty(city.trim())){
                        form.formProfileAgency1Error.visibility = View.GONE
                    }
                    form.formProfileCity1.text = city
                }
                dialog!!.show(form.formProfileCity1.content)
            }
        }

        form.formProfileCity2.setOnClickListener {
            context?.let {
                dialog = SelectCityDialog(it, presenter.cities){ city: String ->
                    form.formProfileAgency2Error.visibility = View.GONE

                    form.formProfileCity2.text = city
                }
                dialog!!.show(form.formProfileCity2.content)
            }
        }

        form.formProfileCity3.setOnClickListener {
            context?.let {
                dialog = SelectCityDialog(it, presenter.cities){ city: String ->
                    form.formProfileAgency3Error.visibility = View.GONE

                    form.formProfileCity3.text = city
                }
                dialog!!.show(form.formProfileCity3.content)
            }
        }
    }

    fun isValid(item: CharSequence) = item.toString().trim().isNotEmpty()

    private fun nextClicked() {
        var agency1Ok = true
        var agency2Ok = true
        var agency3Ok = true

        if(!isValid(form.formProfileMotherAgency.content)){
            form.formProfileMotherAgency.setText("")
            form.formProfileMotherAgency.showCustomError(getString(R.string.mother_agency_error))
        }

        if(!isValid(form.formProfileAgency1.content)){
            form.formProfileAgency1.setText("")
            form.formProfileAgency1.showCustomError(getString(R.string.agency_error))
            agency1Ok = false
        }

        if(!isValid(form.formProfileCity1.content)){
            form.formProfileAgency1Error.visibility = View.VISIBLE
            agency1Ok = false
        }

        if(isValid(form.formProfileAgency2.content) || isValid(form.formProfileCity2.content)){

            if(!isValid(form.formProfileCity2.content)){
                form.formProfileAgency2Error.text = getString(R.string.select_a_city_or_delete)
                form.formProfileAgency2Error.visibility = View.VISIBLE

                agency2Ok = false
            } else if(!isValid(form.formProfileAgency2.content)){
                form.formProfileAgency2.setText("")
                form.formProfileAgency2Error.text = getString(R.string.enter_agency_or_unselect)
                form.formProfileAgency2Error.visibility = View.VISIBLE

                agency2Ok = false
            }
        } else{
            form.formProfileAgency2Error.visibility = View.GONE
        }

        if(isValid(form.formProfileAgency3.content) || isValid(form.formProfileCity3.content)){

            if(!isValid(form.formProfileCity3.content)){
                form.formProfileAgency3Error.text = getString(R.string.select_a_city_or_delete)
                form.formProfileAgency3Error.visibility = View.VISIBLE

                agency3Ok = false
            } else if(!isValid(form.formProfileAgency3.content)){
                form.formProfileAgency3.setText("")
                form.formProfileAgency3Error.text = getString(R.string.enter_agency_or_unselect)
                form.formProfileAgency3Error.visibility = View.VISIBLE

                agency3Ok = false
            }
        } else{
            form.formProfileAgency3Error.visibility = View.GONE
        }

        if(!form.formProfileMotherAgency.errorShowing && agency1Ok && agency2Ok && agency3Ok){
            val motherAgency = form.formProfileMotherAgency.content
            val city1 = form.formProfileCity1.content
            val agency1 = form.formProfileAgency1.content

            val city2 = form.formProfileCity2.content
            val agency2 = form.formProfileAgency2.content

            val city3 = form.formProfileCity3.content
            val agency3 = form.formProfileAgency3.content

            presenter.nextClicked(motherAgency = motherAgency, city1 = city1, agency1 = agency1, city2 = city2, agency2 = agency2, city3 = city3, agency3 = agency3)

            activity?.hideKeyboard()
        }
    }

    private fun getModel() = arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo

    override fun onStop() {
        val profileInfo = presenter.info

        if(isValid(form.formProfileMotherAgency.content)){
            profileInfo.motherAgency = form.formProfileMotherAgency.content
        }

        if(isValid(form.formProfileCity1.content)){
            profileInfo.city1 = form.formProfileCity1.content
        }

        if(isValid(form.formProfileAgency1.content)){
            profileInfo.agency1 = form.formProfileAgency1.content
        }

        if(isValid(form.formProfileAgency2.content) && isValid(form.formProfileCity2.content)){
            profileInfo.city2 = form.formProfileCity2.content
            profileInfo.agency2 = form.formProfileAgency2.content
        }

        if(isValid(form.formProfileAgency3.content) && isValid(form.formProfileCity3.content)){
            profileInfo.city3 = form.formProfileCity3.content
            profileInfo.agency3 = form.formProfileAgency3.content
        }

        presenter.saveState(profileInfo, 2)

        super.onStop()
    }
}
