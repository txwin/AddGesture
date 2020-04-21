package com.example.addgesture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment {
    private View view;
    private TextView textView,textView2;
    private GestureFragment gestureFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.menu,container,false);
        textView=view.findViewById(R.id.textView2);
        textView2=view.findViewById(R.id.textView3);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestureFragment=(GestureFragment)((MainActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.gesture);
                gestureFragment.choose("Add");
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestureFragment=(GestureFragment)((MainActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.gesture);
                gestureFragment.choose("Distinguish");
            }
        });

        return view;
    }
}
