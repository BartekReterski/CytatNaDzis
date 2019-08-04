package com.cytatnadzis.cytaty.model;

public class QuoteModel {

    private String quoteText;
    private String quoteWiki;
    private Integer ID;

    public QuoteModel(String quoteText, String quoteWiki, Integer ID) {
        this.quoteText = quoteText;
        this.quoteWiki = quoteWiki;
        this.ID = ID;
    }


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getQuoteWiki() {
        return quoteWiki;
    }

    public void setQuoteWiki(String quoteWiki) {
        this.quoteWiki = quoteWiki;
    }

}
