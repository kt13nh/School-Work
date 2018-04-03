package ca.brocku.kt13nh.scheduler;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tram on 27/11/2017.
 */

public class DataHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=2;
    public static final String DB_NAME="sophia";
    public static final String DB_TABLE="wisdom";
    private static final String CREATE_TABLE="CREATE TABLE "+DB_TABLE+
            " (id INTEGER PRIMARY KEY, date TEXT, room TEXT, time TEXT);";

    DataHelper (Context context) {
        super(context,DB_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //how to rebuild it for migration
    }

}