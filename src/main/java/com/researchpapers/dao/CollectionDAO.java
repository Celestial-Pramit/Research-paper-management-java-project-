package com.researchpapers.dao;

import com.researchpapers.model.Collection;
import com.researchpapers.utill.ConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionDAO {

    public List<Collection> findByUserId(int userId) {
        List<Collection> list = new ArrayList<>();
        String sql = "SELECT * FROM collections WHERE user_id = ? ORDER BY name";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCollection(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(Collection collection) {
        String sql = "INSERT INTO collections (name, description, user_id) VALUES (?, ?, ?)";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, collection.getName());
            ps.setString(2, collection.getDescription());
            ps.setInt(3, collection.getUserId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void delete(int id) {
        String sql = "DELETE FROM collections WHERE id = ?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPaper(int collectionId, int paperId) {
        String sql = "INSERT IGNORE INTO collection_papers (collection_id, paper_id) VALUES (?, ?)";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, collectionId);
            ps.setInt(2, paperId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePaper(int collectionId, int paperId) {
        String sql = "DELETE FROM collection_papers WHERE collection_id = ? AND paper_id = ?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, collectionId);
            ps.setInt(2, paperId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> findPaperIds(int collectionId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT paper_id FROM collection_papers WHERE collection_id = ?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, collectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getInt("paper_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Collection mapCollection(ResultSet rs) throws SQLException {
        Collection c = new Collection(rs.getString("name"), rs.getInt("user_id"));
        c.setId(rs.getInt("id"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}
