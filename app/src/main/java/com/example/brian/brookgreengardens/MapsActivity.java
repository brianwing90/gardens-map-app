package com.example.brian.brookgreengardens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewGroupCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnGroundOverlayClickListener, GoogleMap.OnMapClickListener, LocationListener{

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1; // Used in onRequestPermissionsResult() as a way to determine which permission request we are receiving a result for.

    private boolean DEBUG = false; // Set this to enable debug logging on map and overlay taps.
    private GoogleMap map;
    private RelativeLayout details;
    private TextView header;
    private TextView body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this); // Listener for general touches and clicks.
        map.setOnGroundOverlayClickListener(this); // Listener for touches and clicks on added map images such as statue markers.
        map.setMaxZoomPreference(19.0f); // Do not allow zooming closer than this.
        map.setMinZoomPreference(16.0f); // Do not allow zooming out more than this.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.51789686511843, -79.09653704613447), 18.0f)); // Set default position and zoom on map.
        details = (RelativeLayout) findViewById(R.id.details);
        header = (TextView) findViewById(R.id.header);
        body = (TextView) findViewById(R.id.body);

        // Setup UI to allow zoom and track current location.
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            map.setMyLocationEnabled(true);

            // Register for location updates.
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4, 8, this);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 8, 8, this);
            onLocationChanged(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }else{
            if(DEBUG){
                Log.d("ERROR", "Fine location data permission not given.");
            }

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // TODO: Show an explanation of why this app needs location data.
            }

            // Request permission to access fine location data.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        // Set bounds of map to the park.
        LatLng neBound = new LatLng(33.52505434497748, -79.08303815871477);
        LatLng swBound = new LatLng(33.511966835663735, -79.09990020096302);
        LatLngBounds bounds = new LatLngBounds(swBound, neBound);
        map.setLatLngBoundsForCameraTarget(bounds);

        // Add overlays for all interactive parts of the park.
        createOverlays();
    }

    /**
     * Adds overlays to the map for each of the attractions at the park.
     */
    public void createOverlays(){
        //////////////// Heron, grouse, and loon ////////////////
        GroundOverlayOptions options = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.heron_grouse_and_loon))
                .anchor(0, 0)
                .position(new LatLng(33.52028507603851, -79.09436948597433), 21.667f, 23.0f);
        GroundOverlay overlay = map.addGroundOverlay(options);
        overlay.setClickable(true);
        overlay.setTag(R.string.hgl);

        //////////////// Don Quixote ////////////////
        options = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.don_quixote))
                .anchor(0, 0)
                .position(new LatLng(33.51948911130676, -79.09525159746408), 17.667f, 27.667f);
        overlay = map.addGroundOverlay(options);
        overlay.setClickable(true);
        overlay.setTag(R.string.dq);

        //////////////// Saint James Triad ////////////////
        options = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.saint_james_triad))
                .anchor(0, 0)
                .position(new LatLng(33.51943954538126, -79.0959556773305), 16.0f, 16.357f);
        overlay = map.addGroundOverlay(options);
        overlay.setClickable(true);
        overlay.setTag(R.string.sjt);

        //////////////// Flying Wild Geese ////////////////
        options = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot))
                .anchor(0, 0)
                .position(new LatLng(33.518551787822396, -79.09638918936253), 4.0f, 4.0f);
        overlay = map.addGroundOverlay(options);
        overlay.setClickable(true);
        overlay.setTag(R.string.fwg);

        //////////////// Circle of Life ////////////////
        options = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot))
                .anchor(0, 0)
                .position(new LatLng(33.518898674023546, -79.09600630402565), 4.0f, 4.0f);
        overlay = map.addGroundOverlay(options);
        overlay.setClickable(true);
        overlay.setTag(R.string.col);

        //////////////// Child of Peace ////////////////
        options = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot))
                .anchor(0, 0)
                .position(new LatLng(33.51919748140876, -79.09591443836689), 4.0f, 4.0f);
        overlay = map.addGroundOverlay(options);
        overlay.setClickable(true);
        overlay.setTag(R.string.cop);
    }

    @Override
    public void onGroundOverlayClick(GroundOverlay overlay){
        String[] sub = getResources().getString((int) overlay.getTag()).split(";");
        String title = sub[0]; // Parse out overlay title/header.
        String info = sub[1]; // Parse out overlay artist, medium, and dates.
        String text = sub[2]; // Parse out overlay text/body.

        //Show name of clicked overlay for debugging.
        if(DEBUG){
            Log.d("DEBUG", title + " was clicked");
        }

        // Fill in details with information about the overlay that was clicked.
        header.setText(title + "\n");
        body.setText(info + "\n\n" + text);
        details.setVisibility(View.VISIBLE);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        details.startAnimation(slideIn);

        // Show header of details for debugging.
        if(DEBUG){
            Log.d("DEBUG", "Set details to " + title);
        }
        // TODO: Add close button.
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(DEBUG) {
            Log.d("DEBUG", "Map clicked: " + latLng.latitude + ", " + latLng.longitude);
            Toast.makeText(this, "Lat: " + latLng.latitude + "\nLong: " + latLng.longitude, Toast.LENGTH_LONG).show();
        }

        // Remove details view when map is clicked with sliding animation.
        if(details.getVisibility() == View.VISIBLE) {
            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
            slideOut.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation anim) {
                }
                @Override
                public void onAnimationRepeat(Animation anim) {
                }
                @Override
                public void onAnimationEnd(Animation anim) {
                    header.setText("");
                    body.setText("");
                    details.setVisibility(View.GONE);
                }
            });
            details.startAnimation(slideOut);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(DEBUG){
            Log.d("DEBUG", "Location changed.");
        }

        // Avoid using null location data.
        if(location == null){
            return;
        }

        // Move camera to new location.
        /*LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, map.getCameraPosition().zoom));*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
