package com.example.strayedx;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {

    LinearLayout layout;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private void showHome() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("STRA YEDX");
        title.setTextSize(34);
        title.setTextColor(Color.GREEN);
        title.setGravity(Gravity.CENTER);

        info = new TextView(this);
        info.setText("STANDALONE QUEST MENU");
        info.setTextSize(18);
        info.setTextColor(Color.WHITE);
        info.setGravity(Gravity.CENTER);

        layout.addView(title);
        layout.addView(info);

        addMenuButton("TOOLS", "Your standalone tools will go here.");
        addMenuButton("SETTINGS", "StrayedX settings will go here.");
        addMenuButton("ABOUT", "StrayedX - Standalone Quest Companion.");

        setContentView(layout);
    }

    private void addMenuButton(String name, String message) {
        Button button = new Button(this);
        button.setText(name);
        button.setTextSize(18);
        button.setTextColor(Color.BLACK);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(Color.GREEN)
        );

        layout.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setText(message);
            }
        });
    }
}
