package com.researchpapers.dao;

import com.researchpapers.model.Paper;
import com.researchpapers.utill.ConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaperDAO {

    public List<Paper> findAll(int userId) {
        List<Paper> list = new ArrayList<>();
        String sql = "SELECT * FROM papers WHERE user_id = ? ORDER BY id DESC";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPaper(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Paper findById(int id) {
        String sql = "SELECT * FROM papers WHERE id = ?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPaper(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(Paper paper) {
        String sql = "INSERT INTO papers (title, authors, abstract_text, publication_venue, publication_year, doi, file_path, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, paper.getTitle());
            ps.setString(2, paper.getAuthors());
            ps.setString(3, paper.getAbstractText());
            ps.setString(4, paper.getPublicationVenue());
            ps.setInt(5, paper.getPublicationYear());
            ps.setString(6, paper.getDoi());
            ps.setString(7, paper.getFilePath());
            ps.setInt(8, paper.getUserId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void update(Paper paper) {
        String sql = "UPDATE papers SET title=?, authors=?, abstract_text=?, publication_venue=?, publication_year=?, doi=?, file_path=? WHERE id=?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paper.getTitle());
            ps.setString(2, paper.getAuthors());
            ps.setString(3, paper.getAbstractText());
            ps.setString(4, paper.getPublicationVenue());
            ps.setInt(5, paper.getPublicationYear());
            ps.setString(6, paper.getDoi());
            ps.setString(7, paper.getFilePath());
            ps.setInt(8, paper.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM papers WHERE id=?";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Paper mapPaper(ResultSet rs) throws SQLException {
        Paper p = new Paper(rs.getString("title"), rs.getString("authors"), rs.getInt("publication_year"), rs.getInt("user_id"));
        p.setId(rs.getInt("id"));
        p.setAbstractText(rs.getString("abstract_text"));
        p.setPublicationVenue(rs.getString("publication_venue"));
        p.setDoi(rs.getString("doi"));
        p.setFilePath(rs.getString("file_path"));
        p.setCategory(rs.getString("category"));
        p.setRating(rs.getInt("rating"));
        return p;
    }
}
