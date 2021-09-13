package chat;

import javax.xml.transform.Source;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**创建服务器:发送数据 + 接收数据
 * 写出数据：输出流
 * 读取数据：输入流
 *
 * @author ZRL
 * @create 2021-09-09 19:10
 */
public class Server {
    private List<MyChannel> myChannels = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        new Server().start();
    }

        public void start() throws IOException {
            ServerSocket server = new ServerSocket(9999);
                while (true){
                    Socket client = server.accept();
                    MyChannel myChannel = new MyChannel(client);
                    myChannels.add(myChannel);
                    new Thread(myChannel).start();
            }
        }

    /**
     * 一个客户端一条道路
     */
    private class MyChannel implements Runnable{
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isRunning = true;
        private String name;
        public MyChannel(Socket client){
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                this.name = dis.readUTF();
                this.send("欢迎进入聊天室");
                sendOthers(this.name + "进入了聊天室",true);


            } catch (IOException e) {
                CloseUtil.closeAll(dis,dos);
                isRunning = false;

            }
        }

        private String receive(){
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
               CloseUtil.closeAll(dis);
               isRunning = false;
               myChannels.remove(this);
            }
            return msg;
        }

        private void send(String msg){
            if(msg == null || msg.equals("")){
                return;
            }
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                CloseUtil.closeAll(dis,dos);
                isRunning = false;
                myChannels.remove(this);
            }
        }

        private void sendOthers(String msg,boolean sys){
            //查看是否为私聊还是群聊
            if(msg.startsWith("@") && msg.contains(":")){
                String name = msg.substring(1,msg.indexOf(":"));
                String content = msg.substring(msg.indexOf(":") + 1);
                for (MyChannel other : myChannels){
                    if(other.name.equals(name)){
                        other.send(this.name + "对您私聊说：" + content);
                    }
                }
            }else{
                for(MyChannel other : myChannels){
                    if(other == this){
                        continue;
                    }
                    if(sys){
                        other.send("系统信息："+ msg);
                    }else {
                        other.send(this.name + "对所有人说:" + msg);
                    }
            }

            }
        }



        @Override
        public void run() {
            while (isRunning){
                sendOthers(receive(),false);
            }
        }
    }
}

