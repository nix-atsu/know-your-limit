package com.gamesofni.knowyourlimit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText addTotalExpenseView;
    private CheckBox user1Checkbox;
    private CheckBox user2Checkbox;
    private Constants constants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        addTotalExpenseView = findViewById(R.id.add_total_expense_input);
        user1Checkbox = findViewById(R.id.user1_expense);
        user2Checkbox = findViewById(R.id.user2_expense);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AddExpenseActivity.this);
        constants = new Constants(AddExpenseActivity.this);
        final boolean two_users = sharedPrefs.getBoolean(constants.TWO_USERS, false);

        if (two_users) {
            user1Checkbox.setVisibility(View.VISIBLE);
            user2Checkbox.setVisibility(View.VISIBLE);
        } else {
            user1Checkbox.setVisibility(View.GONE);
            user2Checkbox.setVisibility(View.GONE);
        }

        Button addTotalExpense = findViewById(R.id.add_total_expense);

        addTotalExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AddExpenseActivity.this);

                final float currentLimit = sharedPrefs.getFloat(constants.CURRENT_LIMIT, 0.0f);
                final float expense = (float) Double.parseDouble(addTotalExpenseView.getText().toString());
                final float newLimit = currentLimit - expense;
                final float user1Limit = sharedPrefs.getFloat(constants.CURRENT_LIMIT_USER1, currentLimit/2);
                final float user2Limit = sharedPrefs.getFloat(constants.CURRENT_LIMIT_USER2, currentLimit/2);

                SharedPreferences.Editor prefEditor = sharedPrefs.edit();
                prefEditor.putFloat(constants.CURRENT_LIMIT, newLimit);

                final boolean two_users = sharedPrefs.getBoolean(constants.TWO_USERS, false);
                if (!two_users || (user1Checkbox.isChecked() && user2Checkbox.isChecked())) {
                    prefEditor.putFloat(constants.CURRENT_LIMIT_USER1, user1Limit - expense/2);
                    prefEditor.putFloat(constants.CURRENT_LIMIT_USER2, user2Limit - expense/2);

                } else if (user2Checkbox.isChecked()) {
                    prefEditor.putFloat(constants.CURRENT_LIMIT_USER2, user2Limit - expense);

                } else {
                    prefEditor.putFloat(constants.CURRENT_LIMIT_USER1, user1Limit - expense);
                }

                prefEditor.apply();

                startActivity(new Intent(AddExpenseActivity.this, MainActivity.class));
            }
        });

    }
}
