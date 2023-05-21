package com.vma.contactarchive;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_WRITE_CONTACTS = 1;
    private static boolean CAN_ACCESS_CONTACTS = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadContacts(View view) {
        requestContactsPermission();
        if (CAN_ACCESS_CONTACTS) {
            Intent intent = new Intent(MainActivity.this, SyncContacts.class);
            startActivity(intent);
        }

    }

    public void addContact(View view) {
        requestContactsPermission();
        if (CAN_ACCESS_CONTACTS) {
            Intent intent = new Intent(MainActivity.this, AddContact.class);
            startActivity(intent);
        }
    }

    private void requestContactsPermission() {
            if (checkSelfPermission(READ_CONTACTS) != PERMISSION_GRANTED ||
                    checkSelfPermission(WRITE_CONTACTS) != PERMISSION_GRANTED) {

                requestPermissions(new String[]{READ_CONTACTS, WRITE_CONTACTS},
                        PERMISSIONS_REQUEST_READ_WRITE_CONTACTS);
            }
            else CAN_ACCESS_CONTACTS = true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                CAN_ACCESS_CONTACTS = true;
            } else {
                CAN_ACCESS_CONTACTS = false;
                Toast.makeText(this, "Permission denied. Cannot access contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}