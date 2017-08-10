package tech.jianka.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import tech.jianka.activity.NewCardGroupActivity;
import tech.jianka.activity.R;
import tech.jianka.adapter.GroupAdapter;
import tech.jianka.data.GroupData;

/**
 * Created by Richa on 2017/8/6.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment implements GroupAdapter.ItemClickListener {
    public static final String ARG_FRAGMENT_TYPE = "TYPE";

    private int fragmentType;
    private OnFragmentInteractionListener mListener;
    private View view;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    public GroupAdapter adapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentType
     * @return A new instance of fragment GroupFragment.
     */
    public static GroupFragment newInstance(int fragmentType) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, fragmentType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentType = getArguments().getInt(ARG_FRAGMENT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.group_recycler_view);
        layoutManager = new GridLayoutManager(getActivity(), 2, GridLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        GroupData data = new GroupData();
        adapter = new GroupAdapter(data.getGroup(), this);
        recyclerView.setAdapter(adapter);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(int clickedCardIndex) {
        // TODO: 2017/8/6 显示group中的内容  fragment 层叠
        // TODO: 2017/7/26 处理卡片单击事件
    }


    @Override
    public void onItemLongClick(final int clickedCardIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] options;
        options = getActivity().getResources().getStringArray(R.array.card_group_options);
        AlertDialog alertDialog = builder.setTitle("选择操作")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                int result = adapter.removeItem(clickedCardIndex);
                                if (result == GroupData.INBOX) {
                                    Toast.makeText(getActivity(),"收信箱不能被删除",Toast.LENGTH_SHORT).show();
                                } else if (result == GroupData.NOT_EMPTY) {
                                    AlertDialog confirmDelete = new AlertDialog.Builder(getContext()).setTitle("删除确认")
                                            .setMessage("卡组不为空,继续删除吗?")
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    adapter.removeGroupAndCards(clickedCardIndex);
                                                }
                                            })
                                            .create();             //创建AlertDialog对象
                                    confirmDelete.show();                    //显示对话框
                                } else if (result == GroupData.DELETE_DONE) {
                                    Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT);
                                }
                                break;
                            case 1:
                                Intent renameGroup = new Intent(getActivity(), NewCardGroupActivity.class);
                                renameGroup.putExtra("RENAME_GROUP", clickedCardIndex);
                                startActivity(renameGroup);

                                // TODO: 2017/8/6 rename
                                break;
                            case 2:
                                // TODO: 2017/8/6 修改封面
                                break;
                        }
                    }
                }).create();
        alertDialog.show();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}



