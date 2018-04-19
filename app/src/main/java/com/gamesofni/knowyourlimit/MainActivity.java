package com.gamesofni.knowyourlimit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    TextView totalLimitTextView;
    TextView user1LimitTextView;
    TextView user2LimitTextView;
    ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // open Settings
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // run AddExpenseActivity when button is pressed
        Button addExpense = findViewById(R.id.add_expense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class));
            }
        });


        // initializing views
        totalLimitTextView = findViewById(R.id.total_limit);
        user1LimitTextView = findViewById(R.id.user1_limit);
        user2LimitTextView = findViewById(R.id.user2_limit);
        progressBar = findViewById(R.id.progress_bar);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Constants constants = new Constants(getBaseContext());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int dailyLimit = Integer.parseInt(sharedPrefs.getString(constants.DAILY_LIMIT_KEY, "10"));
        float currentLimit = sharedPrefs.getFloat(constants.CURRENT_LIMIT, dailyLimit);

        final boolean twoUsers = sharedPrefs.getBoolean(constants.TWO_USERS, false);

        final long today = Calendar.getInstance().getTimeInMillis();
        final long lastOnline = sharedPrefs.getLong(constants.LAST_ONLINE, today);
        final long awayDays = Math.round((double)(today - lastOnline) / (24 * 60 * 60 * 1000));

        currentLimit += awayDays * dailyLimit;

        // save stats to preferences
        SharedPreferences.Editor prefEditor = sharedPrefs.edit();
        prefEditor.putLong(constants.LAST_ONLINE, today);
        prefEditor.putFloat(constants.CURRENT_LIMIT, currentLimit);
        prefEditor.apply();

        // set up separate limits
        if (twoUsers) {
            final String user1Name = sharedPrefs.getString(constants.FIRST_USER_NAME, "User 1");
            final String user2Name = sharedPrefs.getString(constants.SECOND_USER_NAME, "User 2");

            final float currentLimitUser1 = sharedPrefs.getFloat(constants.CURRENT_LIMIT_USER1, dailyLimit/2);
            final float currentLimitUser2 = sharedPrefs.getFloat(constants.CURRENT_LIMIT_USER2, dailyLimit/2);

            user1LimitTextView.setText(user1Name + " limit: " + currentLimitUser1);
            user2LimitTextView.setText(user2Name + " limit: " + currentLimitUser2);

            user1LimitTextView.setVisibility(View.VISIBLE);
            user2LimitTextView.setVisibility(View.VISIBLE);

        } else {
            user1LimitTextView.setVisibility(View.GONE);
            user2LimitTextView.setVisibility(View.GONE);
        }


        // set up progress indicator
        final int finalCurrentLimit = Math.round(currentLimit);
        totalLimitTextView.setText(String.format("%.2f", currentLimit));
        if (finalCurrentLimit > dailyLimit) {
            // saved more than limit
            progressBar.setMax(finalCurrentLimit);
            final Integer increment = finalCurrentLimit / Math.max(1, Math.min(15, finalCurrentLimit));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus <= finalCurrentLimit) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(progressStatus);
                                progressBar.setSecondaryProgress(progressStatus + increment);
                            }
                        });
                        try {
                            // Just to display the progress slowly
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressStatus += increment;
                    }
                    progressBar.setProgress(finalCurrentLimit);
                }
            }).start();
        } else if (currentLimit >= 0) {
            // under limit
            progressBar.setMax(dailyLimit);
            final Integer increment = finalCurrentLimit / Math.max(1, Math.min(15, finalCurrentLimit));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus <= finalCurrentLimit) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(progressStatus);
                                if (progressStatus + increment <= finalCurrentLimit) {
                                    progressBar.setSecondaryProgress(progressStatus + increment);
                                }
                            }
                        });
                        try {
                            // Just to display the progress slowly
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressStatus += increment;
                    }
                    progressBar.setProgress(finalCurrentLimit);
                }
            }).start();
        } else {
            // overdraft
            final int absLimit = Math.abs(finalCurrentLimit);
            progressBar.setMax(Math.max(dailyLimit, absLimit));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.circle_red)));
                progressBar.setSecondaryProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.circle_red_light)));
            } else {
                // TODO smth for older vers
            }
            final Integer increment = absLimit / Math.max(1, Math.min(15, absLimit));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TextView limitTextView = findViewById(R.id.total_limit);
                    limitTextView.setTextColor(getResources().getColor(R.color.circle_red));

                    while (progressStatus <= absLimit) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(progressStatus);
                                if (progressStatus + increment <= absLimit) {
                                    progressBar.setSecondaryProgress(progressStatus + increment);
                                }
                            }
                        });
                        try {
                            // Just to display the progress slowly
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressStatus += increment;
                    }
                    progressBar.setProgress(absLimit);
                }
            }).start();
        }

    }
}
