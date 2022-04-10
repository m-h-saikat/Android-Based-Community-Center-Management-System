package com.example.ccms.Message;

import com.google.firebase.firestore.Exclude;

public class UserMessage {
  private String Email;
  private String User_Message;
  private String Status;
  private String Replied_Message;
  private String key;

  public UserMessage(){
  }
    public UserMessage(String Email, String User_Message, String Status, String Replied_Message){
        this.Email=Email;
        this.User_Message=User_Message;
        this.Status=Status;
        this.Replied_Message=Replied_Message;
    }
    public String getEmail(){
      return Email;
    }
    public String getUser_Message(){
      return User_Message;
    }
    public String getStatus(){
    return Status;
    }
    public String getReplied_Message(){ return Replied_Message; }

  @Exclude
  public String getKey() {
    return key;
  }
  @Exclude
  public void setKey(String key) {
    this.key = key;
  }
}
