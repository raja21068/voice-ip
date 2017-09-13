
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author JAY KUMAR
 */
public class RecieveAndSend extends Thread{
    
    DataInputStream input;
    DataOutputStream output;
    RecieveAndSend(DataInputStream in, DataOutputStream out){
        input = in;
        output = out;
    }
    @Override
    public void run(){
        try{
            byte[] buffer = new byte[MainServer.BUFFER_SIZE];
            while(true){
                int readedBytes = input.read(buffer, 0, MainServer.BUFFER_SIZE);
                output.write(buffer, 0, readedBytes);
                output.flush();
            }
        }catch(Exception ex){
            try{
                if(input !=null){ input.close(); }
                if(output !=null){ output.close(); }
            }catch(Exception e){
                e.printStackTrace();
            }
            ex.printStackTrace(System.err);
        }
    }
}
