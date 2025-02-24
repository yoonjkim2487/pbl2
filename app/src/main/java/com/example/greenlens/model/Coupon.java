package com.example.greenlens.model;

public class Coupon {
    private String brandName;
    private String productName;
    private int point;
    private String category;
    private String expireDate;
    private int imageResId;
    private boolean isUsed;
    private String imageUrl;
    private int validityDays;

    public Coupon(String brandName, String productName, int point, String category,
                  String expireDate, int imageResId) {
        this.brandName = brandName;
        this.productName = productName;
        this.point = point;
        this.category = category;
        this.expireDate = expireDate;
        this.imageResId = imageResId;
        this.isUsed = false;
        this.validityDays = 30;
    }

    // Getter 메서드들
    public String getBrandName() {
        return brandName;
    }

    public String getProductName() {
        return productName;
    }

    public int getPoint() {
        return point;
    }

    public String getCategory() {
        return category;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getValidityDays() {
        return validityDays;
    }

    // Setter 메서드들
    public void setUsed(boolean used) {
        isUsed = used;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setValidityDays(int validityDays) {
        this.validityDays = validityDays;
    }
}