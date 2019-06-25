package com.geniuskid.accidentdetector.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;

import com.geniuskid.accidentdetector.R;
import com.geniuskid.accidentdetector.Utils.AppHelper;
import com.geniuskid.accidentdetector.Utils.Keys;

public class SignUpActivity extends SuperCompatActivity {

    private AppCompatButton loginBtn, signUpBtn;
    private TextInputEditText nameEd, pwdEd, mobNumEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        setClicks();
    }

    private void init() {
        loginBtn = findViewById(R.id.login_btn);
        signUpBtn = findViewById(R.id.register_btn);
        nameEd = findViewById(R.id.sign_Up_name);
        pwdEd = findViewById(R.id.sign_up_password);
        mobNumEd = findViewById(R.id.sign_up_mobile);
    }

    private void setClicks() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(SignUpActivity.this, LoginActivity.class, true);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSignUp();
            }
        });
    }

    private void checkAndSignUp() {
        String name = nameEd.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            AppHelper.showToast(SignUpActivity.this, "Enter your name!");
            return;
        }

        String mobile = mobNumEd.getText().toString().trim();
        if(TextUtils.isEmpty(mobile)) {
            AppHelper.showToast(SignUpActivity.this, "Enter your mobile number!");
            return;
        }

        String password = pwdEd.getText().toString().trim();
        if(TextUtils.isEmpty(password)) {
            AppHelper.showToast(SignUpActivity.this, "Enter your password!");
            return;
        }

        dataStorage.saveString(Keys.NAME, name);
        dataStorage.saveString(Keys.MOBILE, mobile);
        dataStorage.saveString(Keys.PASSWORD, password);
        dataStorage.saveBoolean(Keys.IS_LOGGED_IN, true);

        goTo(this, MainActivity.class, true);
    }
}
