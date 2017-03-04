package interfaces;

import implementations.MP3;

import java.util.List;

/**
 * Created by leonid on 03.03.2017.
 */
public interface MP3Dao {
    void insert(MP3 mp3);

    void delete(MP3 mp3);

    MP3 getMP3ById(int id);

    List<MP3> getMP3ListByName(String name);

    List<MP3> getMP3ListByAuthor(String name);
}
