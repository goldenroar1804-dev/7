 package com.example.strayedx;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private FrameLayout root;
    private LinearLayout page;
    private SharedPreferences prefs;

    private int theme;
    private int buttonSize;
    private boolean editMode = false;

    private ArrayList<String> menuItems = new ArrayList<>();

    private final String[] defaults = {
        "TOOLS", "SETTINGS", "ABOUT", "EXIT"
    };

    private int[] colors = {
        Color.GREEN,
        Color.CYAN,
        Color.RED,
        Color.MAGENTA,
        Color.YELLOW,
        Color.WHITE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("StrayedXUltimate", MODE_PRIVATE);

        theme = prefs.getInt("theme", 0);
        buttonSize = prefs.getInt("buttonSize", 18);

        loadMenu();
        showHome();
    }

    private int accent() {
        return colors[Math.max(0, Math.min(theme, colors.length - 1))];
    }

    private void saveSettings() {
        prefs.edit()
            .putInt("theme", theme)
            .putInt("buttonSize", buttonSize)
            .apply();
    }

    private void loadMenu() {
        String saved = prefs.getString("menu", "");

        if (saved.isEmpty()) {
            Collections.addAll(menuItems, defaults);
        } else {
            Collections.addAll(menuItems, saved.split("\\|"));
        }
    }

    private void saveMenu() {
        StringBuilder result = new StringBuilder();

        for (String item : menuItems) {
            if (result.length() > 0) result.append("|");
            result.append(item);
        }

        prefs.edit().putString("menu", result.toString()).apply();
    }

    private void setupPage(String title) {
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        page.setPadding(25, 25, 25, 25);
        page.setBackgroundColor(Color.BLACK);

        TextView heading = new TextView(this);
        heading.setText(title);
        heading.setTextColor(accent());
        heading.setTextSize(30);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(10, 15, 10, 25);

        page.addView(heading);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);

        scroll.addView(content);

        page.addView(scroll, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0, 1
        ));

        setContentView(page);

        this.content = content;
    }

    private LinearLayout content;

    private void addButton(String label, Runnable action) {
        Button button = new Button(this);

        button.setText(label);
        button.setTextSize(buttonSize);
        button.setTextColor(Color.BLACK);

        GradientDrawable background = new GradientDrawable();
        background.setColor(accent());
        background.setCornerRadius(20);
        button.setBackground(background);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, 7, 0, 7);

        content.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private void showHome() {
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("STRA YEDX 2.0");
        title.setTextColor(accent());
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams titleParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                100,
                Gravity.TOP
            );

        root.addView(title, titleParams);

        FrameLayout menuArea = new FrameLayout(this);

        FrameLayout.LayoutParams areaParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );

        areaParams.topMargin = 100;
        root.addView(menuArea, areaParams);

        for (int i = 0; i < menuItems.size(); i++) {
            final String item = menuItems.get(i);

            Button button = makeHomeButton(item);

            FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    65
                );

            float x = prefs.getFloat(item + "_x", 0.08f);
            float y = prefs.getFloat(item + "_y", 0.08f + i * 0.19f);

            button.setX(x * 1000);
            button.setY(y * 1000);

            menuArea.addView(button, params);

            button.post(() -> {
                float savedX = prefs.getFloat(item + "_x", 0.08f);
                float savedY = prefs.getFloat(
                    item + "_y", 0.08f + menuItems.indexOf(item) * 0.19f
                );

                button.setX(savedX * Math.max(1, menuArea.getWidth()));
                button.setY(savedY * Math.max(1, menuArea.getHeight()));
            });

            enableDragging(button, item, menuArea);
        }

        Button customize = makeHomeButton("CUSTOMIZE LAYOUT");

        FrameLayout.LayoutParams customizeParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                65,
                Gravity.BOTTOM
            );

        customizeParams.setMargins(20, 0, 20, 20);
        root.addView(customize, customizeParams);

        customize.setOnClickListener(v -> showCustomize());

        setContentView(root);
    }

    private Button makeHomeButton(String label) {
        Button button = new Button(this);

        button.setText(label);
        button.setTextSize(buttonSize);
        button.setTextColor(Color.BLACK);

        GradientDrawable background = new GradientDrawable();
        background.setColor(accent());
        background.setCornerRadius(20);

        button.setBackground(background);

        return button;
    }

    private void enableDragging(
        Button button,
        String name,
        FrameLayout area
    ) {
        button.setOnTouchListener(new View.OnTouchListener() {

            float startX, startY;
            float downX, downY;
            boolean moved = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        startX = view.getX();
                        startY = view.getY();
                        downX = event.getRawX();
                        downY = event.getRawY();
                        moved = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:

                        float dx = event.getRawX() - downX;
                        float dy = event.getRawY() - downY;

                        if (Math.abs(dx) > 8 || Math.abs(dy) > 8) {
                            moved = true;
                        }

                        if (moved) {
                            float newX = Math.max(
                                0,
                                Math.min(
                                    startX + dx,
                                    area.getWidth() - view.getWidth()
                                )
                            );

                            float newY = Math.max(
                                0,
                                Math.min(
                                    startY + dy,
                                    area.getHeight() - view.getHeight()
                                )
                            );

                            view.setX(newX);
                            view.setY(newY);
                        }

                        return true;

                    case MotionEvent.ACTION_UP:

                        if (moved) {
                            float x = view.getX() /
                                Math.max(1, area.getWidth());

                            float y = view.getY() /
                                Math.max(1, area.getHeight());

                            prefs.edit()
                                .putFloat(name + "_x", x)
                                .putFloat(name + "_y", y)
                                .apply();
                        } else {
                            openMenuItem(name);
                        }

                        return true;
                }

                return true;
            }
        });
    }

    private void openMenuItem(String name) {
        if (name.equals("TOOLS")) showTools();
        else if (name.equals("SETTINGS")) showSettings();
        else if (name.equals("ABOUT")) showAbout();
        else if (name.equals("EXIT")) finish();
        else showMessage(name, "Custom StrayedX menu button.");
    }

    private void showCustomize() {
        setupPage("CUSTOMIZE LAYOUT");

        addButton("CHANGE THEME", () -> {
            theme++;
            if (theme >= colors.length) theme = 0;
            saveSettings();
            showCustomize();
        });

        addButton("BUTTON SIZE: " + buttonSize, () -> {
            buttonSize += 2;
            if (buttonSize > 30) buttonSize = 12;
            saveSettings();
            showCustomize();
        });

        addButton("DRAG BUTTONS ON HOME SCREEN", () -> {
            Toast.makeText(
                this,
                "Return home and drag any menu button.",
                Toast.LENGTH_LONG
            ).show();

            showHome();
        });

        addButton("ADD CUSTOM BUTTON", () -> addCustomButton());

        addButton("REARRANGE MENU", () -> showRearrange());

        addButton("RESET LAYOUT", () -> {
            for (String item : menuItems) {
                prefs.edit()
                    .remove(item + "_x")
                    .remove(item + "_y")
                    .apply();
            }

            theme = 0;
            buttonSize = 18;

            saveSettings();
            showCustomize();
        });

        addButton("BACK", () -> showSettings());
    }

    private void addCustomButton() {
        setupPage("ADD CUSTOM BUTTON");

        EditText input = new EditText(this);
        input.setHint("Enter button name");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);

        content.addView(input);

        addButton("CREATE BUTTON", () -> {
            String name = input.getText().toString().trim();

            if (!name.isEmpty() &&
                !menuItems.contains(name) &&
                !name.contains("|")) {

                menuItems.add(name);
                saveMenu();
                showHome();

            } else {
                Toast.makeText(
                    this,
                    "Enter a unique button name.",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        addButton("BACK", () -> showCustomize());
    }

    private void showRearrange() {
        setupPage("REARRANGE MENU");

        for (int i = 0; i < menuItems.size(); i++) {
            final int index = i;

            addButton("MOVE " + menuItems.get(i) + " UP", () -> {
                if (index > 0) {
                    Collections.swap(menuItems, index, index - 1);
                    saveMenu();
                    showRearrange();
                }
            });

            addButton("MOVE " + menuItems.get(i) + " DOWN", () -> {
                if (index < menuItems.size() - 1) {
                    Collections.swap(menuItems, index, index + 1);
                    saveMenu();
                    showRearrange();
                }
            });

            if (!isDefault(menuItems.get(i))) {
                addButton("REMOVE " + menuItems.get(i), () -> {
                    menuItems.remove(index);
                    saveMenu();
                    showRearrange();
                });
            }
        }

        addButton("BACK", () -> showCustomize());
    }

    private boolean isDefault(String name) {
        for (String item : defaults) {
            if (item.equals(name)) return true;
        }
        return false;
    }

    private void showTools() {
        setupPage("TOOLS");

        addButton("QUICK NOTES", () -> showNotes());

        addButton("CHECKLIST", () -> showChecklist());

        addButton("BACK", () -> showHome());
    }

    private void showNotes() {
        setupPage("QUICK NOTES");

        EditText notes = new EditText(this);
        notes.setTextColor(Color.WHITE);
        notes.setHintTextColor(Color.GRAY);
        notes.setHint("Write your notes here...");
        notes.setGravity(Gravity.TOP);
        notes.setMinLines(8);

        notes.setText(
            prefs.getString("notes", "")
        );

        content.addView(notes);

        addButton("SAVE NOTES", () -> {
            prefs.edit()
                .putString("notes", notes.getText().toString())
                .apply();

            Toast.makeText(
                this,
                "Notes saved!",
                Toast.LENGTH_SHORT
            ).show();
        });

        addButton("BACK", () -> showTools());
    }

    private void showChecklist() {
        setupPage("CHECKLIST");

        String[] tasks = {
            "Prepare session",
            "Check equipment",
            "Complete objectives",
            "Review progress"
        };

        for (String task : tasks) {
            CheckBox checkBox = new CheckBox(this);

            checkBox.setText(task);
            checkBox.setTextColor(Color.WHITE);

            checkBox.setChecked(
                prefs.getBoolean("check_" + task, false)
            );

            checkBox.setOnCheckedChangeListener((button, checked) -> {
                prefs.edit()
                    .putBoolean("check_" + task, checked)
                    .apply();
            });

            content.addView(checkBox);
        }

        addButton("RESET CHECKLIST", () -> {
            for (String task : tasks) {
                prefs.edit()
                    .remove("check_" + task)
                    .apply();
            }

            showChecklist();
        });

        addButton("BACK", () -> showTools());
    }

    private void showSettings() {
        setupPage("SETTINGS");

        addButton("CUSTOMIZE LAYOUT", () -> showCustomize());

        addButton("RESET ALL SETTINGS", () -> {
            prefs.edit().clear().apply();

            theme = 0;
            buttonSize = 18;

            menuItems.clear();
            Collections.addAll(menuItems, defaults);

            saveMenu();
            saveSettings();

            showHome();
        });

        addButton("BACK", () -> showHome());
    }

    private void showAbout() {
        setupPage("ABOUT");

        addButton("STRA YEDX 2.0", () ->
            showMessage(
                "ABOUT",
                "StrayedX Ultimate 2.0\nStandalone Quest companion app."
            )
        );

        addButton("BACK", () -> showHome());
    }

    private void showMessage(String title, String message) {
        setupPage(title);

        TextView text = new TextView(this);
        text.setText(message);
        text.setTextColor(Color.WHITE);
        text.setTextSize(20);
        text.setGravity(Gravity.CENTER);

        content.addView(text);

        addButton("BACK", () -> showHome());
    }

    @Override
    public void onBackPressed() {
        showHome();
    }
}
