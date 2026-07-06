package com.researchpapers.dao;

import com.researchpapers.model.ReadingProgress;
import com.researchpapers.utill.ConnectionSingleton;

import java.sql.*;

public class ReadingProgressDAO {

    public ReadingProgress findByPaperAndUser(int paperId, int userId) {
        String sql = "SELECT * FROM reading_progress WHERE paper_id = ? AND user_id = ?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paperId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapProgress(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(ReadingProgress progress) {
        String sql = "INSERT INTO reading_progress (paper_id, user_id, status, current_page, total_pages) VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, progress.getPaperId());
            ps.setInt(2, progress.getUserId());
            ps.setString(3, progress.getStatus());
            ps.setInt(4, progress.getCurrentPage());
            ps.setInt(5, progress.getTotalPages());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void update(ReadingProgress progress) {
        String sql = "UPDATE reading_progress SET status=?, current_page=?, total_pages=?, last_read_at=NOW() WHERE id=?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, progress.getStatus());
            ps.setInt(2, progress.getCurrentPage());
            ps.setInt(3, progress.getTotalPages());
            ps.setInt(4, progress.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countByStatus(int userId, String status) {
        String sql = "SELECT COUNT(*) FROM reading_progress rp JOIN papers p ON rp.paper_id = p.id WHERE p.user_id = ? AND rp.status = ?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private ReadingProgress mapProgress(ResultSet rs) throws SQLException {
        ReadingProgress p = new ReadingProgress(rs.getInt("paper_id"), rs.getInt("user_id"), rs.getString("status"));
        p.setId(rs.getInt("id"));
        p.setCurrentPage(rs.getInt("current_page"));
        p.setTotalPages(rs.getInt("total_pages"));
        Timestamp ts = rs.getTimestamp("last_read_at");
        if (ts != null) p.setLastReadAt(ts.toLocalDateTime());
        return p;
    }
}
