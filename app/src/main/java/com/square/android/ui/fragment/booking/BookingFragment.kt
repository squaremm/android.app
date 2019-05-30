package com.square.android.ui.fragment.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.booking.BookingPresenter
import com.square.android.presentation.view.booking.BookingView
import com.square.android.ui.activity.placeDetail.PlaceDetailActivity
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.base.tutorial.TutorialView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.android.synthetic.main.item_interval.view.*
import java.util.*

class BookingFragment : BaseFragment(), BookingView {

    override fun updateMonthName(calendar: Calendar) {
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        bookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))
    }

    override fun setSelectedDayItem(position: Int) {
        adapter?.setSelectedItem(position)
    }

    var dayHandler = object : DaysAdapter.Handler{
        override fun itemClicked(position: Int) {
            presenter.dayItemClicked(position)
            bookingBook.isEnabled = false
        }
    }

    private var adapter: DaysAdapter? = null

    @InjectPresenter
    lateinit var presenter: BookingPresenter

    override fun showProgress() {
        bookingIntervalList.visibility = View.GONE
        bookingEmpty.visibility = View.GONE
        bookProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        bookingIntervalList.visibility = View.VISIBLE
        bookProgress.visibility = View.GONE
    }

    override fun setSelectedItem(previousPosition: Int?, currentPosition: Int) {
        updateList(previousPosition, currentPosition)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookingBook.setOnClickListener { presenter.bookClicked() }
    }

    override fun showDate(calendar: Calendar) {
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        bookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val days = mutableListOf<Day>()
        val calendar2 = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }

        for (x in 0 until 7) {
            val day = Day()

            day.monthNumber = calendar2.get(Calendar.MONTH) + 1
            day.dayValue = calendar2.get(Calendar.DAY_OF_MONTH)
            day.dayName = calendar2.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1)

            days.add(day)

            calendar2.add(Calendar.DAY_OF_YEAR, 1)
        }

        adapter = DaysAdapter(days.toList(), dayHandler)
//        adapter!!.selectedMonth = calendar.get(Calendar.MONTH) + 1
        bookingCalendar.adapter = adapter
        adapter!!.selectedItemPosition = 0
        adapter!!.notifyItemChanged(0, DaysAdapter.SelectedPayload)
    }

    private fun updateList(previousPosition: Int?, currentPosition: Int) {
        previousPosition?.let {
            val previous = bookingIntervalList.getChildAt(it)
            changeSelected(previous, false)
        }

        val current = bookingIntervalList.getChildAt(currentPosition)
        changeSelected(current, true)
    }

    private fun itemClicked(position: Int, enabled: Boolean) {
        presenter.itemClicked(position)

        bookingBook.isEnabled = enabled
    }

    private fun changeSelected(view: View?, isSelected: Boolean) {
        view?.bookingContainer?.isActivated = isSelected
    }

    override fun showIntervals(data: List<Place.Interval>) {
        bookingIntervalList.removeAllViews()

        val inflater = layoutInflater

        bookingEmpty.visibility = if(data.isEmpty()) View.VISIBLE else View.GONE

        data.forEachIndexed { index, interval ->
            val view = inflater.inflate(R.layout.item_interval, bookingIntervalList, false)

            var active = interval.slots > 0

            view.bookingInterval.text = getString(R.string.time_range, interval.start, interval.end)

            when(interval.slots){
                0 -> { view.bookingSpots.text = getString(R.string.full) }
                1 -> view.bookingSpots.text = getString(R.string.spot_one_format, interval.slots)
                else -> view.bookingSpots.text = getString(R.string.spot_format, interval.slots)
            }

            //TODO: (If selected calendar day = today) Check if interval on the current day is available. If not, active = false

            updateInterval(view, active)

            view.setOnClickListener {itemClicked(index,active)}

            bookingIntervalList.addView(view)
        }
    }

    private fun updateInterval(view: View, isActive: Boolean) {
        view.bookingContainer.isActivated = false
        view.bookingInterval.isEnabled = isActive
        view.bookingSpots.isEnabled = isActive
    }

    override val PERMISSION_REQUEST_CODE: Int?
        get() = 1339

    override val tutorial: Tutorial?
        get() =  Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.BOOKING)
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.40f, 0.88f),
                        getString(R.string.tut_2_1),
                        TutorialStep.ArrowPos.TOP,
                        R.drawable.arrow_bottom_right_x_top_left,
                        0.15f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.585f,230f),
                        1,
                        // delay before showing view in ms
                        500f))
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.65f, 0.65f),
                        getString(R.string.tut_2_2),
                        TutorialStep.ArrowPos.BOTTOM,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.4f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.845f,136f),
                        1,
                        // delay before showing view in ms
                        500f))

                .setOnNextStepIsChangingListener {

                }
                .setOnContinueTutorialListener {

                }
                .build()
}
