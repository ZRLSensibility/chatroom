package chat;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;

/**
 * @author ZRL
 * @create 2021-09-09 23:11
 */
public class Send implements Runnable{
    private DataOutputStream dos;
    private BufferedReader reader;
    private boolean isRunning = true;
    private String name;

    public Send() {

        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public Send(Socket client,String name){
        this();
        try {
            dos = new DataOutputStream(client.getOutputStream());
            this.name = name;
            send(this.name);
        } catch (IOException e) {
            isRunning = false;
            CloseUtil.closeAll(dos,reader);
        }
    }

    private String getMsgFromConsole(){
        try {
            return reader.readLine();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return "";
    }

    public void send(String msg){
        if(msg != null && !msg.equals("")){
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
               isRunning = false;
                CloseUtil.closeAll(dos,reader);
            }
            }
        }




    @Override
    public void run() {
        while (isRunning){
            send(getMsgFromConsole());
        }
    }
}
