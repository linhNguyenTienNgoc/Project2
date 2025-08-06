package main.java.com.cafe.dao.base;


import main.java.com.cafe.model.entity.Category;
import java.util.List;

public interface CategoryDAO {
    boolean addCategory(Category category);
    boolean updateCategory(Category category);
    boolean deleteCategory(int categoryId);
    Category getCategoryById(int categoryId);
    List<Category> getAllCategories();
}