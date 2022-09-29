package dev.akaecliptic.enums;

public enum ContentType {
    JSON("application/json"),
    HTML("text/html"),
    IMAGE("image/*"),
    TEXT("text/*");

    private final String content;

    ContentType(String content){
        this.content = content;
    }

    public String value(){
        return this.content;
    }
}
