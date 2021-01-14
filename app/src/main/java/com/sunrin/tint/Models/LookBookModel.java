package com.sunrin.tint.Models;

import com.sunrin.tint.Util.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LookBookModel implements Serializable {

    private List<String> linkList;
    private String id, mainImage;
    private String userName, userEmail, date;

    public LookBookModel() {}

    public LookBookModel(List<String> linkList, String mainImage) {
        this.linkList = linkList;
        this.mainImage = mainImage;
        this.linkList = new ArrayList<>();
        this.id = this.userName = this.userEmail = "";
        this.date = DateUtil.getDateFormat();
    }

    public List<String> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<String> linkList) {
        this.linkList = linkList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
