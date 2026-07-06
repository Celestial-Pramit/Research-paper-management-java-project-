package com.researchpapers.dao;

import com.researchpapers.model.Note;
import com.researchpapers.utill.ConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    public List<Note> findByPaperId(int paperId) {
        List<Note> list = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE paper_id = ? ORDER BY created_at DESC";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paperId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapNote(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(Note note) {
        String sql = "INSERT INTO notes (paper_id, user_id, content) VALUES (?, ?, ?)";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, note.getPaperId());
            ps.setInt(2, note.getUserId());
            ps.setString(3, note.getContent());
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
        String sql = "DELETE FROM notes WHERE id = ?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Note mapNote(ResultSet rs) throws SQLException {
        Note note = new Note(rs.getInt("paper_id"), rs.getInt("user_id"), rs.getString("content"));
        note.setId(rs.getInt("id"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) note.setCreatedAt(ts.toLocalDateTime());
        Timestamp ts2 = rs.getTimestamp("updated_at");
        if (ts2 != null) note.setUpdatedAt(ts2.toLocalDateTime());
        return note;
    }
}
