package com.example.strayedx;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private LinearLayout layout;
    private SharedPreferences prefs;

    private int buttonSize;
    private int theme;
    private int alignment;

    private ArrayList<String> menuOrder;

    private final String[] defaultOrder = {
        "TOOLS", "SETTINGS", "ABOUT", "EXIT"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("StrayedXSettings", MODE_PRIVATE);

        buttonSize = prefs.getInt("buttonSize", 18);
        theme = prefs.getInt("theme", 0);
        alignment = prefs.getInt("alignment", 1);

        loadOrder();
        showHome();
    }

    private int getThemeColor() {
        if (theme == 1) return Color.CYAN;
        if (theme == 2) return Color.MAGENTA;
        return Color.GREEN;
    }

    private int getAlignment() {
        if (alignment == 0) return Gravity.LEFT;
        if (alignment == 2) return Gravity.RIGHT;
        return Gravity.CENTER;
    }

    private void saveSettings() {
        prefs.edit()
            .putInt("buttonSize", buttonSize)
            .putInt("theme", theme)
            .putInt("alignment", alignment)
            .apply();
    }

    private void loadOrder() {
        menuOrder = new ArrayList<>();

        String saved = prefs.getString("menuOrder", "");

        if (saved.isEmpty()) {
            Collections.addAll(menuOrder, defaultOrder);
        } else {
            String[] items = saved.split(",");
            Collections.addAll(menuOrder, items);
        }
    }

    private void saveOrder() {
        StringBuilder saved = new StringBuilder();

        for (String item : menuOrder) {
            if (saved.length() > 0) saved.append(",");
            saved.append(item);
        }

        prefs.edit().putString("menuOrder", saved.toString()).apply();
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
        title.setTextColor(getThemeColor());
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 20, 10, 30);

        layout.addView(title);
        setContentView(layout);
    }

    private void addButton(String name, Runnable action) {
        Button button = new Button(this);

        button.setText(name);
        button.setTextColor(Color.BLACK);
        button.setTextSize(buttonSize);

        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(getThemeColor())
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, 8, 0, 8);

        button.setGravity(getAlignment());

        layout.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private void showHome() {
        setupPage("STRA YEDX");

        for (String item : menuOrder) {
            if (item.equals("TOOLS")) {
                addButton("TOOLS", () -> showTools());
            } else if (item.equals("SETTINGS")) {
                addButton("SETTINGS", () -> showSettings());
            } else if (item.equals("ABOUT")) {
                addButton("ABOUT", () -> showAbout());
            } else if (item.equals("EXIT")) {
                addButton("EXIT", () -> finish());
            }
        }
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

        addButton("CUSTOMIZE LAYOUT", () -> showCustomize());

        addButton("BACK", () -> showHome());
    }

    private void showCustomize() {
        setupPage("CUSTOMIZE LAYOUT");

        addButton("BUTTON SIZE: " + buttonSize, () -> {
            buttonSize += 2;
            if (buttonSize > 30) buttonSize = 12;
            saveSettings();
            showCustomize();
        });

        addButton("CHANGE THEME", () -> {
            theme++;
            if (theme > 2) theme = 0;
            saveSettings();
            showCustomize();
        });

        String position = "CENTER";

        if (alignment == 0) position = "LEFT";
        if (alignment == 2) position = "RIGHT";

        addButton("BUTTON POSITION: " + position, () -> {
            alignment++;
            if (alignment > 2) alignment = 0;
            saveSettings();
            showCustomize();
        });

        addButton("REARRANGE MENU BUTTONS", () -> showRearrange());

        addButton("RESET LAYOUT", () -> {
            buttonSize = 18;
            theme = 0;
            alignment = 1;

            Collections.addAll(menuOrder = new ArrayList<>(), defaultOrder);

            saveSettings();
            saveOrder();
            showCustomize();
        });

        addButton("BACK", () -> showSettings());
    }

    private void showRearrange() {
        setupPage("REARRANGE BUTTONS");

        for (int i = 0; i < menuOrder.size(); i++) {
            final int index = i;

            addButton(
                menuOrder.get(i) + "  UP",
                () -> {
                    if (index > 0) {
                        Collections.swap(menuOrder, index, index - 1);
                        saveOrder();
                        showRearrange();
                    }
                }
            );

            addButton(
                menuOrder.get(i) + "  DOWN",
                () -> {
                    if (index < menuOrder.size() - 1) {
                        Collections.swap(menuOrder, index, index + 1);
                        saveOrder();
                        showRearrange();
                    }
                }
            );
        }

        addButton("BACK", () -> showCustomize());
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
