package org.alsception.pegasus.features.order;

public class PGSCheckoutRequestDTO
{
    private String stol;
    private String comment;

    public PGSCheckoutRequestDTO() {
    }
    
    public PGSCheckoutRequestDTO(String stol, String comment) {
        this.stol = stol;
        this.comment = comment;
    }

    public String getStol() {
        return stol;
    }

    public void setStol(String stol) {
        this.stol = stol;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    
}