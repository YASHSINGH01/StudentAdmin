package com.example.recycleview;

public class Note {
    private String title;
    private String mail;
    private int priority;
    public  String namee;
    public  String url;
    

    public String getNamee() {
        return namee;
    }

    public void setNamee(String namee) {
        this.namee = namee;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Note(String namee, String url) {
        this.namee = namee;
        this.url = url;
    }

    public Note()
    {

    }

    public Note(String title, String mail, int priority) {
        this.title = title;
        this.mail = mail;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getMail() {
        return mail;
    }

    public int getPriority() {
        return priority;
    }

    
}
