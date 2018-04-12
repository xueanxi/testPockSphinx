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
import android.widget.Toast;

/**
 * Created by user on 4/11/18.
 */

public class PermissionActivity extends Activity {

    String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasPermisssion = true;
        for(int i =0;i<permissions.length;i++){
            if(ContextCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_DENIED){
                hasPermisssion = false;
            }
        }

        if(hasPermisssion){
            Toast.makeText(this,"has permission .",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,MainActivity.class));
        }else{
            Toast.makeText(this,"no permission .",Toast.LENGTH_SHORT).show();
            requestPermissions(permissions,1001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1001){
            boolean isGrant = true;
            for(int i = 0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    isGrant = false;
                }
            }

            if(isGrant){
                startActivity(new Intent(this,MainActivity.class));
            }else{
                Toast.makeText(this,"Get permission fail.",Toast.LENGTH_SHORT).show();
            }
        }

        //finish();
    }
}
