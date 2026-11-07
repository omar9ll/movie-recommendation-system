package mcmaster_compsci2me3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RatingManager {
    private final FileHandler fileHandler;
    private final Set<Integer> movieCatalogIds;
    private final Set<Integer> ratedMoviesIds;

    public RatingManager(List<Movie> movieCatalog, FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.movieCatalogIds = populateMovieCatalogIds(movieCatalog);
        this.ratedMoviesIds = new HashSet<>(this.fileHandler.fetchUserRatings().keySet()); // Required if this isn't a
                                                                                           // cold start
    }

    public void rateMovie(int id, int rating) {
        if (this.ratedMoviesIds.contains(id)) {
            this.fileHandler.updateExistingRating(id, rating);
        } else {
            this.fileHandler.addNewRating(id, rating);
            this.ratedMoviesIds.add(id);
        }
    }

    public boolean isMovieIdValid(int id) {
        if (this.movieCatalogIds.contains(id))
            return true;
        return false;
    }

    public boolean isMovieRatingValid(int rating) {
        if (rating >= 1 && rating <= 5)
            return true;
        return false;
    }

    Set<Integer> populateMovieCatalogIds(List<Movie> movieCatalog) {
        Set<Integer> movieCatalogIds = new HashSet<>();
        for (Movie movie : movieCatalog) {
            movieCatalogIds.add(movie.getId());
        }
        return movieCatalogIds;
    }
}
