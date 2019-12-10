package com.example.securityapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class setting_fragment extends Fragment {
    Button sos,rate_us;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rate_us=getActivity().findViewById(R.id.rate_us);
        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse("market://details?id="+"com.android.chrome");
                    Intent Rate = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(Rate);
                }
                catch (ActivityNotFoundException e)
                {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+"com.android.chrome");
                    Intent Rate = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(Rate);
                }
            }
        });
    }
}
