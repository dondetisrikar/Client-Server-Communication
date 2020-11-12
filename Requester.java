import java.net.UnknownHostException;
import java.util.Scanner;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//Please Run Service Provider first then Run serviceBroker.JAVA and after that run Requester.java
//This Asssignment is multithreaded so please wait till the thread are ready to execute.

//To get head start I took reference from Chapter 4 ppt. specifically from figure 4.4 - 4.6
public class Requester {



    public static void main(String[] args) throws Exception {

         int BrokerPort = 8001;
         String ServerIP = "127.0.0.1";


        try{

        Scanner sc = new Scanner(System.in); //scanner object to get input from the user.
        Socket requester = new Socket(ServerIP, BrokerPort); //client socket. Initially created as null but later instantiated as per needed.
        Socket provider = null;
        String message = ""; //reply from the Broker or Service Provide
        ObjectOutputStream send = null;
        ObjectInputStream receive = null;
        ObjectInputStream answer = null;
        ObjectOutputStream reply = null;
        String SPReply = "";
        int ServiceNumber = 0;

        int choice;

        while (ServiceNumber != 555) {
            System.out.println("Please choose below options:");
            System.out.println("1) Contact to Broker for location of service. \n2) Contact to Service Provider for availability of service. \n3)To close the connection.");
            choice = sc.nextInt();
            switch (choice) {
                case 1:

                    //Connection with ServiceBroker for location of the service.
                    System.out.println("Connecting to Service Brocker...");
                    System.out.println("Broker's PortNumber: " + BrokerPort);
                    System.out.println("Broker's ServerIP: " + ServerIP);

                    //client socket to communicate with ServiceBrocker
                    //requester = new Socket(ServerIP, BrokerPort);
                    System.out.println("Connected to the Broker.");
                    System.out.println("Please Enter Service Number to Check Location and Availability");
                    ServiceNumber = sc.nextInt();

                    //sending service number to broker
                    //outBuffer = requester.getOutputStream();
                    send = new ObjectOutputStream(requester.getOutputStream());
                    send.writeObject(ServiceNumber);

                    //receiving message from Broker;
                    //inBuffer = requester.getInputStream();
                    receive = new ObjectInputStream(requester.getInputStream());
                    message = (String) receive.readObject();

                    //requester.close();

                    System.out.println("Availability of Services: " + message);
                    break;



                case 2:
                    //Connecting to the Service Provide for asking for specific service.
                    System.out.println("Please Enter the Port Number to Connect With at Provider...");
                    int ServicePortNumber = sc.nextInt();
                    System.out.println("Connecting to Service Provider...");
                    provider = new Socket(ServerIP, ServicePortNumber);

                    System.out.println("Connected to ServiceProvider");
                    reply = new ObjectOutputStream(provider.getOutputStream());

                    System.out.println("Ask for the Service...");
                    System.out.println("Please Enter Service Number");
                    Integer requestServiceNumber = sc.nextInt();
                    //System.out.println(request);
                    reply.writeObject(requestServiceNumber);

                    answer = new ObjectInputStream(provider.getInputStream());

                    message = (String) answer.readObject();
                    System.out.println("ServiceProvider's Reply:" + message);

                    //provider.close();
                    break;

                case 3:
                    //requester.close();
                    //provider.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Please select any above option in order to run the program.");
                    break;
            }
        }
            send.close();
            receive.close();
            requester.close();
            provider.close();
        }

        catch (Exception e){

            System.out.println("Exception: "+e);

        }



    }

}
