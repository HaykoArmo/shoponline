package com.example.shop.backoffice;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.example.shop.DAO.UserDAO;
import com.example.shop.DAO.OrderDAO;
import com.example.shop.Entity.*;
import com.example.shop.DAO.ProductDAO;
import org.springframework.beans.factory.annotation.Autowired;
public class UserSession {
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private OrderDAO orderDAO;

    private User user;
    @Autowired
    private UserOrder userOrder;


    public void setUser(User user){
        this.user = user;
        this.userOrder.setUserId(user.getId());
    }

    public User getUser(){
        return user;
    }

    public UserOrder getUserOrder(){
        return userOrder;
    }

    public List<Product> viewProducts(){
        return productDAO.getProducts();
    }

    public void addProductToOrder(int productId){
        if(productDAO.getProduct(productId) != null){
            Product product = productDAO.getProduct(productId);
            userOrder.getProducts().add(product);
            //decrease one, but not updated in db until purchased
            product.setQuantity(product.getQuantity()-1);
            userOrder.updateAmount(product);
        }
    }

    public void removeProductFromOrder(int productIndex){
        if(productIndex < userOrder.getProducts().size()){
            userOrder.getProducts().remove(productIndex);
        }
    }

    public void purchaseOrder(int hour){
        if(userOrder.getProducts().size() !=0){
            userOrder.finalizeOrder(hour);
            Order order = UserOrderConverter.convertToOrder(userOrder);
            //update user's balance
            BigDecimal balance = user.getBalance();
            user.setBalance(balance.subtract(order.getAmount()));
            userDAO.updateBalance(user);
            //reset order
            userOrder = new UserOrder();
            userOrder.setUserId(user.getId());
            //add order to db
            orderDAO.addOrder(order);
            for(Product p : order.getProducts()){
                productDAO.updateQuantity(p);
            }
        }
    }

    public void payBalance(){
        user.setBalance(BigDecimal.ZERO.setScale(2, RoundingMode.CEILING));
        userDAO.updateBalance(user);
    }
}
