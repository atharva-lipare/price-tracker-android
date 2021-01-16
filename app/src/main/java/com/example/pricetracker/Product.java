package com.example.pricetracker;

public class Product {
    private String name;
    private String url;
    private String marketPlace;
    private Double price;
    private String imageUrl;
    private String rating;
    private int numberOfRatings;

    public Product(String product_url, String product_site) {
        this.url = product_url;
        this.marketPlace = product_site;
        name = "NA";
        price = 0.0;
        imageUrl = "NA";
        rating = "";
        numberOfRatings = 0;
    }

    public Product() {
        name = "NA";
        url = "NA";
        marketPlace = "NA";
        price = 0.0;
        imageUrl = "NA";
        rating = "";
        numberOfRatings = 0;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getMarketPlace() { return marketPlace; }

    public void setMarketPlace(String marketPlace) { this.marketPlace = marketPlace; }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getRating() { return rating; }

    public void setRating(String rating) { this.rating = rating; }

    public int getNumberOfRatings() { return numberOfRatings; }

    public void setNumberOfRatings(int numberOfRatings) { this.numberOfRatings = numberOfRatings; }
}
