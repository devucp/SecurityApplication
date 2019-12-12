package com.example.securityapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class home_fragment extends Fragment {
    Button bt;
    Boolean is_paid = false;
    public static Boolean test = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bt = getActivity().findViewById(R.id.emergency);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_paid) {
                    Toast.makeText(getContext(), "You are premier member", Toast.LENGTH_SHORT).show();
                } else {
                    //if user using free services only
                    new AlertDialog.Builder(getContext()).setMessage("Upgrade to Premier to Use this function")
                            .setPositiveButton("Purchased", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                        intent.setData(Uri.parse("http://www.w3schools.com"));
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).setNegativeButton("Cancel", null).setCancelable(false).create().show();
                }
            }
        });
        if (navigation.test) {

            TextView tv = getActivity().findViewById(R.id.textView3);
            tv.setVisibility(View.VISIBLE);
        } else {
            TextView tv = getActivity().findViewById(R.id.textView3);
            tv.setVisibility(View.INVISIBLE);
        }

    }
}