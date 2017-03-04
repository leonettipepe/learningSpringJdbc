import implementations.MP3;
import interfaces.MP3Dao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by leonid on 03.03.2017.
 */
public class Test {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
        MP3Dao dao = (MP3Dao) ctx.getBean("dao");
        MP3 obj = new MP3();
        obj.setAuthor("Justin Bieber");
        obj.setName("Armbar");
        //dao.insert(obj);
        System.out.println(dao.getMP3ById(10).getName());
    }
}
