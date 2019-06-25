package com.geniuskid.accidentdetector.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.geniuskid.accidentdetector.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class AppHelper {

    private static final String TAG = "InstanceDebug";
    private static final int THUMBNAIL_SIZE = 150;

//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = null;
//        if (connectivityManager != null) {
//            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        }
//        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
//    }

//    public static boolean isGpsConnected(Context context) {
//
//        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
//        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
//    }

    public static void print(String message) {
        System.out.println("AppHelper: " + message);
    }

    public static void showToast(Context context, String message) {
        if (context != null && context.getResources() != null) {
            if (BuildConfig.DEBUG)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public static void printLog(String info) {
        Log.i(TAG, info);
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int postedHr = calendar.get(Calendar.HOUR_OF_DAY);
        int postedMin = calendar.get(Calendar.MINUTE);
        return postedHr + ":" + postedMin;
    }

    public static String getTodaysDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        return df.format(c);
    }

    public static String getFormattedDate(Calendar calendar) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static byte[] getBitMapFromDrawable(Context context, int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap getBitMapFromByteArray(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        String path = "";
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return path;
        }
    }

    public static Bitmap getThumbnail(Context context, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }
}
