package ru.gpncr.http.server.app;

public class Category {
    private String category_name;
    private int id;

    public Category(String category_id, int id) {
        this.category_name = category_id;
        this.id = id;
    }

    public String getCategory_id() {
        return category_name;
    }

    public void setCategory_id(String category_id) {
        this.category_name = category_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
