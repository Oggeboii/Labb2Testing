package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {



    @Test
    @DisplayName("Creates shopping cart object")
    void createsShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
    }

    @Test
    @DisplayName("new shopping cart is empty")
    void newShoppingCartIsEmpty(){
        ShoppingCart shoppingCart = new ShoppingCart();
        assertThat(shoppingCart.size()).isEqualTo(0);
    }
    @Test
    @DisplayName("Add item to shopping cart increases size")
    void addItemToShoppingCartIncreasesSize(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.add("");
        assertThat(shoppingCart.size()).isEqualTo(1);
    }
    @Test
    @DisplayName("Removing item from shopping cart decreases size")
    void removingItemFromShoppingCartDecreasesSize(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.add("");
        shoppingCart.add("");
        shoppingCart.remove("");
        assertThat(shoppingCart.size()).isEqualTo(1);

    }



}
