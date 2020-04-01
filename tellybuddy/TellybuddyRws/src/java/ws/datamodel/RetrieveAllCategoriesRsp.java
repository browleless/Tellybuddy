package ws.datamodel;

import entity.Category;
import java.util.List;

/**
 *
 * @author tjle2
 */
public class RetrieveAllCategoriesRsp {

    private List<Category> categories;

    public RetrieveAllCategoriesRsp() {
    }

    public RetrieveAllCategoriesRsp(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
