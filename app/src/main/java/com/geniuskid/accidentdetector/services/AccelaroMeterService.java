package com.geniuskid.accidentdetector.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.Utils.AppHelper;
import com.geniuskid.accidentdetector.Utils.DataStorage;
import com.geniuskid.accidentdetector.Utils.DbAdapter;
import com.geniuskid.accidentdetector.Utils.Keys;
import com.geniuskid.accidentdetector.activity.MainActivity;
import com.geniuskid.accidentdetector.adapters.ContactListModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Notification.CATEGORY_PROMO;
import static android.app.Notification.VISIBILITY_PUBLIC;

public class AccelaroMeterService extends Service implements
        SensorEventListener {

    private GpsTracker gps;
    private double latitude, longtitude;
    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    // private SensorManager mSensorMgr;
    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    // private OnShakeListener mShakeListener;
    private Context mContext;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    private SensorManager mSensorEventManager;
    private Sensor mSensor;

    private DbAdapter db;

    private DataStorage dataStorage;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
                    mSensorEventManager.unregisterListener(AccelaroMeterService.this);
                    mSensorEventManager.registerListener(AccelaroMeterService.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }
    };

    private Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();

//        gps = new GpsTracker(AccelaroMeterService.this);
        db = new DbAdapter(AccelaroMeterService.this);

        dataStorage = new DataStorage(this);
        gson = new Gson();

        mContext = getApplicationContext();
        // Obtain a reference to system-wide sensor event manager.
        mSensorEventManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);

        // Get the default sensor for accelerometer
        mSensor = mSensorEventManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register for events.
        mSensorEventManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        // TODO I'll only register at screen off. I don't have a use for shake
        // while not in sleep (yet)
        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        // Unregister our receiver.
        unregisterReceiver(mReceiver);

        // Unregister from SensorManager.
        mSensorEventManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onShake() {
        // Poke a user activity to cause wake?
        Log.v("onShake", "doing wakeup");
        // send in a broadcast for exit request to the main mediator

        Intent intent = new Intent("alert");
        intent.putExtra("do_alert", "1");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        AppHelper.printLog("onShake received");

//        showNotification();
////
////        askAndSendSMS();
//
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                double lat = location.getLatitude();
//                double lng = location.getLongitude();
//
//                AppHelper.printLog("Location received: " + lat + " " + lng);
//
//
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(gps, "Turn On GPS", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//        } else {
//            Toast.makeText(mContext, "Location permission unavailable!", Toast.LENGTH_SHORT).show();
//        }
    }

    private void askAndSendSMS() {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("It seems like you have met an accident, do you want to alert your contacts?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendLocationSMS();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
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
            if (!alertDialog.isShowing()) {
                AppHelper.printLog("Showing alert dialog");
                alertDialog.show();
            }
        } catch (Exception e) {
            AppHelper.printLog("Exception in showing alert dialog");
            e.printStackTrace();
        }
    }

    private void sendLocationSMS() {
        String data = dataStorage.getString(Keys.TRUSTED_CONTACTS);
        if (data != null) {
            ArrayList<ContactListModel> contactListModels;
            Type type = new TypeToken<ArrayList<ContactListModel>>() {
            }.getType();

            contactListModels = gson.fromJson(data, type);

            if (contactListModels != null) {
                if (contactListModels.size() > 0) {
                    for (int i = 0; i < contactListModels.size(); i++) {
                        sendSMS(contactListModels.get(i).getNumber());
                    }
                } else {
                    Toast.makeText(gps, "contacts model size 0", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(gps, "contacts model null", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(gps, "Trusted contacts not added yet!", Toast.LENGTH_SHORT).show();
        }
    }


    public void sendSMS(String phoneNumber) {
        AppHelper.printLog("sending sms to: " + phoneNumber);

        SmsManager smsManager = SmsManager.getDefault();
        String smsBody = "Your friend was met with accident help asap(it's emergency) \n\n " +
                "https://www.google.com/maps/search/?api=1&query=" +
                "13.011412" +
                "," +
                "80.236082";
        smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used right now
    }

    // Used to decide if it is a shake
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        Log.v("sensor", "sensor change is verifying");
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(event.values[SensorManager.DATA_X]
                    + event.values[SensorManager.DATA_Y]
                    + event.values[SensorManager.DATA_Z]
                    - mLastX - mLastY - mLastZ) / diff * 10000;

            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT)
                        && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;

                    onShake();
                }
                mLastForce = now;
            }

            mLastTime = now;
            mLastX = event.values[SensorManager.DATA_X];
            mLastY = event.values[SensorManager.DATA_Y];
            mLastZ = event.values[SensorManager.DATA_Z];
        }

    }

    @SuppressLint("InvalidWakeLockTag")
    public void showNotification() {
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
                String channedName = "OJO Driver";
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

    public void cancelNotification(int notificationId) {

        if (Context.NOTIFICATION_SERVICE != null) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) getApplicationContext()
                    .getSystemService(ns);
            nMgr.cancel(notificationId);
        }
    }

    public void sendalertmessage() {
        try {
            db.open();
            Cursor c = db.getAllContacts();
            if (c.moveToFirst()) {
                do {
                    // DisplayContact(c);
                    sendSMSMessage(c.getString(1));

                } while (c.moveToNext());
            }
            db.close();
        } catch (Exception exp) {
            System.out.println("Message" + exp.getMessage());
        }
    }

    protected void sendSMSMessage(String phone) {
        Log.i("Send SMS", "");
        String phoneNo = phone;
        String message = "Accident Detected  Visit this Link For Getting Location: http://maps.google.com/?q="
                + latitude + "," + longtitude;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void getlocation() {
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longtitude = gps.getLongitude();

            Log.v("Latitude: ", String.valueOf(latitude));
            Log.v("Longitude: ", String.valueOf(longtitude));

        } else {

            gps.showSettingsAlert();
        }
    }
}
