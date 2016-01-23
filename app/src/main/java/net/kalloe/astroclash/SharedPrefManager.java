package net.kalloe.astroclash;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jamie on 23-1-2016.
 */
public class SharedPrefManager {

    //Variables
    private Context context;
    private SharedPreferences preferences;

    //SharedPreferences name
    public static final String PREF_NAME = "player_data";

    //SharedPreferences data key
    public static final String PREF_BEST_SCORE = "player_best_score";

    public SharedPrefManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        //add score
    }

    public void add(String name, int value) {
        try  {
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putInt(name, value);
            prefEditor.commit();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeScore() {
        try {
            if(get(SharedPrefManager.PREF_BEST_SCORE) == -1) {
                add(SharedPrefManager.PREF_BEST_SCORE, 0);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(String name) {
        try {
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.remove(name);
            prefEditor.commit();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int get(String name) {
        return preferences.getInt(name, -1);
    }

}
