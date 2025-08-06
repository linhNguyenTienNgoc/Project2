package main.java.com.cafe.dao.base;
import main.java.com.cafe.model.entity.Product;

import java.util.List;

public interface ProductDAO {
    List<Product> getAllProducts();
    Product getProductById(int id);
    boolean insertProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    List<Product> searchProductsByName(String keyword);
}
