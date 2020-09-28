package com.tttqiu.viewbinder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.textview1)
    public TextView textView1;
    @Bind(R.id.textview2)
    public TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewBinder.bind(this);
        textView1.setText("success");
        textView2.setText("success");
    }
}