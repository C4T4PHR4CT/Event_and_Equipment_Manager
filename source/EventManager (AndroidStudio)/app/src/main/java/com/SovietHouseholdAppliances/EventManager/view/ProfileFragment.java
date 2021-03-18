package com.SovietHouseholdAppliances.EventManager.view;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.SovietHouseholdAppliances.EventManager.R;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    MainActivity activity;

    TextView username;
    TextView type;
    TextView organization;
    CheckBox keep;
    CheckBox remember;

    @Override
    public void onResume() {
        super.onResume();

        activity = (MainActivity) getActivity();

        username = activity.findViewById(R.id.profile_user);
        type = activity.findViewById(R.id.profile_type);
        organization = activity.findViewById(R.id.profile_organization);
        keep = activity.findViewById(R.id.profile_keep);
        remember = activity.findViewById(R.id.profile_remember);

        activity.viewModel.refreshUser();

        username.setText(activity.viewModel.getUser().getValue().name);
        type.setText(activity.viewModel.getUser().getValue().permission);
        organization.setText(activity.viewModel.getUser().getValue().organization);

        activity.viewModel.getUser().observe(this, user -> {
            username.setText(user.name);
            type.setText(user.permission);
            organization.setText(user.organization);
        });

        keep.setChecked(activity.viewModel.getKeepLoggedIn());
        remember.setChecked(activity.viewModel.getRememberCredentials());

        if (!keep.isChecked())
            remember.setPaintFlags(remember.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        keep.setOnClickListener(e -> {
            activity.viewModel.setKeepLoggedIn(keep.isChecked());
            if (keep.isChecked()) {
                remember.setClickable(true);
                remember.setPaintFlags(remember.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                remember.setClickable(false);
                remember.setChecked(false);
                remember.setPaintFlags(remember.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        remember.setOnClickListener(e -> {
            activity.viewModel.setRememberCredentials(remember.isChecked());
            if (remember.isChecked()) {
                Intent temp = new Intent(activity, LoginActivity.class);
                temp.putExtra("auth", false);
                activity.startActivity(temp);
            }
        });
    }
}