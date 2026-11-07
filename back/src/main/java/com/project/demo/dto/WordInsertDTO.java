package com.project.demo.dto;
import java.util.List;


public class WordInsertDTO {
    private String wordId;
    private String wordName;
    private String addPersonId;
    private String type;
    private String parentWordId;
    private List<Integer> categoryId;

    public WordInsertDTO(String wordId, String wordName, String addPersonId, String type, String parentWordId, List<Integer> categoryId) {
        this.wordId = wordId;
        this.wordName = wordName;
        this.addPersonId = addPersonId;
        this.type = type;
        this.parentWordId = null; // 預設為 null
        this.categoryId = categoryId;
    }

    // Getters
    public String getWordId() { return wordId; }
    public String getWordName() { return wordName; }
    public String getAddPersonId() { return addPersonId; }
    public String getType() { return type; }
    // Getter
    public String getParentWordId() {
        return parentWordId;
    }

    public List<Integer> getCategoryId() { return categoryId; }
    
    // Setter
    public void setParentWordId(String parentWordId) {
        this.parentWordId = parentWordId;
    }

}
