package sp.esolz.player;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SplashActivity extends AppCompatActivity {


    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private Runnable moveNext = null;
    private Handler forwordHandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        //============Thread initialization


        moveNext = new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        };

        forwordHandler = new Handler();
        permissionChk();

    }


    public void permissionChk() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.MEDIA_CONTENT_CONTROL) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.MEDIA_CONTENT_CONTROL)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.MEDIA_CONTENT_CONTROL},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.MEDIA_CONTENT_CONTROL},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
            return;
        } else {
            forwordHandler.postDelayed(moveNext, 5000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    forwordHandler.postDelayed(moveNext, 5000);
                } else {
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(SplashActivity.this);
                    alertDialogBuilder.setMessage("With out these permissions, we cant fetch your device media list.");
                    alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            permissionChk();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                return;
            }
        }
    }

}
