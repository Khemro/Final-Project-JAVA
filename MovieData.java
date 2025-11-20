import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieData {

    public static class Genre {
        public String prefix;
        public String name;
        public int menuNumber;

        public Genre(int menuNumber, String name, String prefix) {
            this.menuNumber = menuNumber;
            this.name = name;
            this.prefix = prefix;
        }
    }

    public static class Movie {
        public String genrePrefix;
        public String movieId;
        public String title;
        // Potentially add seats available, etc. here if needed for booking logic

        public Movie(String genrePrefix, String movieId, String title) {
            this.genrePrefix = genrePrefix;
            this.movieId = movieId;
            this.title = title;
        }
    }

    // Hardcoded genre mapping for consistent menu numbering
    private static final Map<Integer, Genre> MENU_NUMBER_TO_GENRE = new HashMap<>();
    static {
        MENU_NUMBER_TO_GENRE.put(1, new Genre(1, "Action", "AC"));
        MENU_NUMBER_TO_GENRE.put(2, new Genre(2, "Thriller", "TR"));
        MENU_NUMBER_TO_GENRE.put(3, new Genre(3, "Horror", "HR"));
        MENU_NUMBER_TO_GENRE.put(4, new Genre(4, "Funny", "FU"));
        MENU_NUMBER_TO_GENRE.put(5, new Genre(5, "Romantic", "RO"));
    }

    private final Map<String, Genre> genrePrefixToGenre = new HashMap<>();
    private final Map<String, Movie> movieIdToMovie = new HashMap<>();
    private final Map<String, List<Movie>> genrePrefixToMovies = new HashMap<>();

    public MovieData(String genresCsvPath, String moviesCsvPath) throws IOException {
        loadGenres(genresCsvPath);
        loadMovies(moviesCsvPath);
    }

    private void loadGenres(String genresCsvPath) throws IOException {
        // Populate genrePrefixToGenre map from the hardcoded mapping
        for (Genre genre : MENU_NUMBER_TO_GENRE.values()) {
            genrePrefixToGenre.put(genre.prefix, genre);
            genrePrefixToMovies.put(genre.prefix, new ArrayList<>()); // Initialize movie lists for each genre
        }

        // Read genres.csv to validate or ensure names are consistent
        // The prompt says "Read from genres.csv to get genre prefixes and names"
        // and "CRITICAL: Maintain consistent main menu numbering".
        // So, we prioritize the hardcoded menu numbers and ensure the names/prefixes match.
        try (BufferedReader br = new BufferedReader(new FileReader(genresCsvPath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String prefix = parts[0].trim();
                    String name = parts[1].trim();

                    // If a genre from CSV exists in our hardcoded map, update its name if necessary
                    if (genrePrefixToGenre.containsKey(prefix)) {
                        // We can choose to either strictly use the hardcoded name or update from CSV
                        // For consistency, let's ensure the hardcoded names are consistent with the CSV
                        // If they are not, we will log a warning but stick to the hardcoded name for the menu.
                        Genre hardcodedGenre = genrePrefixToGenre.get(prefix);
                        if (!hardcodedGenre.name.equals(name)) {
                            System.err.println("Warning: Genre name mismatch for prefix '" + prefix + "'. Hardcoded: '" + hardcodedGenre.name + "', CSV: '" + name + "'. Using hardcoded name for menu display.");
                        }
                    } else {
                        // This case implies a genre in CSV not in our fixed menu.
                        // Per requirement "Main Menu Numbering Requirement: The main genre selection menu must maintain consistent numbering: Option 1: Action ... These numbers must NOT change based on CSV file order or content",
                        // we ignore genres not in our fixed menu for display purposes, but still add them to our internal mapping if they exist
                        // For now, let's not add new genres to the menu from CSV if they are not hardcoded.
                        System.err.println("Warning: Genre prefix '" + prefix + "' from genres.csv not found in hardcoded menu mapping. Movies with this genre will be loaded, but the genre won't appear in the main selection menu.");
                        // However, we still need to make sure movies of this genre can be loaded, so we should add a placeholder to genrePrefixToGenre and genrePrefixToMovies
                        // This ensures getMoviesByGenrePrefix works for these "hidden" genres if needed elsewhere.
                        genrePrefixToGenre.put(prefix, new Genre(-1, name, prefix)); // -1 for menuNumber indicates not in main menu
                        genrePrefixToMovies.put(prefix, new ArrayList<>());
                    }
                }
            }
        }
    }

    private void loadMovies(String moviesCsvPath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(moviesCsvPath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String genrePrefix = parts[0].trim();
                    String movieId = parts[1].trim();
                    String title = parts[2].trim();

                    Movie movie = new Movie(genrePrefix, movieId, title);
                    movieIdToMovie.put(movieId, movie);

                    // Add movie to the list associated with its genre prefix
                    // Ensure the genre list exists, even for "hidden" genres from CSV
                    genrePrefixToMovies.computeIfAbsent(genrePrefix, k -> new ArrayList<>()).add(movie);
                }
            }
        }
    }

    public Map<Integer, Genre> getMenuNumberToGenreMapping() {
        return MENU_NUMBER_TO_GENRE;
    }

    public Genre getGenreByPrefix(String prefix) {
        return genrePrefixToGenre.get(prefix);
    }

    public List<Movie> getMoviesByGenrePrefix(String genrePrefix) {
        return genrePrefixToMovies.getOrDefault(genrePrefix, new ArrayList<>());
    }

    public Movie getMovieById(String movieId) {
        return movieIdToMovie.get(movieId);
    }
}
