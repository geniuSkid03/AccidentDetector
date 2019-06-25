package com.geniuskid.accidentdetector.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.Utils.AppHelper;
import com.geniuskid.accidentdetector.Utils.Keys;

public class LoginActivity extends SuperCompatActivity {

    private AppCompatButton loginBtn, signUpBtn;
    private TextInputEditText mobNumEd, passwordEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        setClicks();
    }

    private void init() {
        mobNumEd = findViewById(R.id.login_mobile);
        passwordEd = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        signUpBtn = findViewById(R.id.register_btn);
    }

    private void setClicks() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndLogin();
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(LoginActivity.this, SignUpActivity.class, true);
            }
        });
    }

    private void checkAndLogin() {
        String mobile = mobNumEd.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            AppHelper.showToast(LoginActivity.this, "Enter Mobile number!");
            return;
        }

        String password = passwordEd.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            AppHelper.showToast(LoginActivity.this, "Enter password!");
            return;
        }

        dataStorage.saveString(Keys.MOBILE, mobile);
        dataStorage.saveString(Keys.PASSWORD, password);
        dataStorage.saveBoolean(Keys.IS_LOGGED_IN, true);

        goTo(this, MainActivity.class, true);
    }
}
