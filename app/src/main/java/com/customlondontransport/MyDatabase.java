package com.customlondontransport;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "transport.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<String> getBusRoutes() {
        List<String> busRoutes = new ArrayList<String>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busRoutes";
        String column1ToFetch = "busRouteID";
        String columnToOrderBy = "_id";

        Cursor c = db.rawQuery("SELECT DISTINCT " + column1ToFetch + " FROM " + sqlTable1 +
                " ORDER BY " + columnToOrderBy+ ";",null);

        c.moveToFirst();
        busRoutes.add(""); //add empty item to the front of the list
        do  {
           busRoutes.add(c.getString(0));
        } while (c.moveToNext());
        return busRoutes;
    }

    public List<ComboItem> getBusDirections(String busRouteID) {

        List<ComboItem> busDirections = new ArrayList<ComboItem>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busRoutes";
        String sqlTable2 = "busStops";
        String column1ToFetch = sqlTable1+".busRouteDirection";
        String column2ToFetch= sqlTable2+".busStopName";
        String column1ToInnerJoin = sqlTable1+".busStopID";
        String column2ToInnerJoin = sqlTable2+"._id";
        String column1ToFilterBy = sqlTable1+".busRouteID";
        String column2ToFilterBy = sqlTable1+".busRouteFinalDestination";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch + ", " + column2ToFetch + " FROM " + sqlTable1 +
                " INNER JOIN " + sqlTable2 +
                " ON " +  column1ToInnerJoin + "=" + column2ToInnerJoin +
                " WHERE " + column1ToFilterBy + " = " + busRouteID + " AND " + column2ToFilterBy + " = '1'" +
                " ORDER BY " + column1ToFetch + ";",null);

        c.moveToFirst();
        do  {
            busDirections.add(new ComboItem(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return busDirections;
    }

    public List<ComboItem> getBusStops(String busRouteID, int busDirection) {

        List<ComboItem> busStops = new ArrayList<ComboItem>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busRoutes";
        String sqlTable2 = "busStops";
        String column1ToFetch = sqlTable1+".busStopID";
        String column2ToFetch= sqlTable2+".busStopName";
        String column1ToInnerJoin = sqlTable1+".busStopID";
        String column2ToInnerJoin = sqlTable2+"._id";
        String column1ToFilterBy = sqlTable1+".busRouteID";
        String column2ToFilterBy = sqlTable1+".busRouteDirection";
        String columnToOrderBy = sqlTable1+"._id";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch +", " +column2ToFetch + " FROM " + sqlTable1 +
                " INNER JOIN " + sqlTable2 +
                " ON " +  column1ToInnerJoin + "=" + column2ToInnerJoin +
                " WHERE " + column1ToFilterBy + " = " + busRouteID + " AND " + column2ToFilterBy + " = " + busDirection +
                " ORDER BY " + columnToOrderBy +";",null);

        c.moveToFirst();
        do {
            busStops.add(new ComboItem(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return busStops;
    }

    public List<ComboItem> getTubeLines() {

        List<ComboItem> tubeLines = new ArrayList<ComboItem>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeLines";
        String column1ToFetch = "_id";
        String column2ToFetch= "tubeLineName";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch +", " +column2ToFetch + " FROM " + sqlTable1 +
                " ORDER BY " + column1ToFetch + ";",null);

        c.moveToFirst();
        tubeLines.add(new ComboItem("","")); //add blank item to front of list
        do  {
            tubeLines.add(new ComboItem(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return tubeLines;
    }

    public List<ComboItem> getTubeStations(String tubeLineID) {

        List<ComboItem> tubeStations = new ArrayList<ComboItem>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeStations";
        String column1ToFetch = "tubeStationID";
        String column2ToFetch= "tubeStationName";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch +", " +column2ToFetch + " FROM " + sqlTable1 +
                " WHERE " + column1ToFetch + " = " + tubeLineID +
                " ORDER BY " + column2ToFetch +";",null);

        c.moveToFirst();
        do  {
            tubeStations.add(new ComboItem(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return tubeStations;
    }
}