package com.example.securityapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class home_fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(), "onviewcreate", Toast.LENGTH_SHORT).show();

        if (navigation.test) {

            TextView tv =getActivity().findViewById(R.id.textView3);
            tv.setVisibility(View.VISIBLE);


        } else {
            TextView tv =getActivity().findViewById(R.id.textView3);
            tv.setVisibility(View.INVISIBLE);

        }
    }
}
