package mcmaster_compsci2me3;

import java.util.List;
import java.nio.file.Path;
import mcmaster_compsci2me3.*;

public class MovieRecommendationSystem {
    public static void main(String[] args) {
        try {
            FileHandler fileHandler = new FileHandler(Path.of("data/movies.txt"), Path.of("data/my_ratings.txt"));
            List<Movie> movieCatalog = fileHandler.fetchMovieCatalog();

            RatingManager ratingManager = new RatingManager(movieCatalog, fileHandler);
            RecommendationEngine engine = new RecommendationEngine(movieCatalog, fileHandler);

            new ConsoleUI(ratingManager, engine).start();
        } catch (RuntimeException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            if (ex.getCause() != null) {
                System.out.println("Caused by: " + ex.getCause());
            }
            System.exit(-1);
        }
    }
}