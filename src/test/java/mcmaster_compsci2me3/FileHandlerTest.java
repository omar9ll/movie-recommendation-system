package mcmaster_compsci2me3;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class FileHandlerTest {
    static private FileHandler filehandler;

    @BeforeAll
    static void setUp() {
        filehandler = new FileHandler(Path.of("src/test/data/movies.txt"), Path.of("src/test/data/my_ratings.txt"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "The Shawshank Redemption,Frank Darabont,1994,Drama,4.9,2847", // missing movie id
            "2,The Godfather,Francis Ford Coppola,Crime;Drama,4.8,2156", // missing year
            "3,The Dark Knight,Christopher Nolan,200rewf8,Action;Crime;Drama,4.7,3421", // invalid year
            "4,Pulp Fiction,Quentin Tarantino,1994,Crime;Drama,NO_RATING,2698", // invalid average rating
            "5,Forrest Gump,Robert Zemeckis,1994,,4.5,2134", // missing genres
            "6,Inception,Christopher Nolan,2010,NO_VALID_GENRE,4.7,3156", // invalid genre
            "7,The Matrix,The Wachowskis,1999,;;;;,4.6,2876", // missing genres (only delimiters)
            "8,Goodfellas,Martin Scorsese,1990,Biography;Crime;Drama,4.7,-1234", // negative number of ratings
            "9,The Silence of the Lambs,Jonathan Demme,1991,Crime;Drama;Thriller,-2.4234223,1876", // negative average rating
            "10.234,Saving Private Ryan,Steven Spielberg,1998,Drama;War,4.7,1654",  // invalid movie id
            "11,The Lord of the Rings: The Return of the King,Peter Jackson,2003,Adventure;Dr4m4;Fantasy,4.8,2234", // invalid genre
            })
    void validateMovieParsingLogic(String input) {
        Movie movie = assertDoesNotThrow(() -> filehandler.parseMovieRecord(input));
        assertNull(movie, "Movie should be null for invalid input: " + input);
    }

    @Test
    void validateMyRatingsCreationDuringColdStart(@TempDir Path tempDir) throws Exception {
        Path moviesPath = Path.of("src/test/data/movies.txt"); 
        Path myRatingsPath = tempDir.resolve("my_ratings.txt"); 
        FileHandler fileHandler = new FileHandler(moviesPath, myRatingsPath);

        assertTrue(Files.exists(myRatingsPath), "my_ratings.txt file should be created if cold starting");

        String header = Files.readString(myRatingsPath);
        assertEquals("movie_id,rating,timestamp", header.trim());
    }

    @Test
    void validateNonExistentMoviesFileThrowsException(@TempDir Path tempDir) {
        Path nonExistentMoviesPath = tempDir.resolve("non_existent_movies.txt");
        Path myRatingsPath = tempDir.resolve("my_ratings.txt");

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            new FileHandler(nonExistentMoviesPath, myRatingsPath);
        });

        assertEquals("movies.txt does not exist in data directory! Exiting application...", exception.getMessage());
    }

    @Test
    void validateAddNewRatingToUserRatingsFile(@TempDir Path tempDir) throws Exception {
        Path moviesPath = Path.of("src/test/data/movies.txt"); 
        Path myRatingsPath = tempDir.resolve("my_ratings.txt"); 
        FileHandler fileHandler = new FileHandler(moviesPath, myRatingsPath);

        LocalDate today = LocalDate.now();
        fileHandler.addNewRating(1, 5);
        fileHandler.addNewRating(2, 4);

        String[] lines = Files.readString(myRatingsPath).trim().split("\n");
        assertEquals(3, lines.length, "my_ratings.txt should contain 3 lines (1 header + 2 ratings)");

        assertEquals("movie_id,rating,timestamp", lines[0].trim(), "Header line is incorrect");
        assertTrue(lines[1].startsWith("1,5," + today), "First rating line is incorrect");
        assertTrue(lines[2].startsWith("2,4," + today), "Second rating line is incorrect");
    }
}
