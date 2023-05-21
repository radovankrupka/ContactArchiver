package com.vma.contactarchive.service;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.vma.contactarchive.model.PhoneContactProjection;

import java.util.ArrayList;
import java.util.List;

public class ContactFetcher {

    private Context context;

    public ContactFetcher(Context context) {
        this.context = context;
    }

    @SuppressLint("Range")
    public List<PhoneContactProjection> getAllContacts() {
        List<PhoneContactProjection> contacts = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            PhoneContactProjection contact = new PhoneContactProjection(displayName, phoneNumber);
                            contacts.add(contact);
                        }
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
        }

        return contacts;
    }
}
