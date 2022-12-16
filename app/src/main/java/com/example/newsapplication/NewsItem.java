package com.example.newsapplication;

public class NewsItem {

    private String title;
    private String description;
    private String pubDate;
    private String link;
    private long id;

    public NewsItem(String title, long id, String description, String pubDate, String link) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
    }

    public NewsItem() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return this.title;
    }

//    @Override
//    public String toString() {
//        return " Title= "+title + "\n Description= " + description + "\n Date= " + pubDate + "\n Link= " + link;
//    }
}