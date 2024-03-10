package com.indeed.suggest;

import java.util.Map;

/** Represents a location by name and geographical location. */
public final class Location {
    static double NO_VALUE = Double.MIN_VALUE; // this is well outside the bounds of either lat or long
    static String NO_VAL_STR = String.valueOf(NO_VALUE);

    /**
     * Human-friendly name of this location.
     */
    public final String name;

    /**
     * Location latitude.
     */
    public final double lat;
    /**
     * Location longitude.
     */
    public final double lon;

    /**
     * Create new location from the log entry.
     *
     * @param logEntry - source log entry
     */
    public Location(Map<String, String> logEntry) {
        this(
                logEntry.getOrDefault("match", "").trim(),
                // gonna trust that if it has a value, it will be parseable:
                Double.parseDouble(logEntry.getOrDefault("loclat", NO_VAL_STR)),
                Double.parseDouble(logEntry.getOrDefault("loclon", NO_VAL_STR)));
    }

    /**
     * Create new location.
     *
     * @param name - see {@link #name}
     * @param lat  - see {@link #lat}
     * @param lon  - see {@link #lon}
     */
    public Location(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    // only calculate as needed for distance
    private Double _latSin = null;

    private double latSin() {
        if (_latSin == null) {
            _latSin = Math.sin(deg2rad(this.lat));
        }
        return _latSin;
    }

    // only calculate as needed for distance
    private Double _latCos = null;

    private double latCos() {
        if (_latCos == null) {
            _latCos = Math.cos(deg2rad(this.lat));
        }
        return _latCos;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /** Gets if this location has latitude/longitude information. */
    public Boolean hasLocation() {
        return this.lat != NO_VALUE && this.lon != NO_VALUE;
    }

    /**
     * Get the distance in miles from this location to the given location.
     *
     * @param toLocation - locaiton to measure distance to
     */
    public double getDistanceInMiles(Location toLocation) {
        double theta = Math.cos(deg2rad(this.lon - toLocation.lon));
        double dist = this.latSin() * toLocation.latSin()
                + this.latCos() * toLocation.latCos() * theta;
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 0.8684; // get miles
        return dist;
    }

}
