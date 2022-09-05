package com.acfm.ble_transform.reprater_fra;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.fragment.FragmentRecyclerAdapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class re_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private View view;
    private re_FragmentRecyclerAdapter fragmentRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.re_fragment,container,false);
        initRecyclerView();
        return view;
    }

    public void initRecyclerView(){
        recyclerView = view.findViewById(R.id.re_fragment1);
        fragmentRecyclerAdapter = new re_FragmentRecyclerAdapter(getActivity(),1);
        recyclerView.setAdapter(fragmentRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        fragmentRecyclerAdapter.setOnItemClickListener(new re_FragmentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Integer id) {
                Toast.makeText(getActivity(),"我是item", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
