package mcmaster_compsci2me3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendationEngine {
    private static final int HIGH_RATING_THRESHOLD = 4;
    private static final int MAX_RECOMMENDATIONS = 5;

    private final FileHandler fileHandler;
    private final List<Movie> movieCatalog;

    public RecommendationEngine(List<Movie> movieCatalog, FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.movieCatalog = movieCatalog;
    }

    public List<Movie> getMovieCatalog() {
        return this.movieCatalog;
    }

    public List<Movie> generateMovieRecommendations() {
        Map<Integer, Integer> userRatings = fileHandler.fetchUserRatings();

        if (userRatings.size() >= movieCatalog.size()) {
            throw new RuntimeException("User has rated more movies than exist in the catalog - unable to generate recommendations.");
        }

        if (userRatings == null || userRatings.isEmpty()) {
            return getPopularMovies();
        }

        Set<Integer> ratedMovieIds = new HashSet<>(userRatings.keySet());

        Set<Integer> highlyRatedMovieIds = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : userRatings.entrySet()) {
            int rating = entry.getValue();
            if (rating >= HIGH_RATING_THRESHOLD) {
                highlyRatedMovieIds.add(entry.getKey());
            }
        }

        if (highlyRatedMovieIds.isEmpty()) {
            return getPopularMoviesExcluding(ratedMovieIds);
        }

        Map<Movie.Genre, Integer> preferredGenres = new HashMap<>();
        Map<String, Integer> preferredDirectors = new HashMap<>();

        for (int movieId : highlyRatedMovieIds) {
            Movie m = findMovieById(movieId);
            if (m == null) {
                continue;
            }

            for (Movie.Genre g : m.getGenres()) {
                preferredGenres.put(g, preferredGenres.getOrDefault(g, 0) + 1);
            }

            String director = m.getDirector();
            preferredDirectors.put(director, preferredDirectors.getOrDefault(director, 0) + 1);
        }

        if (preferredGenres.isEmpty() && preferredDirectors.isEmpty()) {
            return getPopularMoviesExcluding(ratedMovieIds);
        }

        Map<Movie, Double> scores = new HashMap<>();
        for (Movie candidate : movieCatalog) {
            if (ratedMovieIds.contains(candidate.getId())) {
                continue;
            }

            double score = computeSimilarityScore(candidate, preferredGenres, preferredDirectors);
            if (score > 0.0) {
                scores.put(candidate, score);
            }
            
            // Display similarity score to 2 decimal places
            // System.out.println("INFO: Similarity score for movie [" + candidate.getTitle() + "] is " + String.format("%.2f", score));
        }

        List<Movie> ranked = new ArrayList<>(scores.keySet());
        ranked.sort((m1, m2) -> {
            double s1 = scores.get(m1);
            double s2 = scores.get(m2);

            int cmp = Double.compare(s2, s1);
            if (cmp != 0) {
                return cmp;
            }

            cmp = Double.compare(m2.getAverageRating(), m1.getAverageRating());
            if (cmp != 0) {
                return cmp;
            }

            cmp = Integer.compare(m2.getNumberOfRatings(), m1.getNumberOfRatings());
            if (cmp != 0) {
                return cmp;
            }

            return m1.getTitle().compareToIgnoreCase(m2.getTitle());
        });

        if (ranked.size() > MAX_RECOMMENDATIONS) {
            return ranked.subList(0, MAX_RECOMMENDATIONS);
        }
        return ranked;
    }

    private List<Movie> getPopularMovies() {
        List<Movie> copy = new ArrayList<>(movieCatalog);

        copy.sort((m1, m2) -> {
            int cmp = Double.compare(m2.getAverageRating(), m1.getAverageRating());
            if (cmp != 0) {
                return cmp;
            }

            cmp = Integer.compare(m2.getNumberOfRatings(), m1.getNumberOfRatings());
            if (cmp != 0) {
                return cmp;
            }

            return m1.getTitle().compareToIgnoreCase(m2.getTitle());
        });

        if (copy.size() > MAX_RECOMMENDATIONS) {
            return copy.subList(0, MAX_RECOMMENDATIONS);
        }
        return copy;
    }

    private List<Movie> getPopularMoviesExcluding(Set<Integer> excludeIds) {
        List<Movie> filtered = new ArrayList<>();

        for (Movie m : movieCatalog) {
            if (!excludeIds.contains(m.getId())) {
                filtered.add(m);
            }
        }

        filtered.sort((m1, m2) -> {
            int cmp = Double.compare(m2.getAverageRating(), m1.getAverageRating());
            if (cmp != 0) {
                return cmp;
            }

            cmp = Integer.compare(m2.getNumberOfRatings(), m1.getNumberOfRatings());
            if (cmp != 0) {
                return cmp;
            }

            return m1.getTitle().compareToIgnoreCase(m2.getTitle());
        });

        if (filtered.size() > MAX_RECOMMENDATIONS) {
            return filtered.subList(0, MAX_RECOMMENDATIONS);
        }
        return filtered;
    }

    private Movie findMovieById(int movieId) {
        for (Movie m : movieCatalog) {
            if (m.getId() == movieId) {
                return m;
            }
        }
        return null;
    }

    private double computeSimilarityScore(Movie candidate,
            Map<Movie.Genre, Integer> preferredGenres,
            Map<String, Integer> preferredDirectors) {
        double score = 0;
        int numberOfGenres = candidate.getGenres().size();
        int numberOfMoviesWithHighlyRatedDirectors = preferredDirectors.values().stream().mapToInt(Integer::intValue).sum();
        int numberOfGenresPreferred = preferredGenres.values().stream().mapToInt(Integer::intValue).sum();

        for (Movie.Genre g : candidate.getGenres()) {
            if (preferredGenres.containsKey(g)) {
                score += (preferredGenres.get(g) / (double) numberOfGenresPreferred) * (1.0 / numberOfGenres);
            }
        }

        String director = candidate.getDirector();
        if (preferredDirectors.containsKey(director)) {
            score += (preferredDirectors.get(director) / (double) numberOfMoviesWithHighlyRatedDirectors);
        }

        return score;
    }
}
