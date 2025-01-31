package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShoppingCartTest {

    ShoppingCart shoppingCart = new ShoppingCart();


    @Test
    @DisplayName("new shopping cart is empty")
    void newShoppingCartIsEmpty(){
        assertThat(shoppingCart.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Add item to shopping cart increases size")
    void addItemToShoppingCartIncreasesSize(){
        shoppingCart.add("Milk",5);
        assertThat(shoppingCart.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("if added item is null an exception is thrown")
    void ifAddedItemIsNullThrowException(){
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.add(null,5);
        });
        assertThat(exception.getMessage()).isEqualTo("Name cannot be null or empty");
    }

    @Test
    @DisplayName("if added item name is blank an exception is thrown")
    void ifAddedItemNameIsBlankAnExceptionIsThrown(){
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.add("",5);
        });
        assertThat(exception.getMessage()).isEqualTo("Name cannot be null or empty");
    }

    @Test
    @DisplayName("if price is null but same item has been added before price can be found")
    void ifPriceIsNullButSameItemHasBeenAddedBeforePriceCanBeFound(){
            shoppingCart.add("Milk",5);
            shoppingCart.add("Milk",null);
        assertThat(shoppingCart.findAll()).extracting(Item::getName).containsExactly("Milk", "Milk");
    }

    @Test
    @DisplayName("if price is null and can not be found an exception is thrown")
    void ifPriceIsNullAndCanNotBeFoundAnExceptionIsThrown(){
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.add("Potatoes",5);
            shoppingCart.add("Milk",null);
        });
        assertThat(exception.getMessage()).isEqualTo("Price cannot be null or less than 0");

    }


    @Test
    @DisplayName("Removing item from shopping cart decreases size")
    void removingItemFromShoppingCartDecreasesSize(){
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        shoppingCart.remove("Potatoes",5);
        assertThat(shoppingCart.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Item is removed from cart after remove has been called")
    void itemIsRemovedFromCartAfterRemoveHasBeenCalled(){
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        shoppingCart.remove("Potatoes",5);
        assertThat(shoppingCart.findAll())
                .extracting(Item::getName)
                .containsOnly("Milk");
    }
    
    @Test
    @DisplayName("Calculate price of all items in shopping cart")
    void calculatePriceOfItemsInShoppingCart(){
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        assertThat(shoppingCart.totalPrice()).isEqualTo(10);
    }

    @Test
    @DisplayName("Calculate price after discount been applied to whole cart")
    void calculatePriceAfterDiscountBeenAppliedToWholeCart(){
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        shoppingCart.totalDiscount(0.2F);
        assertThat(shoppingCart.totalPrice()).isEqualTo(8);
    }
    @Test
    @DisplayName("Shopping cart contains correct items")
    void shoppingCartContainsCorrectItems(){
        shoppingCart.add("Milk",5);
        shoppingCart.add("Potatoes",5);
        assertThat(shoppingCart.findAll())
                .extracting(Item::getName)
                .containsOnly("Milk","Potatoes");
    }

    @Test
    @DisplayName("Quantity changes removes correct number of the items in cart")
    void quantityChangesLeavesCorrectNumberOfTheItemsInCart(){
        shoppingCart.add("Milk",5);
        shoppingCart.add("Milk",5);
        shoppingCart.add("Milk",5);
        shoppingCart.quantityChange("Milk",5,2);
        assertThat(shoppingCart.findAll()).extracting(Item::getName).containsExactly("Milk", "Milk");
    }

    @Test
    @DisplayName("Quantity changes adds correct number of items to the cart")
    void quantityChangesAddsCorrectNumberOfItemsToTheCart(){
        shoppingCart.add("Milk",5);
        shoppingCart.quantityChange("Milk",5,2);
        assertThat(shoppingCart.findAll()).extracting(Item::getName).containsExactly("Milk", "Milk");
    }






}
