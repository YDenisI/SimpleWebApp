package ru.gpncr.http.server.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gpncr.http.server.ExceptionHandler;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBStore {
    private static final Logger log = LogManager.getLogger(DBStore.class);

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Product";
    private static final String INSERT_ITEM_QUERY = "INSERT INTO products (name, description, price, quantity, category_id) VALUES (?,?,?,?,?) ";
    private static final String DELETE_ITEM_QUERY = "DELETE FROM products WHERE id = ?";
    private static final String UPDATE_ITEM_QUERY = "UPDATE products SET name = ?, description = ?, price = ?, quantity = ?, category_id = ? WHERE id = ?";
    private static final String GET_ITEMS_QUERY = "SELECT \n" +
            "    p.id AS product_id,\n" +
            "    p.name AS product_name,\n" +
            "    p.description AS product_description,\n" +
            "    p.price AS product_price,\n" +
            "    p.quantity AS product_quantity,\n" +
            "    c.name AS category_name,\n" +
            "    c.id AS category_id\n" +
            "FROM \n" +
            "    products p\n" +
            "JOIN \n" +
            "    categories c ON p.category_id = c.id ORDER BY p.name;";
    private static final String GET_PRODUCT_BY_ID_QUERY = "SELECT \n" +
            "    p.id AS product_id,\n" +
            "    p.name AS product_name,\n" +
            "    p.description AS product_description,\n" +
            "    p.price AS product_price,\n" +
            "    p.quantity AS product_quantity,\n" +
            "    c.name AS category_name,\n" +
            "    c.id AS category_id\n" +
            "FROM \n" +
            "    products p\n" +
            "JOIN \n" +
            "    categories c ON p.category_id = c.id WHERE p.id = ? ORDER BY p.name;";

    private static final String GET_PRODUCT_BY_CATEGORY_ID_QUERY = "SELECT \n" +
            "    p.id AS product_id,\n" +
            "    p.name AS product_name,\n" +
            "    p.description AS product_description,\n" +
            "    p.price AS product_price,\n" +
            "    p.quantity AS product_quantity,\n" +
            "    c.name AS category_name,\n" +
            "    c.id AS category_id\n" +
            "FROM \n" +
            "    products p\n" +
            "JOIN \n" +
            "    categories c ON p.category_id = c.id WHERE c.id = ? ORDER BY p.name;";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Zrjdktd7!1";

    private List<Product> products;
    private Connection connection;

    public DBStore()  {
        try {
            connection = DriverManager.getConnection(DATABASE_URL,USER , PASSWORD);
            log.info("Database connected!");
        }catch (SQLException e){
            log.error(e.getLocalizedMessage());
            throw new ExceptionHandler(500,"Internal Server Error. \n"+e.getLocalizedMessage());
        }
    }

    public List<Product> getProducts(){
        List<Product> items = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(GET_ITEMS_QUERY)) {
                while (resultSet.next()) {
                    int Id = resultSet.getInt("product_id");
                    String name = resultSet.getString("product_name");
                    String description = resultSet.getString("product_description");
                    BigDecimal price = resultSet.getBigDecimal("product_price");
                    int quantity = resultSet.getInt("product_quantity");
                    Category category = new Category(resultSet.getString("category_name"),resultSet.getInt("category_id")) ;
                    items.add(new Product(Id,name,description,price,quantity,category));
                }

            }
        }catch (SQLException e) {
            log.error("Error: Method getProducts "+e.getLocalizedMessage());
            throw new ExceptionHandler(500,"Internal Server Error\n"+e.getLocalizedMessage());
        }
        return Collections.unmodifiableList(items);
    }

    public void save(Product product) {

        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ITEM_QUERY)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setBigDecimal(3, product.getPrice());
            preparedStatement.setInt(4, product.getQuantity());
            preparedStatement.setInt(5, product.getCategory().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error: Method save " + e.getLocalizedMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception exp) {
                    log.error("Error: rollback " + exp.getLocalizedMessage());
                    throw new ExceptionHandler(500,"Internal Server Error");
                }
            }
            throw new ExceptionHandler(500,"Internal Server Error");
        }
    }

    public void delete(int id) {

        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ITEM_QUERY)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error: Method save " + e.getLocalizedMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception exp) {
                    log.error("Error: rollback " + exp.getLocalizedMessage());
                    throw new ExceptionHandler(500,"Internal Server Error");
                }
            }
            throw new ExceptionHandler(500,"Internal Server Error");
        }
    }

    public boolean update(Product product) {

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ITEM_QUERY)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setBigDecimal(3, product.getPrice());
            preparedStatement.setInt(4, product.getQuantity());
            preparedStatement.setInt(5, product.getCategory().getId());
            preparedStatement.setInt(6, product.getId());
            if (preparedStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            log.error("Error: Method update " + e.getLocalizedMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception exp) {
                    log.error("Error: rollback " + exp.getLocalizedMessage());
                }
            }
            throw new ExceptionHandler(500,"Internal Server Error");
        }
        return false;
    }

   public Product getProductById(int id){
       try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCT_BY_ID_QUERY)) {
           preparedStatement.setInt(1, id);
           Product product = null;
           try (ResultSet resultSet = preparedStatement.executeQuery()) {
               if (resultSet.next()) {
                   int Id = resultSet.getInt("product_id");
                   String name = resultSet.getString("product_name");
                   String description = resultSet.getString("product_description");
                   BigDecimal price = resultSet.getBigDecimal("product_price");
                   int quantity = resultSet.getInt("product_quantity");
                   Category category = new Category(resultSet.getString("category_name"),resultSet.getInt("category_id")) ;
                   product = new Product(id, name, description,price,quantity, category);
                   return product;
               }
           }
       } catch (SQLException e) {
           log.error("Error: Method getProductById " + e.getLocalizedMessage());
           if (connection != null) {
               try {
                   connection.rollback();
               } catch (Exception exp) {
                   log.error("Error: rollback " + exp.getLocalizedMessage());
                   throw new ExceptionHandler(500,"Internal Server Error");
               }
           }
           throw new ExceptionHandler(500,"Internal Server Error");
       }
       return null;
   }

    public List<Product> getProductsByCategoryId(int id){
        List<Product> products = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCT_BY_CATEGORY_ID_QUERY)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int Id = resultSet.getInt("product_id");
                    String name = resultSet.getString("product_name");
                    String description = resultSet.getString("product_description");
                    BigDecimal price = resultSet.getBigDecimal("product_price");
                    int quantity = resultSet.getInt("product_quantity");
                    Category category = new Category(resultSet.getString("category_name"),resultSet.getInt("category_id")) ;
                    products.add(new Product(id, name, description,price,quantity, category));
                }
            }
        } catch (SQLException e) {
            log.error("Error: Method getProductById " + e.getLocalizedMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception exp) {
                    log.error("Error: rollback " + exp.getLocalizedMessage());
                    throw new ExceptionHandler(500,"Internal Server Error");
                }
            }
            throw new ExceptionHandler(500,"Internal Server Error");
        }
        return products;
    }
}
