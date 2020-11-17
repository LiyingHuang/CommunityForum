package com.coral.community.entity;

/*
*  Encapsulate Paging-related Information
* */
public class Page {
    private int current = 1;  // current page
    private int limit = 10;
    private  int rows;  // used to calculate the the total pages
    private String path; // each page corresponding a url

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >=1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /* get the start row through current page */
    public int getOffset(){
        return (current - 1) * limit;
    }

    /* get total pages number */
    public int getTotal(){
        if(rows % limit == 0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }

    /* get 2 page before current page*/
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }
    /* get 2 page after current page*/
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
