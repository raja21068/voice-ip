
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/* client have to send two strings 
 * 1. 1st status    
 *         CALL   request for call to other client
 *         REG    register the number
 * 2. 2nd Phone number
 *          If it is REG then it will be user's phone number and store it into hashtable.
 *          If it is CALL then it will be destination phone number.
 */

public class MainServer {
    public static final int SERVER_PORT = 8000;
    public static String CALL_STATUS = "CALL";
    public static String REGISTER = "REG";
    public static String CALLING = "1";
    public static String INVALID = "0";
    public static final int BUFFER_SIZE = 512; 
    
    public static void main(String arg[]){   
        Hashtable<String,Socket> table = new Hashtable();
        try{
            ServerSocket server = new ServerSocket(SERVER_PORT);
            System.out.println("Waiting.."+SERVER_PORT);
            while(true){
                try{
                    Socket fromSocket = server.accept();
                    // Connection Accepted from client android
                    
                    DataInputStream fromClientIn = new DataInputStream(fromSocket.getInputStream());
                    DataOutputStream fromClientOut = new DataOutputStream(fromSocket.getOutputStream());
                    // Streaming Object
                    System.out.println("Accepted..");
                    
                    // STATUS ->   CALL : REGISTER
                    String status = fromClientIn.readLine();
                    if(status.equalsIgnoreCase(REGISTER)){
                        // GETTING PHONE NUMBER AND STORE IN HASHTABLE
                        String phoneNum = fromClientIn.readLine();
                        table.put(phoneNum, fromSocket);
                        fromClientOut.writeBytes("OK\n");
                        System.out.println("Registered..."+phoneNum);     
                        
                    }else if(status.equalsIgnoreCase(CALL_STATUS)){
                        // CLIENT REQUESTED FOR CALL
                        System.out.println("Requested For Call");
                        String phoneNumToCall = fromClientIn.readLine();
                        String fromCallNumber = fromClientIn.readLine();
                        System.out.println("Call From: "+fromCallNumber);
                        // SEARCHING FROM HASHTABLE
                        if(table.containsKey(phoneNumToCall)){
                            // FOUND IN HASHTABLE
                            fromClientOut.writeBytes(CALLING+"\n");
                            Socket toSocket =  table.get(phoneNumToCall);
                            try{
                                DataOutputStream toClientOut = new DataOutputStream(toSocket.getOutputStream());
                                DataInputStream toClientIn = new DataInputStream(toSocket.getInputStream());
                                System.out.println("To Socket: "+toSocket.getInetAddress().getHostAddress());
                                System.out.println("From Socket: "+fromSocket.getInetAddress().getHostAddress());
                                toClientOut.writeBytes(CALLING+"\n");
                                toClientOut.writeBytes(fromCallNumber+"\n");
                                System.out.println("Last step to accept");
                                String accepted = toClientIn.readLine();
                                System.out.println("Last step to accept");
                                fromClientOut.writeBytes(accepted+"\n");
                                if(accepted.equalsIgnoreCase("ACCEPTED")){
                                    //STARTING CONVERSATION
                                    new RecieveAndSend(toClientIn, fromClientOut).start();
                                    new RecieveAndSend(fromClientIn, toClientOut).start();
                                    System.out.println("Conversation started "+toSocket+" AND "+fromClientIn);
                                }else{
                                    System.out.println("Conversation declined");
                                }
                                
                            }catch(Exception ex){ex.printStackTrace(System.err);}
                        }else{
                            fromClientOut.writeBytes(INVALID+"\n");
                        }
                    }
                }catch(Exception ex){ex.printStackTrace(System.err);}
            }
        }catch(Exception ex){
            ex.printStackTrace(System.err);
        }
    }
}
