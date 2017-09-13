package dbhandler;

public class ContactBean {

	  //private variables
    private int id;
    private String name;
    private String password;
    private String phone_number;
    private String email; 
    private String status;
    private String mode;
    // Empty constructor
    public ContactBean(){         
    }
    

  //getter methods
    public int getID(){
        return this.id;
    }
     
    public String getPassword(){
        return this.password;
    }

    public String getName(){
        return this.name;
    }

    public String getEmail(){
        return this.email;
    }

    public String getStatus(){
        return this.status;
    }

    public String getMode(){
        return this.mode;
    }

    public String getPhoneNumber(){
        return this.phone_number;
    }
     

//setter methods
    
    public void setID(int id){
        this.id = id;
    }
     
    public void setPassword(String password){
        this.password = password;
    }
    
    public void setName(String name){
        this.name = name;
    }
     
    public void setStatus(String status){
        this.status = status;
    }
    
    public void setMode(String mode){
        this.mode = mode;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public void setPhoneNumber(String phone_number){
        this.phone_number = phone_number;
    }
}