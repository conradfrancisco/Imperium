package capstone.uic.com.imperium;


/**
 * Created by Conrad Francisco Jr on 2/23/2018.
 */

public class User {

    private String username;
    private String email;
    private String fullname;
    private String emails;
    private String pin;

    public User (){

        this.email = "";
        this.username = "";
        this.fullname = "";
        this.emails = "";
        this.pin = " ";

    }

    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }

    public void setFullname(String fullname){
        this.fullname = fullname;
    }
    public String getFullname(){
        return fullname;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }

    public void setPin(String pin){
        this.pin = pin;
    }
    public String getPin(){
        return pin;
    }

}
