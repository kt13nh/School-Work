package ca.brocku.kt13nh.Student_Connect.chatroom_components;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String fileName;
    private String fileUrl;
    private String name;
    private String photoUrl;
    private String text;
    private String time;
    public Message(){}

    /**
     * Constructor for Message object that is used for creating messages pre-filled with
     * its attributes
     * @param text
     * @param name
     * @param photoUrl
     * @param fileName
     * @param fileUrl
     */
    public Message(String text, String name, String photoUrl, String fileName, String fileUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.time = new SimpleDateFormat("h:mm a").format(new Date());
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public String getFileName() {
        return this.fileName;
    }
}
