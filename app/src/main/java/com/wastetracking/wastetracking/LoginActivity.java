package com.wastetracking.wastetracking;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.wastetracking.wastetracking.customfonts.MyTextView_Roboto_Regular;

/**
 * Created by VR-Visitor on 3/12/2018.
 */

public class LoginActivity extends Activity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_immediate);

        MyTextView_Roboto_Regular loginButton = (MyTextView_Roboto_Regular) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
