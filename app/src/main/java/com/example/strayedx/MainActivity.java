package com.example.strayedx;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.*;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private LinearLayout page;
    private LinearLayout content;
    private SharedPreferences prefs;

    private int theme;
    private int buttonSize;

    private boolean hiddenLasers;

    private int laserColor;

    private ArrayList<String> menuItems = new ArrayList<>();

    private final String[] defaults = {
        "TOOLS", "CHEATS", "SETTINGS", "ABOUT", "EXIT"
    };

    private final int[] colors = {
        Color.GREEN,
        Color.CYAN,
        Color.RED,
        Color.MAGENTA,
        Color.YELLOW,
        Color.WHITE,
        Color.rgb(212, 175, 55)
    };

    private final String[] colorNames = {
        "GREEN", "CYAN", "RED", "PURPLE",
        "YELLOW", "WHITE", "GOLD"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(
            "StrayedXUltimate", MODE_PRIVATE
        );

        theme = prefs.getInt("theme", 0);
        buttonSize = prefs.getInt("buttonSize", 18);

        hiddenLasers = prefs.getBoolean(
            "hiddenLasers", false
        );

        laserColor = prefs.getInt("laserColor", 0);

        loadMenu();
        showHome();
    }

    private int accent() {
        return colors[Math.max(
            0, Math.min(theme, colors.length - 1)
        )];
    }

    private void saveSettings() {
        prefs.edit()
            .putInt("theme", theme)
            .putInt("buttonSize", buttonSize)
            .putBoolean("hiddenLasers", hiddenLasers)
            .putInt("laserColor", laserColor)
            .apply();
    }

    private void loadMenu() {
        String saved = prefs.getString("menu", "");

        if (saved.isEmpty()) {
            Collections.addAll(menuItems, defaults);
        } else {
            Collections.addAll(menuItems, saved.split("\\|"));
        }

        if (!menuItems.contains("CHEATS")) {
            menuItems.add(1, "CHEATS");
            saveMenu();
        }
    }

    private void saveMenu() {
        StringBuilder result = new StringBuilder();

        for (String item : menuItems) {
            if (result.length() > 0) {
                result.append("|");
            }

            result.append(item);
        }

        prefs.edit()
            .putString("menu", result.toString())
            .apply();
    }

    private GradientDrawable buttonBackground() {
        GradientDrawable background = new GradientDrawable();

        background.setColor(accent());
        background.setCornerRadius(dp(18));

        return background;
    }

    private void setupPage(String title) {

        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        page.setPadding(dp(25), dp(25), dp(25), dp(25));
        page.setBackgroundColor(Color.BLACK);

        TextView heading = new TextView(this);

        heading.setText(title);
        heading.setTextColor(accent());
        heading.setTextSize(30);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(dp(10), dp(15), dp(10), dp(25));

        page.addView(heading);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);

        scroll.addView(content);

        page.addView(scroll, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1
        ));

        setContentView(page);
    }

    private void addButton(String label, Runnable action) {

        Button button = new Button(this);

        button.setText(label);
        button.setTextSize(buttonSize);
        button.setTextColor(Color.BLACK);
        button.setBackground(buttonBackground());

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, dp(7), 0, dp(7));

        content.addView(button, params);

        button.setOnClickListener(v -> action.run());
    }

    private int dp(float value) {
        return (int) (
            value * getResources()
                .getDisplayMetrics().density + 0.5f
        );
    }

    // MAIN MENU

    private void showHome() {

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);

        title.setText("STRA YEDX 2.1");
        title.setTextColor(accent());
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams titleParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(85),
                Gravity.TOP
            );

        root.addView(title, titleParams);

        FrameLayout menuArea = new FrameLayout(this);

        FrameLayout.LayoutParams areaParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );

        areaParams.topMargin = dp(85);
        areaParams.bottomMargin = dp(85);

        root.addView(menuArea, areaParams);

        Button customize = makeButton("CUSTOMIZE LAYOUT");

        FrameLayout.LayoutParams customizeParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60),
                Gravity.BOTTOM
            );

        customizeParams.setMargins(
            dp(20), 0, dp(20), dp(15)
        );

        root.addView(customize, customizeParams);

        customize.setOnClickListener(v -> showCustomize());

        setContentView(root);

        menuArea.post(() -> {

            int areaWidth = menuArea.getWidth();
            int areaHeight = menuArea.getHeight();

            int buttonWidth = (int)(areaWidth * 0.80f);
            int buttonHeight = dp(58);

            for (int i = 0; i < menuItems.size(); i++) {

                String item = menuItems.get(i);

                Button button = makeButton(item);

                FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(
                        buttonWidth,
                        buttonHeight
                    );

                menuArea.addView(button, params);

                float defaultX = 0.5f;
                float defaultY = (float)i /
                    Math.max(1, menuItems.size());

                float savedX = prefs.getFloat(
                    item + "_x", defaultX
                );

                float savedY = prefs.getFloat(
                    item + "_y", defaultY
                );

                float x = savedX *
                    Math.max(0, areaWidth - buttonWidth);

                float y = savedY *
                    Math.max(0, areaHeight - buttonHeight);

                button.setX(x);
                button.setY(y);

                enableDragging(button, item, menuArea);
            }
        });
    }

    private Button makeButton(String label) {

        Button button = new Button(this);

        button.setText(label);
        button.setTextSize(buttonSize);
        button.setTextColor(Color.BLACK);
        button.setBackground(buttonBackground());

        return button;
    }

    // DRAGGING

    private void enableDragging(
        Button button,
        String name,
        FrameLayout area
    ) {

        final int threshold =
            ViewConfiguration.get(this).getScaledTouchSlop();

        button.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    view.setTag(new float[] {
                        view.getX(),
                        view.getY(),
                        event.getRawX(),
                        event.getRawY()
                    });

                    return true;

                case MotionEvent.ACTION_MOVE:

                    float[] start =
                        (float[])view.getTag();

                    if (start == null) return true;

                    float dx = event.getRawX() - start[2];
                    float dy = event.getRawY() - start[3];

                    if (Math.abs(dx) > threshold ||
                        Math.abs(dy) > threshold) {

                        float newX = start[0] + dx;
                        float newY = start[1] + dy;

                        newX = Math.max(0, Math.min(
                            newX,
                            area.getWidth() - view.getWidth()
                        ));

                        newY = Math.max(0, Math.min(
                            newY,
                            area.getHeight() - view.getHeight()
                        ));

                        view.setX(newX);
                        view.setY(newY);
                    }

                    return true;

                case MotionEvent.ACTION_UP:

                    float[] original =
                        (float[])view.getTag();

                    if (original == null) return true;

                    float moveX =
                        event.getRawX() - original[2];

                    float moveY =
                        event.getRawY() - original[3];

                    boolean dragged =
                        Math.abs(moveX) > threshold ||
                        Math.abs(moveY) > threshold;

                    if (dragged) {

                        float x = view.getX() /
                            Math.max(
                                1,
                                area.getWidth() - view.getWidth()
                            );

                        float y = view.getY() /
                            Math.max(
                                1,
                                area.getHeight() - view.getHeight()
                            );

                        prefs.edit()
                            .putFloat(name + "_x", x)
                            .putFloat(name + "_y", y)
                            .apply();

                    } else {

                        openMenuItem(name);
                    }

                    view.setTag(null);
                    return true;

                case MotionEvent.ACTION_CANCEL:

                    view.setTag(null);
                    return true;
            }

            return true;
        });
    }

    private void openMenuItem(String name) {

        if (name.equals("TOOLS")) {
            showTools();
        } else if (name.equals("CHEATS")) {
            showCheats();
        } else if (name.equals("SETTINGS")) {
            showSettings();
        } else if (name.equals("ABOUT")) {
            showAbout();
        } else if (name.equals("EXIT")) {
            finish();
        } else {
            showMessage(name, "Custom StrayedX menu button.");
        }
    }

    // CHEATS

    private void showCheats() {

        setupPage("CHEATS");

        addButton("HIDDEN LASERS", () ->
            showHiddenLasers()
        );

        addButton("CHEAT REFERENCE", () ->
            showMessage(
                "CHEAT REFERENCE",
                "Save and organize game codes and commands here."
            )
        );

        addButton("CUSTOM CHEAT NOTES", () ->
            showCheatNotes()
        );

        addButton("GAME TIPS", () ->
            showMessage(
                "GAME TIPS",
                "Keep your useful game tips and shortcuts here."
            )
        );

        addButton("BACK", () -> showHome());
    }

    private void showHiddenLasers() {

        setupPage("HIDDEN LASERS");

        TextView explanation = new TextView(this);

        explanation.setText(
            "Laser preferences\n\n" +
            "These settings are saved in StrayedX. " +
            "They do not activate an aiming laser inside Strayed."
        );

        explanation.setTextColor(Color.WHITE);
        explanation.setTextSize(17);
        explanation.setGravity(Gravity.CENTER);
        explanation.setPadding(dp(10), dp(10), dp(10), dp(20));

        content.addView(explanation);

        addButton(
            hiddenLasers ? "LASERS: ON" : "LASERS: OFF",
            () -> {
                hiddenLasers = !hiddenLasers;
                saveSettings();
                showHiddenLasers();
            }
        );

        addButton("LASER COLOR: GREEN", () -> {
            laserColor = 0;
            saveSettings();
            showHiddenLasers();
        });

        addButton("LASER COLOR: RED", () -> {
            laserColor = 1;
            saveSettings();
            showHiddenLasers();
        });

        addButton("LASER COLOR: BLUE", () -> {
            laserColor = 2;
            saveSettings();
            showHiddenLasers();
        });

        addButton("LASER COLOR: GOLD", () -> {
            laserColor = 3;
            saveSettings();
            showHiddenLasers();
        });

        addButton("BACK", () -> showCheats());
    }

    private void showCheatNotes() {

        setupPage("CUSTOM CHEAT NOTES");

        EditText notes = new EditText(this);

        notes.setTextColor(Color.WHITE);
        notes.setHintTextColor(Color.GRAY);
        notes.setHint("Enter cheat codes or commands...");
        notes.setGravity(Gravity.TOP);
        notes.setMinLines(8);

        notes.setText(
            prefs.getString("cheatNotes", "")
        );

        content.addView(notes);

        addButton("SAVE CHEAT NOTES", () -> {

            prefs.edit()
                .putString(
                    "cheatNotes",
                    notes.getText().toString()
                )
                .apply();

            Toast.makeText(
                this,
                "Cheat notes saved!",
                Toast.LENGTH_SHORT
            ).show();
        });

        addButton("BACK", () -> showCheats());
    }

    // CUSTOMIZATION

    private void showCustomize() {

        setupPage("CUSTOMIZE LAYOUT");

        addButton("CHANGE THEME: " + colorNames[theme], () -> {

            theme++;

            if (theme >= colors.length) {
                theme = 0;
            }

            saveSettings();
            showCustomize();
        });

        addButton("BUTTON SIZE: " + buttonSize, () -> {

            buttonSize += 2;

            if (buttonSize > 30) {
                buttonSize = 12;
            }

            saveSettings();
            showCustomize();
        });

        addButton("DRAG BUTTONS ON HOME SCREEN", () ->
            showHome()
        );

        addButton("ADD CUSTOM BUTTON", () ->
            addCustomButton()
        );

        addButton("REARRANGE MENU", () ->
            showRearrange()
        );

        addButton("RESET BUTTON POSITIONS", () -> {

            for (String item : menuItems) {
                prefs.edit()
                    .remove(item + "_x")
                    .remove(item + "_y")
                    .apply();
            }

            showHome();
        });

        addButton("RESET APPEARANCE", () -> {

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

            if (!name.isEmpty()
                && !menuItems.contains(name)
                && !name.contains("|")) {

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
            String item = menuItems.get(i);

            addButton("MOVE " + item + " UP", () -> {

                if (index > 0) {

                    Collections.swap(
                        menuItems,
                        index,
                        index - 1
                    );

                    saveMenu();
                    showRearrange();
                }
            });

            addButton("MOVE " + item + " DOWN", () -> {

                if (index < menuItems.size() - 1) {

                    Collections.swap(
                        menuItems,
                        index,
                        index + 1
                    );

                    saveMenu();
                    showRearrange();
                }
            });

            if (!isDefault(item)) {

                addButton("REMOVE " + item, () -> {

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

    // TOOLS

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
                .putString(
                    "notes",
                    notes.getText().toString()
                )
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

            checkBox.setOnCheckedChangeListener(
                (button, checked) -> {

                    prefs.edit()
                        .putBoolean("check_" + task, checked)
                        .apply();
                }
            );

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

    // SETTINGS

    private void showSettings() {

        setupPage("SETTINGS");

        addButton("CUSTOMIZE LAYOUT", () ->
            showCustomize()
        );

        addButton("RESET ALL SETTINGS", () -> {

            prefs.edit().clear().apply();

            theme = 0;
            buttonSize = 18;
            hiddenLasers = false;
            laserColor = 0;

            menuItems.clear();
            Collections.addAll(menuItems, defaults);

            saveMenu();
            saveSettings();

            showHome();
        });

        addButton("BACK", () -> showHome());
    }

    // ABOUT

    private void showAbout() {

        setupPage("ABOUT");

        addButton("STRAYEDX 2.1", () ->
            showMessage(
                "ABOUT",
                "StrayedX Ultimate 2.1\n" +
                "Standalone Quest companion app."
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
