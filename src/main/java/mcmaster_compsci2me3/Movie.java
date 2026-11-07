package mcmaster_compsci2me3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Movie {
    private final int id;
    private final String title;
    private final String director;
    private final int year;
    private final List<Genre> genres;
    private final double averageRating;
    private final int numberOfRatings;
    static final Map<String, Genre> movieGenreStringToEnumMap = Map.ofEntries(
            Map.entry("action", Genre.ACTION),
            Map.entry("adventure", Genre.ADVENTURE),
            Map.entry("animation", Genre.ANIMATION),
            Map.entry("biography", Genre.BIOGRAPHY),
            Map.entry("comedy", Genre.COMEDY),
            Map.entry("crime", Genre.CRIME),
            Map.entry("drama", Genre.DRAMA),
            Map.entry("family", Genre.FAMILY),
            Map.entry("fantasy", Genre.FANTASY),
            Map.entry("history", Genre.HISTORY),
            Map.entry("horror", Genre.HORROR),
            Map.entry("mystery", Genre.MYSTERY),
            Map.entry("romance", Genre.ROMANCE),
            Map.entry("sci-fi", Genre.SCI_FI),
            Map.entry("sport", Genre.SPORT),
            Map.entry("thriller", Genre.THRILLER),
            Map.entry("war", Genre.WAR),
            Map.entry("western", Genre.WESTERN),
            Map.entry("musical", Genre.MUSICAL),
            Map.entry("music", Genre.MUSIC));

    public Movie(int id, String title, String director, int year, String genres,
            double averageRating, int numberOfRatings) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
        this.genres = createGenreList(genres);
        this.averageRating = averageRating;
        this.numberOfRatings = numberOfRatings;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getYear() {
        return year;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    private List<Genre> createGenreList(String genres) {
        String[] genreTokens = genres.split(";");
        List<Genre> genreList = new ArrayList<>();

        for (String token : genreTokens) {
            String genreKey = token.trim().toLowerCase();

            if (genreKey.isEmpty()) {
                throw new RuntimeException("missing genre");
            }
            if (!movieGenreStringToEnumMap.containsKey(genreKey)) {
                throw new RuntimeException("unknown genre: " + genreKey);
            }
            genreList.add(movieGenreStringToEnumMap.get(genreKey));
        }

        if (genreList.isEmpty()) {
            throw new RuntimeException("movie genres are missing");
        }

        return genreList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{id: ").append(this.id)
                .append(", title: ").append(this.title)
                .append(", director: ").append(this.director)
                .append(", year: ").append(this.year)
                .append(", genres: [");

        for (int i = 0; i < this.genres.size(); i++) {
            sb.append(this.genres.get(i));
            if (i < this.genres.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}");
        return sb.toString();
    }

    public enum Genre {
        ACTION,
        ADVENTURE,
        BIOGRAPHY,
        COMEDY,
        DRAMA,
        HORROR,
        HISTORY,
        ROMANCE,
        SCI_FI,
        FANTASY,
        THRILLER,
        MYSTERY,
        DOCUMENTARY,
        ANIMATION,
        CRIME,
        FAMILY,
        MUSICAL,
        WAR,
        WESTERN,
        SPORT,
        MUSIC
    }
}
