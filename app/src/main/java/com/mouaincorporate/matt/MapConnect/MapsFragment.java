package com.mouaincorporate.matt.MapConnect;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mouaincorporate.matt.MapConnect.MapsUtilities.ApiService;
import com.mouaincorporate.matt.MapConnect.MapsUtilities.Constant;
import com.mouaincorporate.matt.MapConnect.MapsUtilities.Data;
import com.mouaincorporate.matt.MapConnect.MapsUtilities.DirectionResultsModel;
import com.mouaincorporate.matt.MapConnect.MapsUtilities.Legs;
import com.mouaincorporate.matt.MapConnect.MapsUtilities.LoggingInterceptorGoogle;
import com.mouaincorporate.matt.MapConnect.firebase_entry.City;
import com.mouaincorporate.matt.MapConnect.firebase_entry.Event;
import com.mouaincorporate.matt.MapConnect.firebase_entry.Messages;
import com.mouaincorporate.matt.MapConnect.util.CircleTransform;
import com.mouaincorporate.matt.MapConnect.util.RHRNNotifications;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static java.lang.Integer.parseInt;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnPoiClickListener{

    public TextView     driveDuration,
                        driveDistance,
                        walkDuration,
                        walkDistance,
                        bikeDuration,
                        bikeDistance,
                        busDuration,
                        busDistance;
    public RadioButton  driveRB,
                        walkRB,
                        bikeRB;
    public RadioGroup   rgModes;
    int mMode = 0;

    private ApiService serviceGoogleDirection;
    private static final int LOCATIONS_PERMISSION = 0;
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public int countNumber = 0;
    public int isEducation = 0, isSports = 0, isParty = 0, isClubEvent = 0, isOther = 0, logout = 0;
    Map<String, Integer> map;
    int[] filter;

    private GoogleMap mMap;
    public MapView mapView;
    public static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public int radius = 1000;
    private FloatingActionButton button;
    private Menu menu;

    private double kmToMiles = 0.621371;
    public double curLatitude;
    public double curLongitude;
    private boolean first = true;

    private String curUserID;
    private GeoLocation curLocation;

    public DatabaseReference eventsOnMap,
            postsOnMap;

    public BottomNavigationView topNavigationView;

    // eventually remove these
    ValueAnimator temp;
    Marker ourLoc;
    Circle removeCircle;

    public GeoQuery eventQuery;
    public GeoQuery postQuery;

    public Bitmap Marker;

    ArrayList<LatLng> MarkerPoints;
    public HashMap<Marker, String> eventMarkerKeys = new HashMap<Marker, String>();
    public HashMap<String, Marker> eventKeyMarkers = new HashMap<String, Marker>();
    public HashMap<Marker, String> postMarkerKeys = new HashMap<Marker, String>();
    public HashMap<String, Marker> postKeyMarkers = new HashMap<String, Marker>();

    //Globals related to on map long click
    private LinearLayout layoutToAdd;
    private int inflateButtons = 0;

    //Uploading to fb
    // creating an instance of Firebase Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //creating a storage reference.
    StorageReference storageRef = storage.getReferenceFromUrl("gs://mapconnect-cf482.appspot.com/");
    Uri filePath;
    boolean alreadyExecuted = false;

    public ArrayList<Event> eventArrayList;
    public TrendingFragment.EventAdapter eventAdapter;
    public ListView eventListView;
    public int listview = 0;
    FloatingActionButton addEvent, directions;
    ImageView changeSattelite;
    View view2;
    public GeoDataClient mGeoDataClient;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View r = (View) inflater.inflate(R.layout.maps_fragment_layout, container, false);

        mapView = (MapView) r.findViewById(R.id.primary_map);
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        changeSattelite = (ImageView) r.findViewById(R.id.sattelite);

        MarkerPoints = new ArrayList<>();
        mGeoDataClient = Places.getGeoDataClient(getContext(), null);

        //mapView = new MapView(getActivity());
        layoutToAdd = (LinearLayout) r.findViewById(R.id.maps_fragment_layout);
        view2 = r.findViewById(R.id.direction_arrow);

        directions = (FloatingActionButton) r.findViewById(R.id.place_new_marker);

        addEvent = (FloatingActionButton) r.findViewById(R.id.create_new_message);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateEvent.class);
                startActivity(intent);
            }
        });

        topNavigationView = (BottomNavigationView) r.findViewById(R.id.top_navigation);

        try {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) topNavigationView.getChildAt(0);
            BottomNavigationItemView city = (BottomNavigationItemView) menuView.getChildAt(2);
            city.setShiftingMode(false);
            city.setChecked(city.getItemData().isChecked());
        } catch (Exception e) {
        }

        topNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem bottom_navigation) {
                        View menuItemView = r.findViewById(R.id.search);
                        View options = r.findViewById(R.id.refresh);
                        switch (bottom_navigation.getItemId()) {
                            case R.id.search:
                                filterMenu(menuItemView);
                                break;
                            case R.id.message:
                                getCurrentUserInfo();
                                break;
                            case R.id.current_city:
                                break;
                            case R.id.favorite: //TODO: Refresh or favorite?
                                Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
                                view2.setVisibility(View.GONE);
                                mMap.clear();
                                drawPointsWithinUserRadius();
                                onResume();
                                Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT).show();
                                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
                                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                LatLng latLng = null;
                                if (location != null) {
                                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                }
                                MarkerOptions option = new MarkerOptions()
                                        .position(latLng)
                                        .draggable(true)
                                        .title("My Location");
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.exc));
                                mMap.addMarker(option);
                                animateCircle(latLng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                break;
                            case R.id.refresh:
                                optionsMenu(options);
                                break;
                        }
                        return true;
                    }
                });

        drawPointsWithinUserRadius();

        return r;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing googleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        Marker = drawMarkerWithSize(100, 100);
        eventArrayList = new ArrayList<>();
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnPoiClickListener(this);

        changeSattelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                else if(mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID)
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                else
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        try{
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));}
        catch (Exception e){e.printStackTrace();}
        mMap.getUiSettings().setMapToolbarEnabled(true);

        curUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        mMap.clear();
                        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
                        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        LatLng latLng = null;
                        if (location != null) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                        MarkerPoints.add(latLng);
                        MarkerPoints.add(point);

                        MarkerOptions opt = new MarkerOptions();

                        // Setting the position of the marker
                        opt.position(point).title("Destination");
                        opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mMap.addMarker(opt);
//                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                            @Override
//                            public boolean onMarkerClick(Marker marker) {
//                                return false;
//                            }
//                        });

                        final LatLng origin = MarkerPoints.get(0);
                        final LatLng dest = MarkerPoints.get(1);

                        Legs legs = getDirectionAndDuration(origin,dest);
                        final Data data = new Data();
                        data.setLatitude(Double.toString(dest.latitude) + "");
                        data.setLongitude(Double.toString(dest.longitude) + "");
                        data.setModified("Destination");
                        data.setLegs(legs);


                        view2.setVisibility(View.VISIBLE);
                        view2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Dialog mBottomSheetDialog = new Dialog(getActivity(),R.style.DialogSlideAnim);//getActivity(), R.style.MaterialDialogSheet);
                                mBottomSheetDialog.setContentView(R.layout.directions); // your custom view.
                                //mBottomSheetDialog.setCancelable(true);
                                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                                mBottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                mBottomSheetDialog.show();

                                View v = getLayoutInflater().inflate(R.layout.directions, null);

                                setDurationAndDistance(mBottomSheetDialog, data, origin, dest);
                            }
                        });

                        MarkerPoints.clear();

                        MarkerOptions options = new MarkerOptions()
                                .position(latLng)
                                .draggable(true)
                                .title("My Location");
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.exc));
                        mMap.addMarker(options);
//
                        drawPointsWithinUserRadius();
                        animateCircle(latLng);

                    }
                });
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (eventMarkerKeys.containsKey(marker)) {
                    ViewEventDialogFragment.createInstance(eventMarkerKeys.get(marker)).show(getChildFragmentManager(), null);
                    drawRoute(marker);
                }  // if marker clicked is an event
                else if (postMarkerKeys.containsKey(marker)) {
                    ViewPostDialogFragment.createInstance(postMarkerKeys.get(marker)).show(getChildFragmentManager(), null);
                }  // if marker clicked is a post
                return false;
            }
        });  // add listeners for clicking markers

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragEnd(Marker marker) {
                ourLoc.remove();
                removeCircle.remove();
                temp.removeAllListeners();
                temp.end();
                temp.cancel();

                Location newPos = new Location(LocationManager.GPS_PROVIDER);
                newPos.setLatitude(marker.getPosition().latitude);
                newPos.setLongitude(marker.getPosition().longitude);
                handleNewLocation(newPos);
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //if user long clicks at even numbers (0, 2, 4, 6, 8 times) then inflate view
                if ((inflateButtons % 2) == 0) {
                    //Toast.makeText(getContext(), "Create Event or Post", Toast.LENGTH_SHORT).show();
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View view = inflater.inflate(R.layout.post_event_create_shim_layout, null);
                    layoutToAdd.addView(view);
                    giveButtonFunctionality();
                } else {
                    //Else remove the view if odd numbers
                    View namebar = getView().findViewById(R.id.post_event_create);
                    layoutToAdd.removeView(namebar);
                }
                inflateButtons++;
            }
        });




        /*
        //////Save an image of the city using Google's Metadata

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {
                for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                    Place place = placeLikelihood.getPlace();
                    Log.d("idddd", place.getId());
                }
                placeLikelihoods.release();
            }
        });



        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, "ChIJIQBpAG2ahYAR_6128GcTUEo").setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(final PlacePhotoMetadataResult placePhotoMetadataResult) {
                if (placePhotoMetadataResult.getStatus().isSuccess()) {
                    final PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();
                    final PlacePhotoMetadata placePhotoMetadata = photoMetadata.get(0);
                    final String photoDetail = placePhotoMetadata.toString();
                    placePhotoMetadata.getPhoto(mGoogleApiClient).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(PlacePhotoResult placePhotoResult) {
                            if (placePhotoResult.getStatus().isSuccess()) {
                                Log.d("Photo", "Photo "+photoDetail+" loaded");
                                try {
                                    //converts the bitmap to uri,
                                    filePath = getImageUri(getApplicationContext(), placePhotoResult.getBitmap());
                                    uploadToFirebase(photoDetail);
                                } catch(Exception e){}
                            } else {
                                Log.d("Photo", "Photo "+photoDetail+" failed to load");
                            }
                        }
                    });
                    photoMetadata.release();
                } else {
                    Log.e(TAG, "No photos returned");
                }
            }
        });
*/


    }

    public void setDurationAndDistance(Dialog view, Data data, final LatLng origin, final LatLng dest){
        driveDistance = (TextView) view.findViewById(R.id.drive_distance);
        driveDuration = (TextView) view.findViewById(R.id.drive_duration);
        walkDistance = (TextView) view.findViewById(R.id.walk_distance);
        walkDuration = (TextView) view.findViewById(R.id.walk_duration);
        bikeDistance = (TextView) view.findViewById(R.id.bike_distance);
        bikeDuration = (TextView) view.findViewById(R.id.bike__duration);
        //busDistance = (TextView) view.findViewById(R.id.bus_distance);
        //busDuration = (TextView) view.findViewById(R.id.bus_duration);
        driveRB = (RadioButton) view.findViewById(R.id.car_rb);
        walkRB = (RadioButton) view.findViewById(R.id.walk_rb);
        bikeRB = (RadioButton) view.findViewById(R.id.bike_rb);

        // Getting Reference to rg_modes
        rgModes = (RadioGroup) view.findViewById(R.id.rg_modes);

        rgModes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);
                    FetchUrl downloadTask = new FetchUrl();
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            });

        //Driving
        driveDistance.setText(data.getLegs().getDistance().getText());
        driveDuration.setText(data.getLegs().getDuration().getText());

        Legs legs = getDDwithType(origin, dest, "walking");
        data.setLegs(legs);
        walkDistance.setText(data.getLegs().getDistance().getText());
        walkDuration.setText(data.getLegs().getDuration().getText());

        legs = getDDwithType(origin, dest, "bicycling");
        data.setLegs(legs);
        bikeDistance.setText(data.getLegs().getDistance().getText());
        bikeDuration.setText(data.getLegs().getDuration().getText());



    }

    public void drawRoute(Marker marker) {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng latLng = null;
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        MarkerPoints.add(latLng);
        MarkerPoints.add(marker.getPosition());

        LatLng origin = MarkerPoints.get(0);
        LatLng dest = MarkerPoints.get(1);

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        MarkerPoints.clear();
        mMap.clear();
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("My Location");
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.exc));
        mMap.addMarker(options);

        drawPointsWithinUserRadius();
        animateCircle(latLng);
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=driving";
        try {
            if (driveRB.isChecked()) {
                mode = "mode=driving";
                mMode = 0;
            } else if (walkRB.isChecked()) {
                mode = "mode=walking";
                mMode = 1;
            } else if (bikeRB.isChecked()) {
                mode = "mode=bicycling";
                mMode = 2;
            }
        }catch (Exception e){e.printStackTrace();}

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        try {
            //if app has permission to use current location,
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //finds the current location
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location == null) {
                    //if it cannot, then it requests for the location from client
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                } else {

                    //set the map to the current location
                    handleNewLocation(location);
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        eventQuery.setCenter(new GeoLocation(curLatitude = location.getLatitude(), curLongitude = location.getLongitude()));
        postQuery.setCenter(new GeoLocation(curLatitude, curLongitude));

        LatLng latLng = new LatLng(curLatitude, curLongitude);
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            final List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0 & addresses != null) {

                MenuItem cityMenuItem = topNavigationView.getMenu().findItem(R.id.current_city);
                cityMenuItem.setTitle(addresses.get(0).getLocality());
                final DatabaseReference cityRef = FirebaseDatabase.getInstance().getReference().child("City").child(addresses.get(0).getLocality());
                cityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) { //if city is already in the database
                            //TODO: If you can think of a better way to do city pictures, then implement it
                            if ((dataSnapshot.child("CityName").getValue()).equals("Davis"))
                                cityRef.child("Picture").setValue("https://firebasestorage.googleapis.com/v0/b/righthererightnow-72e20.appspot.com/o/davis.jpg?alt=media&token=9a201385-b9e7-400c-9e63-dee572aebce3");
                            else if ((dataSnapshot.child("CityName").getValue()).equals("Sacramento"))
                                cityRef.child("Picture").setValue("https://firebasestorage.googleapis.com/v0/b/righthererightnow-72e20.appspot.com/o/sacramento.jpg?alt=media&token=1beabb71-309a-4661-8456-73403c27c933");
                            else if ((dataSnapshot.child("CityName").getValue()).equals("Galt"))
                                cityRef.child("Picture").setValue("https://firebasestorage.googleapis.com/v0/b/righthererightnow-72e20.appspot.com/o/galt.jpg?alt=media&token=967f71cf-8a6c-4025-b9bd-62ac03f798ec");
                            else if ((dataSnapshot.child("CityName").getValue()).equals("Dixon"))
                                cityRef.child("Picture").setValue("https://firebasestorage.googleapis.com/v0/b/righthererightnow-72e20.appspot.com/o/dixon.jpg?alt=media&token=b4d67a69-4016-405c-9a91-1c8d94195440");
                            else if ((dataSnapshot.child("CityName").getValue()).equals("Vacaville"))
                                cityRef.child("Picture").setValue("https://firebasestorage.googleapis.com/v0/b/righthererightnow-72e20.appspot.com/o/vacaville.jpg?alt=media&token=89ec6f85-edb5-4428-8384-ceb554e14113");
                            else if ((dataSnapshot.child("CityName").getValue()).equals("Winters"))
                                cityRef.child("Picture").setValue("https://firebasestorage.googleapis.com/v0/b/righthererightnow-72e20.appspot.com/o/winters.png?alt=media&token=7c1b633b-c1e3-4df3-bd36-9a9d082c1841");
                            else if ((dataSnapshot.child("CityName").getValue()).equals("San Francisco"))
                                cityRef.child("Picture").setValue("https://firebasestorage.googleapis.com/v0/b/righthererightnow-72e20.appspot.com/o/sanfrancisco.jpg?alt=media&token=6cdb2008-b37a-40ec-b8c2-92780aa08ebb");


                        } else {
                            //city does not exist, so create new
                            try { //Sometimes, the city doesnt exist on google maps, so try.
                                City city = new City(addresses.get(0).getLocality(),
                                        addresses.get(0).getAdminArea(),
                                        addresses.get(0).getCountryName(),
                                        "https://firebasestorage.googleapis.com/v0/b/mapconnect-cf482.appspot.com/o/prettycity.jpg?alt=media&token=29644a0b-daef-429b-9a03-4bae113a1af9",
                                        "0");
                                cityRef.setValue(city);
                            } catch (Exception e) {
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("My Location");
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.exc));
        ourLoc = mMap.addMarker(options);;
        if (first) {
            first = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

        animateCircle(latLng);

    }

    public void animateCircle(LatLng latLng){
        final Circle circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .strokeColor(Color.CYAN)
                .radius(radius));
        removeCircle = circle;

        ValueAnimator vAnimator = new ValueAnimator();
        temp = vAnimator;
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        //TODO: Implement radius change where user wants to change radius.
        //radius can be changed.
        vAnimator.setIntValues(0, radius);
        //This sets how long you want the duration of animation to be.
        vAnimator.setDuration(4000);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                // Log.e("", "" + animatedFraction);
                circle.setRadius(animatedFraction * radius);
            }
        });

        vAnimator.start();
    }

    public static void deleteExpiredEvents() {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Event");
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //loop through events and check if current time exceeds end date and time
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Event e = dataSnapshot1.getValue(Event.class);
                    String eventID = e.eventID;
                    String currentDateandTime = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
                    Log.d("CURRENTTIME", currentDateandTime);

                    String time = e.endDate +" " + e.endTime;
                    DateFormat inDateFormat = new SimpleDateFormat("M/dd/yyyy HH:mmaa");
                    DateFormat outDateFormat = new SimpleDateFormat("MMddyyyyHHmm");
                    try {
                        Date outDate = inDateFormat.parse(time);
                        Date newDate = new Date(outDate.getTime() + (7L * 24L * 60L * 60L * 1000L) ); // get the date 7 days later (expired dates after 7 days get deleted from database)
                        time = outDateFormat.format(newDate);
                    } catch (ParseException e1) {e1.printStackTrace();}
                    Log.d("CURRENTTIMEEE", time);

                    if(Long.parseLong(currentDateandTime) > Long.parseLong(time)){
                        //TODO: Delete events after they expire
                        //Toast.makeText(getApplicationContext(), "Deleting Event...", Toast.LENGTH_SHORT).show();
                        Log.d("Delete", eventID);
//                        FirebaseDatabase.getInstance().getReference().child("Event").child(eventID).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("EventLocations").child(eventID).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("OtherEventLocations").child(eventID).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("PartyEventLocations").child(eventID).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("SportEventLocations").child(eventID).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("EducationEventLocations").child(eventID).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("ClubEventEventLocations").child(eventID).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("Likes").child(eventID).removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void drawPointsWithinUserRadius() {
        eventsOnMap = FirebaseDatabase.getInstance().getReference("EventLocations");
        GeoFire eventFire = new GeoFire(eventsOnMap);

        postsOnMap = FirebaseDatabase.getInstance().getReference("PostLocations");
        GeoFire postFire = new GeoFire(postsOnMap);

        eventQuery = eventFire.queryAtLocation(new GeoLocation(curLatitude, curLongitude), radius / 1000); // 12800.0);
        //(radius * 0.001) * kmToMiles * 70);
        postQuery = postFire.queryAtLocation(eventQuery.getCenter(), radius / 1000);//12800.0);
        // (radius * 0.001) * kmToMiles * 70);

        // might be something to do with initialization

        Log.d("CENTER", Double.toString(eventQuery.getCenter().latitude));

        eventQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation l) {

                LatLng location = new LatLng(l.latitude, l.longitude);

                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(location).draggable(false)
                        //TODO: MM: Change marker size with our algorithm -> query likes and multiply
                        //.icon(BitmapDescriptorFactory.fromBitmap(Marker)));
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_point)));
                eventMarkerKeys.put(m, s);
                eventKeyMarkers.put(s, m);

                //if(listview == 1)
                //  storeEventToList(s);
            }  // have discovered an event, so put it in hashmap and put a marker for it

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                Marker toRemove = eventKeyMarkers.get(s);
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));
                toRemove.remove();

                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));

                eventMarkerKeys.put(m, s);
                eventKeyMarkers.put(s, m);
            }  // event has been moved, so move marker

            @Override
            public void onKeyExited(String s) {
                Marker m = eventKeyMarkers.get(s);
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));
                m.remove();
            }  // event no longer in range, so remove it from hashmaps and map


            @Override
            public void onGeoQueryError(DatabaseError e) {
                Log.d("ERROR", "GEOQUERY EVENT ERROR");
            }

            @Override
            public void onGeoQueryReady() {

            }
        });

        postQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation l) {
                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_blue)));
                postMarkerKeys.put(m, s);
                postKeyMarkers.put(s, m);
            }

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                Marker toRemove = postKeyMarkers.get(s);
                postMarkerKeys.remove(postKeyMarkers.remove(s));
                toRemove.remove();

                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));

                postMarkerKeys.put(m, s);
                postKeyMarkers.put(s, m);
            }

            @Override
            public void onKeyExited(String s) {
                Marker m = postKeyMarkers.get(s);
                postMarkerKeys.remove(postKeyMarkers.remove(s));
                m.remove();
            }

            @Override
            public void onGeoQueryError(DatabaseError e) {
                Log.d("ERROR", "GEOQUERY POST ERROR");
            }

            @Override
            public void onGeoQueryReady() {

            }
        });
    }


//    public void storeEventToList(String eventKey)
//    {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Event");
//        ref.orderByChild("eventID").equalTo(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.exists()) return;
//                else{
//                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                    eventArrayList.add(dataSnapshot1.getValue(Event.class));
//                    Log.d("EVENTTT", eventArrayList.get(0).eventID);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


    public void getCurrentUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = user.getUid();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("User");
        rootRef.child(userKey).child("UsersMessaged").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> keys = new ArrayList<String>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String other = userSnapshot.getKey();
                    keys.add(other);
                }

                Bundle extra = new Bundle();
                extra.putSerializable("objects", keys);

                Intent intent = new Intent(getApplicationContext(), MessageListActivity.class);
                intent.putExtra("extra", extra);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public Bitmap drawMarkerWithSize(int width, int height) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources()
                .getDrawable(R.drawable.exclamation_point, null);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b, width, height, false);
        return marker;
    }

    public void promptFavorite() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
        dlgAlert.setTitle("Would you like to save this location?");
        dlgAlert.setMessage("You can come back to this location later.");

        dlgAlert.setNegativeButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Opens the gallery of the phone if user clicked "Upload"
                Toast.makeText(getApplicationContext(), "Location Saved!", Toast.LENGTH_LONG).show();
            }
        });

        //if user cancels
        dlgAlert.setPositiveButton("No", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dlgAlert.setCancelable(true);
        dlgAlert.create();
        dlgAlert.show();
    }

    public void optionsMenu(final View r) {
        final PopupMenu popup = new PopupMenu(getActivity(), r);
        popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                /*if (i == R.id.action1) {
                    Toast.makeText(getApplicationContext(), "Local Post and Events in a List.", Toast.LENGTH_LONG).show();
                    //listview=1;
                    //drawPointsWithinUserRadius();
                    listView();
                    return true;
                } else */
                if (i == R.id.logout) {
                    logout = 1;
                    // TODO delete token
                    RHRNNotifications.unsubscribeFromMessages();
                    RHRNNotifications.unsubscribeFromFollows();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear all activities above it
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                } else {
                    return onMenuItemClick(item);
                }
            }
        });
        popup.show();
    }

    public void drawWithFilters(final Map<String, Integer> aMap) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Event");
        //iterate through all the keys/flags that are on filtering
        for (final String key : aMap.keySet()) {
            int val = aMap.get(key);
            if (val == 1) {
                Log.d("KEY", key);
                eventRef.orderByChild(key).equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Log.d("keyevent", childSnapshot.getKey());
                            mMap.clear();
                            @SuppressLint("MissingPermission") Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            handleNewLocation(loc);
                            queryWithFilter(key, childSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    public void queryWithFilter(String filter, String filterKey) {
        DatabaseReference filterEvent;
        GeoFire eventFire;
        //Filter by event
        if (filter.equals("isSports"))
            filterEvent = FirebaseDatabase.getInstance().getReference("SportEventLocations");
        else if (filter.equals("isEducation"))
            filterEvent = FirebaseDatabase.getInstance().getReference("EducationEventLocations");
        else if (filter.equals("isClubEvent"))
            filterEvent = FirebaseDatabase.getInstance().getReference("ClubEventLocations");
        else if (filter.equals("isOther"))
            filterEvent = FirebaseDatabase.getInstance().getReference("OtherEventLocations");
        else if (filter.equals("isParty"))
            filterEvent = FirebaseDatabase.getInstance().getReference("PartyEventLocations");
        else
            filterEvent = FirebaseDatabase.getInstance().getReference("EventLocations");
        eventFire = new GeoFire(filterEvent);

        DatabaseReference filterPost = FirebaseDatabase.getInstance().getReference("SportPostLocations");
        GeoFire postFire = new GeoFire(filterPost);

        GeoQuery filterEventQuery = eventFire.queryAtLocation(new GeoLocation(curLatitude, curLongitude), radius / 1000); // 12800.0);
        //(radius * 0.001) * kmToMiles * 70);
        GeoQuery filterPostQuery = postFire.queryAtLocation(filterEventQuery.getCenter(), radius / 1000);//12800.0);
        // (radius * 0.001) * kmToMiles * 70);

        Log.d("CENTER", Double.toString(filterEventQuery.getCenter().latitude));

        filterEventQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation l) {
                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(location).draggable(false)
                        //.icon(BitmapDescriptorFactory.fromBitmap(Marker)));
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_point)));
                eventMarkerKeys.put(m, s);
                eventKeyMarkers.put(s, m);
            }  // have discovered an event, so put it in hashmap and put a marker for it

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                Marker toRemove = eventKeyMarkers.get(s);
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));
                toRemove.remove();

                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));

                eventMarkerKeys.put(m, s);
                eventKeyMarkers.put(s, m);
            }  // event has been moved, so move marker

            @Override
            public void onKeyExited(String s) {
                Marker m = eventKeyMarkers.get(s);
                eventMarkerKeys.remove(eventKeyMarkers.remove(s));
                m.remove();
            }  // event no longer in range, so remove it from hashmaps and map

            @Override
            public void onGeoQueryError(DatabaseError e) {
                Log.d("ERROR", "GEOQUERY EVENT ERROR");
            }

            @Override
            public void onGeoQueryReady() {

            }
        });

        filterPostQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation l) {
                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.exclamation_blue)));
                postMarkerKeys.put(m, s);
                postKeyMarkers.put(s, m);
            }

            @Override
            public void onKeyMoved(String s, GeoLocation l) {
                Marker toRemove = postKeyMarkers.get(s);
                postMarkerKeys.remove(postKeyMarkers.remove(s));
                toRemove.remove();

                LatLng location = new LatLng(l.latitude, l.longitude);
                Marker m = mMap.addMarker(new MarkerOptions().position(location).draggable(false));

                postMarkerKeys.put(m, s);
                postKeyMarkers.put(s, m);
            }

            @Override
            public void onKeyExited(String s) {
                Marker m = postKeyMarkers.get(s);
                postMarkerKeys.remove(postKeyMarkers.remove(s));
                m.remove();
            }

            @Override
            public void onGeoQueryError(DatabaseError e) {
                Log.d("ERROR", "GEOQUERY POST ERROR");
            }

            @Override
            public void onGeoQueryReady() {

            }
        });
    }


    public void filterMenu(View r) {
        PopupMenu popup = new PopupMenu(getActivity(), r);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.filter1) {
                    checkboxFilter(item);
                    isEducation = 1;
                    return false;
                } else if (i == R.id.filter2) {
                    checkboxFilter(item);
                    isSports = 1;
                    return false;
                } else if (i == R.id.filter3) {
                    checkboxFilter(item);
                    isParty = 1;
                    return false;
                } else if (i == R.id.filter4) {
                    checkboxFilter(item);
                    isClubEvent = 1;
                    return false;
                } else if (i == R.id.filter5) {
                    checkboxFilter(item);
                    isOther = 1;
                    return false;
                } else if (i == R.id.done_filter) {
                    map = new HashMap<String, Integer>();
                    map.put("isEducation", isEducation);
                    map.put("isSports", isSports);
                    map.put("isParty", isParty);
                    map.put("isClubEvent", isClubEvent);
                    map.put("isOther", isOther);
                    clearFilterFlags();
                    drawWithFilters(map);

                    return true;
                } else {
                    return onMenuItemClick(item);
                }
            }
        });
        popup.show();
    }

    public void clearFilterFlags() {
        isEducation = isClubEvent = isOther = isParty = isSports = 0;
    }

    public void checkboxFilter(MenuItem item) {
        item.setChecked(!item.isChecked());
        SharedPreferences settings = getActivity().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checkbox", item.isChecked());
        editor.commit();
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setActionView(new View(getApplicationContext()));
    }

    //stackoverflow function
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public void giveButtonFunctionality() {
        Button createPost, createEvent;
        createPost = (Button) getView().findViewById(R.id.create_post_button);
        createEvent = (Button) getView().findViewById(R.id.create_event_button);

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction()
                            .add(R.id.post_event_create_shim_fragment_container, new CreatePostFragment())
                            .addToBackStack(null).commit();


                ImageButton back = new ImageButton(getContext());
                back.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
                back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                back.setBackgroundColor(Color.TRANSPARENT);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if back button clicked, pop the back fragment
                        manager.popBackStack();
                        //delete the created view on the bottom
                        View namebar = getView().findViewById(R.id.post_event_create);
                        layoutToAdd.removeView(namebar);
                        inflateButtons++;
                    }
                });
                //add the back button to the layout
                FrameLayout frameLayout = (FrameLayout) getView().findViewById(R.id.post_event_create_shim_fragment_container);
                frameLayout.addView(back);

            }
        });
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction()
                            .add(R.id.post_event_create_shim_fragment_container, new CreateEventFragment())
                            .addToBackStack(null)
                            .commit();

                ImageButton backButton = new ImageButton(getActivity());
                backButton.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
                backButton.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                backButton.setBackgroundColor(Color.TRANSPARENT);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if back button clicked, pop the back fragment
                        manager.popBackStack();
                        //delete the created view on the bottom
                        View namebar = getView().findViewById(R.id.post_event_create);
                        layoutToAdd.removeView(namebar);
                        inflateButtons++;
                    }
                });
                //add the back button to the layout
                FrameLayout frameLayout = (FrameLayout) getView().findViewById(R.id.post_event_create_shim_fragment_container);
                frameLayout.addView(backButton);
            }
        });
    }

    public void listView() {
        final FragmentManager manager = getActivity().getSupportFragmentManager();
        if (manager.findFragmentById(R.id.map_as_list) != null)
            manager.beginTransaction()
                    .replace(R.id.map_as_list, new MapListFragment())
                    .addToBackStack(null)
                    .commit();
        else
            manager.beginTransaction()
                    .add(R.id.map_as_list, new MapListFragment())
                    .addToBackStack(null)
                    .commit();
    }


    public void uploadToFirebase(String photoDetail) {
        //create the profile picture name using their uid + .jpg
        String childFile = photoDetail + ".jpg";

        //If the file was chosen from gallery then != null
        if (filePath != null) {
            //Create child using the above string
            StorageReference fileRef = storageRef.child(childFile);
            //Create the upload using built-in UploadTask
            UploadTask uploadTask = fileRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(getApplicationContext(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    //Set the download URL
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Store URL under the current user
                    FirebaseDatabase.getInstance().getReference().child("City")
                            .child("Davis").child("Picture").setValue(downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //failed to upload

                }
            });
        } else {
            //no image to upload
        }
    }


    // Fetches data from url passed
    public class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                if(mMode==0) //Drive
                    lineOptions.color(Color.RED);
                else if(mMode==1) // Walk
                    lineOptions.color(Color.BLUE);
                else if(mMode==2) // Bike
                    lineOptions.color(Color.GREEN);

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    private Legs getDirectionAndDuration(final LatLng origin, final LatLng destination){
        OkHttpClient clientGoogleApi = new OkHttpClient();
        //clientGoogleApi.interceptors().add(new LoggingInterceptorGoogle());
        Retrofit retrofitGoogleApi = new Retrofit.Builder()
                .baseUrl(Constant.GOOGLE_END_POINT)
                .client(clientGoogleApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceGoogleDirection = retrofitGoogleApi.create(ApiService.class);

        final Legs[] distanceDurationModel = new Legs[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Call<DirectionResultsModel> directionResultsCall;
                directionResultsCall = serviceGoogleDirection.getDistanceAndDuration(origin.latitude + "," + origin.longitude ,
                        destination.latitude + "," + destination.longitude, "false","driving","true");
                try {
                    DirectionResultsModel results = directionResultsCall.execute().body();
                    distanceDurationModel[0] = results.getRoutes().get(0).getLegs().get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();

            }
        });

        thread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  distanceDurationModel[0];
    }


    private Legs getDDwithType(final LatLng origin, final LatLng destination, final String type){
        OkHttpClient clientGoogleApi = new OkHttpClient();
        //clientGoogleApi.interceptors().add(new LoggingInterceptorGoogle());
        Retrofit retrofitGoogleApi = new Retrofit.Builder()
                .baseUrl(Constant.GOOGLE_END_POINT)
                .client(clientGoogleApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceGoogleDirection = retrofitGoogleApi.create(ApiService.class);

        final Legs[] distanceDurationModel = new Legs[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Call<DirectionResultsModel> directionResultsCall;
                directionResultsCall = serviceGoogleDirection.getDistanceAndDuration(origin.latitude + "," + origin.longitude ,
                        destination.latitude + "," + destination.longitude, "false",type,"true");
                try {
                    DirectionResultsModel results = directionResultsCall.execute().body();
                    distanceDurationModel[0] = results.getRoutes().get(0).getLegs().get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                latch.countDown();

            }
        });

        thread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  distanceDurationModel[0];
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        LatLng point = new LatLng(poi.latLng.latitude,poi.latLng.longitude);
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(poi.name);
        mMap.addMarker(option);
    }


    // Request photos and metadata for the specified place.
    private void getPhotos(final String placeId) {
//        placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                    }
                });
            }
        });
    }


}
