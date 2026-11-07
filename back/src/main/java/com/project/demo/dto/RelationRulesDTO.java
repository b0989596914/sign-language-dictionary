package com.project.demo.dto;

import com.project.demo.dto.RelationRuleItemDTO;
import java.util.Map;

public class RelationRulesDTO {
    private RelationRuleItemDTO polysemy;
    private RelationRuleItemDTO synonym;
    private RelationRuleItemDTO antonym;
    private RelationRuleItemDTO related;
    private RelationRuleItemDTO variant;

    
    public RelationRuleItemDTO getPolysemy() {
        return polysemy;
    }

    public void setPolysemy(RelationRuleItemDTO polysemy) {
        this.polysemy = polysemy;
    }

    public RelationRuleItemDTO getSynonym() {
        return synonym;
    }

    public void setSynonym(RelationRuleItemDTO synonym) {
        this.synonym = synonym;
    }

    public RelationRuleItemDTO getAntonym() {
        return antonym;
    }

    public void setAntonym(RelationRuleItemDTO antonym) {
        this.antonym = antonym;
    }

    public RelationRuleItemDTO getRelated() {
        return related;
    }

    public void setRelated(RelationRuleItemDTO related) {
        this.related = related;
    }

    public RelationRuleItemDTO getVariant() {
        return variant;
    }

    public void setVariant(RelationRuleItemDTO variant) {
        this.variant = variant;
    }
}   
