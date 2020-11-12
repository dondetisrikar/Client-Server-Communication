import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.lang.String;


//Please run the MultipleServiceProvider.java before Running ServiceBroker.java.

public class ServiceBroker {

    public static int SPPort = 8004;
    public static int BrokerPort = 8001;
    public static String ServerName = "127.0.0.1";

    //Creating ArrayList of Integers to Store Portnumber that are generated by the Broker. Reference From https://www.geeksforgeeks.org/arraylist-in-java/
    public static ArrayList<Integer> Ports = new ArrayList<Integer>(7);

    //To get rid of the redundancy of the PortNumbers and serviceNumber, Map interface is used. Reference From https://www.geeksforgeeks.org/arraylist-in-java/
    public static Map<Integer, Integer> MapPortNumbers = new HashMap<Integer, Integer>();

    //Creating another Arraylist of Integer for storing Service Numbers:
    public static ArrayList<Integer> ServiceNumbers = new ArrayList<Integer>(7);

    public static int RandomServiceNumbers;
    public static int RandomPorts;
    public static  int RequestedNumber = 0;



    public static void main(String[] args) throws UnknownHostException, Exception {


        //I am using Timer Class in oder to schedule Task1 and Task2 after certain intervals.
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                Random r = new Random();

                    RandomServiceNumbers = r.nextInt(100);
                    RandomPorts = r.nextInt(100) + 1 + SPPort;

                    ServiceNumbers.add(RandomServiceNumbers);
                    Ports.add( RandomPorts);


                    System.out.println("Service Number: " + RandomServiceNumbers + " Port Number: " + RandomPorts);
                    MapPortNumbers.put(RandomServiceNumbers, RandomPorts);

            }
        };

        if (ServiceNumbers.contains(RandomServiceNumbers)){
            ServiceNumbers.remove(RandomServiceNumbers);
        }
        if (Ports.contains(RandomPorts)){
            Ports.remove(RandomPorts);
        }

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {

                ServiceNumbers.remove(ServiceNumbers.get(0));
                Ports.remove(Ports.get(0));
                System.out.println("Services Available: "+ServiceNumbers);
            }
        };

        Timer T1 = new Timer();
        T1.schedule(task1, 0, 15000);

        Timer T2 = new Timer();
        T2.schedule(task2, 15000, 30000);

        try{
            ServerSocket ClientSocket = new ServerSocket(8001);
            //Socket Broker = new Socket("127.0.0.1", 8004);
            //ObjectOutputStream send1 = new ObjectOutputStream(Broker.getOutputStream());
            Timer ProviderTask = new Timer();
            System.out.println("Broker is ready!");
            while (true){

                Socket requester = ClientSocket.accept();
                RequesterThread rt = new RequesterThread(requester);
                rt.start();
                

                ProviderTask.schedule(new ProviderThread(), 60000, 15000);



            }

        }catch (Exception e){

        }

    }


    static class RequesterThread extends Thread{
        Socket requester;

        RequesterThread(Socket requester){
            this.requester = requester;
        }

        public void run(){
            try {
                System.out.println("Listining to Requester...");
                ObjectOutputStream send = new ObjectOutputStream(requester.getOutputStream());
                ObjectInputStream receive = new ObjectInputStream(requester.getInputStream());

                String BrokerReply = "";

                while (RequestedNumber != 555){
                    RequestedNumber = (Integer) receive.readObject();
                    System.out.println("Requested Number is "+RequestedNumber);

                    //int replymessage = Integer.parseInt(RequestedNumber);
                    Integer MapPortNumber = MapPortNumbers.get(RequestedNumber);

                    if (RequestedNumber != 0 && Ports.contains(MapPortNumber)){
                    send.writeObject("Port Number for Requested Service:"+MapPortNumber);
                    send.flush();
                    }
                    else {
                        send.writeObject("Service is not available...");
                    }
                }

                send.close();
                receive.close();
                requester.close();
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    static class ProviderThread extends TimerTask{
        public void run(){
            try{

                Socket Broker = new Socket("127.0.0.1", 8004);
                ObjectOutputStream send1 = new ObjectOutputStream(Broker.getOutputStream());
                System.out.println("Provider Connected");

               // while (true) {

                if (ServiceNumbers.size() > 0) {
                    System.out.println("Service Numbers are sent to Provider.");
                    //List<Integer> SNumbers = ServiceNumbers.subList(0, 3);
                    send1.writeObject(ServiceNumbers);
                }
               // }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}