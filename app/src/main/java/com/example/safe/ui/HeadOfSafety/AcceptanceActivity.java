package com.example.safe.ui.HeadOfSafety;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.safe.R;

import java.util.Calendar;

public class AcceptanceActivity extends AppCompatActivity {
    private TextView acceptanceTimeText;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#008dea"));
        }
        setContentView(R.layout.activity_acceptance);
        Toolbar toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());
        acceptanceTimeText =findViewById(R.id.acceptanceTimeText);
        acceptanceTimeText.setOnClickListener(v -> {
            Calendar calendar=Calendar.getInstance();
            new DatePickerDialog( this, (view, year, month, dayOfMonth) ->
                    acceptanceTimeText.setText(year+"-"+(month+1)+"-"+dayOfMonth)
                    ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
}
