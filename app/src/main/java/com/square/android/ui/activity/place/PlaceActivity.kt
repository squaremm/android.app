package com.square.android.ui.activity.place

import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.place.PlacePresenter
import com.square.android.presentation.view.place.PlaceView
import com.square.android.ui.activity.LocationActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_place.*
import ru.terrakok.cicerone.Navigator
import android.os.Build
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.data.pojo.Day
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImageForIcon
import com.square.android.ui.fragment.map.MarginItemDecorator
import com.square.android.ui.fragment.places.GridItemDecoration
import java.util.*

const val PLACE_EXTRA_ID = "EXTRA_ID"

class PlaceActivity : LocationActivity(), PlaceView {
    @InjectPresenter
    lateinit var presenter: PlacePresenter

    var place: Place? = null

    private var isCalculated = false

    private var titleMovePoint: Float = 0F

    private var titleAnimationWeight: Float = 0F

    private var titleMinHeight: Int = 0

    private var isStatusBarLight: Boolean = false

    private var adapter: AboutAdapter? = null

    private var offerAdapter: OfferAdapter? = null

    private var dialog: OfferDialog? = null

    private var daysAdapter: DaysAdapter? = null

    private var decorationAdded = false

    private var intervalsAdapter: IntervalAdapter? = null

    var placeAboutSize = 0

    @ProvidePresenter
    fun providePresenter() = PlacePresenter(getId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        placeArrowBack.setOnClickListener { onBackPressed() }

        placeAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    if(!isCalculated){
                        titleMovePoint = 1 - 0.9f
                        titleAnimationWeight =  1 / (1 - titleMovePoint)
                        isCalculated = true
                    }
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
        })

        placeReadMore.setOnClickListener {
            placeReadMore.visibility = View.GONE
            placeAbout.maxLines = Integer.MAX_VALUE

            checkAndShowAboutRv()
        }

        placeAddressCl.setOnClickListener {
            if(presenter.latitude != null && presenter.longitude != null){
                presenter.data?.let {
                    val uri = "https://www.google.com/maps/dir/?api=1&origin=${presenter.latitude},${presenter.longitude}&destination=${it.address}&travelmode=walking"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(uri)
                    val chooser = Intent.createChooser(intent, getString(R.string.select_an_app))
                    startActivity(chooser)
                }
            }
        }

        placeBookingBtn.setOnClickListener { presenter.bookClicked() }
    }

    private fun updateViews(offset: Float){
        when (offset) {
            in 0.555F..1F -> {

                if(!isStatusBarLight){
                    isStatusBarLight = true
                    setLightStatusBar(this)
                }

                placeArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
            }
            in 0F..0.555F -> {

                if(isStatusBarLight){
                    isStatusBarLight = false
                    clearLightStatusBar(this)
                }

                placeArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
            }
        }

        placeName.apply {
            when {
                offset > titleMovePoint -> {
                    val titleAnimationOffset = (offset - titleMovePoint) * titleAnimationWeight

                    val measuredMargin = Math.round(resources.getDimension(R.dimen.ac_place_default_margin) + ((resources.getDimension(R.dimen.backArrowSize) + resources.getDimension(R.dimen.backArrowMarginStart)) * titleAnimationOffset))
                    this.layoutParams.also {
                        (it as CollapsingToolbarLayout.LayoutParams).setMargins(Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0,measuredMargin,0)
                        this.requestLayout()
                    }
                    this.translationX = (resources.getDimension(R.dimen.backArrowSize) + resources.getDimension(R.dimen.backArrowMarginStart)) * titleAnimationOffset

                    this.height = Math.round(titleMinHeight + (resources.getDimension(R.dimen.toolbar_extra_space)* titleAnimationOffset))
                }
                else ->{
                    this.layoutParams.also {
                        translationX = 0f
                        (it as CollapsingToolbarLayout.LayoutParams).setMargins(Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0,Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0)
                    }
                }
            }
        }
    }

    override fun showProgress() {
        placeIntervalsRv.visibility = View.GONE
        placeProgress.visibility = View.VISIBLE

        placeBookingText.text = ""
    }

    override fun hideProgress() {
        placeIntervalsRv.visibility = View.GONE
        placeProgress.visibility = View.VISIBLE

        placeBookingText.text = ""
    }

    override fun setSelectedIntervalItem(position: Int) {
        intervalsAdapter?.setSelectedItem(position)
    }

    override fun showIntervals(data: List<Place.Interval>) {
        intervalsAdapter = IntervalAdapter(data, intervalHandler)

        placeIntervalsRv.layoutManager = GridLayoutManager(this, 2)
        placeIntervalsRv.adapter = intervalsAdapter

        if(!decorationAdded){
            decorationAdded = true
            placeIntervalsRv.addItemDecoration(GridItemDecoration(2,placeIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
        }
    }

    var intervalHandler = object : IntervalAdapter.Handler{
        override fun itemClicked(position: Int, text: String, offers: List<Long>) {
            presenter.intervalItemClicked(position)
            placeBookingBtn.isEnabled = true
            placeBookingText.text = text

            offerAdapter?.updateAlpha(offers)
        }
    }

    override fun updateMonthName(calendar: Calendar) {
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        placeBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).capitalize()
        val m = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()

        placeBookingDate.text = d + ", " + m + " " + dayToString(calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun setSelectedDayItem(position: Int) {
        daysAdapter?.setSelectedItem(position)
    }

    var dayHandler = object : DaysAdapter.Handler{
        override fun itemClicked(position: Int) {
            presenter.dayItemClicked(position)
            placeBookingBtn.isEnabled = false
            offerAdapter?.updateAlpha(null)
        }
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showDistance(distance: Int?) {
        if (distance != null)  {
            val distanceFormatted = distance.asDistance()

            placeDistance.visibility = View.VISIBLE
            placeDistance.text = distanceFormatted
        } else {
            placeDistance.visibility = View.GONE
        }
    }

    override fun showOfferDialog(offer: OfferInfo, place: Place?) {
        dialog = OfferDialog(this)
        dialog!!.show(offer, place)
    }

    override fun showData(place: Place, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?) {
        this.place = place

        typeImage?.let { placeAboutImage.loadImageForIcon(it) }

        placeMainImage.loadImage(place.mainImage ?: (place.photos?.firstOrNull() ?: ""))

        placeAbout.text = place.description

        //TODO delete this and get data from place
        val aboutItems = listOf("www", "insta")

        placeAboutSize = aboutItems.size

        adapter = AboutAdapter(aboutItems)
        placeAboutRv.adapter = adapter
        placeAboutRv.layoutManager = LinearLayoutManager(placeAboutRv.context, RecyclerView.HORIZONTAL, false)
        placeAboutRv.addItemDecoration(MarginItemDecorator(placeAboutRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), vertical = false))

        //TODO delete this and get data from place
        val dressCode: String? = ""
        val minimumTip: String? = ""

        if(!TextUtils.isEmpty(dressCode) || !TextUtils.isEmpty(minimumTip)){
            placeRequirementsCl.visibility = View.VISIBLE

            if(!TextUtils.isEmpty(dressCode) && !TextUtils.isEmpty(minimumTip)){
                placeTipValue.text = minimumTip
                placeTipContainer.visibility = View.VISIBLE

                placeDressCodeValue.text = dressCode
            } else {
                minimumTip?.let {
                    placeDressCodeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.r_discount))
                    placeDressCodeName.text = getString(R.string.minimal_tip)
                    placeDressCodeValue.text = it

                } ?: run{
                    placeDressCodeValue.text = dressCode
                }
            }
        }

        if(!offers.isNullOrEmpty()){
            placeOffersCl.visibility = View.VISIBLE

            offerAdapter = OfferAdapter(offers, object: OfferAdapter.Handler {
                override fun itemClicked(position: Int) {
                    presenter.offersItemClicked(position, place)
                }
            })

            placeOffersRv.layoutManager = GridLayoutManager(this, 3)
            placeOffersRv.adapter = offerAdapter
            placeOffersRv.addItemDecoration(GridItemDecoration(3,placeOffersRv.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(), false))
        }

        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        placeBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).capitalize()
        val m =  calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()

        placeBookingDate.text = d +", " + m +" " + dayToString(calendar.get(Calendar.DAY_OF_MONTH))

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

        daysAdapter = DaysAdapter(days.toList(), dayHandler)
//        daysAdapter!!.selectedMonth = calendar.get(Calendar.MONTH) + 1
        placeBookingCalendar.adapter = daysAdapter
        daysAdapter!!.selectedItemPosition = 0
        daysAdapter!!.notifyItemChanged(0, DaysAdapter.SelectedPayload)


        placeName.text = place.name

        placeName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                placeToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = placeName.measuredHeight
                        placeName.height = titleMinHeight

                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        placeCollapsing.layoutParams.apply {
                            this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                        }
                    }
                }
                placeName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        placeAbout.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onAboutLoaded()
                placeAbout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        placeAddress.text = place.address
    }

    private fun dayToString(day: Int): String{
       val s = when(day){
           1, 21, 31 -> "st"
           2, 22 -> "nd"
           3, 23 -> "rd"
           else -> "th"
       }

       return day.toString() + s
   }

    private fun onAboutLoaded(){
        var startOffset: Int
        var endOffset: Int
        var lineToEnd = 3
        val maxLines = 3
        var isLineSelected = false
        var notEmptyLinesToShowMore = 0

        if (!TextUtils.isEmpty(placeAbout.text)) {
            if (placeAbout.layout != null) {
                var shouldShowReadMore = true

                if (placeAbout.layout.lineCount <= maxLines) {
                    shouldShowReadMore = false
                } else {

                    for(i in 2 until placeAbout.layout.lineCount){
                        startOffset = placeAbout.layout.getLineStart(i)
                        endOffset = placeAbout.layout.getLineEnd(i)
                        if (!TextUtils.isEmpty((placeAbout.layout.text.subSequence(startOffset, endOffset)).toString().trim())) {
                            if (!isLineSelected) {
                                lineToEnd = i + 1
                                isLineSelected = true
                            } else {
                                notEmptyLinesToShowMore++
                            }
                        }
                    }

                    if (notEmptyLinesToShowMore < 2) {
                        shouldShowReadMore = false
                    }
                }

                if(shouldShowReadMore){
                    placeAbout.maxLines = lineToEnd
                    placeReadMore.visibility = View.VISIBLE
                } else{
                    checkAndShowAboutRv()
                }
            }
        }
    }

    private fun checkAndShowAboutRv(){
        if(placeAboutSize > 0){
            placeAboutRv.visibility = View.VISIBLE
        }
    }

    private fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    private fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    private fun getId() = intent.getLongExtra(PLACE_EXTRA_ID, 0)
}