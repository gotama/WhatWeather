package com.gautamastudios.whatweather.storage.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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

@Entity(tableName = "flags")
public class Flags {

    @PrimaryKey(autoGenerate = true)
    private int autoGeneratedKey;

    @SerializedName("darksky-unavailable")
    @ColumnInfo(name = "darkskyunavailable")
    private String darkskyUnavailable;

    @ColumnInfo(name = "sources")
    private ArrayList<String> sources;

    @ColumnInfo(name = "units")
    private String units;

    public Flags(int autoGeneratedKey, String darkskyUnavailable, ArrayList<String> sources, String units) {
        this.autoGeneratedKey = autoGeneratedKey;
        this.darkskyUnavailable = darkskyUnavailable;
        this.sources = sources;
        this.units = units;
    }

    public int getAutoGeneratedKey() {
        return autoGeneratedKey;
    }

    public String getDarkskyUnavailable() {
        return darkskyUnavailable;
    }

    public ArrayList<String> getSources() {
        return sources;
    }

    public String getUnits() {
        return units;
    }
}
