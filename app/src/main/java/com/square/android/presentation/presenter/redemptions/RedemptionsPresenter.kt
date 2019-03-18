package com.square.android.presentation.presenter.redemptions

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.square.android.App
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.extensions.distanceTo
import com.square.android.extensions.relativeTimeString
import com.square.android.extensions.toDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.view.redemptions.RedemptionsView
import com.square.android.ui.activity.claimedRedemption.ClaimedExtras
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*
import kotlin.collections.ArrayList

class RedemptionsUpdatedEvent

private const val MAXIMAL_DISTANCE = 75_000_000 // TODO change before release to the 75 m

@Suppress("unused")
@InjectViewState
class RedemptionsPresenter : BasePresenter<RedemptionsView>() {
    private val eventBus: EventBus by inject()

    private var data: MutableList<Any>? = null

    private var groups: MutableMap<String, MutableList<RedemptionInfo>>? = null

    private var lastLocation: Location? = null

    init {
        eventBus.register(this)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }

    override fun attachView(view: RedemptionsView?) {
        super.attachView(view)

        if (data == null) loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedemptionsUpdatedEvent(event: RedemptionsUpdatedEvent) {
        loadData()
    }

    private fun loadData() {
        launch {
            val redemptions = repository.getRedemptions().await()

            data = addHeaders(redemptions).await().toMutableList()

            viewState.hideProgress()
            viewState.showData(data!!)
        }
    }


    fun claimClicked(position: Int) {
        val item = data!![position] as? RedemptionInfo ?: return

        if (item.claimed) {
            router.navigateTo(SCREENS.SELECT_OFFER, item.id)
            return
        }

        if (lastLocation == null) {
            viewState.showMessage(R.string.cannot_obtain_location)
            return
        }

        val distance = lastLocation!!.distanceTo(item.place.location)

        if (distance > MAXIMAL_DISTANCE) {
            viewState.showMessage(R.string.too_far_from_book)
            return
        }

        router.navigateTo(SCREENS.SELECT_OFFER, item.id)
    }

    fun claimedInfoClicked(position: Int) {
        val item = data!![position] as? RedemptionInfo ?: return

        val offerId = item.offers.firstOrNull()

        if (offerId == null) {
            viewState.showMessage(R.string.no_offer_found)
            return
        }

        val extras = ClaimedExtras(offerId, item.id)

        router.navigateTo(SCREENS.CLAIMED_REDEMPTION, extras)
    }

    fun cancelClicked(position: Int) {
        val item = data!![position] as? RedemptionInfo ?: return

        launch {
            val result = repository.deleteRedemption(item.id).await()

            var removeHeader: Boolean

            val previous = data!![position - 1]
            removeHeader = previous is String

            val isNotLast = position < data!!.size - 1

            if (isNotLast) {
                val next = data!![position + 1]

                val isNextHeader = next is String

                removeHeader = removeHeader && isNextHeader
            }

            data!!.removeAt(position)
            viewState.removeItem(position)

            if (removeHeader) {
                data!!.removeAt(position - 1)
                viewState.removeItem(position - 1)
            }

            sendBadgeEvent()

            viewState.showMessage(result.message)
        }
    }

    private fun sendBadgeEvent() {
        val event = BadgeStateChangedEvent()

        eventBus.post(event)
    }

    private fun addHeaders(data: List<RedemptionInfo>): Deferred<List<Any>> = GlobalScope.async {
        val today = Calendar.getInstance()
        val itemCalendar = Calendar.getInstance()

        val result = ArrayList<Any>()

        val closedTitle = App.getString(R.string.closed)
        val claimedTitle = App.getString(R.string.claimed)

        groups = data.groupByTo(mutableMapOf()) {
            if (it.closed) {
                return@groupByTo closedTitle
            }

            if (it.claimed) {
                return@groupByTo claimedTitle
            }

            val date = it.date.toDate()
            itemCalendar.time = date

            itemCalendar.relativeTimeString(today)
        }

        groups!!.forEach { (title, list) ->
            result.addAll(0, list)
            result.add(0, title)
        }

        result
    }

    fun locationGotten(location: Location?) {
        lastLocation = location

    }
}
