package com.infoline.doctorcha.presentation.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.util.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
	@BindView(R.id.fl_map)
	FrameLayout fl_map;

	/*
	@BindView(R.id.tv_ar)
	TextView tv_ar;
	@BindView(R.id.tv_tel)
	TextView tv_tel;
	@BindView(R.id.tv_mobile)
	TextView tv_mobile;
	@BindView(R.id.tv_44)
	TextView tv_44;
	*/

	private BottomSheetDialog bsd;
	private LatLng latLng;
	private String addr;
	ProgressDialog pd;


    public MapViewFragment() {

    }

    public static MapViewFragment newInstance(String addr, String location) {
		final MapViewFragment fragment = new MapViewFragment();
		fragment.setRetainInstance(true);

		final Bundle bundle = new Bundle();

        bundle.putString("addr", addr);
		bundle.putString("location", location);
        fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_mapview, container, false);

        ButterKnife.bind(this, rootView);

		pd = ProgressDialog.show(getActivity(), "", "지도 검색 중입니다", true);

		final String locationString = getArguments().getString("location");
		final String[] loArr = locationString.split(",");
		this.latLng = new LatLng(Double.parseDouble(loArr[0]), Double.parseDouble(loArr[1]));

		this.addr = getArguments().getString("addr");

		final GoogleMapOptions options = new GoogleMapOptions();
		options.liteMode(true).zoomControlsEnabled(true).zoomGesturesEnabled(false).compassEnabled(true).mapToolbarEnabled(true);

		CameraPosition camera = new CameraPosition(latLng, 15, 0, 0);
		options.camera(camera);

		final MapFragment mapFragment = MapFragment.newInstance(options);
		mapFragment.getMapAsync(this);

		final FragmentManager fm = getChildFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();

		ft.add(R.id.fl_map, mapFragment).commit();

		/*
		//mapFragment.addMarker(marker);
		//mGoogleMap.addMarker(optSecond).showInfoWindow();


		MarkerOptions optFirst = new MarkerOptions();
		optFirst.snippet("Snippet");
		optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
		*/

		return rootView;
	}

	@Override
	public void onMapReady(GoogleMap map) {
		CommonUtil.writeLog(null);
		MarkerOptions marker = new MarkerOptions();
		//marker.draggable(false).position(latLng).title("현대종합정비").snippet("description");
		marker.draggable(false).position(latLng).title(addr); //.snippet("description");
		map.addMarker(marker).showInfoWindow();
		////////////////////////////map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
		//map.addMarker(marker);

		pd.dismiss();
	}
}