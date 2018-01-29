package com.example.shop.Entity;

public class ShopProductConverter {
    public static Product convertToProduct(ShopProduct shopProduct){
        Product product = new Product();
        product.setName(shopProduct.getName());
        product.setPrice(shopProduct.getPrice());
        product.setQuantity(shopProduct.getQuantity());
        return product;
    }
}
