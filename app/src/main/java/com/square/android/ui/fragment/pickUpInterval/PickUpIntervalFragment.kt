package com.square.android.ui.fragment.pickUpInterval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.CampaignInterval
import com.square.android.data.pojo.Day
import com.square.android.presentation.presenter.pickUpInterval.PickUpIntervalPresenter
import com.square.android.presentation.view.pickUpInterval.PickUpIntervalView
import com.square.android.ui.activity.campaignDetails.EXTRA_CAMPAIGN_ID
import com.square.android.ui.activity.campaignDetails.EXTRA_INTERVAL_ID
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.booking.DaysAdapter
import com.square.android.ui.fragment.pickUpSpot.PickUpSpotFragment
import kotlinx.android.synthetic.main.fragment_pick_up_interval.*
import kotlinx.android.synthetic.main.item_interval_campaign.view.*
import org.jetbrains.anko.bundleOf
import java.util.*

class PickUpIntervalExtras(val campaignId: Long, val intervalId: Long)

class PickUpIntervalFragment: BaseFragment(),PickUpIntervalView , DaysAdapter.Handler{

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(campaignId: Long, intervalId: Long): PickUpSpotFragment {
            val fragment = PickUpSpotFragment()

            val args = bundleOf(EXTRA_CAMPAIGN_ID to campaignId, EXTRA_INTERVAL_ID to intervalId)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: PickUpIntervalPresenter

    @ProvidePresenter
    fun providePresenter() = PickUpIntervalPresenter(getCampaignId(), getIntervalId())

    private var clickEnabled = true

    private var adapter: DaysAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_up_interval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickupBook.setOnClickListener { presenter.bookClicked() }

        pickupDayLeft.setOnClickListener {
            if(clickEnabled){
                presenter.changeMonth(2)
            }
        }

        pickupDayRight.setOnClickListener {
            if(clickEnabled){
                presenter.changeMonth(1)
            }
        }
    }

    override fun setSelectedItem(previousPosition: Int?, currentPosition: Int) {
        updateList(previousPosition, currentPosition)
    }

    private fun updateList(previousPosition: Int?, currentPosition: Int) {
        previousPosition?.let {
            val previous = pickupIntervalList.getChildAt(it)
            changeSelected(previous, false)
        }

        val current = pickupIntervalList.getChildAt(currentPosition)
        changeSelected(current, true)
    }

    private fun changeSelected(view: View?, isSelected: Boolean) {
        view?.campaignIntervalContainer?.isActivated = isSelected
    }

    override fun setSelectedDayItem(position: Int) {
        adapter?.setSelectedItem(position)
    }

    override fun changeDate(calendar: Calendar, useCalendarDay: Boolean) {
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        pickupMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val days = mutableListOf<Day>()
        val calendar2 = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }
        calendar2.set(Calendar.DAY_OF_MONTH, 1)

        for (x in 0 until calendar2.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            val day = Day()

            day.monthNumber = calendar2.get(Calendar.MONTH) + 1
            day.dayValue = calendar2.get(Calendar.DAY_OF_MONTH)
            day.dayName = calendar2.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1)

            days.add(day)

            calendar2.add(Calendar.DAY_OF_YEAR, 1)
        }

        adapter = DaysAdapter(days.toList(), this)
        pickupCalendar.adapter = adapter
        pickupCalendar.layoutManager = LinearLayoutManager(pickupCalendar.context, RecyclerView.HORIZONTAL,false)

        if(useCalendarDay){
            val position = calendar.get(Calendar.DAY_OF_MONTH) - 1

            adapter!!.selectedItemPosition = position
            adapter!!.notifyItemChanged(position, DaysAdapter.SelectedPayload)

            //TODO check if working
            pickupCalendar.scrollToPosition(position)

        } else{
            adapter!!.selectedItemPosition = 0
            adapter!!.notifyItemChanged(0, DaysAdapter.SelectedPayload)
        }
    }

    override fun itemClicked(position: Int) {
        if(clickEnabled){
            presenter.daySelected(position)
        }
    }

    override fun updateSlots(intervalSlots: List<CampaignInterval.Slot>) {
        pickupIntervalList.removeAllViews()

        val inflater = layoutInflater

        pickupEmpty.visibility = if(intervalSlots.isEmpty()) View.VISIBLE else View.GONE

        pickupSv.visibility = if(intervalSlots.isEmpty()) View.GONE else View.VISIBLE

        intervalSlots.forEachIndexed { index, interval ->
            val view = inflater.inflate(R.layout.item_interval_campaign, pickupIntervalList, false)

            val active = interval.slots > 0

            view.campaignInterval.text = getString(R.string.time_range, interval.start, interval.end)

            when(interval.slots){
                0 -> { view.campaignIntervalSpots.text = getString(R.string.full) }
                1 -> view.campaignIntervalSpots.text = getString(R.string.spot_one_format, interval.slots)
                else -> view.campaignIntervalSpots.text = getString(R.string.spot_format, interval.slots)
            }

            updateInterval(view, active)

            view.setOnClickListener {slotClicked(index, active)}

            pickupIntervalList.addView(view)
        }
    }

    private fun slotClicked(position: Int, enabled: Boolean) {
        pickupBook.isEnabled = enabled
        presenter.slotClicked(position)
    }

    private fun updateInterval(view: View, isActive: Boolean) {
        view.campaignIntervalContainer.isActivated = false
        view.campaignInterval.isEnabled = isActive
        view.campaignIntervalSpots.isEnabled = isActive
    }

    override fun setSlotsLoading() {
        pickupBook.isEnabled = false

        pickupSv.visibility = View.GONE
        pickupEmpty.visibility = View.GONE

        pickupSpotsProgress.visibility = View.VISIBLE
    }

    override fun setSlotsNormal() {
        pickupSpotsProgress.visibility = View.GONE
    }

    override fun showProgress() {
        clickEnabled = false

        pickupBook.visibility = View.GONE
        pickupBookProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        pickupBookProgress.visibility = View.GONE
        pickupBook.visibility = View.VISIBLE

        clickEnabled = true
    }

    override fun setContentLoading() {
        pickupContent.visibility = View.GONE
        pickupContentProgress.visibility = View.VISIBLE
    }

    override fun setContentNormal() {
        pickupContentProgress.visibility = View.GONE
        pickupContent.visibility = View.VISIBLE
    }

    private fun getCampaignId() = arguments?.getLong(EXTRA_CAMPAIGN_ID, 0) as Long
    private fun getIntervalId() = arguments?.getLong(EXTRA_INTERVAL_ID, 0) as Long
}
