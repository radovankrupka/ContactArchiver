package com.vma.contactarchive.service;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.vma.contactarchive.model.PhoneContactProjection;

import java.util.ArrayList;

public class ContactSaver {
    private static ContactSaver saverInstance;
    private final ContentResolver contentResolver;

    private ContactSaver(Context context) {
        contentResolver = context.getContentResolver();
    }

    public static ContactSaver getInstance(Context context) {
        if (saverInstance == null) {
            saverInstance = new ContactSaver(context.getApplicationContext());
        }
        return saverInstance;
    }

    public boolean saveContactLocaly(PhoneContactProjection contact) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        contact.getDisplayName())
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            ContentProviderResult[] results = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            if (results != null && results.length > 0) {
                return true;
            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        return false;
    }
}
