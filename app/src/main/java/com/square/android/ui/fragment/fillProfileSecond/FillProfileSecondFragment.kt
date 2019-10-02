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
//        form.formProfileCurrentAgency.setText(profileInfo.currentAgency)

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

    private var agency2CityError: Boolean = false
    private var agency2AgencyError: Boolean = false

    private var agency3CityError: Boolean = false
    private var agency3AgencyError: Boolean = false

    //TODO create city picker and change this value depending on which form.formProfileCity was clicked
    //TODO Use this value to determine which formProfileAgencyError should be hidden when city is selected and which form.formProfileCity.text should be filled with selected city value
    private var cityPickerNumber: Int = 1

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
                if(agency2AgencyError){
                    agency2AgencyError = false
                    form.formProfileAgency2Error.visibility = View.GONE
                }
            }
        })

        form.formProfileAgency3.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(agency3AgencyError){
                    agency3AgencyError = false
                    form.formProfileAgency3Error.visibility = View.GONE
                }
            }
        })
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
            //TODO when city selected for agency1 -> form.formProfileAgency1Error.visibility = View.GONE
        }

        if(isValid(form.formProfileAgency2.content) || isValid(form.formProfileCity2.content)){

            if(!isValid(form.formProfileCity2.content)){
                form.formProfileAgency2Error.text = getString(R.string.select_a_city_or_delete)
                form.formProfileAgency2Error.visibility = View.VISIBLE

                agency2CityError = true
                agency2AgencyError = false

                agency2Ok = false

                //TODO when city selected for agency2, if agency2CityError -> agency2CityError = false, form.formProfileAgency2Error.visibility = View.GONE
            } else if(!isValid(form.formProfileAgency2.content)){
                form.formProfileAgency2.setText("")

                form.formProfileAgency2Error.text = getString(R.string.enter_agency_or_unselect)
                form.formProfileAgency2Error.visibility = View.VISIBLE

                agency2AgencyError = true
                agency2CityError = false

                agency2Ok = false
            } else{
                agency2AgencyError = false
                agency2CityError = false
            }
        }

        if(isValid(form.formProfileAgency3.content) || isValid(form.formProfileCity3.content)){

            if(!isValid(form.formProfileCity3.content)){
                form.formProfileAgency3Error.text = getString(R.string.select_a_city_or_delete)
                form.formProfileAgency3Error.visibility = View.VISIBLE

                agency3CityError = true
                agency3AgencyError = false

                agency3Ok = false

                //TODO when city selected for agency3, if agency3CityError -> agency3CityError = false, form.formProfileAgency3Error.visibility = View.GONE
            } else if(!isValid(form.formProfileAgency3.content)){
                form.formProfileAgency3.setText("")

                form.formProfileAgency3Error.text = getString(R.string.enter_agency_or_unselect)
                form.formProfileAgency3Error.visibility = View.VISIBLE

                agency3AgencyError = true
                agency3CityError = false

                agency3Ok = false
            } else{
                agency3AgencyError = false
                agency3CityError = false
            }
        }

        if(!form.formProfileMotherAgency.errorShowing && agency1Ok && agency2Ok && agency3Ok){

            val motherAgency = form.formProfileMotherAgency.content

//            val currentAgency = form.formProfileCurrentAgency.content

            presenter.nextClicked(motherAgency = motherAgency)

//            presenter.nextClicked(motherAgency = motherAgency,
//                    currentAgency = currentAgency)

            activity?.hideKeyboard()
        }
    }

    private fun getModel() = arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo

    override fun onStop() {
        val profileInfo = presenter.info

        if(isValid(form.formProfileMotherAgency.content)){
            profileInfo.motherAgency = form.formProfileMotherAgency.content
        }

//        if(isValid(form.formProfileCurrentAgency.content)){
//            profileInfo.currentAgency = form.formProfileCurrentAgency.content
//        }

        presenter.saveState(profileInfo, 2)

        super.onStop()
    }
}
