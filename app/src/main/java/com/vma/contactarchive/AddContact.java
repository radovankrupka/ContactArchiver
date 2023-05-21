package com.vma.contactarchive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vma.contactarchive.model.PhoneContactProjection;
import com.vma.contactarchive.service.ContactSaver;

import java.util.ArrayList;

public class AddContact extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPhoneNumber;

    private ContactSaver contactSaver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        contactSaver = ContactSaver.getInstance(this.getApplicationContext());
    }

    public void saveContact(View view) {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contactSaver.saveContactLocaly(new PhoneContactProjection(firstName + " " + lastName, phoneNumber))){
            Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}