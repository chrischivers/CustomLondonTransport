package com.customlondontransport;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "transport.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<RouteLine> getBusRoutesAlphabetical() {
        List<RouteLine> busRoutes = new ArrayList<RouteLine>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busRoutes";
        String column1ToFetch = "busRouteID";
        String columnToOrderBy = "_id";

        Cursor c = db.rawQuery("SELECT DISTINCT " + column1ToFetch + " FROM " + sqlTable1 +
                " ORDER BY " + columnToOrderBy+ ";",null);

        c.moveToFirst();
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
        do  {
            busDirections.add(new Direction(c.getInt(0), c.getString(1)));
        } while (c.moveToNext());
        return busDirections;
    }

    public List<StationStop> getBusStopsForRouteAlphabetical(String busRouteID, int busDirection) {

        List<StationStop> busStops = new ArrayList<StationStop>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busRoutes";
        String sqlTable2 = "busStops";
        String column1ToFetch = sqlTable1+".busStopID";
        String column2ToFetch= sqlTable2+".busStopName";
        String column3ToFetch= sqlTable2+".busStopLetterCode";
        String column4ToFetch= sqlTable2+".longitude";
        String column5ToFetch= sqlTable2+".latitude";
        String column1ToInnerJoin = sqlTable1+".busStopID";
        String column2ToInnerJoin = sqlTable2+"._id";
        String column1ToFilterBy = sqlTable1+".busRouteID";
        String column2ToFilterBy = sqlTable1+".busRouteDirection";
        String columnToOrderBy = sqlTable1+"._id";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch +", " +column2ToFetch +", " + column3ToFetch +", "+column4ToFetch + ", " + column5ToFetch + " FROM " + sqlTable1 +
                " INNER JOIN " + sqlTable2 +
                " ON " +  column1ToInnerJoin + "=" + column2ToInnerJoin +
                " WHERE " + column1ToFilterBy + " = '" + busRouteID + "' AND " + column2ToFilterBy + " = " + busDirection +
                " ORDER BY " + columnToOrderBy +";",null);

        c.moveToFirst();
        do {
            busStops.add(new StationStop(c.getString(0), c.getString(1), c.getString(2), c.getFloat(3), c.getFloat(4)));
        } while (c.moveToNext());
        return busStops;
    }



    public List<StationStop> getAllBusStopsOrderByNearest(Location currentLocation) {

        List<StationStop> busStops = new ArrayList<StationStop>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busStops";
        String column1ToFetch = "_id";
        String column2ToFetch= "busStopName";
        String column3ToFetch = "busStopLetterCode";
        String column4ToFetch= "longitude";
        String column5ToFetch= "latitude";
        String columnToOrderBy1 = "latitude";
        String columnToOrderBy2 = "longitude";


        Cursor c = db.rawQuery("SELECT DISTINCT " + column1ToFetch +", " +column2ToFetch +", " + column3ToFetch +", "+column4ToFetch +", "+column5ToFetch +    " FROM " + sqlTable1 +
                " ORDER BY abs(" + columnToOrderBy1 + " - " + currentLocation.getLatitude() + ") + abs(" + columnToOrderBy2 + " - " + currentLocation.getLongitude() + ")",null);

        c.moveToFirst();
        do {
            busStops.add(new StationStop(c.getString(0), c.getString(1), c.getString(2), c.getFloat(3), c.getFloat(4)));
        } while (c.moveToNext());
        return busStops;
    }

    public List<StationStop> getAllDistinctBusStopsOrderByAlphabetical() {

        List<StationStop> busStops = new ArrayList<StationStop>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "busStops";
        String column1ToFetch = "_id";
        String column2ToFetch= "busStopName";

        Cursor c = db.rawQuery("SELECT DISTINCT " + column1ToFetch +", " +column2ToFetch +  " FROM " + sqlTable1,null);

        c.moveToFirst();
        do {
            busStops.add(new StationStop(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return busStops;
    }


    public List<RouteLine> getNearestBusRoutes(Location currentLocation) {

        SQLiteDatabase db = getReadableDatabase();
        List<RouteLine> busRoutesList = new ArrayList<RouteLine>();

        String sqlTable1 = "busRoutes";
        String sqlTable2 = "busStops";
        String column1ToFetch = "busRouteID";
        String virtualColumn2 = "distanceFromHere";

        String column1ToInnerJoin = sqlTable1+".busStopID";
        String column2ToInnerJoin = sqlTable2+"._id";

        String column1ToOrderBy = sqlTable2+".latitude";
        String column2ToOrderBy = sqlTable2+".longitude";


            Cursor c = db.rawQuery(
                    "SELECT DISTINCT " + column1ToFetch + " FROM (" +
                        "SELECT DISTINCT " + sqlTable1 + "." +column1ToFetch + ", " + "abs(" + column1ToOrderBy + " - " + currentLocation.getLatitude() + ") + abs(" + column2ToOrderBy + " - " + currentLocation.getLongitude() + ")" + " as " + virtualColumn2 +
                        " FROM " + sqlTable1 +
                        " INNER JOIN " + sqlTable2 +
                        " ON " +  column1ToInnerJoin + "=" + column2ToInnerJoin +
                        " ORDER BY " + virtualColumn2 + ")",null);


            c.moveToFirst();
            do  {
                busRoutesList.add(new RouteLine(c.getString(0)));
            } while (c.moveToNext());

        return busRoutesList;
    }

    public List<RouteLine> getBusRoutesForAStop(String busStopID) {

        SQLiteDatabase db = getReadableDatabase();
        List<RouteLine> busRoutesList = new ArrayList<RouteLine>();

        String sqlTable1 = "busRoutes";
        String column1ToFetch = "busRouteID";
        String column1ToFilterBy = "busStopID";

        Cursor c = db.rawQuery(
                "SELECT " + column1ToFetch +
                " FROM " + sqlTable1 +
                " WHERE " + column1ToFilterBy + "='" + busStopID + "'" +
                " ORDER BY " + column1ToFetch,null);


        c.moveToFirst();
        do  {
            busRoutesList.add(new RouteLine(c.getString(0)));
        } while (c.moveToNext());

        return busRoutesList;
    }

    public List<RouteLine> getTubeLinesAlphabetical() {

        List<RouteLine> tubeLines = new ArrayList<RouteLine>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeLines";
        String column1ToFetch = "_id";
        String column2ToFetch = "tubeLineAbrvName";
        String column3ToFetch= "tubeLineFullName";

        Cursor c = db.rawQuery("SELECT " + column1ToFetch +", " + column2ToFetch +", " +column3ToFetch + " FROM " + sqlTable1 +
                " ORDER BY " + column3ToFetch + ";",null);

        c.moveToFirst();
        do  {
            tubeLines.add(new RouteLine(c.getString(0), c.getString(1), c.getString(2)));
        } while (c.moveToNext());
        return tubeLines;
    }

    public List<RouteLine> getTubeLinesByStation(String tubeStationID) {

        List<RouteLine> tubeLines = new ArrayList<RouteLine>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeStations";
        String sqlTable2 = "tubeLines";
        String column1ToFetch = sqlTable1+".tubeLineID";
        String column2ToFetch = sqlTable2+".tubeLineAbrvName";
        String column1ToJoinOn = sqlTable2+"._id";
        String column1ToFilterBy = sqlTable1+".tubeStationID";

            Cursor c = db.rawQuery("SELECT " + column1ToFetch + ", " + column2ToFetch +
                                    " FROM " + sqlTable1 +
                                    " INNER JOIN " + sqlTable2+
                                    " ON " + column1ToFetch + "=" + column1ToJoinOn +
                                    " WHERE " + column1ToFilterBy + " = '" + tubeStationID + "';" ,null);

        c.moveToFirst();
        do  {
            tubeLines.add(new RouteLine(c.getString(0), c.getString(1), ""));
        } while (c.moveToNext());
        return tubeLines;
    }

    public List<StationStop> getTubeStationsAlphabetical(String tubeLineID) {

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


        c.moveToFirst();
        do  {
            tubeStations.add(new StationStop(c.getString(0), c.getString(1),Float.parseFloat(c.getString(2)),Float.parseFloat(c.getString(3))));
        } while (c.moveToNext());
        return tubeStations;
    }

    public List<StationStop> getDistinctTubeStationsAlphabetical() {

        List<StationStop> tubeStations = new ArrayList<StationStop>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeStations";
        String column1ToFetch = "tubeStationID";
        String column2ToFetch= "tubeStationName";


        Cursor c = db.rawQuery("SELECT DISTINCT " + column1ToFetch + ", " + column2ToFetch + " FROM " + sqlTable1 +
                " ORDER BY " + column2ToFetch +";",null);


        c.moveToFirst();
        do  {
            tubeStations.add(new StationStop(c.getString(0), c.getString(1)));
        } while (c.moveToNext());
        return tubeStations;
    }

    public List<StationStop> getAllTubeStationsByNearest(Location currentLocation) {

        List<StationStop> tubeStations = new ArrayList<StationStop>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlTable1 = "tubeStations";
        String column1ToFetch = "tubeStationID";
        String column2ToFetch= "tubeStationName";
        String column3ToFetch = "longitude";
        String column4ToFetch = "latitude";
        String columnToOrderBy1 = "latitude";
        String columnToOrderBy2 = "longitude";

        Cursor c = db.rawQuery("SELECT DISTINCT " + column1ToFetch + ", " + column2ToFetch + ", " + column3ToFetch + ", " + column4ToFetch + " FROM " + sqlTable1 +
                                " ORDER BY abs(" + columnToOrderBy1 + " - " + currentLocation.getLatitude() + ") + abs(" + columnToOrderBy2 + " - " + currentLocation.getLongitude() + ")",null);

        c.moveToFirst();
        do  {
            tubeStations.add(new StationStop(c.getString(0), c.getString(1),Float.parseFloat(c.getString(2)),Float.parseFloat(c.getString(3))));
        } while (c.moveToNext());
        return tubeStations;
    }
}