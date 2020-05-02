/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import java.util.List;
import javax.ejb.Local;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewCategoryException;
import util.exception.DeleteCategoryException;
import util.exception.InputDataValidationException;
import util.exception.UpdateCategoryException;

/**
 *
 * @author kaikai
 */
@Local
public interface CategorySessionBeanLocal {



    public Category createNewCategory(Category newCategory) throws InputDataValidationException, CreateNewCategoryException;

    public List<Category> retrieveAllCategories();

//    public List<Category> retrieveAllRootCategories();

//    public List<Category> retrieveAllLeafCategories();

    public List<Category> retrieveAllCategoriesWithoutProduct();

    public Category retrieveCategoryByCategoryId(Long categoryId) throws CategoryNotFoundException;

    public void updateCategory(Category category) throws InputDataValidationException, CategoryNotFoundException, UpdateCategoryException;

    public void deleteCategory(Long categoryId) throws CategoryNotFoundException, DeleteCategoryException;
    
}
