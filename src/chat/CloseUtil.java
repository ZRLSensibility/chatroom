package chat;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author ZRL
 * @create 2021-09-11 13:29
 */
public class CloseUtil {
    public static void closeAll(Closeable... io){
        for (Closeable temp : io){
            if(temp != null){
                try {
                    temp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
