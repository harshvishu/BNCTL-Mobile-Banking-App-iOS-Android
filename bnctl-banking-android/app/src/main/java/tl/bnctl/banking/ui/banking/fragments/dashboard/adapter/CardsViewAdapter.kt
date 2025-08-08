package tl.bnctl.banking.ui.banking.fragments.dashboard.adapter

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.data.cards.model.Card

class CardsViewAdapter(
    private val cardsResult: LiveData<Result<List<Card>>>,
    private val onClickListener: (Card) -> Unit
) : RecyclerView.Adapter<CardsViewAdapter.ViewHolder>() {

    class ViewHolder(view: View, val onClickListener: (Card) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private var selectedCard: Card? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}