package com.geniuskid.accidentdetector.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.Utils.AppHelper;
import com.geniuskid.accidentdetector.Utils.Keys;

public class ProfileActivity extends SuperCompatActivity {

    private AppCompatButton updateBtn;
    private EditText nameEd, mobileEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEd = findViewById(R.id.name_ed);
        mobileEd = findViewById(R.id.mobile_ed);
        updateBtn = findViewById(R.id.update_btn);

        if(dataStorage.isDataAvailable(Keys.MOBILE)) {
            mobileEd.setText(dataStorage.getString(Keys.MOBILE));
        }

        if(dataStorage.isDataAvailable(Keys.NAME)) {
            nameEd.setText(dataStorage.getString(Keys.NAME));
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nameEd.getText().toString().trim())) {
                    AppHelper.showToast(ProfileActivity.this, "Enter Name");
                    return;
                }

                if(TextUtils.isEmpty(mobileEd.getText().toString().trim())) {
                    AppHelper.showToast(ProfileActivity.this, "Enter mobile");
                    return;
                }

                dataStorage.saveString(Keys.NAME, nameEd.getText().toString().trim());
                dataStorage.saveString(Keys.MOBILE, mobileEd.getText().toString().trim());

                AppHelper.showToast(ProfileActivity.this, "Profile Updated");
                nameEd.setText("");
                mobileEd.setText("");
            }
        });
    }
}
