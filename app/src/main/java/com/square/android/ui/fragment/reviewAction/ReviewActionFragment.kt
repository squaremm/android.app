package com.square.android.ui.fragment.reviewAction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.extensions.loadImageInside
import com.square.android.ui.fragment.BaseNoMvpFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_review_action.*

class ReviewActionFragment(private val action: Offer.Action, private var subActions: List<Offer.Action> = listOf(), private var instaName: String, private var fbName: String): BaseNoMvpFragment() {

    private var rememberItems: MutableList<String>? = null
    private var avoidItems: MutableList<String>? = null
    private var typologyItems: MutableMap<String, String>? = null

    private var rememberAdapter: RememberAdapter? = null
    private var avoidAdapter: AvoidAdapter? = null
    private var typologyAdapter: TypologyAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(action.type){

            //TODO there will be more types - facebook review, facebook story etc
            TYPE_FACEBOOK_POST -> {
                rememberItems = mutableListOf(getString(R.string.fb_post_remember_1), getString(R.string.fb_post_remember_2), getString(R.string.fb_post_remember_3, "@"+fbName), getString(R.string.fb_post_remember_4, "@"+fbName))
                avoidItems = mutableListOf(getString(R.string.fb_post_avoid_1), getString(R.string.fb_post_avoid_2))
            }
            TYPE_INSTAGRAM_POST ->{
                rememberItems = mutableListOf(getString(R.string.insta_post_remember_1),getString(R.string.insta_post_remember_2),getString(R.string.insta_post_remember_3, "@"+instaName.replace("/", "")),getString(R.string.insta_post_remember_4, "@"+instaName.replace("/", "")))
                avoidItems = mutableListOf(getString(R.string.insta_post_avoid_1), getString(R.string.insta_post_avoid_2))
            }
            TYPE_INSTAGRAM_STORY -> {
                rememberItems = mutableListOf(getString(R.string.insta_story_remember_1), getString(R.string.insta_story_remember_2), getString(R.string.insta_story_remember_3), getString(R.string.insta_story_remember_4, "@"+instaName.replace("/", "")))
                avoidItems = mutableListOf(getString(R.string.insta_story_avoid_1), getString(R.string.insta_story_avoid_2))
            }
            TYPE_TRIP_ADVISOR -> {
                rememberItems = mutableListOf(getString(R.string.tripadvisor_remember_1), getString(R.string.tripadvisor_remember_2))
                avoidItems = mutableListOf(getString(R.string.tripadvisor_avoid_1), getString(R.string.tripadvisor_avoid_2))
            }
            TYPE_GOOGLE_PLACES -> {
                rememberItems = mutableListOf(getString(R.string.google_places_remember_1),getString(R.string.google_places_remember_2))
                avoidItems = mutableListOf(getString(R.string.google_places_avoid_1),getString(R.string.google_places_avoid_2))
            }
            TYPE_YELP -> {
                rememberItems = mutableListOf(getString(R.string.yelp_remember_1), getString(R.string.yelp_remember_2))
                avoidItems = mutableListOf(getString(R.string.yelp_avoid_1), getString(R.string.yelp_avoid_2))
            }
            TYPE_PICTURE -> {
                rememberItems = mutableListOf(getString(R.string.photo_remember_1), getString(R.string.photo_remember_2))
                typologyItems = mutableMapOf()

                //TODO update values in strings for all sub actions
                for(subAction in subActions){
                    var value = when (subAction.type){
                        SUBTYPE_FOOD_PIC -> "TODO"
                        SUBTYPE_ATMOSPHERE -> "TODO"
                        SUBTYPE_MODEL_IN_VENUE -> "TODO"
                        SUBTYPE_STILL_LIFE -> getString(R.string.photo_still_life)
                        else -> "TODO"
                    }
                    typologyItems!!.put(subAction.displayName, value)
                }
            }
            else ->{
                rememberItems = mutableListOf("TODO")
                avoidItems = mutableListOf("TODO")
            }
        }

        typologyItems?.let {
            reviewDialogRememberContainer.visibility = View.VISIBLE
            rememberAdapter = RememberAdapter(rememberItems!!.toList())
            reviewDialogRememberRv.adapter = rememberAdapter
            reviewDialogRememberRv.layoutManager = LinearLayoutManager(reviewDialogRememberRv.context, RecyclerView.VERTICAL,false)
            reviewDialogRememberRv.addItemDecoration(MarginItemDecorator(reviewDialogRememberRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), true))

            reviewDialogTypologiesContainer.visibility = View.VISIBLE

            val typologies: MutableList<PictureTypology> = mutableListOf()
            for((title, value) in typologyItems!!){
                typologies.add(PictureTypology(title, value))
            }

            typologyAdapter = TypologyAdapter(typologies)
            reviewDialogTypologiesRv.adapter = typologyAdapter
            reviewDialogTypologiesRv.layoutManager = LinearLayoutManager(reviewDialogTypologiesRv.context, RecyclerView.VERTICAL,false)
            reviewDialogTypologiesRv.addItemDecoration(MarginItemDecorator(reviewDialogTypologiesRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), true))
        } ?: run{
            reviewDialogRememberContainer.visibility = View.VISIBLE
            rememberAdapter = RememberAdapter(rememberItems!!.toList())
            reviewDialogRememberRv.adapter = rememberAdapter
            reviewDialogRememberRv.layoutManager = LinearLayoutManager(reviewDialogRememberRv.context, RecyclerView.VERTICAL,false)
            reviewDialogRememberRv.addItemDecoration(MarginItemDecorator(reviewDialogRememberRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), true))

            reviewDialogAvoidContainer.visibility = View.VISIBLE
            avoidAdapter = AvoidAdapter(avoidItems!!.toList())
            reviewDialogAvoidRv.adapter = avoidAdapter
            reviewDialogAvoidRv.layoutManager = LinearLayoutManager(reviewDialogAvoidRv.context, RecyclerView.VERTICAL,false)
            reviewDialogAvoidRv.addItemDecoration(MarginItemDecorator(reviewDialogAvoidRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), true))
        }

        var d = when(action.type){
            //TODO there will be more types - facebook review, facebook story etc
            TYPE_FACEBOOK_POST -> R.drawable.facebook_logo
            TYPE_INSTAGRAM_POST, TYPE_INSTAGRAM_STORY -> R.drawable.instagram_logo
            TYPE_TRIP_ADVISOR -> R.drawable.trip_advisor_logo
            TYPE_GOOGLE_PLACES -> R.drawable.google_logo
            TYPE_YELP -> R.drawable.yelp_logo

            //TODO update this drawable
            TYPE_PICTURE -> R.drawable.add_photo
            else -> null
        }

        d?.let {
            reviewDialogImage.loadImageInside(it, whitePlaceholder = true)
        }

        reviewDialogName.text = action.displayName
        reviewDialogCreditsValue.text = "+${action.credits}"
    }

}