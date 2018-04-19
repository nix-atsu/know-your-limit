package com.gamesofni.knowyourlimit;

import android.content.Context;

public class Constants {

    // resource constants
    public String DAILY_LIMIT_KEY;
    public String TWO_USERS;
    public String CURRENT_LIMIT_RESET;
    public String FIRST_USER_NAME;
    public String SECOND_USER_NAME;

    // hidden preferences constants
    public String CURRENT_LIMIT;
    public String LAST_ONLINE;
    public String CURRENT_LIMIT_USER1;
    public String CURRENT_LIMIT_USER2;

    public Constants(Context applicationContext) {
        DAILY_LIMIT_KEY = applicationContext.getString(R.string.key_daily_limit);
        TWO_USERS = applicationContext.getString(R.string.key_two_users);
        CURRENT_LIMIT_RESET = applicationContext.getString(R.string.key_current_limit_reset);
        FIRST_USER_NAME = applicationContext.getString(R.string.key_first_user_name);
        SECOND_USER_NAME = applicationContext.getString(R.string.key_second_user_name);

        CURRENT_LIMIT = "key_current_limit";
        LAST_ONLINE = "key_last_online";
        CURRENT_LIMIT_USER1 = "key_current_limit_user1";
        CURRENT_LIMIT_USER2 = "key_current_limit_user2";
    }
}
