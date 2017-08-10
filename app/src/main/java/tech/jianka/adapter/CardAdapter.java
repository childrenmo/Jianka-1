package tech.jianka.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.percent.PercentFrameLayout;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import tech.jianka.activity.R;
import tech.jianka.data.Card;
import tech.jianka.data.Item;
import tech.jianka.data.RecentData;

import static tech.jianka.utils.ItemUtils.longToString;

/**
 * Created by Richard on 2017/7/28.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private RecyclerView recyclerView;
    private ItemClickListener listener;
    private List<Card> cards;
    private int mExpandedPosition = -1;
    public CardAdapter(List<Card> cards, RecyclerView recyclerView, ItemClickListener listener) {
        this.listener = listener;
        this.recyclerView = recyclerView;
        this.cards = cards;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.card_item_big_rectangle, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final boolean isExpand = mExpandedPosition == position;
        holder.mDetails.setVisibility(isExpand ? View.VISIBLE : View.GONE);
        holder.mCardContentBrief.setVisibility(isExpand?View.GONE:View.VISIBLE);
        Card card = cards.get(position);
        holder.mCardTitle.setText(card.getCardTitle());
        holder.mCardContent.setText(card.getCardContent());
        try {
            String date = longToString(card.getModifiedTime(), "HH:mm MM/dd");
            holder.mCardDate.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.mCardContent.setText(card.getCardContent());
    }

    @Override
    public int getItemViewType(int position) {
        return cards.get(position).getItemType();
    }

    @Override
    public int getItemCount() {
        return cards == null ? 0 : cards.size();
    }

    public void addItem(Card card) {
        RecentData.addCard(card);
        notifyDataSetChanged();
    }

    public boolean removeItem(int position) {
        if (RecentData.removeCard(position)) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void shareItem(int clickedCardIndex, Activity context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, cards.get(clickedCardIndex).getCardTitle() +
                "\n" + cards.get(clickedCardIndex).getCardContent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getTitle()));
    }

    public Item getItem(int clickedItemIndex) {
        return cards.get(clickedItemIndex);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        TextView mCardTitle;
        TextView mCardDate;
        TextView mCardContentBrief;

        PercentFrameLayout mDetails;
        TextView mCardContent;
        ImageView mCardImage;

        public CardViewHolder(View itemView) {
            super(itemView);
            mCardTitle = (TextView) itemView.findViewById(R.id.task_item_title);
            mCardDate = (TextView) itemView.findViewById(R.id.card_item_date);
            mCardContentBrief = (TextView) itemView.findViewById(R.id.card_item_content_brief);
            mDetails = (PercentFrameLayout) itemView.findViewById(R.id.card_content_detail);
            mCardImage = (ImageView) itemView.findViewById(R.id.card_item_image);
            mCardContent = (TextView) itemView.findViewById(R.id.card_item_content);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            if (mExpandedPosition == clickedPosition) {
                mExpandedPosition = -1;
                TransitionManager.beginDelayedTransition(recyclerView);
                notifyDataSetChanged();
                listener.onItemClick(clickedPosition);
            }else {
                mExpandedPosition = clickedPosition;
                TransitionManager.beginDelayedTransition(recyclerView);
                notifyDataSetChanged();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onItemLongClick(clickedPosition);
            return true;
        }

    }

    /**
     * 实现长按和点击的接口
     */
    public interface ItemClickListener {
        void onItemClick(int clickedCardIndex);

        void onItemLongClick(int clickedCardIndex);
    }

}
