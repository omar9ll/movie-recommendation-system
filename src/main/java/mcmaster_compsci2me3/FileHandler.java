package mcmaster_compsci2me3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileHandler {
    private final Path movieCatalogPath;
    private final Path userRatingPath;

    public FileHandler(Path movieCatalogPath, Path userRatingPath) {
        this.movieCatalogPath = movieCatalogPath;
        this.userRatingPath = userRatingPath;

        if (!Files.exists(this.movieCatalogPath)) {
            throw new RuntimeException(
                    "movies.txt does not exist in data directory! Exiting application...");
        }
        if (!Files.exists(this.userRatingPath)) {
            try {
                Files.writeString(this.userRatingPath, "movie_id,rating,timestamp\n", StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW);
            } catch (IOException ex) {
                throw new RuntimeException(
                        "Unable to create new file my_ratings.txt within data directory!", ex);
            }
            System.out.println("INFO: Cold start detected... Created new my_ratings.txt file");
        }
    }

    public Map<Integer, Integer> fetchUserRatings() {
        Map<Integer, Integer> userRatings = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(this.userRatingPath, StandardCharsets.UTF_8)) {
            reader.readLine(); // Skip my_ratings.txt header that simply outlines the format of the data file
            String line = null;

            while ((line = reader.readLine()) != null) {
                String[] ratingData = line.trim().split(",");

                try {
                    if (ratingData.length != 3) {
                        throw new RuntimeException("Missing user rating information");
                    }

                    int id = Integer.parseUnsignedInt(ratingData[0].trim());
                    int rating = Integer.parseUnsignedInt(ratingData[1].trim());
                    userRatings.put(id, rating);
                } catch (Exception e) {
                    System.out.println(
                            "WARNING: Error parsing the following record: [" + line + "]. Error msg: (" + e.getMessage()
                                    + "). Ignoring this user rating");
                    return null;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unable to fetch user ratings from my_ratings.txt!");
        }

        return userRatings;
    }

    public List<Movie> fetchMovieCatalog() {
        Set<Integer> movieCatalogIds = new HashSet<>();
        List<Movie> movieCatalog = new ArrayList<>();
        int numberOfRecordsFiltered = 0;
        try (BufferedReader reader = Files.newBufferedReader(this.movieCatalogPath, StandardCharsets.UTF_8)) {
            reader.readLine(); // Skip movies.txt header that simply outlines the format of the data file
            String line = null;

            while ((line = reader.readLine()) != null) {
                Movie movie = parseMovieRecord(line);
                if (movie != null && !movieCatalogIds.contains(movie.getId())) {
                    movieCatalog.add(movie);
                    movieCatalogIds.add(movie.getId());
                } else {
                    ++numberOfRecordsFiltered;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unable to fetch movie catalog from movies.txt!");
        }

        if (movieCatalog.size() == 0) {
            throw new RuntimeException("No movie data available!");
        }

        System.out.println("INFO: movies.txt successfully parsed - " + movieCatalog.size() + " total records found ("
                + numberOfRecordsFiltered + " records filtered out)");
        return movieCatalog;
    }

    public Movie parseMovieRecord(String line) {
        String[] movieData = line.trim().split(",");

        try {
            if (movieData.length != 7) {
                throw new RuntimeException("Missing movie information");
            }

            int id = Integer.parseUnsignedInt(movieData[0].trim());

            String title = movieData[1].trim();
            if (title.isEmpty()) {
                throw new RuntimeException("Movie title is missing");
            }

            String director = movieData[2].trim();
            if (director.isEmpty()) {
                throw new RuntimeException("Movie director is missing");
            }

            int year = Integer.parseUnsignedInt(movieData[3].trim());

            String genres = movieData[4].trim();
            if (genres.isEmpty()) {
                throw new RuntimeException("Movie genres are missing");
            }

            double averageRating = Double.parseDouble(movieData[5].trim());
            if (averageRating < 0.0) {
                throw new RuntimeException("Movie average rating cannot be negative");
            }

            int numberOfRatings = Integer.parseUnsignedInt(movieData[6].trim());

            return new Movie(id, title, director, year, genres, averageRating, numberOfRatings);
        } catch (NumberFormatException e) {
            System.out.println(
                    "WARNING: Error parsing the following record: [" + line
                            + "]. Incorrect data type used for movie data (" + e.getMessage()
                            + "). Proceeding without this data");
        } catch (RuntimeException e) {
            System.out.println(
                    "WARNING: Error parsing the following record: [" + line + "]. Error msg: (" + e.getMessage()
                            + "). Proceeding without this data");
        }
        return null;
    }

    void addNewRating(int id, int rating) {
        try (BufferedWriter writer = Files.newBufferedWriter(this.userRatingPath, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            String newLine = String.format("%d,%d,%s\n", id, rating, LocalDate.now());
            writer.write(newLine);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to update existing rating in my_ratings.txt!");
        }
    }

    void updateExistingRating(int id, int rating) {
        try (BufferedReader reader = Files.newBufferedReader(this.userRatingPath, StandardCharsets.UTF_8)) {
            List<String> lines = Files.readAllLines(this.userRatingPath, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); ++i) {
                String[] userRatingData = lines.get(i).trim().split(",");
                if (Integer.parseInt(userRatingData[0]) == id) {
                    String newUserRatingData = String.format("%d,%d,%s", id, rating, LocalDate.now());
                    lines.set(i, newUserRatingData);
                    break;
                }
            }

            Files.write(this.userRatingPath, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to update existing rating in my_ratings.txt!");
        }
    }
}
