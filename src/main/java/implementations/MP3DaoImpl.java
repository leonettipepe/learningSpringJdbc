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

    private static final String mp3Table = "mp3";
    private static final String mp3View = "mp3_view";

    private SimpleJdbcInsert simpleJdbcInsert;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public MP3DaoImpl(DataSource dataSource) {
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
        simpleJdbcInsert.setTableName("mp3");
        simpleJdbcInsert.setColumnNames(Arrays.asList(new String[] {"author", "name"}));
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int insert(MP3 mp3) {
        Author author = mp3.getAuthor();
        String insertingAuthor = "INSERT INTO author (author) values (:author)";
        MapSqlParameterSource paramsAuthor = new MapSqlParameterSource();
        paramsAuthor.addValue("author", author.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertingAuthor, paramsAuthor, keyHolder);
        System.out.println(keyHolder.getKey().intValue());

        String insertingMP3 = "INSERT INTO mp3 (author_id, name) values (:author_id, :name)";
        MapSqlParameterSource paramsMP3 = new MapSqlParameterSource();
        paramsMP3.addValue("author_id", keyHolder.getKey().intValue());
        paramsMP3.addValue("name", mp3.getName());
        jdbcTemplate.update(insertingMP3, paramsMP3);
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
        String sql = "select * from " + mp3View + " where mp3_id=:mp3_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mp3_id", id);

        return jdbcTemplate.queryForObject(sql, params, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByName(String name) {
        String sql = "select * from " + mp3View + " where upper(mp3_name) LIKE :mp3_name";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("mp3_name", "%" + name.toUpperCase() + "%");
        return jdbcTemplate.query(sql, source, new MP3RowMapper());
    }

    public List<MP3> getMP3ListByAuthor(String author) {
        String sql = "select * from " + mp3View + " where upper(author_name) like :author_name";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("author_name", "%" + author.toUpperCase() + "%");

        return jdbcTemplate.query(sql, params, new MP3RowMapper());
    }

    public Map<String, Integer> getStat() {
        String sql = "SELECT author_name, count (*) as count from mp3_view group by author_name";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            public Map<String, Integer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, Integer> map = new HashMap<String, Integer>();
                while (resultSet.next()) {
                    map.put( (String) resultSet.getObject("author_name"),  resultSet.getInt("count"));
                }
                return map;
            }
        });
    }

    private static final class MP3RowMapper implements RowMapper<MP3> {
        public MP3 mapRow(ResultSet resultSet, int i) throws SQLException {
            Author author = new Author();
            author.setName((String)resultSet.getObject("author_name"));
            author.setId(resultSet.getInt("author_id"));
            MP3 obj = new MP3();
            obj.setAuthor((author));
            obj.setName((String) resultSet.getObject("mp3_name"));
            return obj;
        }
    }

}
