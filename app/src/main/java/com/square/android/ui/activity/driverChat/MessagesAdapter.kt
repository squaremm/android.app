package com.square.android.ui.activity.driverChat

import android.content.res.ColorStateList
import android.view.View
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.data.pojo.Message
import com.square.android.data.pojo.MessageHeader
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.item_message_header.*

private const val TYPE_HEADER = R.layout.item_message_header
private const val TYPE_MESSAGE = R.layout.item_message

class MessagesAdapter(data: List<Any>, private val userId: Long, private val handler: Handler) : BaseAdapter<Any, MessagesAdapter.Holder>(data) {

    override fun getLayoutId(viewType: Int) = viewType

    override fun instantiateHolder(view: View): Holder = Holder(view, handler,userId)

    override fun getViewType(position: Int): Int {
        val item = data[position]
        return when (item) {
            is Message -> TYPE_MESSAGE
            else -> TYPE_HEADER
        }
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }

    class Holder(containerView: View, var handler: Handler, var userId: Long) : BaseHolder<Any>(containerView) {

        override fun bind(item: Any, vararg extras: Any?) {
            when (item) {
                is MessageHeader -> bindHeader(item)
                is Message -> bindMessage(item)
            }
        }

        private fun bindHeader(header: MessageHeader) {
            headerDate.text = header.date
            headerTime.text = header.time
        }

        private fun bindMessage(message: Message){
            messageImage.visibility = View.GONE
            messageProgress.visibility = View.GONE
            messageTime.visibility = View.GONE
            messageTryAgain.visibility = View.GONE
            messageTryAgainClick.visibility = View.GONE

            messageText.text = message.text

            //TODO get time from timestamp and format to HH:MM
            messageTime.text = message.timestamp.toString()

            if(message.userId == userId) bindMyMessage(message) else bindOtherMessage(message)
        }

        private fun bindMyMessage(message: Message){
            messageText.setTextColor(ContextCompat.getColor(messageText.context, android.R.color.white))

            if(!message.sent){
                messageTryAgain.visibility = View.VISIBLE
                messageTryAgainClick.visibility = View.VISIBLE

                //TODO send message id and position?
//                messageTryAgainClick.setOnClickListener { handler.tryAgainClicked() }

                messageText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(messageText.context, R.color.gray_disabled))
            } else{
                messageTime.visibility = View.VISIBLE
                messageText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(messageText.context, R.color.nice_pink))
            }
        }

        private fun bindOtherMessage(message: Message){
            if (URLUtil.isValidUrl(message.image)) {
                messageImage.visibility = View.VISIBLE
                messageImage.loadImage(message.image!!, roundedCornersRadiusPx = 360)
            }

            messageText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(messageText.context, R.color.white))
            messageText.setTextColor(ContextCompat.getColor(messageText.context, android.R.color.black))
        }

    }

    interface Handler {
        fun tryAgainClicked(position: Int)

        fun cancelClicked(position: Int)

        fun claimedItemClicked(position: Int)

        fun campaignItemClicked(position: Int)
    }
}