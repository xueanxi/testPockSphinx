package edu.cmu.pocketsphinx.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

/**
 * Created by user on 4/11/18.
 */

public class PermissionActivity extends Activity {

    String p1 = Manifest.permission.RECORD_AUDIO;
    String p2 = Manifest.permission.READ_EXTERNAL_STORAGE;
    String p3 = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, p1)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限，申请权限。
            requestPermissions(new String[]{p1,p2,p3},1);
        }else{
            // 有权限了，去放肆吧。
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startActivity(new Intent(this,MainActivity.class));
        //finish();
    }
}
