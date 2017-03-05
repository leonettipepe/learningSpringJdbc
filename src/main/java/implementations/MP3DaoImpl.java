package implementations;

import interfaces.MP3Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leonid on 03.03.2017.
 */
@Component("dao")
public class MP3DaoImpl implements MP3Dao {

    private SimpleJdbcInsert simpleJdbcInsert;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public MP3DaoImpl(DataSource dataSource) {
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
        simpleJdbcInsert.setTableName("mp3");
        simpleJdbcInsert.setColumnNames(Arrays.asList(new String[] {"author", "name"}));
         jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

//    public int insert(final MP3 mp3) {
//        final String sql = "INSERT into mp3 (name, author) VALUES (?, ?)";
//        KeyHolder key = new GeneratedKeyHolder();
//       // jdbcTemplate.update(sql, new Object[] {mp3.getName(), mp3.getAuthor()});
//        jdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                PreparedStatement statement = con.prepareStatement(sql);
//                statement.setString(1, mp3.getName());
//                statement.setString(2, mp3.getAuthor());
//                return statement;
//            }
//        }, key);
//        return key.getKey().intValue();
//    }


    public int insert(MP3 mp3) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("author", mp3.getAuthor());
        source.addValue("name", mp3.getName());
        simpleJdbcInsert.execute(source);
        return 0;
    }

    public void insertBatch(List<MP3> list) {
        MapSqlParameterSource[] source = new MapSqlParameterSource[list.size()];
        for (int i = 0; i < list.size(); i++) {
            MapSqlParameterSource putting = new MapSqlParameterSource();
            putting.addValue("author", list.get(i).getAuthor());
            putting.addValue("name", list.get(i).getName());
            source[i] = putting;
        }
        simpleJdbcInsert.executeBatch(source);
    }

    public void delete(MP3 mp3) {
        final String sql = "DELETE * from mp3 WHERE name = :name AND author = :author";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("name", mp3.getName());
        source.addValue("author", mp3.getAuthor());
        jdbcTemplate.update(sql, source);

    }

    public MP3 getMP3ById(int id) {
        String sql = "SELECT * FROM mp3 WHERE id = :id";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("id", id);
        return jdbcTemplate.queryForObject(sql, source, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByName(String name) {
        String sql = "SELECT * FROM mp3 WHERE name = :name";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("name", name);
        return jdbcTemplate.query(sql, source, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByAuthor(String author) {
        String sql = "SELECT * FROM mp3 WHERE author = :author";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("author", author);
        return jdbcTemplate.query(sql, source, new MP3RowMapper());
    }

    public Map<String, Integer> getStat() {
        String sql = "SELECT author, count (*) as count from mp3 group by author";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            public Map<String, Integer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, Integer> map = new HashMap<String, Integer>();
                while (resultSet.next()) {
                    map.put( (String) resultSet.getObject("author"),  resultSet.getInt("count"));
                }
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
