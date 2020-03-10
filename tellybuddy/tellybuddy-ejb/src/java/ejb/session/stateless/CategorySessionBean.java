/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewCategoryException;
import util.exception.DeleteCategoryException;
import util.exception.InputDataValidationException;
import util.exception.UpdateCategoryException;

/**
 *
 * @author kaikai
 */
@Stateless
@Local
public class CategorySessionBean implements CategorySessionBeanLocal {

    @PersistenceContext(unitName = "tellybuddy-ejbPU")
    private EntityManager em;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CategorySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Category createNewCategory(Category newCategory, Long parentCategoryId) throws InputDataValidationException, CreateNewCategoryException {
        Set<ConstraintViolation<Category>> constraintViolations = validator.validate(newCategory);

        if (constraintViolations.isEmpty()) {
            try {
                if (parentCategoryId != null) {
                    Category parentCategoryEntity = retrieveCategoryByCategoryId(parentCategoryId);

                    if (!parentCategoryEntity.getProducts().isEmpty()) {
                        throw new CreateNewCategoryException("Parent category cannot be associated with any product");
                    }

                    newCategory.setParentCategory(parentCategoryEntity);
                }

                em.persist(newCategory);
                em.flush();

                return newCategory;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewCategoryException("Category with same name already exist");
                } else {
                    throw new CreateNewCategoryException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewCategoryException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<Category> retrieveAllCategories() {
        Query query = em.createQuery("SELECT c FROM Category c ORDER BY c.name ASC");
        List<Category> categories = query.getResultList();

        for (Category category : categories) {
            category.getParentCategory();
            category.getSubCategories().size();
            category.getProducts().size();
        }

        return categories;
    }

    @Override
    public List<Category> retrieveAllRootCategories() {
        Query query = em.createQuery("SELECT c FROM Category c WHERE c.parentCategory IS NULL ORDER BY c.name ASC");
        List<Category> rootCategories = query.getResultList();

        for (Category rootCategory : rootCategories) {
            lazilyLoadSubCategories(rootCategory);

            rootCategory.getProducts().size();
        }

        return rootCategories;
    }

    @Override
    public List<Category> retrieveAllLeafCategories() {
        Query query = em.createQuery("SELECT c FROM Category c WHERE c.subCategories IS EMPTY ORDER BY c.name ASC");
        List<Category> leafCategories = query.getResultList();

        for (Category leafCategory : leafCategories) {
            leafCategory.getProducts().size();
        }

        return leafCategories;
    }

    @Override
    public List<Category> retrieveAllCategoriesWithoutProduct() {
        Query query = em.createQuery("SELECT c FROM Category c WHERE c.products IS EMPTY ORDER BY c.name ASC");
        List<Category> rootCategories = query.getResultList();

        for (Category rootCategory : rootCategories) {
            rootCategory.getParentCategory();
        }

        return rootCategories;
    }

    @Override
    public Category retrieveCategoryByCategoryId(Long categoryId) throws CategoryNotFoundException {
        Category category = em.find(Category.class, categoryId);

        if (category != null) {
            return category;
        } else {
            throw new CategoryNotFoundException("Category ID " + categoryId + " does not exist!");
        }
    }

    @Override
    public void updateCategory(Category category, Long parentCategoryId) throws InputDataValidationException, CategoryNotFoundException, UpdateCategoryException {
        Set<ConstraintViolation<Category>> constraintViolations = validator.validate(category);

        if (constraintViolations.isEmpty()) {
            if (category.getCategoryId() != null) {
                Category categoryEntityToUpdate = retrieveCategoryByCategoryId(category.getCategoryId());

                Query query = em.createQuery("SELECT c FROM Category c WHERE c.name = :inName AND c.categoryId <> :inCategoryId");
                query.setParameter("inName", category.getName());
                query.setParameter("inCategoryId", category.getCategoryId());

                if (!query.getResultList().isEmpty()) {
                    throw new UpdateCategoryException("The name of the category to be updated is duplicated");
                }

                categoryEntityToUpdate.setName(category.getName());
                categoryEntityToUpdate.setDescription(category.getDescription());

                if (parentCategoryId != null) {
                    if (categoryEntityToUpdate.getCategoryId().equals(parentCategoryId)) {
                        throw new UpdateCategoryException("Category cannot be its own parent");
                    } else if (categoryEntityToUpdate.getParentCategory() == null || (!categoryEntityToUpdate.getParentCategory().getCategoryId().equals(parentCategoryId))) {
                        Category parentCategoryEntityToUpdate = retrieveCategoryByCategoryId(parentCategoryId);

                        if (!parentCategoryEntityToUpdate.getProducts().isEmpty()) {
                            throw new UpdateCategoryException("Parent category cannot have any product associated with it");
                        }

                        categoryEntityToUpdate.setParentCategory(parentCategoryEntityToUpdate);
                    }
                } else {
                    categoryEntityToUpdate.setParentCategory(null);
                }
            } else {
                throw new CategoryNotFoundException("Category ID not provided for category to be updated");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public void deleteCategory(Long categoryId) throws CategoryNotFoundException, DeleteCategoryException {
        Category categoryToRemove = retrieveCategoryByCategoryId(categoryId);

        if (!categoryToRemove.getSubCategories().isEmpty()) {
            throw new DeleteCategoryException("Category ID " + categoryId + " is associated with existing sub-categories and cannot be deleted!");
        } else if (!categoryToRemove.getProducts().isEmpty()) {
            throw new DeleteCategoryException("Category ID " + categoryId + " is associated with existing products and cannot be deleted!");
        } else {
            categoryToRemove.setParentCategory(null);

            em.remove(categoryToRemove);
        }
    }

    private void lazilyLoadSubCategories(Category category) {
        for (Category c : category.getSubCategories()) {
            lazilyLoadSubCategories(c);
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Category>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
