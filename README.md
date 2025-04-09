# README

- [Overview](#overview)
- [Location autocomplete](#location-autocomplete)
- [What Code to Write](#what-code-to-write)
- [Building and Running](#building-and-running)

## OVERVIEW

Ever since Google introduced Google Suggest several years ago, a "suggest" or autocomplete feature has become standard on many search-related websites. As an example, see [indeed's what autocomplete](images/autocomplete.png).  
Your task is to implement part of the "suggest" component for the "where" field on indeed.com.

## LOCATION AUTOCOMPLETE

We have provided a skeleton Java program that currently returns a hard-coded list as the list of suggestions. Your job is to implement the functionality in *Suggester.java* to return a better set of suggestions given user input.

To help you build the list of suggestions, we have provided a log file containing the actual locations for about 440,000 actual user searches. The log file (`query.log`) contains one line per user search. Here's an example:

```txt
ip=24.116.26.171&iplat=33.585&iplon=-88.414&l=columbus+ms&match=Columbus%2C+MS&loclat=33.495&loclon=-88.423
```

There's also a sample log file (`sample_query.log`) with only 10 lines you can use for development/testing.

Each line is formatted like URL parameters, with name/value pairs separated by `&`, and URL encoded. The code for parsing is already written in *LogReader*. The callback method in *Suggester.java* is `processEntries`, and it receives a map of keys/values.

From the example line above, you’d get:

| Name   | Value           | Meaning                                 |
|--------|------------------|-----------------------------------------|
| ip     | 24.116.26.171    | The user's IP address                   |
| iplat  | 33.585           | Latitude for the user's IP              |
| iplon  | -88.414          | Longitude for the user's IP             |
| l      | columbus ms      | What the user typed                     |
| match  | Columbus, MS     | Formatted, matched location             |
| loclat | 33.495           | Latitude of the matched location        |
| loclon | -88.423          | Longitude of the matched location       |

You don't have to use every field.

## WHAT CODE TO WRITE

You’ll implement the methods `processEntries` and `getTopSuggestions`. Modify *Suggester.java* as needed, and create any helper classes or data structures.

Key priorities, in order:

1. The suggester gives useful suggestions.
2. It’s fast—called on every keystroke.
3. Memory usage is reasonable (512MB limit).
4. Fast startup/init is nice, but not critical.

Once done, write a brief explanation of your approach and improvements you’d consider in a `SOLUTION.txt`. The solution must be entirely your own—no 3rd party libraries or external code.

## BUIDLING AND RUNNING

To run the suggester:

```sh
./autocomplete.sh query.log
```

It will prompt for a prefix and display suggestions. You can use `sample_query.log` for testing.

**Note:** `autocomplete.sh` requires [Apache Ant](http://ant.apache.org/) to be installed and in your `PATH`.
