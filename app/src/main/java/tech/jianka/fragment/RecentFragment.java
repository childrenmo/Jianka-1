package tech.jianka.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import tech.jianka.activity.NewCardActivity;
import tech.jianka.activity.R;
import tech.jianka.adapter.ItemAdapter;
import tech.jianka.data.Item;
import tech.jianka.utils.SpaceItemDecoration;

import static tech.jianka.utils.CardUtil.getChildItems;
import static tech.jianka.utils.CardUtil.getSpecifiedSDPath;

/**
 * Created by Richa on 2017/8/6.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentFragment extends Fragment implements ItemAdapter.ItemClickListener {
    public static final String ARG_FRAGMENT_TYPE = "TYPE";

    private static Toast mToast;

    private int fragmentType;

    private OnFragmentInteractionListener mListener;
    private View view;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    public ItemAdapter adapter;

    public RecentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentType
     * @return A new instance of fragment RecentFragment.
     */
    public static RecentFragment newInstance(int fragmentType) {
        RecentFragment fragment = new RecentFragment();
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
        view = inflater.inflate(R.layout.fragment_recent, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recent_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        String path = getSpecifiedSDPath("jianka/data/收信箱");
        List<Item> items = getChildItems(path);
        adapter = new ItemAdapter(items, ItemAdapter.CARD, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(5));
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
        // TODO: 2017/7/26 处理卡片单击事件
        Intent intent = new Intent(getActivity(), NewCardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CARD_DETAILS", adapter.getItem(clickedCardIndex));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onItemLongClick(final int clickedCardIndex) {
        builder = new AlertDialog.Builder(getContext());
        String[] options;
        options = getActivity().getResources().getStringArray(R.array.card_options);
        alertDialog = builder.setTitle("选择操作")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                adapter.shareItem(clickedCardIndex, getActivity());
                                break;
                            case 1:
                                if (!adapter.removeItem(clickedCardIndex)) {
                                    mToast.makeText(getActivity(), "删除失败", Toast.LENGTH_LONG).show();
                                }

                                break;
                            case 2:
                                // TODO: 2017/8/6 切换分组功能
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



