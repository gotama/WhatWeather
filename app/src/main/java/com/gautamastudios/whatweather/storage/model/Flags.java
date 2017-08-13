package com.gautamastudios.whatweather.storage.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * The flags object contains various metadata information related to the request. This object may optionally contain
 * any of the following properties:
 * <p>
 * {@link Flags#darkskyUnavailable} optional <br>
 * The presence of this property indicates that the Dark Sky data source supports the given location, but a temporary
 * error (such as a radar station being down for maintenance) has made the data unavailable.
 * <p>
 * {@link Flags#sources} required <br>
 * This property contains an array of IDs for each data source utilized in servicing this request. This data type needs
 * to be deserialized to a java object.
 * <p>
 * {@link Flags#units} required <br>
 * Indicates the units which were used for the data in this request.
 */

@Entity(tableName = Flags.TABLE_NAME)
public class Flags {

    public static final String TABLE_NAME = "flags";

    public static final String FIELD_PRIMARY_KEY = "primary_key";
    public static final String FIELD_API_UNAVAILABLE = "darkskyunavailable";
    public static final String FIELD_SOURCES = "sources";
    public static final String FIELD_UNITS = "units";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FIELD_PRIMARY_KEY)
    public long autoGeneratedKey;

    @SerializedName("darksky-unavailable")
    @ColumnInfo(name = FIELD_API_UNAVAILABLE)
    public String darkskyUnavailable;

    @SerializedName("sources")
    @Ignore
    private ArrayList<String> sources;

    @ColumnInfo(name = FIELD_SOURCES)
    public String sourcesArray;

    @ColumnInfo(name = FIELD_UNITS)
    public String units;

    /**
     * Create a new {@link Alert} from the specified {@link ContentValues}.
     *
     * @param values A {@link ContentValues} that contains {@link #FIELD_PRIMARY_KEY}, {@link #FIELD_API_UNAVAILABLE},
     *               {@link #FIELD_SOURCES}, {@link #FIELD_UNITS}
     * @return An instance of {@link Alert}.
     */
    public static Flags fromContentValues(ContentValues values) {
        final Flags flags = new Flags();
        if (values.containsKey(FIELD_PRIMARY_KEY)) {
            flags.autoGeneratedKey = values.getAsLong(FIELD_PRIMARY_KEY);
        }
        if (values.containsKey(FIELD_API_UNAVAILABLE)) {
            flags.darkskyUnavailable = values.getAsString(FIELD_API_UNAVAILABLE);
        }
        if (values.containsKey(FIELD_SOURCES)) {
            flags.sourcesArray = values.getAsString(FIELD_SOURCES);
        }
        if (values.containsKey(FIELD_UNITS)) {
            flags.units = values.getAsString(FIELD_UNITS);
        }
        return flags;
    }

    public ArrayList<String> getSources() {
        return sources;
    }

    public static String strSeparator = ",";

    public static String convertArrayToString(ArrayList<String> stringArrayList) {

        String[] array = new String[stringArrayList.size()];
        int count = 0;
        for (String string : stringArrayList) {
            array[count] = string;
            count++;
        }

        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            if (i < array.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

}
