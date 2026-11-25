package com.joysistvi.recordingapp.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt;

public class Main {
    static String url = "jdbc:mysql://localhost:3306/recording_studio_db";
    static String username = "root";
    static String password = "";s

    public static void main(String[] args) {

        // Albums guard clauses
        createAlbum("   ", 2024, 1);
        createAlbum("Valid Name", 1800, 1);
        createAlbum("Valid Name", 2024, 0);
        updateAlbum(0, "Album", 2024, 1);
        deleteAlbum(-1);
        archiveAlbum(0);
        restoreAlbum(0);
        searchAlbum("   ");

        // Artists guard clauses
        createArtist(null);
        updateArtist(0, "Artist");
        updateArtist(1, "   ");
        deleteArtist(0);
        archiveArtist(-5);
        restoreArtist(0);
        searchArtist("");


        createUser("", "secret123", "valid@mail.com");
        createUser("jdoe", "secret123", "invalid-email");
        createUser("jdoe", "123", "jdoe@example.com");
        loginUser("   ", "secret123");
        loginUser("jdoe", "");

        System.out.println("\n=== 2. TESTING VALID OPERATIONS ===");
        createArtist("IV of Spades");
        searchArtist("Spades");
        createUser("testuser_" + System.currentTimeMillis(), "secret123", "test@example.com");
        loginUser("jdoe", "secret123");

        //viewAllAlbums();
        //createAlbum("Coco", 1990, 1);
        //viewAllArtists();
        //createArtist("IV of Spades");
        //updateAlbum(6, "Coco (Remastered)", 1995, 1);
        //deleteAlbum(14);
        //updateArtist(6, "IV Of Spades");
        //deleteArtist(15);
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void viewAllAlbums() {
        String query = "SELECT albums.id, albums.name, albums.year, artists.name AS artist_name " +
                "FROM albums " +
                "JOIN artists " +
                "ON albums.artist_id = artists.id";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)
        ) {
            System.out.println("\n==================== Albums ========================");
            System.out.printf("%-4s %-20s %-8s %-20s%n", "ID", "Name", "Year", "Artist");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int year = resultSet.getInt("year");
                String artistName = resultSet.getString("artist_name");

                System.out.printf("%-4d %-20s %-8d %-20s%n", id, name, year, artistName);
            }
        } catch (SQLException e) {
            System.out.println("Read Albums: " + e.getMessage());
        }
    }

    public static void createAlbum(String name, int year, int artistId) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Album name is required.");
            return;
        }
        if (year < 1900 || year > 2100) {
            System.out.println("\nValidation Error: Release year must be between 1900 and 2100.");
            return;
        }
        if (artistId <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

        String query = "INSERT INTO albums (name, year, artist_id) " +
                "VALUES (?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, name.trim());
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, artistId);

            preparedStatement.executeUpdate();

            System.out.println("Album: " + name + " created successfully!");
            viewAllAlbums();
        } catch (SQLException e) {
            System.out.println("Create Album: " + e.getMessage());
        }
    }

    public static void updateAlbum(int id, String name, int year, int artistId) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Album name is required.");
            return;
        }
        if (year < 1900 || year > 2100) {
            System.out.println("\nValidation Error: Release year must be between 1900 and 2100.");
            return;
        }
        if (artistId <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

        String sql = "UPDATE albums SET name = ?, year = ?, artist_id = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name.trim());
            pstmt.setInt(2, year);
            pstmt.setInt(3, artistId);
            pstmt.setInt(4, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " updated successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Update Album: " + e.getMessage());
        }
    }

    public static void deleteAlbum(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return;
        }

        String sql = "DELETE FROM albums WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " deleted successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Delete Album: " + e.getMessage());
        }
    }

    public static void archiveAlbum(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return;
        }

        String sql = "UPDATE albums SET is_archived = 1 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " archived successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Archive Album: " + e.getMessage());
        }
    }

    public static void restoreAlbum(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return;
        }

        String sql = "UPDATE albums SET is_archived = 0 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " restored successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Restore Album: " + e.getMessage());
        }
    }

    public static void searchAlbum(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("\nValidation Error: Search keyword is required.");
            return;
        }

        String sql = "SELECT albums.id, albums.name, albums.year, artists.name AS artist_name " +
                "FROM albums " +
                "JOIN artists ON albums.artist_id = artists.id " +
                "WHERE albums.name LIKE ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.trim() + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n================ Search Albums =====================");
            System.out.printf("%-4s %-20s %-8s %-20s%n", "ID", "Name", "Year", "Artist");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int year = rs.getInt("year");
                String artistName = rs.getString("artist_name");

                System.out.printf("%-4d %-20s %-8d %-20s%n", id, name, year, artistName);
            }
        } catch (SQLException e) {
            System.out.println("Search Album: " + e.getMessage());
        }
    }

    public static void viewAllArtists() {
        String sql = "SELECT id, name FROM artists";

        System.out.println("\n===================== Artists ======================");
        System.out.printf("%-4s %-25s%n", "ID", "Artist Name");

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.printf("%-4d %-25s%n", id, name);
            }
        } catch (SQLException e) {
            System.out.println("View Artists: " + e.getMessage());
        }
    }

    public static void createArtist(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Artist name is required.");
            return;
        }

        String sql = "INSERT INTO artists (name) VALUES (?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name.trim());
            pstmt.executeUpdate();

            System.out.println("Artist: " + name + " created successfully!");
            viewAllArtists();
        } catch (SQLException e) {
            System.out.println("Create Artist: " + e.getMessage());
        }
    }

    public static void updateArtist(int id, String name) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Artist name is required.");
            return;
        }

        String sql = "UPDATE artists SET name = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name.trim());
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " updated successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Update Artist: " + e.getMessage());
        }
    }

    public static void deleteArtist(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

        String sql = "DELETE FROM artists WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " deleted successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Delete Artist: " + e.getMessage());
        }
    }

    public static void archiveArtist(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

        String sql = "UPDATE artists SET is_archived = 1 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " archived successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Archive Artist: " + e.getMessage());
        }
    }

    public static void restoreArtist(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

        String sql = "UPDATE artists SET is_archived = 0 WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " restored successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Restore Artist: " + e.getMessage());
        }
    }

    public static void searchArtist(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("\nValidation Error: Search keyword is required.");
            return;
        }

        String sql = "SELECT id, name FROM artists WHERE name LIKE ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.trim() + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n================ Search Artists ====================");
            System.out.printf("%-4s %-25s%n", "ID", "Artist Name");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.printf("%-4d %-25s%n", id, name);
            }
        } catch (SQLException e) {
            System.out.println("Search Artist: " + e.getMessage());
        }
    }

    public static void createUser(String uname, String rawPassword, String email) {
        if (uname == null || uname.trim().isEmpty()) {
            System.out.println("\nValidation Error: Username is required.");
            return;
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
            System.out.println("\nValidation Error: A valid email address is required.");
            return;
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            System.out.println("\nValidation Error: Password must be at least 6 characters long.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String sql = "INSERT INTO users (uname, password, email) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uname.trim());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email.trim());
            pstmt.executeUpdate();

            System.out.println("User: " + uname + " created successfully!");
        } catch (SQLException e) {
            System.out.println("Create User: " + e.getMessage());
        }
    }

    public static void loginUser(String uname, String candidatePassword) {
        if (uname == null || uname.trim().isEmpty()) {
            System.out.println("\nValidation Error: Username is required.");
            return;
        }
        if (candidatePassword == null || candidatePassword.isEmpty()) {
            System.out.println("\nValidation Error: Password is required.");
            return;
        }

        String sql = "SELECT password FROM users WHERE uname = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uname.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(candidatePassword, hashedPassword)) {
                    System.out.println("Login successful! Welcome, " + uname + ".");
                } else {
                    System.out.println("Invalid username or password.");
                }
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("Login User: " + e.getMessage());
        }
    }

}