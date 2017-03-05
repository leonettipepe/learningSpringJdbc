import implementations.MP3;
import interfaces.MP3Dao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * Created by leonid on 03.03.2017.
 */
public class Test {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
        MP3Dao dao = (MP3Dao) ctx.getBean("dao");
        MP3 obj = new MP3();
        obj.setAuthor("Justin Bieber");
        obj.setName("Leglock");
        //System.out.println(dao.insert(obj));
//        for (Map.Entry<String, Integer> pair : dao.getStat().entrySet()) {
//            System.out.println(pair.getKey() + " - " + pair.getValue());
//        }
        for (MP3 kaka : dao.getMP3ListByName("Leglock")) {
            System.out.println(kaka.getAuthor() + " - " + kaka.getName());
        }
    }
}
