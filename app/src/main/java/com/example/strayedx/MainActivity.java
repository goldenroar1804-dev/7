package com.example.strayedx;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private void setupPage(String titleText) {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(30);
        title.setTextColor(Color.GREEN);
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 20, 10, 30);

        layout.addView(title);
        setContentView(layout);
    }

    private void addButton(String name, Runnable action) {
        Button button = new Button(this);
        button.setText(name);
        button.setTextColor(Color.BLACK);
        button.setTextSize(18);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(Color.GREEN)
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, 8, 0, 8);
        layout.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private void showHome() {
        setupPage("STRA YEDX");

        addButton("TOOLS", () -> showTools());
        addButton("SETTINGS", () -> showSettings());
        addButton("ABOUT", () -> showAbout());
        addButton("EXIT", () -> finish());
    }

    private void showTools() {
        setupPage("TOOLS");

        addButton("Quick Notes", () ->
            showMessage("QUICK NOTES",
                "Use this area for your Strayed session notes.")
        );

        addButton("Checklist", () ->
            showMessage("CHECKLIST",
                "Your standalone companion checklist.")
        );

        addButton("BACK", () -> showHome());
    }

    private void showSettings() {
        setupPage("SETTINGS");

        addButton("Display: Green Theme", () ->
            showMessage("DISPLAY",
                "StrayedX currently uses its green theme.")
        );

        addButton("BACK", () -> showHome());
    }

    private void showAbout() {
        setupPage("ABOUT");

        addButton("StrayedX Version 1.0", () ->
            showMessage("VERSION",
                "You are running StrayedX version 1.0.")
        );

        addButton("BACK", () -> showHome());
    }

    private void showMessage(String title, String message) {
        setupPage(title);

        TextView text = new TextView(this);
        text.setText(message);
        text.setTextColor(Color.WHITE);
        text.setTextSize(18);
        text.setGravity(Gravity.CENTER);

        layout.addView(text);

        addButton("BACK", () -> showHome());
    }
}
