package com.infoline.doctorcha.presentation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.infoline.doctorcha.R;
import com.infoline.doctorcha.core.gps.FetchAddressIntentService;
import com.infoline.doctorcha.core.util.CommonUtil;
import com.infoline.doctorcha.presentation.MainCons;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    @BindView(R.id.tb) Toolbar tb;
    @BindView(R.id.tv_lat) TextView tv_lat;
    @BindView(R.id.tv_lng) TextView tv_lng;

    @OnClick(R.id.bt_ok)
    protected void onClick(final View v) {
        final Intent intent = new Intent();
        intent.putExtra(MainCons.EnumExtraName.NAME1.name(), marker.getSnippet() == null ? "" : marker.getSnippet());
        intent.putExtra(MainCons.EnumExtraName.NAME2.name(), location);
        setResult(RESULT_OK, intent);
        finish();
    }

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private GoogleMap map = null;
    private Location location = null;
    private boolean isApplyLocation = false;
    private boolean isMapReady = false;

    private Marker marker;

    private AddressResultReceiver mResultReceiver;

    public MapsActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        tb.setTitle("주소 설정"); //setSupportActionBar(tb) 보다 선행되어야 동작한다
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mResultReceiver = new AddressResultReceiver(new Handler());

        final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        //---------------------------------------------------

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();

            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000); //10000
            locationRequest.setFastestInterval(500); //5000
            //locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setSmallestDisplacement(10); //10m
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    //이하 Location 관련 루틴


    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    /*
	@Override
	protected void onResume() {
		super.onResume();

		checkPlayServices();

		// Resuming the periodic location updates
		if (googleApiClient.isConnected()) {
			startLocationUpdates();
		}
	}
	*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    /*
	@Override
	protected void onPause() {
		super.onPause();
		stopLocationUpdates();
	}
	*/

    //위치 재설정시 사용
    private void checkLocationSetting() {
        //dialog.show();

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                CommonUtil.writeLog("onResult fire");
                final Status status = result.getStatus();
                //final LocationSettingsStates states = result.getLocationSettingsStates(); //필요없는 것 같은데.

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        CommonUtil.writeLog("LocationSettingsStatusCodes == SUCCESS");
                        startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        CommonUtil.writeLog("LocationSettingsStatusCodes == RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, 100);
                        } catch (IntentSender.SendIntentException e) {
                            //dialog.dismiss();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        CommonUtil.writeLog("LocationSettingsStatusCodes == SETTINGS_CHANGE_UNAVAILABLE");
                        //dialog.dismiss();
                        break;
                }
            }
        });
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.showErrorDialogFragment(this, resultCode, 1); //REQUEST_GOOGLE_PLAY_SERVICES
            }
            else {
                Toast.makeText(getApplicationContext(),	"Google Play Service를 사용할 수 없습니다", Toast.LENGTH_LONG).show();
                CommonUtil.writeLog("Google Play Service를 사용할 수 없습니다. resultCode == " + resultCode);
            }

            /*
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,	1000).show();
            }
            else {
                Toast.makeText(getApplicationContext(),	"Google Play Service를 사용할 수 없습니다", Toast.LENGTH_LONG).show();
                /////////////////////finish();
            }
            */
            return false;
        }
        return true;
    }

    //Creating google api client object
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    protected void startLocationUpdates() {
        CommonUtil.writeLog(null);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        catch(SecurityException e) {

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        CommonUtil.writeLog("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        CommonUtil.writeLog(null);

        //1. GPS가 켜져있고 최종 위치(LocationServices.FusedLocationApi.getLastLocation(googleApiClient)가 존재한다면 현재위치 요청하지 않고 바로 사용
        //2. 만약 getLastLocation() 사용에 문제가 있다면 startLocationUpdates()을 호출하여 onLocationChanged()에서 처리할 것
        //3. startLocationUpdates()

        //----------------------------------

        /*
        final Location location =  LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if(location == null) {
            checkLocationSetting();
        }
        else {
            displayLocation(location);
        }
        */

        checkLocationSetting();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        CommonUtil.writeLog(null);
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        CommonUtil.writeLog(null);
        this.location = location;

        //1. 위치변경을 추적할 필요가 있을 경우는 removeLocationUpdates()를 수행치 않으면 된다.
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        if(isMapReady) {
            applyLocation();
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        CommonUtil.writeLog(null);
        this.map = map;
        isMapReady = true;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        map.setPadding(0, CommonUtil.getPxFromDip(this, 56), 0, 0);

        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);;

        //1. 특수한 경우 onMapReady()가 onConnected 또는 onLocationChanged()보다 늦게 trigger될 수도 있다.
        if(location != null && !isApplyLocation) {
            applyLocation();
        }

        /*
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(location != null) {
                    //1. 내위치 버튼을 click했을 경우 때문에 이곳으로 이동
                    final LatLng latLng = cameraPosition.target;
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    if(marker == null) {
                        marker = map.addMarker(new MarkerOptions().title("현재위치").position(latLng));
                    } else {
                        marker.setPosition(latLng);
                    }

                    tv_lat.setText(String.format(Locale.KOREA, "%s : %f", "위도", latLng.latitude));
                    tv_lng.setText(String.format(Locale.KOREA, "%s : %f", "경도", latLng.longitude));

                    startIntentService();
                }
            }
        });
        */

        /*
        GoogleMap.OnCameraMoveStartedListener
        GoogleMap.OnCameraMoveListener
        GoogleMap.OnCameraIdleListener
         */

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                //Toast.makeText(context,map.getCameraPosition().target.latitude + " " + mMap.getCameraPosition().target.longitude,Toast.LENGTH_SHORT).show();
                final LatLng latLng = map.getCameraPosition().target;
                CommonUtil.writeLog("onCameraMoveStarted : " + latLng.toString());
            }
        });

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                final LatLng latLng = map.getCameraPosition().target;
                CommonUtil.writeLog("onCameraMoveStarted : " + latLng.toString());
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(location != null) {
                    //1. 내위치 버튼을 click했을 경우 때문에 이곳으로 이동
                    final LatLng latLng = map.getCameraPosition().target;
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    if(marker == null) {
                        marker = map.addMarker(new MarkerOptions().title("현재위치").position(latLng));
                    } else {
                        marker.setPosition(latLng);
                    }

                    tv_lat.setText(String.format(Locale.KOREA, "%s : %f", "위도", latLng.latitude));
                    tv_lng.setText(String.format(Locale.KOREA, "%s : %f", "경도", latLng.longitude));

                    startIntentService();
                }
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                final CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                map.animateCamera(center);

                /*
                marker.setPosition(latLng);

                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                startIntentService();
                */
            }
        });
    }

    private void applyLocation() {
        isApplyLocation = true;
        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        /*
        final MarkerOptions markerOptions = new MarkerOptions().title("현재위치").position(latLng);
        marker = map.addMarker(markerOptions);

        startIntentService();
        */
    }

    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        final Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }




    public final class Constants {
        public static final int SUCCESS_RESULT = 0;

        public static final int FAILURE_RESULT = 1;

        public static final String PACKAGE_NAME =
                "com.google.android.gms.location.sample.locationaddress";

        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

        public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            final String address = resultData.getString(Constants.RESULT_DATA_KEY);

            marker.setSnippet(address);
            marker.showInfoWindow();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //address found
            }
        }
    }

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
}
