import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

public class Display_Menu {
    private MovieData movieData;
    private Scanner userInput;
    private BookingTickets bookingTickets; // To initiate booking after selection

    public Display_Menu(MovieData movieData, Scanner userInput, BookingTickets bookingTickets) {
        this.movieData = movieData;
        this.userInput = userInput;
        this.bookingTickets = bookingTickets;
    }

    public void showMainMenu() {
        System.out.println("\n=== TICKET RESERVATION SYSTEM ===");
        System.out.println("1. Browse and Book Ticket for Movies");
        System.out.println("2. View My Reservations");
        System.out.println("3. Cancel Reservation");
        System.out.println("4. Exit");
    }

    public void displayGenreAndMovieSelection() {
        boolean browseMore = true;
        while (browseMore) { // Loop for browsing more movies
            int genreChoice = -1;
            MovieData.Genre selectedGenre = null;

            while (selectedGenre == null) {
                System.out.println("\n=== MOVIE GENRES ===");
                for (Map.Entry<Integer, MovieData.Genre> entry : movieData.getMenuNumberToGenreMapping().entrySet()) {
                    System.out.printf("%d. %s (%s)\n", entry.getKey(), entry.getValue().name, entry.getValue().prefix);
                }
                System.out.println("6. Back to Main Menu"); // New option
                System.out.println("-------------------------");
                System.out.print("Select genre: ");

                try {
                    genreChoice = userInput.nextInt();
                    userInput.nextLine(); // Consume newline

                    if (genreChoice == 6) { // User chose to go back to main menu
                        browseMore = false; // Set flag to exit outer loop
                        return; // Exit this method, returning to main menu loop
                    }

                    selectedGenre = movieData.getMenuNumberToGenreMapping().get(genreChoice);

                    if (selectedGenre == null) {
                        System.out.println("Invalid genre choice. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    userInput.nextLine(); // Consume the invalid input
                }
            }

            System.out.println("\n=== " + selectedGenre.name.toUpperCase() + " MOVIES ===");
            List<MovieData.Movie> moviesInGenre = movieData.getMoviesByGenrePrefix(selectedGenre.prefix);

            if (moviesInGenre.isEmpty()) {
                System.out.println("No movies found in this genre.");
                // Option to go back to genre selection or main menu
                System.out.print("No movies found in this genre. Do you want to select another genre? (yes/no): ");
                String choice = userInput.nextLine();
                if (!choice.equalsIgnoreCase("yes")) {
                    browseMore = false; // Exit browsing loop
                }
                continue; // Continue to next iteration of while (browseMore)
            }

            for (MovieData.Movie movie : moviesInGenre) {
                System.out.printf("%s: %s\n", movie.movieId, movie.title);
            }

            String movieChoiceId = null;
            MovieData.Movie selectedMovie = null;

            while (selectedMovie == null) {
                System.out.println("-------------------------");
                System.out.print("Select movie (e.g., " + moviesInGenre.get(0).movieId + ") or type 'back' to choose another genre: ");
                movieChoiceId = userInput.nextLine().trim().toUpperCase();

                if (movieChoiceId.equalsIgnoreCase("BACK")) {
                    selectedGenre = null; // Reset selected genre to go back to genre selection
                    break; // Break from movie selection loop to re-enter genre selection loop
                }

                selectedMovie = movieData.getMovieById(movieChoiceId);

                if (selectedMovie == null || !selectedMovie.genrePrefix.equals(selectedGenre.prefix)) {
                    System.out.println("Invalid movie ID for the selected genre. Please try again.");
                    selectedMovie = null; // Reset to loop again
                }
            }
            
            // If the user typed 'back', selectedMovie will be null, and we continue the main loop
            if (selectedMovie == null) {
                continue; 
            }

            // Now that a movie is selected, initiate the booking process
            // bookingTickets.bookMovie now returns a boolean
            browseMore = bookingTickets.bookMovie(selectedMovie);
        }
    }
}
