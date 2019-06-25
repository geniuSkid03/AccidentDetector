package com.geniuskid.accidentdetector.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.Utils.AppHelper;
import com.geniuskid.accidentdetector.Utils.Keys;
import com.geniuskid.accidentdetector.adapters.ContactListModel;
import com.geniuskid.accidentdetector.adapters.ContactsAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ContactsActvivity extends SuperCompatActivity {

    private ContactsAdapter adapterContact;

    private RecyclerView trustedRecycler;
    private ArrayList<ContactListModel> trustedList;

    private AppCompatButton addContactsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_actvivity);

        trustedRecycler = findViewById(R.id.added_recycler);
        addContactsBtn = findViewById(R.id.add_contacts);

        trustedList = new ArrayList<>();

        addContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(uri, ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 100);
            }
        });

        if (dataStorage.isDataAvailable(Keys.TRUSTED_CONTACTS)) {
            String data = dataStorage.getString(Keys.TRUSTED_CONTACTS);

            Type type = new TypeToken<ArrayList<ContactListModel>>() {
            }.getType();

            trustedList = gson.fromJson(data, type);
            trustedRecycler.setLayoutManager(new LinearLayoutManager(ContactsActvivity.this, LinearLayoutManager.VERTICAL, false));
            adapterContact = new ContactsAdapter(ContactsActvivity.this, trustedList);
            trustedRecycler.setAdapter(adapterContact);
            adapterContact.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                ContactListModel contactListModel = new ContactListModel();
                contactListModel.setName(name);
                contactListModel.setNumber(number);

                addToListAndSetAdapter(contactListModel);
            }
        }
    }

    private void addToListAndSetAdapter(ContactListModel contactListModel) {

        if (!trustedList.contains(contactListModel)) {
            trustedList.add(contactListModel);
            AppHelper.print("Contacts added");
        } else {
            AppHelper.print("Contacts not added");
        }

        dataStorage.saveString(Keys.TRUSTED_CONTACTS, gson.toJson(trustedList));

        trustedRecycler.setLayoutManager(new LinearLayoutManager(ContactsActvivity.this, LinearLayoutManager.VERTICAL, false));
        adapterContact = new ContactsAdapter(ContactsActvivity.this, trustedList);
        trustedRecycler.setAdapter(adapterContact);
        adapterContact.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
