package tech.jianka.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import tech.jianka.activity.R;
import tech.jianka.data.Item;

import static tech.jianka.utils.CardUtil.getSpecifiedSDPath;
import static tech.jianka.utils.SDCardHelper.Obj2Bytes;
import static tech.jianka.utils.SDCardHelper.saveFileToSDCard;

/**
 * Created by Richard on 2017/7/28.
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int GROUP = 1;
    public static final int CARD = 2;
    public static final int CARD_AND_GROUP = 3;
    public static final int TASK_GROUP = 4;
    public static final int TASK_AND_GROUP = 5;
    public static final int TASK_IMPORTANT_EMERGENT = 6;
    public static final int TASK_IMPORTANT_NOT_EMERGENT = 7;
    public static final int TASK_UNIMPORTANT_EMERGENT = 8;
    public static final int TASK_UNIMPORTANT_NOT_EMERGENT = 9;
    public static final int ITEM_ONE_COLOMN = 40;
    public static final int ITEM_TWO_COLOMN = 785;
    public static final int CARD_TWO_COLUMN = 20;

    private ItemClickListener listener;
    private List<Item> items;
    private int adapterType = 0;

    public ItemAdapter(List<Item> items, int adapterType, ItemClickListener listener) {
        this.listener = listener;
        this.items = items;
        this.adapterType = adapterType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case GROUP:
                view = inflater.inflate(R.layout.group_item, parent, false);
                return new GroupViewHolder(view);
            case CARD:
                view = inflater.inflate(R.layout.card_item_big_rectangle, parent, false);
                return new CardViewHolder(view);
            case TASK_GROUP:
                view = inflater.inflate(R.layout.task_group_item, parent, false);
                return new GroupViewHolder(view);
            case TASK_IMPORTANT_EMERGENT:
                break;
            case TASK_IMPORTANT_NOT_EMERGENT:
                break;
            case TASK_UNIMPORTANT_EMERGENT:
                break;
            case TASK_UNIMPORTANT_NOT_EMERGENT:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            ((GroupViewHolder) holder).mTitle.setText(items.get(position).getFileName());
        } else if (holder instanceof CardViewHolder) {
            if (items != null) {
                ((CardViewHolder) holder).mCardTitle.setText(items.get(position).getCardTitle());
                ((CardViewHolder) holder).mCardContent.setText((String) items.get(position).getCardContent());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (adapterType) {
            case GROUP:
                return GROUP;
            case CARD:
                return CARD;
            case TASK_GROUP:
                return TASK_GROUP;
            default:
                return GROUP;
        }

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void addItem(Item item) {
        items.add(item);
        try {
            if (item.getItemType() == Item.GROUP) {
                new File(getSpecifiedSDPath(item.getFilePath())).createNewFile();
            } else {
                saveFileToSDCard(Obj2Bytes(item), item.getFilePath(), item.getCardTitle() + ".card");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public boolean removeItem(int position) {
        Item toDeleteItem = items.get(position);
        // TODO: 2017/8/6 完善能不能删除的逻辑
        String[] canNotDelete = {"收信箱", "任务", "重要-紧急"};
        String toCompare = toDeleteItem.getFileName();
        for (String name : canNotDelete) {
            if (name.equals(toCompare)) {
                return false;
            }
        }
        new File(items.get(position).getFilePath()).delete();
        items.remove(position);
        notifyDataSetChanged();
        return true;
    }

    public void shareItem(int clickedCardIndex, Activity context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, items.get(clickedCardIndex).getCardTitle() +
                "\n" + items.get(clickedCardIndex).getCardContent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getTitle()));
    }

    public Item getItem(int clickedItemIndex) {
        return items.get(clickedItemIndex);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        TextView mCardTitle;
        TextView mCardDate;
        TextView mCardGroup;
        TextView mCardContent;
        ImageView mCardImage;

        public CardViewHolder(View itemView) {
            super(itemView);
            mCardTitle = (TextView) itemView.findViewById(R.id.card_item_title);
            mCardDate = (TextView) itemView.findViewById(R.id.card_item_date);
            mCardGroup = (TextView) itemView.findViewById(R.id.card_item_group);
            mCardImage = (ImageView) itemView.findViewById(R.id.card_item_image);
            mCardContent = (TextView) itemView.findViewById(R.id.card_item_content);
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
    }
}
