package com.square.android.ui.activity.place

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_offer.*
import org.jetbrains.anko.dimen

import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OfferAdapter(data: List<OfferInfo>,
                   private val handler: Handler?) : BaseAdapter<OfferInfo, OfferAdapter.OfferHolder>(data) {

    private var intervalTimeframes: String? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_offer

    override fun getItemCount() = data.size

    override fun bindHolder(holder: OfferHolder, position: Int) {
        holder.bind(data[position], intervalTimeframes)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: OfferHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is AlphaPayload }
                .forEach { holder.bindAlpha(data[position],intervalTimeframes) }
    }

    fun updateAlpha(intervalTimeframes: String?) {
        this.intervalTimeframes = intervalTimeframes

        notifyItemRangeChanged(0,data.size, AlphaPayload)
    }

    override fun instantiateHolder(view: View): OfferHolder = OfferHolder(view, handler)

    class OfferHolder(containerView: View,
                      handler: Handler?) : BaseHolder<OfferInfo>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: OfferInfo, vararg extras: Any?) {
            val intervalTimeframes = extras.first() as String?

            itemOfferName.text = item.name
            itemOfferCredits.text =  itemOfferCredits.context.getString(R.string.credits_format_lowercase,item.price)

            itemOfferImv.loadImage((item.mainImage ?: item.photo) ?: "", roundedCornersRadiusPx = itemOfferImv.context.dimen(R.dimen.value_4dp))

            bindAlpha(item, intervalTimeframes)
        }

        fun bindAlpha(item: OfferInfo, intervalTimeframes: String?) {

   /* TODO Uncomment when API done

            if(!item.timeframes.isNullOrEmpty() && intervalTimeframes != null){
                println("BVBVB !item.timeframes.isNullOrEmpty() && intervalTimeframes != null")

                try {
                    println("BVBVB try")

                    var shouldChangeAlpha = true

                    var itemStart: Double = item.timeframes!![0].toDouble()
                    var itemEnd: Double = item.timeframes!![1].toDouble()

                    val timeList: MutableList<String> = mutableListOf()

                    if(itemStart % 1.0 != 0.0){

                        if(itemStart < 10){
                            timeList.add("0"+itemStart.toString().replace(".", ":"))
                        } else{
                            timeList.add(itemStart.toString().replace(".", ":"))
                        }

                        itemStart = itemStart.toInt().toDouble()

                        println("BVBVB itemStart"+itemStart)

                        if(itemStart + 1 >= itemEnd){
                        } else{
                            itemStart++
                        }
                    }

                    if(itemEnd % 1.0 != 0.0){
                        if(itemEnd < 10){
                            timeList.add("0"+itemEnd.toString().replace(".", ":"))
                        } else{
                            timeList.add(itemEnd.toString().replace(".", ":"))
                        }
                    }

                    var intStart = itemStart.toInt()
                    var intEnd = itemEnd.toInt()

                    for(x in intStart..intEnd){
                        var s = ""

                        if(x <10){
                            s += "0"
                        }

                        s += x.toString()+":00"

                        timeList.add(s)
                    }

                    println("BVBVB timeList"+timeList.toString())

                    //TODO uncomment when API ok
//                    val intervalSubstring = intervalTimeframes.substring(intervalTimeframes.indexOf(" "))
//                    var intervalStart = intervalSubstring[0].toDouble()
//                    var intervalEnd = intervalSubstring[1].toDouble()

                    //TODO delete when API ok
                    var intervalStart: Double = 01.01
                    var intervalEnd: Double = 1.59

                    var intervalStartS = ""
                    var intervalEndS = ""

                    if(intervalStart < 10){
                        intervalStartS = "0"+ intervalStart.toString().replace(".", ":")
                    } else{
                        intervalStartS = intervalStart.toString().replace(".", ":")
                    }

                    if(intervalEnd < 10){
                        intervalEndS = "0"+ intervalEnd.toString().replace(".", ":")
                    } else{
                        intervalEndS = intervalEnd.toString().replace(".", ":")
                    }

                    for(item in timeList){
                        if(isTimeBetweenTwoTime(intervalStartS, intervalEndS, item)){
                            shouldChangeAlpha = false
                        }
                    }

                    println("BVBVB shouldChangeAlpha: "+shouldChangeAlpha)

                    if(shouldChangeAlpha){
                        itemOfferContainer.alpha = 0.3f
                    } else{
                        itemOfferContainer.alpha = 1f
                    }

                } catch (e: Exception){
                    println("BVBVB catch: "+e.toString())

                    itemOfferContainer.alpha = 1f
                }

            } else{
                itemOfferContainer.alpha = 1f
            }
        }

        @Throws(ParseException::class)
        fun isTimeBetweenTwoTime(argStartTime: String,
                                  argEndTime: String, argCurrentTime: String): Boolean {

            val newStart = argStartTime + ":00"
            val newEnd = argEndTime + ":00"
            val newCurrent = argCurrentTime + ":00"

            println("BVBVB newStart: "+newStart+" newEnd:"+newEnd+" newCurrent:"+newCurrent)

            val reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$"
            //
            if (newStart.matches(reg.toRegex()) && newEnd.matches(reg.toRegex())
                    && newCurrent.matches(reg.toRegex())) {
                var valid = false
                // Start Time
                var startTime = SimpleDateFormat("HH:mm:ss")
                        .parse(newStart)
                val startCalendar = Calendar.getInstance()
                startCalendar.setTime(startTime)

                // Current Time
                var currentTime = SimpleDateFormat("HH:mm:ss")
                        .parse(newCurrent)
                val currentCalendar = Calendar.getInstance()
                currentCalendar.setTime(currentTime)

                // End Time
                var endTime = SimpleDateFormat("HH:mm:ss")
                        .parse(newEnd)
                val endCalendar = Calendar.getInstance()
                endCalendar.setTime(endTime)

                //
                if (currentTime.compareTo(endTime) < 0) {
                    currentCalendar.add(Calendar.DATE, 1)
                    currentTime = currentCalendar.getTime()
                }

                if (startTime.compareTo(endTime) < 0) {
                    startCalendar.add(Calendar.DATE, 1)
                    startTime = startCalendar.getTime()
                }
                //
                if (currentTime.before(startTime)) {
                    println("BVBVB Time is Lesser ")
                    valid = false
                } else {

                    if (currentTime.after(endTime)) {
                        endCalendar.add(Calendar.DATE, 1)
                        endTime = endCalendar.getTime()
                    }

                    println("BVBVB Start Time /n $startTime")
                    println("BVBVB End Time /n $endTime")
                    println("BVBVB Current Time /n $currentTime")

                    if (currentTime.before(endTime)) {
                        println("BVBVB RESULT, Time lies b/w")
                        valid = true
                    } else {
                        valid = false
                        println("BVBVB RESULT, Time does not lies b/w")
                    }

                }
                return valid

            } else {
                throw IllegalArgumentException(
                        "BVBVB Not a valid time, expecting HH:MM:SS format")
            }


    */
        }


    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object AlphaPayload
}