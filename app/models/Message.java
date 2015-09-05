package models;


public class Message {
    private String messageId;
    private String messagebody;
    private boolean edited;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessagebody() {
        return messagebody;
    }

    public void setMessagebody(String messagebody) {
        this.messagebody = messagebody;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
