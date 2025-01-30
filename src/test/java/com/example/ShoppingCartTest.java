package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.*;

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
        shoppingCart.add("Milk",5);
        assertThat(shoppingCart.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Removing item from shopping cart decreases size")
    void removingItemFromShoppingCartDecreasesSize(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        shoppingCart.remove("Potatoes",5);
        assertThat(shoppingCart.size()).isEqualTo(1);
    }
    @Test
    @DisplayName("Item is removed from cart after remove has been called")
    void itemIsRemovedFromCartAfterRemoveHasBeenCalled(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        shoppingCart.remove("Potatoes",5);
        assertThat(shoppingCart.findAll())
                .extracting(Item::getName)
                .contains("Milk");

    }
    
    @Test
    @DisplayName("Calculate price of all items in shopping cart")
    void calculatePriceOfItemsInShoppingCart(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        assertThat(shoppingCart.totalPrice()).isEqualTo(10);
    }
    @Test
    @DisplayName("Calculate price after discount been applied to whole cart")
    void calculatePriceAfterDiscountBeenAppliedToWholeCart(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        shoppingCart.totalDiscount(0.2F);
        assertThat(shoppingCart.totalPrice()).isEqualTo(8);
    }
    @Test
    @DisplayName("Shopping cart contains correct items")
    void shoppingCartContainsCorrectItems(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        assertThat(shoppingCart.findAll())
                .extracting(Item::getName)
                .contains("Milk","Potatoes");
    }


}
