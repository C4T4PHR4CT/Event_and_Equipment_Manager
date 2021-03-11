package com.SovietHouseholdAppliances.EventManager;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    TextView username;
    TextView usertype;
    CheckBox remember;
    CheckBox cache;

    @Override
    public void onResume() {
        super.onResume();
        username = getActivity().findViewById(R.id.profile_user);
        usertype = getActivity().findViewById(R.id.profile_type);
        remember = getActivity().findViewById(R.id.profile_remember);
        cache = getActivity().findViewById(R.id.profile_cache);

        //mock
        username.setText("username");
        usertype.setText("employee");

        if (!remember.isChecked())
            cache.setPaintFlags(cache.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        remember.setOnClickListener(e -> {
            if (remember.isChecked()) {
                cache.setClickable(true);
                cache.setPaintFlags(cache.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                cache.setClickable(false);
                cache.setChecked(false);
                cache.setPaintFlags(cache.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });
    }
}