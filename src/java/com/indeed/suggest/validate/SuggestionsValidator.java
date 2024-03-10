package com.indeed.suggest.validate;

import com.indeed.suggest.LogReader;
import com.indeed.suggest.Suggester;
import com.indeed.suggest.Location;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Does simple validation of location autocomplete using input file containing
 * prefixies
 *
 * @TODO - special characters like .,$ in input
 */
public class SuggestionsValidator {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Need to provide log file (query.log) and prefix input file as arguments");
            System.exit(-1);
        }
        final String logInputfile = args[0];

        final String prefixInputFile = args[1];

        long elapsed = -System.currentTimeMillis();

        Suggester suggester = new Suggester();
        LogReader logReader = new LogReader(logInputfile, suggester);
        logReader.read();

        elapsed += System.currentTimeMillis();

        System.out.println("Took " + (elapsed) + " millis to initialize ");

        BufferedReader reader = new BufferedReader(new FileReader(prefixInputFile));

        Location userLocation = null;

        try {
            String prefix = "";

            int numValid = 0;
            int total = 0;
            long totalElapsed = 0;
            while ((prefix = reader.readLine()) != null) {
                elapsed = -System.currentTimeMillis();
                final List<String> suggestions = suggester.getTopSuggestions(prefix, 5, userLocation);
                elapsed += System.currentTimeMillis();
                if (validate(prefix.toLowerCase(), suggestions)) {
                    numValid++;
                }
                total++;
            }

            double avgTime = ((double) totalElapsed / total);

            System.out.println(numValid + " valid suggestions out of " + total);
            System.out.println("Average req time " + avgTime);
        } finally {
            reader.close();
        }

    }

    private static boolean validate(String prefix, List<String> suggestions) {
        if (suggestions == null || suggestions.size() == 0) {
            System.err.println("Got empty output for prefix " + prefix);
            return false;
        }
        for (String sug : suggestions) {
            if (!sug.toLowerCase().startsWith(prefix)) {
                System.err.println("Suggestion " + sug + " did not start with prefix " + prefix);
                return false;
            }
        }
        return true;
    }
}
