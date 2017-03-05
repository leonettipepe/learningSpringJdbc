package implementations;

import interfaces.MP3Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public int insert(final MP3 mp3) {
        final String sql = "INSERT into mp3 (name, author) VALUES (?, ?)";
        KeyHolder key = new GeneratedKeyHolder();
       // jdbcTemplate.update(sql, new Object[] {mp3.getName(), mp3.getAuthor()});
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(sql);
                statement.setString(1, mp3.getName());
                statement.setString(2, mp3.getAuthor());
                return statement;
            }
        }, key);
        return key.getKey().intValue();
    }

    public void delete(MP3 mp3) {
        final String sql = "DELETE * from mp3 WHERE name = ? AND author = ?";
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

    public Map<String, Integer> getStat() {
        String sql = "SELECT author, count (*) as count from mp3 group by author";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            public Map<String, Integer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, Integer> map = new HashMap<String, Integer>();
                map.put( (String) resultSet.getObject("author"),  resultSet.getInt("count"));
                return map;
            }
        });
    }

    private static final class MP3RowMapper implements RowMapper<MP3> {
        public MP3 mapRow(ResultSet resultSet, int i) throws SQLException {
            MP3 obj = new MP3();
            obj.setAuthor((String)resultSet.getObject("author"));
            obj.setName((String) resultSet.getObject("name"));
            return obj;
        }
    }

}
