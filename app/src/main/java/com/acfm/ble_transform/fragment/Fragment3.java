package com.acfm.ble_transform.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acfm.ble_transform.R;

public class Fragment3 extends Fragment {
    private RecyclerView recyclerView;
    private View view;
    private FragmentRecyclerAdapter fragmentRecyclerAdapter;
    private static int signal3;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment3,container,false);
        initRecyclerView();
        return view;
    }
    public int getvalue()
    {
        int ans;
        Bundle bundle = getArguments();
        if(bundle==null)
        {
            return -1;
        }
        else {
            ans = bundle.getInt("signal", -1);
        }
        return ans;
    }

    public void initRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_fragment3);
        if (getvalue() != -1)
        {
            signal3=getvalue();
        }
        fragmentRecyclerAdapter = new FragmentRecyclerAdapter(getActivity(),128,signal3);
        recyclerView.setAdapter(fragmentRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        fragmentRecyclerAdapter.setOnItemClickListener(new FragmentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Integer id) {
                Toast.makeText(getActivity(),"我是item", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
