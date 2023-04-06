package org.se.mac.blorksandbox.scanner.rest;

//TODO: Make this a record instead
public class CompareHashPairRequest {

    private String idA;
    private String idB;

    public CompareHashPairRequest(String idA, String idB) {
        this.idA = idA;
        this.idB = idB;
    }

    public String getIdA() {
        return idA;
    }

    public String getIdB() {
        return idB;
    }
}
