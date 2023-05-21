package com.vma.contactarchive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vma.contactarchive.adapter.ContactAdapter;
import com.vma.contactarchive.model.PhoneContactProjection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.vma.contactarchive.service.ContactFetcher;
import com.vma.contactarchive.service.ContactSaver;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SyncContacts extends AppCompatActivity {

    private final String SERVER_FILE_READ_URL = "http://server-phpapp.000webhostapp.com/contact-archive/read.php";
    private final String SERVER_FILE_WRITE_URL = "http://server-phpapp.000webhostapp.com/contact-archive/insert.php";
    List<PhoneContactProjection> deviceNotSyncedContacts = new ArrayList<>();
    List<PhoneContactProjection> remoteNotSyncedContacts = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private ContactFetcher contactFetcher;
    private ContactSaver contactSaver;
    private ListView listViewContacts;
    RequestQueue requestQueue;
    TextView tempTextView;
    Button syncButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_archived_contacts);
        syncButton = findViewById(R.id.syncButton);
        syncButton.setEnabled(false);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        contactSaver = ContactSaver.getInstance(this.getApplicationContext());
        contactFetcher = new ContactFetcher(this.getApplicationContext());

        fetchData();

        listViewContacts = findViewById(R.id.listViewContacts);
        tempTextView = findViewById(R.id.tempTv);

    }

    private void setUpData(List<PhoneContactProjection> remoteContacts) {
        List<PhoneContactProjection> deviceContacts = contactFetcher.getAllContacts();

        List<PhoneContactProjection> syncedContacts = getSyncedContacts(deviceContacts,remoteContacts);

        deviceNotSyncedContacts = deviceContacts.stream()
                                        .filter(item -> !syncedContacts.contains(item))
                                        .collect(Collectors.toList());
       remoteNotSyncedContacts = remoteContacts.stream()
                                        .filter(item -> !syncedContacts.contains(item))
                                        .collect(Collectors.toList());

        ArrayList<PhoneContactProjection> allContacts = new ArrayList<>(syncedContacts);
        allContacts.addAll(deviceNotSyncedContacts);
        allContacts.addAll(remoteNotSyncedContacts);

        contactAdapter = new ContactAdapter(this,  allContacts);
        listViewContacts.setAdapter(contactAdapter);
    }

    private List<PhoneContactProjection> getSyncedContacts(List<PhoneContactProjection> list1, List<PhoneContactProjection> list2) {
       return list1.stream()
                .filter(c1 -> list2.stream()
                        .anyMatch(c2 -> c2.getDisplayName().equals(c1.getDisplayName())
                                && c2.getPhoneNumber().equals(c1.getPhoneNumber())))
                .peek(c1 -> c1.setSynced(true))
                .collect(Collectors.toList());
    }


    public void fetchData() {
        List<PhoneContactProjection> remoteContacts = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, SERVER_FILE_READ_URL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String displayName = obj.getString("displayName");
                                String phoneNumber = obj.getString("phoneNumber");
                                remoteContacts.add(new PhoneContactProjection(displayName,phoneNumber) );
                            }

                            hideTempTv();
                            setUpData(remoteContacts);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    public void sendData(List<PhoneContactProjection> contacts) throws JSONException {
        JSONArray reqObject;
        String reqString = new Gson().toJson(contacts);
        try {
            reqObject = new JSONArray(reqString);
        } catch (JSONException e) {
            reqObject = new JSONArray();
            System.out.println(e.getMessage());
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, SERVER_FILE_WRITE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        informUserOfSuccess();
                        fetchData();
                        contactAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Chyba pocas odosielania dat.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return reqString.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        requestQueue.add(postRequest);
    }

    public void syncContacts(View view) {
        informUserOfSending();
        saveData(remoteNotSyncedContacts);
        try {
            sendData(deviceNotSyncedContacts);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveData(List<PhoneContactProjection> deviceNotSyncedContacts) {
        deviceNotSyncedContacts.forEach(contact -> contactSaver.saveContactLocaly(contact));
    }

    private void informUserOfSuccess() {
        Toast.makeText(getApplicationContext(), "Data boli úspešne odoslané!", Toast.LENGTH_SHORT).show();
    }

    private void informUserOfSending() {
        Toast.makeText(getApplicationContext(), "Počkajte prosím, dáta sa odosielaju!", Toast.LENGTH_SHORT).show();
    }
    private void hideTempTv() {
        syncButton.setEnabled(true);
        tempTextView.setVisibility(View.GONE);
    }
}
