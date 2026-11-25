package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Artist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepositoryImpl implements ArtistRepository {
    private final DbConnection dbConnection;

    public ArtistRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Artist> findAllActive() {
        return fetchArtistsByQuery("SELECT id, name, is_archived FROM artists WHERE is_archived = 0");
    }

    @Override
    public List<Artist> findAllArchived() {
        return fetchArtistsByQuery("SELECT id, name, is_archived FROM artists WHERE is_archived = 1");
    }

    @Override
    public boolean save(Artist artist) {
        String sql = "INSERT INTO artists (name, is_archived) VALUES (?, 0)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artist.getName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error saving artist: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Artist artist) {
        String sql = "UPDATE artists SET name = ? WHERE id = ? AND is_archived = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artist.getName());
            ps.setInt(2, artist.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error updating artist: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM artists WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error deleting artist: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        String sql = "UPDATE artists SET is_archived = 1 WHERE id = ? AND is_archived = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error archiving artist: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreById(int id) {
        String sql = "UPDATE artists SET is_archived = 0 WHERE id = ? AND is_archived = 1";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error restoring artist: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Artist> searchByName(String keyword) {
        List<Artist> list = new ArrayList<>();
        String sql = "SELECT id, name, is_archived FROM artists WHERE is_archived = 0 AND name LIKE ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error searching artists: " + e.getMessage());
        }
        return list;
    }

    private List<Artist> fetchArtistsByQuery(String sql) {
        List<Artist> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("DB Error fetching artists: " + e.getMessage());
        }
        return list;
    }

    private Artist mapRow(ResultSet rs) throws SQLException {
        return new Artist(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("is_archived") == 1
        );
    }
}