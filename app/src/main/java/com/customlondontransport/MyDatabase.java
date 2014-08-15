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

    public List<RouteLine> getBusRoutes() {
        List<RouteLine> busRoutes = new ArrayList<RouteLine>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busRoutes";
        String column1ToFetch = "busRouteID";
        String columnToOrderBy = "_id";

        Cursor c = db.rawQuery("SELECT DISTINCT " + column1ToFetch + " FROM " + sqlTable1 +
                " ORDER BY " + columnToOrderBy+ ";",null);

        c.moveToFirst();
        busRoutes.add(new RouteLine()); //add empty item to the front of the list
        do  {
           busRoutes.add(new RouteLine(c.getString(0)));
        } while (c.moveToNext());
        return busRoutes;
    }

    public List<Direction> getBusDirections(String busRouteID) {

        List<Direction> busDirections = new ArrayList<Direction>();

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
                " WHERE " + column1ToFilterBy + " = '" + busRouteID + "' AND " + column2ToFilterBy + " = '1'" +
                " ORDER BY " + column1ToFetch + ";",null);

        c.moveToFirst();
        busDirections.add(new Direction()); //add empty item to the front of the list
        do  {
            busDirections.add(new Direction(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return busDirections;
    }

    public List<StationStop> getBusStops(String busRouteID, int busDirection) {

        List<StationStop> busStops = new ArrayList<StationStop>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busRoutes";
        String sqlTable2 = "busStops";
        String column1ToFetch = sqlTable1+".busStopID";
        String column2ToFetch= sqlTable2+".busStopName";
        String column3ToFetch= sqlTable2+".longitude";
        String column4ToFetch= sqlTable2+".latitude";
        String column1ToInnerJoin = sqlTable1+".busStopID";
        String column2ToInnerJoin = sqlTable2+"._id";
        String column1ToFilterBy = sqlTable1+".busRouteID";
        String column2ToFilterBy = sqlTable1+".busRouteDirection";
        String columnToOrderBy = sqlTable1+"._id";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch +", " +column2ToFetch +", " + column3ToFetch +", "+column4ToFetch +   " FROM " + sqlTable1 +
                " INNER JOIN " + sqlTable2 +
                " ON " +  column1ToInnerJoin + "=" + column2ToInnerJoin +
                " WHERE " + column1ToFilterBy + " = '" + busRouteID + "' AND " + column2ToFilterBy + " = " + busDirection +
                " ORDER BY " + columnToOrderBy +";",null);

        busStops.add(new StationStop()); //add empty item to the front of the list
        c.moveToFirst();
        do {
            busStops.add(new StationStop(c.getString(0), c.getString(1), Float.parseFloat(c.getString(2)), Float.parseFloat(c.getString(3))));
        } while (c.moveToNext());
        return busStops;
    }

    public List<RouteLine> getTubeLines() {

        List<RouteLine> tubeLines = new ArrayList<RouteLine>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeLines";
        String column1ToFetch = "_id";
        String column2ToFetch= "tubeLineName";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch +", " +column2ToFetch + " FROM " + sqlTable1 +
                " ORDER BY " + column1ToFetch + ";",null);

        c.moveToFirst();
        tubeLines.add(new RouteLine()); //add blank item to front of list
        do  {
            tubeLines.add(new RouteLine(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return tubeLines;
    }

    public List<StationStop> getTubeStations(String tubeLineID) {

        List<StationStop> tubeStations = new ArrayList<StationStop>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeStations";
        String column1ToFetch = "tubeStationID";
        String column2ToFetch= "tubeStationName";
        String column3ToFetch = "longitude";
        String column4ToFetch = "latitude";
        String column1ToFilterBy = "tubeLineID";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch + ", " + column2ToFetch + ", " + column3ToFetch + ", " + column4ToFetch + " FROM " + sqlTable1 +
                " WHERE " + column1ToFilterBy + " = '" + tubeLineID + "'" +
                " ORDER BY " + column2ToFetch +";",null);

        tubeStations.add(new StationStop()); //add blank item to front of list
        c.moveToFirst();
        do  {
            tubeStations.add(new StationStop(c.getString(0), c.getString(1),Float.parseFloat(c.getString(2)),Float.parseFloat(c.getString(3))));
        } while (c.moveToNext());
        return tubeStations;
    }
}