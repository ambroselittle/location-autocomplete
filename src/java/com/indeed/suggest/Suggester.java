package com.indeed.suggest;

import com.indeed.suggest.Location;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Used for performing keyword and distance searches against a set of locations.
 */
final class LocationSearch {
    // we use a treemap to sort by name, alphabetical, which should have the effect
    // of matching starts with first
    // secondarily, if user location is supplied, it provides a rational secondary
    // ordering if any location is exactly equal in distance
    private TreeMap<String, Location> locations = new TreeMap<String, Location>();
    private static Pattern cleaner = Pattern.compile("[^\\w]"); // used to strip out to just word chars

    /**
     * Format the input for use in comparing/searching word values.
     *
     * @param input - string to format
     * @return formatted string
     */
    private static String formatComparer(String input) {
        if (input == null) {
            return "";
        }
        return cleaner.matcher(input).replaceAll("").toLowerCase();
    }

    /**
     * Get the total number of unique locations discovered.
     *
     * @return Number of unique locations.
     */
    public int locationCount() {
        return this.locations.size();
    }

    /**
     * Extract location from given log entry.
     *
     * @param logEntry - log entry from log file
     */
    public void addLocation(Map<String, String> logEntry) {
        Location loc = new Location(logEntry);
        if (loc.name == "") {
            return; // ignore locations with no name
        }
        // we'll use a word-only, lowercase key to search
        String locKey = formatComparer(loc.name);
        if (this.locations.containsKey(locKey)) {
            return; // we only need to keep track of a location 1x
        }
        this.locations.put(locKey, loc);
    }

    /**
     * Search the discovered locations for a match.
     *
     * @param searchString - string to match
     * @param max          - maximum number of matches to return
     * @return list of matches up to the max
     */
    public List<Location> search(String searchString, int max) {
        ArrayList<Location> found = new ArrayList<Location>(500);
        String tester = formatComparer(searchString);
        for (Map.Entry<String, Location> entry : this.locations.entrySet()) {
            String nameMatcher = entry.getKey();
            if (nameMatcher.contains(tester)) {
                found.add(entry.getValue());
                if (found.size() >= max) {
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Search the discovered locations for a match, ordered by proximity to given
     * user location.
     *
     * @param searchString - string to match
     * @param max          - maximum number of matches to return
     * @param userLocation - location of current user, to use for proximity
     *                     comparison
     * @return list of matches up to the max, ordered by proximity to user
     */
    public List<Location> search(String searchString, int max, Location userLocation) {
        // get all matching locations
        List<Location> allMatches = this.search(searchString, Integer.MAX_VALUE);
        // calculate the distances and sort using tree map's key sorting
        TreeMap<Double, Location> byDistance = new TreeMap<Double, Location>();
        for (Location matchedLocation : allMatches) {
            // we have to calc distance for all matches so we can properly sort
            double distance = userLocation.getDistanceInMiles(matchedLocation);
            byDistance.put(distance, matchedLocation);
        }
        // get the requested top N locations out of the now sorted by distance matches
        return byDistance.entrySet().stream().limit(max)
                .map(entry -> entry.getValue()).collect(Collectors.toList());
    }
}

public class Suggester implements LogEntryProcessor {
    private LocationSearch searcher = new LocationSearch();

    public Suggester() {
    }

    /*
     * @param logEntryMap - Map containing keys and values in each log line
     * This is a call back method that LogReader.read() calls on every line in the
     * input file, after parsing them into key-value pairs
     */
    public void processEntries(Map<String, String> logEntryMap) {
        this.searcher.addLocation(logEntryMap);
    }

    /**
     * Returns a list of suggestions for a given user query.
     *
     * @param query the string that the user has typed so far
     * @param max   the maximum number of suggestions requested
     */
    public List<String> getTopSuggestions(String query, int max, Location userLocation) {
        List<Location> locations = userLocation != null ? this.searcher.search(query, max, userLocation)
                : this.searcher.search(query, max);
        return locations.stream()
                .map(l -> l.name)
                .collect(Collectors.toList());
    }

    // main() for command-line testing
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Need to provide log file (query.log) as argument");
        }
        final String inputFile = args[0];
        Suggester suggester = new Suggester();
        LogReader logReader = new LogReader(inputFile, suggester);

        long elapsedTime = -System.currentTimeMillis();
        logReader.read();
        elapsedTime += System.currentTimeMillis();

        System.out.println(elapsedTime + "ms to read file");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        Location userLocation = null;
        if (args.length >= 3) {
            final String inLat = args[1];
            final String inLong = args[2];
            try {
                userLocation = new Location("User", Double.parseDouble(inLat), Double.parseDouble(inLong));
                if (!userLocation.hasLocation()) {
                    userLocation = null;
                }
            } catch (NumberFormatException formatEx) {
                System.err.print(
                        String.format("Invalid latitude '%s' or longitude '%s'. Must be doubles.", inLat, inLong));
                return;
            }
        }

        try {
            System.out.println("Type 'quit' or 'exit' when you're done.");
            while (true) {
                System.out.print("query> ");
                String line = in.readLine();
                if ("".equals(line))
                    continue;
                if (line == null || "quit".equals(line) || "exit".equals(line))
                    break;

                elapsedTime = -System.currentTimeMillis();
                List<String> suggestions = suggester.getTopSuggestions(line, 10, userLocation);
                elapsedTime += System.currentTimeMillis();

                System.out
                        .println("Suggestions for '" + line + "' " + suggestions + " fetched in " + elapsedTime + "ms");
            }
            System.out.println();
        } finally {
            in.close();
        }
    }
}
