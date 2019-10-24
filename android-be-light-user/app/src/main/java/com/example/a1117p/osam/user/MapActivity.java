package com.example.a1117p.osam.user;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    MapInfoWindowFragment mMapFragment;
    GoogleMap mGoogleMap;
    Boolean issearch = false;
    BitmapDescriptor bitmapDescriptor;
    long backKeyClickTime = 0;
    HashMap<Marker, InfoWindow> hashmap;
    InfoWindowManager manager;
    int checkInCount = 1;
    ImageView profile;

    void OvalProfile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profile.setBackground(new ShapeDrawable(new OvalShape()));
            profile.setClipToOutline(true);
        }
    }

    void downReview() {
        findViewById(R.id.review).setClickable(false);
        //findViewById(R.id.down).setClickable(false);
        // findViewById(R.id.bottom).startAnimation(AnimationUtils.loadAnimation(this, R.anim.down_anim));
    }

    void downReview_fast() {
//        findViewById(R.id.review).setClickable(false);
        // findViewById(R.id.down).setClickable(false);
        //findViewById(R.id.bottom).startAnimation(AnimationUtils.loadAnimation(this, R.anim.down_anim_fast));
    }

    void setProfileImg() {
        if (MySharedPreferences.getProfileImgPath() != null) {
            File imgFile = new File(MySharedPreferences.getProfileImgPath());

            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profile.setImageBitmap(myBitmap);
            }
        }
        OvalProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setProfileImg();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.makerpin02);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 120, false);
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);


        mMapFragment = (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        //downReview_fast();
        manager = mMapFragment.infoWindowManager();


        profile = findViewById(R.id.profile_img);
        ((TextView) findViewById(R.id.name)).setText(RequestHttpURLConnection.name);
        ((TextView) findViewById(R.id.email)).setText(RequestHttpURLConnection.email);

        final EditText editText = findViewById(R.id.search_edit);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 검색 동작
                    findViewById(R.id.search_btn).performClick();
                } else {// 기본 엔터키 동작
                    return false;
                }
                return true;
            }
        });


        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issearch = true;
                final ProgressDialog dialog = new ProgressDialog(MapActivity.this);
                dialog.setMessage("검색중입니다.");

                dialog.show();
                mGoogleMap.clear();
                mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });

                InputMethodManager imm;
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                String search = editText.getText().toString();
                Geocoder geocoder = new Geocoder(MapActivity.this);
                try {
                    List<Address> addressList = geocoder.getFromLocationName(search, 1);

                    dialog.dismiss();
                    if (addressList.size() == 0) {
                        Toast.makeText(MapActivity.this, "검색결과없음", Toast.LENGTH_LONG).show();
                        issearch = false;
                        onMapReady(mGoogleMap);
                        return;
                    }
                    final Address address = addressList.get(0);
                    issearch = false;
                    final ProgressDialog Pdialog = new ProgressDialog(MapActivity.this);
                    Pdialog.setMessage("검색중입니다.");

                    Pdialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String html = RequestHttpURLConnection.request("https://be-light.store/api/map/hosts?latitude=" + address.getLatitude() + "&longitude=" + address.getLongitude(), null, "GET");
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Pdialog.dismiss();
                                    mGoogleMap.clear();
                                    mGoogleMap.setOnMarkerClickListener(null);
                                    //Toast.makeText(MapActivity.this, html, Toast.LENGTH_LONG).show();

                                    try {
                                        JSONParser jsonParser = new JSONParser();
                                        JSONArray jsonArr = (JSONArray) jsonParser.parse(html);
                                        if (0 == jsonArr.size()) {
                                            Toast.makeText(MapActivity.this, "검색결과없음", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        hashmap = new HashMap<>();
                                        for (Object o : jsonArr) {
                                            JSONObject object = (JSONObject) o;
                                            LatLng latLng = new LatLng(Double.parseDouble((String) object.get("hostLatitude")), Double.parseDouble((String) object.get("hostLongitude")));
                                            InfoWindowData data = new InfoWindowData(object);
                                            final Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .icon(bitmapDescriptor)
                                                    .title((String) object.get("hostName")));
                                            InfoWindow.MarkerSpecification markerSpec = new InfoWindow.MarkerSpecification(0, 120);
                                            final InfoWindow infoWindow = new InfoWindow(marker, markerSpec, new CustomInfoWindowFragment(data, MapActivity.this));
                                            // Shows the InfoWindow or hides it if it is already opened.
                                            //  manager.toggle(infoWindow, true);
                                            hashmap.put(marker, infoWindow);


                                        }
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                                                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                            @Override
                                                            public boolean onMarkerClick(Marker marker) {
                                                                InfoWindow window = hashmap.get(marker);
                                                                if (window != null) {
                                                                    manager.toggle(window, true);
                                                                }
                                                                return true;
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }).start();
                                    } catch (final Exception e) {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                Toast.makeText(MapActivity.this, "검색결과가 없습니다.", Toast.LENGTH_LONG).show();

                                            }

                                        });
                                    }

                                }

                            });

                        }
                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.order_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapActivity.this, ReciptMgtActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.review_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapActivity.this, ReviewListActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestHttpURLConnection.cookie = "";
                Toast.makeText(MapActivity.this, "로그아웃되었습니다.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(MapActivity.this, SplashActivity.class);
                i.putExtra("needLoading", false);
                MySharedPreferences.removeIdPw();
                startActivity(i);
                finish();
            }
        });
        findViewById(R.id.faq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, FAQActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInCount <= 2)
                    checkInCount = 2;
                ((TextView) findViewById(R.id.count)).setText(--checkInCount + "");
            }
        });
        findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.count)).setText(++checkInCount + "");
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                findViewById(R.id.bottom).startAnimation(AnimationUtils.loadAnimation(MapActivity.this, R.anim.down_anim));

            }
        });
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, false);

        // Getting Current Location
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location

            final LatLng latLng = new LatLng(latitude, longitude);

            VectorDrawableCompat vectorDrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_makerpin, null);

            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(bitmapDescriptor);
            markerOptions.title("내위치");

            googleMap.addMarker(markerOptions);

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyClickTime + 1000) {
            backKeyClickTime = System.currentTimeMillis();
            Toast.makeText(this, "앱을 종료하시려면 뒤로가기 버튼을 다시 눌러주세요", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }


}
