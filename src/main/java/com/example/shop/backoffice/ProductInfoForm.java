package com.example.shop.backoffice;

import com.example.shop.Entity.ShopProduct;
import java.math.BigDecimal;
public class ProductInfoForm {
    private ShopProduct shopProduct;

    public void createProduct(String name, BigDecimal price, int quantity){
        this.shopProduct = new ShopProduct(name, price, quantity);
    }

    public ShopProduct getShopProduct(){
        return shopProduct;
    }
}
