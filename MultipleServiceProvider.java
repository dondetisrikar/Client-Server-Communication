import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class MultipleServiceProvider {

    public static int Message = 0;
    public static ArrayList<Integer> ServiceNumbers = new ArrayList<Integer>();
    public static Scanner sc = new Scanner(System.in);

    public static Timer Task1 = new Timer();

    public static void main(String[] args) {
        try{
            ServerSocket ProviderSocket = new ServerSocket(8004);
            System.out.println("Service Provider initiated!");

            while (true){
                Socket Broker = ProviderSocket.accept();
                    Task1.schedule(new BrokerThread(Broker), 60000, 15000);




                        //Socket requester = ClientSocket.accept();

                        //System.out.println("Connected to Requester.");
                    RequesterThread rt = new RequesterThread();
                    rt.start();
                    Thread.sleep(60000);

                }



            }

        catch (IOException | InterruptedException e){
            System.out.println("My Exception");

        }
    }

    static class RequesterThread extends Thread{


        public void run(){
            try{
                System.out.println("Please Enter Port Number to connect with Requester:");
                int port = sc.nextInt();
                ServerSocket CLientSocket = new ServerSocket(port);
                Socket requester = null;
                ObjectInputStream receive = null;
                ObjectOutputStream send =null;
                Integer Request = 0;
                while (Request != 555) {
                    requester = CLientSocket.accept();
                    System.out.println("Connected to requester...");
                    receive = new ObjectInputStream(requester.getInputStream());
                    send = new ObjectOutputStream(requester.getOutputStream());
                    System.out.println("listning to the Requester...");
                    Request = (Integer) receive.readObject();
                    System.out.println(Request);

                    if (ServiceNumbers.contains(Request)){
                        send.writeObject("Yes");
                        send.flush();
                    }
                    else {
                        send.writeObject("No, I can't");
                        System.out.println("Please Wait for the Service...");
                        send.flush();
                    }
                }

                send.close();
                receive.close();
                requester.close();
            }
            catch (IOException | ClassNotFoundException e){
                System.out.println("Exception " +e);
            }
        }
    }

    static class BrokerThread extends TimerTask{

        Socket s;

        BrokerThread(Socket s){
            this.s = s;
        }
        public void run(){
            try{
                System.out.println("Connected to broker.");
                ObjectInputStream receive = new ObjectInputStream(s.getInputStream());
                Object object = receive.readObject();
                ServiceNumbers = (ArrayList<Integer>) object;
                //ServiceNumbers = (ArrayList<Integer>) list;
                //ServiceNumbers.add(list);
                System.out.println("Services: "+ServiceNumbers);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
    }


}
