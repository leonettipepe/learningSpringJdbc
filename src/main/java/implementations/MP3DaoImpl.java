package implementations;

import interfaces.MP3Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by leonid on 03.03.2017.
 */
@Component("dao")
public class MP3DaoImpl implements MP3Dao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public MP3DaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(MP3 mp3) {
        String sql = "INSERT into mp3 (name, author) VALUES (?, ?)";
        jdbcTemplate.update(sql, new Object[] {mp3.getName(), mp3.getAuthor()});
    }

    public void delete(MP3 mp3) {
        String sql = "DELETE * from mp3 WHERE name = ? AND author = ?";
        jdbcTemplate.update(sql, new Object[] {mp3.getName(), mp3.getAuthor()});
    }

    public MP3 getMP3ById(int id) {
        String sql = "SELECT * FROM mp3 WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByName(String name) {
        String sql = "SELECT * FROM mp3 WHERE name = ?";
        return jdbcTemplate.query(sql, new Object[]{name}, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByAuthor(String name) {
        String sql = "SELECT * FROM mp3 WHERE author = ?";
        return jdbcTemplate.query(sql, new Object[]{name}, new MP3RowMapper());
    }

    private class MP3RowMapper implements RowMapper<MP3> {
        public MP3 mapRow(ResultSet resultSet, int i) throws SQLException {
            MP3 obj = new MP3();
            obj.setAuthor((String)resultSet.getObject("author"));
            obj.setName((String) resultSet.getObject("name"));
            return obj;
        }
    }
}
