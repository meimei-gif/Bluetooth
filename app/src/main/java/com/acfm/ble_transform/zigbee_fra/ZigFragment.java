package com.acfm.ble_transform.zigbee_fra;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acfm.ble_transform.R;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ZigFragment extends Fragment {

    private RecyclerView recyclerView;
    private View view;
    private ZigbeeFragmentRecyclerAdapter fragmentRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.re_fragment,container,false);
        initRecyclerView();
        return view;
    }


    /**  getActivity()说明
     *      返回一个和此fragment绑定的FragmentActivity或者其子类的实例。
     *      相反，如果此fragment绑定的是一个context的话，怎可能会返回null。
     *      因为getActivity()大部分都是在fragment中使用到，而fragment需要依赖于activity，
     *      所有我们在fragment里头需要做一些动作，比如启动一个activity，就需要拿到activity对象才可以启动，
     *      而fragment对象是没有startActivity()方法的。
     */


    public void initRecyclerView(){
        recyclerView = view.findViewById(R.id.re_fragment1);
        fragmentRecyclerAdapter = new ZigbeeFragmentRecyclerAdapter(getActivity(),1);
        recyclerView.setAdapter(fragmentRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        fragmentRecyclerAdapter.setOnItemClickListener(new ZigbeeFragmentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Integer id) {
                Toast.makeText(getActivity(),"我是item", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
