package com.vma.contactarchive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.vma.contactarchive.R;
import com.vma.contactarchive.model.PhoneContactProjection;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<PhoneContactProjection> {

    private ArrayList<PhoneContactProjection> contacts;
    private LayoutInflater inflater;

    public ContactAdapter(Context context, ArrayList<PhoneContactProjection> contacts) {
        super(context, 0, contacts);
        this.contacts = contacts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.displayNameTextView = convertView.findViewById(R.id.textViewDisplayName);
            viewHolder.phoneNumberTextView = convertView.findViewById(R.id.textViewPhoneNumber);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PhoneContactProjection contact = contacts.get(position);
        viewHolder.displayNameTextView.setText(contact.getDisplayName());
        viewHolder.phoneNumberTextView.setText(contact.getPhoneNumber());

        if (contact.isSynced()) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView displayNameTextView;
        TextView phoneNumberTextView;
    }
}
