package tech.jianka.adapter;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import tech.jianka.activity.R;
import tech.jianka.data.DataType;
import tech.jianka.data.Group;
import tech.jianka.data.GroupData;

/**
 * Created by Richard on 2017/7/28.
 */

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemClickListener listener;
    private List<Group> groups;

    public GroupAdapter(List<Group> groups, ItemClickListener listener) {
        this.listener = listener;
        this.groups = groups;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            String name = groups.get(position).getFileName();
            String path = groups.get(position).getCoverPath();
            ((GroupViewHolder) holder).mTitle.setText(name);
            if (new File(path).exists()) {
                ((GroupViewHolder) holder).mImage.setImageBitmap(BitmapFactory.decodeFile(path));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return DataType.GROUP;
    }

    @Override
    public int getItemCount() {
        return groups == null ? 0 : groups.size();
    }

    public void addItem(Group group) {
        groups.add(group);
        GroupData.addGroup(group);
        notifyDataSetChanged();
    }

    public int removeItem(int position) {
        int result = GroupData.removeGroup(position);
        if (result == GroupData.DELETE_DONE) {
            groups.remove(position);
            notifyDataSetChanged();
        }
        return result;
    }

    public void removeGroupAndCards(int position) {
        GroupData.removeGroupAndCards(position);
        notifyDataSetChanged();
    }

    public void renameGroup(int index, String title) {
        GroupData.renameGroup(index, title);
        notifyDataSetChanged();
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
