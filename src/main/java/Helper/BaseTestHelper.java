package Helper;

import java.io.File;
import java.util.Date;

public class BaseTestHelper {

    public static  void Createfolder(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }else {
            System.out.println("Folder already created");
        }
    }
    public static String Timestamp(){
        Date now = new Date();
        String Timestamp = now.toString().replace(":","-");
        return Timestamp;
    }
}
