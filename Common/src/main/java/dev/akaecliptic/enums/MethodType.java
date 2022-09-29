package dev.akaecliptic.enums;

public enum MethodType {
    GET, POST, PUT;

    public static MethodType of(String type){
        for (MethodType method: MethodType.values()) {
            if(method.name().equals(type.toUpperCase())) return method;
        }

        return null;
    }
}
