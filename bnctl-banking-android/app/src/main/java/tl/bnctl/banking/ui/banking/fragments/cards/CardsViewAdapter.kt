package tl.bnctl.banking.ui.banking.fragments.cards

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cards.CardStatus
import tl.bnctl.banking.data.cards.model.Card

class CardsViewAdapter(
    private val cardsResult: LiveData<Result<List<Card>>>
) : PagerAdapter() {

    private var mViewHolders: MutableList<ViewGroup> = mutableListOf()

    // It's either this, or having to use reflexion to find the actual drawable ID from the product code. Deal with it.
    private val cardDesigns = mapOf(
        "4199380" to R.drawable.card_image_4199380,
        "4249820" to R.drawable.card_image_4249820,
        "5352520" to R.drawable.card_image_5352520,
        "4244680" to R.drawable.card_image_4244680,
        "5168950" to R.drawable.card_image_5168950,
        "5397200" to R.drawable.card_image_5397200,
        "4249800" to R.drawable.card_image_4249800,
        "5168959" to R.drawable.card_image_5168959,
        "5397209" to R.drawable.card_image_5397209,
        "4249810" to R.drawable.card_image_4249810,
        "5200110" to R.drawable.card_image_5200110,
        "5526210" to R.drawable.card_image_5526210,
    )

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val viewHolder: ViewGroup = createViewHolder(container, position)

        container.addView(viewHolder)
        return viewHolder
    }

    private fun createViewHolder(container: ViewGroup, position: Int): ViewGroup {
        val layout: ViewGroup = LayoutInflater.from(container.context)
            .inflate(R.layout.item_bank_card, container, false) as ViewGroup

        val panTextView: TextView = layout.findViewById(R.id.tv_card_number)
        val cardImageView: ImageView = layout.findViewById(R.id.iv_card_image)
        val cardHolder: TextView = layout.findViewById(R.id.tv_card_holder)

        val card: Card = getItemOnPosition(position)

        // Hide PAN if the card is new (no PAN returned)
        if (CardStatus.fromString(card.cardStatus) == CardStatus.PRODUCED_NOT_RECEIVED) {
            panTextView.visibility = View.INVISIBLE
        }

        panTextView.text = card.cardNumber
        cardHolder.text = card.cardPrintName

        if (CardStatus.fromString(card.cardStatus) != CardStatus.ACTIVE) {
            cardImageView.setColorFilter(Color.argb(125, 255, 255, 255));
        }

        // Set card image
        if (cardDesigns.containsKey(card.cardProductCode)) {
            cardDesigns[card.cardProductCode]?.let { cardImageView.setImageResource(it) }
        } else {
            cardImageView.setImageResource(R.drawable.card_generic)
        }

        mViewHolders.add(layout)
        return layout
    }

    private fun getItemOnPosition(position: Int): Card {
        return (cardsResult.value as Result.Success).data[position]
    }

    override fun getCount(): Int {
        if (cardsResult.value is Result.Success) {
            return (cardsResult.value as Result.Success).data.size
        }
        return 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeViewInLayout(`object` as View)
    }

    fun getItemLayoutOnPosition(position: Int): ViewGroup? {
        if (mViewHolders.size == 0) {
            return null
        }
        return if (position <= mViewHolders.size - 1) mViewHolders.get(position) else mViewHolders.get(
            mViewHolders.size - 1
        )
    }
}