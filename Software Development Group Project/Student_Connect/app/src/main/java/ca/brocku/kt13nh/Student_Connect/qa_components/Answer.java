package ca.brocku.kt13nh.Student_Connect.qa_components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Goal Diggers
 * Class that is used to encapsulate answer data so that it can be saved to the database.
 */

public class Answer {
    private String explanation;
    private String user;
    private String photoUrl;
    private String fileName;
    private String fileUrl;
    private String time;

    /**
     * Default constructor for Answer object
     */
    public Answer(){
    }

    /**
     * Constructor for Answer bject that is used to encapsulate Answer data
     * @param explanation
     * @param user
     * @param photoUrl
     * @param fileName
     * @param fileUrl
     */
    public Answer(String explanation, String user, String photoUrl, String fileName, String fileUrl ) {
        this.explanation = explanation;
        this.user = user;
        this.photoUrl = photoUrl;
        this.fileName = fileName;
        this.fileUrl = fileUrl;

        DateFormat dateFormat = new SimpleDateFormat("h:mm a");
        this.time = dateFormat.format(new Date());
    }

    /**
     * Getter and setter methods for the various attributes of an answer object
     * @return
     */

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String text) {
        this.explanation = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime(){ return time; }

    public void setTime(String time) { this.time = time; }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileName() {
        return fileName;
    }
}
