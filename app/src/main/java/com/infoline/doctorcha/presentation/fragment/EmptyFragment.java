package com.infoline.doctorcha.presentation.fragment;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infoline.doctorcha.R;

import butterknife.ButterKnife;

public class EmptyFragment extends Fragment {
    public EmptyFragment() {

    }

	public static EmptyFragment newInstance() {
		return new EmptyFragment();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		////////////////////////////////////applyViewHolderType();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_empty, container, false);
        ButterKnife.bind(this, rootView);



		return rootView;
	}
}