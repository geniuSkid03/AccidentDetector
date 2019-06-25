package com.geniuskid.accidentdetector.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.Utils.AcceleraMeterListener;
import com.geniuskid.accidentdetector.Utils.AccelerometerManager;
import com.geniuskid.accidentdetector.Utils.AppHelper;
import com.geniuskid.accidentdetector.Utils.Keys;
import com.geniuskid.accidentdetector.Utils.PermissionsHandler;
import com.geniuskid.accidentdetector.adapters.ContactListModel;
import com.geniuskid.accidentdetector.fragments.SensorFragment;
import com.geniuskid.accidentdetector.services.AccelaroMeterService;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.app.Notification.CATEGORY_PROMO;
import static android.app.Notification.VISIBILITY_PUBLIC;
import static com.geniuskid.accidentdetector.Utils.AppHelper.showToast;

public class MainActivity extends SuperCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AcceleraMeterListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;

    private NavigationView navigationView;

    private long backPressed = 0;

    private TextView tipTv;

    private ArrayList<String> tipsList = new ArrayList<>();

    private TextInputEditText xAxis, yAxis, zAxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolBar();

        initViews();

        initClicks();

        setNavToggle();

        loadProfile();

        tipsList = new ArrayList<>();
        tipsList.add("Update your custom threshold for altering in sensors section");
        tipsList.add("Add contacts that you want in trusted contacts section");
        tipsList.add("Better the signal, better the location accuracy");
        tipsList.add("Your friends will be notified if the threshold values of sensor was reached");

        showTips();

        Intent intent = new Intent(this, AccelaroMeterService.class);
        startService(intent);
    }

    private void setToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
    }

    private void initViews() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tipTv = findViewById(R.id.tip_tv);
        xAxis = findViewById(R.id.x_axis);
        yAxis = findViewById(R.id.y_axis);
        zAxis = findViewById(R.id.z_axis);
    }

    private void initClicks() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setNavToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadProfile() {
        if (navigationView != null) {
            View header = navigationView.getHeaderView(0);
            TextView nameTv = header.findViewById(R.id.nav_name);
            TextView mobileTv = header.findViewById(R.id.nav_mobile);

            nameTv.setText(dataStorage.getString(Keys.NAME));
            mobileTv.setText(dataStorage.getString(Keys.MOBILE));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backPressed == 0) {
                backPressed = System.currentTimeMillis();
                showToast(this, getString(R.string.info_exit));
            } else if (backPressed <= 3000) {
                backPressed = 0;
                super.onBackPressed();
            } else {
                onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:

                break;
            case R.id.nav_accelerometer:
                goTo(this, SensorFragment.class, false);
                break;
            case R.id.nav_trusted_contacts:
                goTo(this, ContactsActvivity.class, false);
                break;
            case R.id.nav_profile:
                goTo(this, ProfileActivity.class, false);
                break;
//            case R.id.nav_settings:
//                goTo(this, SettingsActivity.class, false);
//                break;
            case R.id.nav_logout:
                askForLogout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void askForLogout() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Log Out");
        alertDialog.setMessage("Are you sure, you want to logout?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logOut();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setCancelable(true);
        if (!isFinishing() && !alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    private void logOut() {
        dataStorage.removeAllData();
        goTo(this, LoginActivity.class, true);
    }

    Handler handler = new Handler();
    Runnable runnable;

    private void showTips() {

        runnable = new Runnable() {
            @Override
            public void run() {
                tipTv.setText(String.format("TIPS : %s", getTips()));
                handler.postDelayed(runnable, 4000);
            }
        };
        handler.post(runnable);


    }

    @Override
    protected void onPause() {
        super.onPause();

        if (handler != null) {
            handler.removeCallbacks(runnable);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(alertReceiver);
    }

    private String getTips() {
        if (tipsList != null) {
            return tipsList.get(new Random().nextInt(tipsList.size()));
        } else {
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAndGetPermission();

        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        } else {
            Toast.makeText(MainActivity.this, "Accelerometer was not supported on this device, and this app" +
                    "may not work properly!", Toast.LENGTH_SHORT).show();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(alertReceiver, new IntentFilter("alert"));
    }

    private BroadcastReceiver alertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String alertString = intent.getStringExtra("do_alert");

            if (alertString.equals("1")) {
                AppHelper.printLog("Intent received from service in activity");
                getLocationData();

                showNotification();
            }
        }
    };

    @SuppressLint("InvalidWakeLockTag")
    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        String channedId = "ojo_driver_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            boolean isChannelFound = false;
            List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

            noty:
            for (int n = 0; n < channelList.size(); n++) {
                if (!isChannelFound) {
                    isChannelFound = channelList.get(n).getId().equals(channedId);
                    break noty;
                }
            }

            if (!isChannelFound) {
                String channedName = "OJO_Driver";
                @SuppressLint("WrongConstant")
                NotificationChannel notificationChannel = new NotificationChannel(channedId, channedName, NotificationManager.IMPORTANCE_MAX);
                //notificationChannel.setSound(Uri.parse("android:resource://"+getBaseContext().getPackageName()+"/"+R.raw.notification), null);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);


        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = new Notification.Builder(this)
                    .setCategory(CATEGORY_PROMO)
                    .setSmallIcon(R.drawable.location_tracking)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_tracking))
                    .setContentTitle("Alert")
                    .setContentText("It has been recorded that you have been struggled with an accident")
                    .setContentIntent(contentIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(channedId);
        }

        //startAlertSound();
        if (notificationBuilder != null) {
            notificationManager.notify(1, notificationBuilder.build());
        }

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wl.acquire(15000);
        }

        AppHelper.printLog("Showing notification");
    }

    private void getLocationData() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                AppHelper.printLog("Location received: " + lat + " " + lng);

                askAndSendSms(location);
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
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Turn On GPS", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            Toast.makeText(this, "Location permission unavailable!", Toast.LENGTH_SHORT).show();
        }
    }

    private void askAndSendSms(final Location location) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("It seems like you have met an accident, do you want to alert your contacts?");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendLocationSMS(location);
            }
        });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setCancelable(false);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            AppHelper.printLog("");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O                                                              ) {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }

        if (!alertDialog.isShowing()) {
            AppHelper.printLog("Showing alert dialog");
            alertDialog.show();
        }
    }

    private void sendLocationSMS(Location location) {
        String data = dataStorage.getString(Keys.TRUSTED_CONTACTS);
        if (data != null) {
            ArrayList<ContactListModel> contactListModels;
            Type type = new TypeToken<ArrayList<ContactListModel>>() {
            }.getType();

            contactListModels = gson.fromJson(data, type);

            if (contactListModels != null) {
                if (contactListModels.size() > 0) {
                    for (int i = 0; i < contactListModels.size(); i++) {
                        String mobileNumber = contactListModels.get(i).getNumber();

                        sendSMS(mobileNumber, location);
                    }
                } else {
                    Toast.makeText(this, "contacts model size 0", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "contacts model null", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Trusted contacts not added yet!", Toast.LENGTH_SHORT).show();
        }
    }


    public void sendSMS(String phoneNumber, Location location) {
        AppHelper.printLog("sending sms to: " + phoneNumber);

        SmsManager smsManager = SmsManager.getDefault();
        StringBuilder smsBody = new StringBuilder();
        smsBody.append("Your friend")
                .append("(")
                .append(dataStorage.getString(Keys.MOBILE))
                .append(")")
                .append("was met with accident help asap(it's emergency) \n\n https://www.google.com/maps/search/?api=1&query=")
                .append(location.getLatitude())
                .append(",")
                .append(location.getLongitude());
        smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null);
    }


    private void checkAndGetPermission() {
        permissionsHandler.initAllPermissions();

        if (!permissionsHandler.isAllPermissionAvailable()) {
            permissionsHandler.askAllPermissions(PermissionsHandler.REQ_LOC_PERMISSION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                // Check if Android M or higher
                // Show alert dialog to the user saying a separate permission is needed
                // Launch the settings activity if the user prefers
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            } else {
                // Do as per your logic
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!permissionsHandler.isAllPermissionGranted(grantResults)) {
            permissionsHandler.requestPermissionsIfDenied(PermissionsHandler.REQ_LOC_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (AccelerometerManager.isSupported(this)) {
            if (AccelerometerManager.isListening()) {
                AccelerometerManager.stopListening();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (AccelerometerManager.isSupported(this)) {
            if (AccelerometerManager.isListening()) {
                AccelerometerManager.stopListening();
            }
        }
    }

//    protected void sendSMSMessage(String mobileno) {
//        String phoneNo = "";
//        String message = "Your Friend met An Accident ";
//
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNo, null, message, null, null);
//            Toast.makeText(getApplicationContext(), "SMS sent.",
//                    Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(),
//                    "SMS faild, please try again.", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
//    }
//
//    public void showNotification() {
//
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Notification mNotification = new Notification.Builder(this)
//
//                .setContentTitle("Accident Detected")
//                .setContentText("You Are Strugled In Accident Emergency Alert Send With In 10 Secs")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setSound(soundUri)
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, mNotification);
//    }
//
//    public void cancelNotification(int notificationId) {
//        if (Context.NOTIFICATION_SERVICE != null) {
//            String ns = Context.NOTIFICATION_SERVICE;
//            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
//            nMgr.cancel(notificationId);
//        }
//    }


//    public void accelerometersetup(View view) {
//        Intent myint = new Intent(MainActivity.this, AcceleraMeterSetting.class);
//        startActivity(myint);
//    }

    @Override
    public void onAccelerationChanged(final float x, final float y, final float z) {
        xAxis.setText(String.valueOf(x));
        yAxis.setText(String.valueOf(y));
        zAxis.setText(String.valueOf(z));
    }

    @Override
    public void onShake(float force) {

    }
}
