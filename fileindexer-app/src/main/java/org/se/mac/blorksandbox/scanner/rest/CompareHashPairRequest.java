package org.se.mac.blorksandbox.scanner.rest;

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
