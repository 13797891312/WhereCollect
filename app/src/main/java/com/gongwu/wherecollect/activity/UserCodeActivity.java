package com.gongwu.wherecollect.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gongwu.wherecollect.R;

public class UserCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_code);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UserCodeActivity.class);
        context.startActivity(intent);
    }
}
