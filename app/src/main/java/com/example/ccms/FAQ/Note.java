package com.example.ccms.FAQ;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String question;
    private String answer;
    private String documentId;

    public Note(){
        //no arg constructor needed for firebase
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Note(String question, String answer){
        this.question=question;
        this.answer=answer;
    }
    public String getQuestion(){
        return question;
    }
    public String getAnswer(){
        return answer;
    }
}
