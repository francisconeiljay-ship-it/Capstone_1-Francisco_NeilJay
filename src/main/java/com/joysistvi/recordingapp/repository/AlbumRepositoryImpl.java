package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Album;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlbumRepositoryImpl implements AlbumRepository {
    private final DbConnection dbConnection;

    public AlbumRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Album> findAllActive() {
        String sql = "SELECT a.id, a.name, a.year, a.artist_id, ar.name AS artist_name, a.is_archived " +
                "FROM albums a JOIN artists ar ON a.artist_id = ar.id WHERE a.is_archived = 0";
        return fetchAlbumsByQuery(sql);
    }

    @Override
    public List<Album> findAllArchived() {
        String sql = "SELECT a.id, a.name, a.year, a.artist_id, ar.name AS artist_name, a.is_archived " +
                "FROM albums a JOIN artists ar ON a.artist_id = ar.id WHERE a.is_archived = 1";
        return fetchAlbumsByQuery(sql);
    }

    @Override
    public boolean save(Album album) {
        String sql = "INSERT INTO albums (name, year, artist_id, is_archived) VALUES (?, ?, ?, 0)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, album.getName());
            ps.setInt(2, album.getYear());
            ps.setInt(3, album.getArtistId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error saving album: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Album album) {
        String sql = "UPDATE albums SET name = ?, year = ?, artist_id = ? WHERE id = ? AND is_archived = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, album.getName());
            ps.setInt(2, album.getYear());
            ps.setInt(3, album.getArtistId());
            ps.setInt(4, album.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error updating album: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM albums WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error deleting album: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        String sql = "UPDATE albums SET is_archived = 1 WHERE id = ? AND is_archived = 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error archiving album: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreById(int id) {
        String sql = "UPDATE albums SET is_archived = 0 WHERE id = ? AND is_archived = 1";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error restoring album: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Album> searchByName(String keyword) {
        List<Album> list = new ArrayList<>();
        String sql = "SELECT a.id, a.name, a.year, a.artist_id, ar.name AS artist_name, a.is_archived " +
                "FROM albums a JOIN artists ar ON a.artist_id = ar.id " +
                "WHERE a.is_archived = 0 AND a.name LIKE ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error searching albums: " + e.getMessage());
        }
        return list;
    }

    private List<Album> fetchAlbumsByQuery(String sql) {
        List<Album> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("DB Error fetching albums: " + e.getMessage());
        }
        return list;
    }

    private Album mapRow(ResultSet rs) throws SQLException {
        return new Album(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("year"),
                rs.getInt("artist_id"),
                rs.getString("artist_name"),
                rs.getInt("is_archived") == 1);
    }
}
