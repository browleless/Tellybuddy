/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.List;

/**
 *
 * @author ngjin
 */
public class RetrieveProductsByMultipleCategoriesReq {
    
    private List<Long> categoryIds;

    public RetrieveProductsByMultipleCategoriesReq() {
    }

    public RetrieveProductsByMultipleCategoriesReq(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    /**
     * @return the categoryIds
     */
    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    /**
     * @param categoryIds the categoryIds to set
     */
    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
    
    
    
}
