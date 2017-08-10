package tech.jianka.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.util.List;

import tech.jianka.activity.R;
import tech.jianka.data.Card;
import tech.jianka.data.DataType;
import tech.jianka.data.Item;
import tech.jianka.data.TaskData;

import static tech.jianka.utils.ItemUtils.longToString;

/**
 * Created by Richard on 2017/7/28.
 */

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemClickListener listener;
    private List<Card> cards;

    public TaskAdapter(List<Card> cards, ItemClickListener listener) {
        this.listener = listener;
        this.cards = cards;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == DataType.GROUP) {
            view = inflater.inflate(R.layout.task_group_item, parent, false);
            return new GroupViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            ((GroupViewHolder) holder).mTitle.setText(cards.get(position).getFileName());
            switch (cards.get(position).getCardType()) {
                case DataType.TASK_IMPORTANT_EMERGENT:
                    ((GroupViewHolder) holder).mImage.setImageResource(R.drawable.background_important_emergent);
                    break;
                case DataType.TASK_IMPORTANT_NOT_EMERGENT:
                    ((GroupViewHolder) holder).mImage.setImageResource(R.drawable.background_important_not_emergent);
                    break;
                case DataType.TASK_UNIMPORTANT_EMERGENT:
                    ((GroupViewHolder) holder).mImage.setImageResource(R.drawable.background_unimportant_emergent);
                    break;
                case DataType.TASK_UNIMPORTANT_NOT_EMERGENT:
                    ((GroupViewHolder) holder).mImage.setImageResource(R.drawable.background_umimportant_not_emergent);
                    break;
            }

        } else if (holder instanceof TaskViewHolder) {

            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            layoutParams.setMargins(10, 5, 5, 10);
            holder.itemView.setLayoutParams(layoutParams);
            Card card = cards.get(position);
            ((TaskViewHolder) holder).mTaskTitle.setText(card.getCardTitle());
            ((TaskViewHolder) holder).mTaskContent.setText(card.getCardContent());
            try {
                String date = longToString(card.getModifiedTime(), "HH:mm MM/dd");
                ((TaskViewHolder) holder).mTaskDate.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            switch (card.getCardType()) {
                case DataType.TASK_IMPORTANT_EMERGENT:
                    ((TaskViewHolder) holder).mTaskImage.setImageResource(R.drawable.task_image_important_emergent);
                    break;
                case DataType.TASK_IMPORTANT_NOT_EMERGENT:
                    ((TaskViewHolder) holder).mTaskImage.setImageResource(R.drawable.task_image_important_not_emergent);
                    break;
                case DataType.TASK_UNIMPORTANT_EMERGENT:
                    ((TaskViewHolder) holder).mTaskImage.setImageResource(R.drawable.task_image_unimportant_emergent);
                    break;
                case DataType.TASK_UNIMPORTANT_NOT_EMERGENT:
                    ((TaskViewHolder) holder).mTaskImage.setImageResource(R.drawable.task_image_unimportant_not_emergent);
                    break;
            }
        }
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
        TaskData.addTask(card);
        notifyDataSetChanged();
    }

    public boolean removeItem(int position) {
        Item toDeleteItem = cards.get(position);
        String[] canNotDelete = {"很重要-很紧急", "很重要-不紧急", "不重要-很紧急", "不重要-不紧急"};
        String toCompare = toDeleteItem.getFileName();
        for (String name : canNotDelete) {
            if (name.equals(toCompare)) {
                return false;
            }
        }
        new File(cards.get(position).getFilePath()).delete();
        cards.remove(position);
        notifyDataSetChanged();
        return true;
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

    public class TaskViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
        TextView mTaskTitle;
        TextView mTaskDate;
        TextView mTaskContent;
        ImageView mTaskImage;
        CheckBox mCheckBox;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mTaskTitle = (TextView) itemView.findViewById(R.id.task_item_title);
            mTaskDate = (TextView) itemView.findViewById(R.id.task_item_date);
            mTaskImage = (ImageView) itemView.findViewById(R.id.task_item_image);
            mTaskContent = (TextView) itemView.findViewById(R.id.task_item_content);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.task_item_check_box);
            mCheckBox.setOnCheckedChangeListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onItemClick(clickedPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onItemLongClick(clickedPosition);
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int clickedPosition = getAdapterPosition();
            listener.onTaskCheck(clickedPosition);
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mTitle;
        ImageView mImage;

        public GroupViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.group_item_title);
            mImage = (ImageView) itemView.findViewById(R.id.group_item_image);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onItemClick(clickedPosition);
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

        void onTaskCheck(int clickedPosition);
    }
}
