package mcmaster_compsci2me3;

import java.util.Scanner;

public class ConsoleUI {
    private final RatingManager ratingManager;
    private final RecommendationEngine engine;

    public ConsoleUI(RatingManager ratingManager, RecommendationEngine engine) {
        this.ratingManager = ratingManager;
        this.engine = engine;
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Movie Recommendation System!");
        this.printMainMenu();
        while (true) {
            System.out.print("Enter selection here ('q' to quit or '4' to re-display main menu): ");
            try {
                String entry = sc.nextLine().trim();
                if (entry.equalsIgnoreCase("q"))
                    break;
                if (entry.equals("4")) {
                    this.printMainMenu();
                    continue;
                }

                switch (Integer.parseInt(entry)) {
                    case 1 -> this.promptUserToRateMovie(sc);
                    case 2 -> this.displayMovieCatalogToUser();
                    case 3 -> this.displayRecommendationsToUser();
                    default -> throw new RuntimeException(
                            "Invalid selection. Please choose 1-3 or '4' to display the menu");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid integer!");
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        sc.close();
        System.out.println("Exiting application...");
    }

    private void promptUserToRateMovie(Scanner sc) {
        while (true) {
            try {
                System.out.print("Which movie would you like to rate? (Enter movie id or 'q' to quit):");
                String firstEntry = sc.nextLine().trim();

                if (firstEntry.equalsIgnoreCase("q"))
                    return;

                int id = Integer.parseInt(firstEntry);
                if (!this.ratingManager.isMovieIdValid(id))
                    throw new RuntimeException(
                            "Invalid movie id provided by user, choose a movie in the catalog to rate");

                System.out.print("How would you rate this movie? (Enter 1-5 (inclusive) for rating or 'q' to quit):");
                String secondEntry = sc.nextLine().trim();

                if (secondEntry.equalsIgnoreCase("q"))
                    return;

                int rating = Integer.parseInt(secondEntry);
                if (!this.ratingManager.isMovieRatingValid(rating))
                    throw new RuntimeException(
                            "Invalid movie rating provided by user, provide a rating between 1-5 (inclusive)");

                this.ratingManager.rateMovie(id, rating);
                System.out.println("INFO: Rating saved");
                return;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid integer!");
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void displayRecommendationsToUser() {
        System.out.println("INFO: Generating movie recommendations...");
        int movieNumber = 1;
        for (Movie movie : this.engine.generateMovieRecommendations()) {
            StringBuilder sb = new StringBuilder();
            sb.append(movieNumber++).append(") ");
            sb.append(movie.getTitle());
            sb.append(" (").append(movie.getYear()).append(") ");
            sb.append("directed by ").append(movie.getDirector()).append(", Genres: ");
            for (Movie.Genre g : movie.getGenres()) {
                sb.append(g.toString().toLowerCase()).append(" ");
            }
            System.out.println(sb.toString());
        }
    }

    private void displayMovieCatalogToUser() {
        for (Movie movie : this.engine.getMovieCatalog()) {
            System.out.println(movie);
        }
    }

    private void printMainMenu() {
        System.out.println("Enter '1' to rate a movie");
        System.out.println("Enter '2' to display movie catalog");
        System.out.println("Enter '3' to generate movie recommendations");
    }
}
