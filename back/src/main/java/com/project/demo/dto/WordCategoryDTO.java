package com.project.demo.dto;

import java.util.List;



public class WordCategoryDTO {
    private String name;
    private List<Integer> category;

    // Getter 與 Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getCategory() {
        return category;
    }

    public void setCategory(List<Integer> category) {
        this.category = category;
    }
}
