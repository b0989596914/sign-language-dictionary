package com.project.demo.dto;

import com.project.demo.dto.WordCategoryDTO;
import java.util.List;

public class ManualWordsDTO {
    private List<WordCategoryDTO> polysemy;
    private List<WordCategoryDTO> synonym;
    private List<WordCategoryDTO> antonym;
    private List<WordCategoryDTO> related;
    private List<WordCategoryDTO> variant;

    // Getter 與 Setter
    public List<WordCategoryDTO> getPolysemy() { return polysemy; }
    public void setPolysemy(List<WordCategoryDTO> polysemy) { this.polysemy = polysemy; }

    public List<WordCategoryDTO> getSynonym() { return synonym; }
    public void setSynonym(List<WordCategoryDTO> synonym) { this.synonym = synonym; }

    public List<WordCategoryDTO> getAntonym() { return antonym; }
    public void setAntonym(List<WordCategoryDTO> antonym) { this.antonym = antonym; }

    public List<WordCategoryDTO> getRelated() { return related; }
    public void setRelated(List<WordCategoryDTO> related) { this.related = related; }

    public List<WordCategoryDTO> getVariant() { return variant; }
    public void setVariant(List<WordCategoryDTO> variant) { this.variant = variant; }
}
