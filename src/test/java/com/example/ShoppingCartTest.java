package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShoppingCartTest {

    ShoppingCart shoppingCart = new ShoppingCart();


    @Test
    @DisplayName("new shopping cart is empty")
    void newShoppingCartIsEmpty() {
        assertThat(shoppingCart.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Add item to shopping cart increases size")
    void addItemToShoppingCartIncreasesSize() {
        shoppingCart.add("Milk", 5);
        assertThat(shoppingCart.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("if added item is null an exception is thrown")
    void ifAddedItemIsNullThrowException() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.add(null, 5));
        assertThat(exception.getMessage()).isEqualTo("Name cannot be null or empty");
    }

    @Test
    @DisplayName("if added item name is blank an exception is thrown")
    void ifAddedItemNameIsBlankAnExceptionIsThrown() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.add("", 5));
        assertThat(exception.getMessage()).isEqualTo("Name cannot be null or empty");
    }


    @Test
    @DisplayName("if add is called and price is null but same item has been added before price can be found")
    void ifAddIsCalledAndPriceIsNullButSameItemHasBeenAddedBeforePriceCanBeFound() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Milk", null);
        assertThat(shoppingCart.findAll()).extracting(Item::getName).containsExactly("Milk", "Milk");
    }

    @Test
    @DisplayName("if add is called and price is null and can not be found an exception is thrown")
    void ifAddIsCalledAndPriceIsNullAndCanNotBeFoundAnExceptionIsThrown() {
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.add("Potatoes", 5);
            shoppingCart.add("Milk", null);
        });
        assertThat(exception.getMessage()).isEqualTo("Price cannot be null");
    }

    @Test
    @DisplayName("if price is less than zero an exception is thrown")
    void ifPriceIsZeroAnExceptionIsThrown() {
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.add("Potatoes", 5);
            shoppingCart.add("Milk", -5);
        });
        assertThat(exception.getMessage()).isEqualTo("Price cannot be less than 0");
    }

    @Test
    @DisplayName("add can be used without Price if same Item is already in cart")
    void addCanBeUsedWithoutPrice(){
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Milk");
        assertThat(shoppingCart.findAll()).extracting(Item::getName).containsExactly("Milk", "Milk");
    }

    @Test
    @DisplayName("Item cannot be added with two different prices")
    void itemCannotBeAddedWithTwoDifferentPrices(){
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.add("Milk", 5);
            shoppingCart.add("Milk", 6);
        });
        assertThat(exception.getMessage()).isEqualTo("Item cannot have two different prices");
    }


    @Test
    @DisplayName("Removing item from shopping cart decreases size")
    void removingItemFromShoppingCartDecreasesSize() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        shoppingCart.remove("Potatoes", 5);
        assertThat(shoppingCart.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Removing non-existing item throws exception")
    void removingNonExistingItemThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingCart.add("Milk", 5);
            shoppingCart.remove("Potatoes", 5);
        });
        assertThat(exception.getMessage()).isEqualTo("Item not found");

    }

    @Test
    @DisplayName("Item is removed from cart after remove has been called")
    void itemIsRemovedFromCartAfterRemoveHasBeenCalled() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        shoppingCart.remove("Potatoes", 5);
        assertThat(shoppingCart.findAll())
                .extracting(Item::getName)
                .containsOnly("Milk");
    }

    @Test
    @DisplayName("Calculate price of all items in shopping cart")
    void calculatePriceOfItemsInShoppingCart() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        assertThat(shoppingCart.totalPrice()).isEqualTo(10);
    }

    @Test
    @DisplayName("Discount applies to all items of same typ")
    void discountAppliesToAllItemsOfSameTyp() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Milk", 5);
        shoppingCart.addDiscount("Milk", 0.2F);
        assertThat(shoppingCart.totalPrice()).isEqualTo(8);
    }

    @Test
    @DisplayName("addDiscount throws exception if discount greater than 1")
    void addDiscountThrowsExceptionIfDiscountGreaterThan1() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Milk", 5);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.addDiscount("Milk", 1.1F));
        assertThat(exception.getMessage()).isEqualTo("Discount cannot be greater than 1");
    }

    @Test
    @DisplayName("addDiscount throws exception if discount is less than 0")
    void addDiscountThrowsExceptionIfDiscountIsLessThan0() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.addDiscount("Milk", -0.1F));
        assertThat(exception.getMessage()).isEqualTo("Discount cannot be less than 0");

    }

    @Test
    @DisplayName("Calculate price after discount been applied to whole cart")
    void calculatePriceAfterDiscountBeenAppliedToWholeCart() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        shoppingCart.totalDiscount(0.2F);
        assertThat(shoppingCart.totalPrice()).isEqualTo(8);
    }

    @Test
    @DisplayName("totalDiscount throws exception if discount greater than 1")
    void totalDiscountThrowsExceptionIfDiscountGreaterThan1() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.totalDiscount(1.1F));
        assertThat(exception.getMessage()).isEqualTo("Discount cannot be greater than 1");
    }

    @Test
    @DisplayName("totalDiscount throws exception if discount less than 0")
    void totalDiscountThrowsExceptionIfDiscountLessThan0() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.totalDiscount(-0.1F));
        assertThat(exception.getMessage()).isEqualTo("Discount cannot be less than 0");

    }


    @Test
    @DisplayName("Shopping cart contains correct items")
    void shoppingCartContainsCorrectItems() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Potatoes", 5);
        assertThat(shoppingCart.findAll())
                .extracting(Item::getName)
                .containsOnly("Milk", "Potatoes");
    }

    @Test
    @DisplayName("Quantity changes removes correct number of the items in cart")
    void quantityChangesLeavesCorrectNumberOfTheItemsInCart() {
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Milk", 5);
        shoppingCart.add("Milk", 5);
        shoppingCart.quantityChange("Milk", 5, 2);
        assertThat(shoppingCart.findAll()).extracting(Item::getName).containsExactly("Milk", "Milk");
    }

    @Test
    @DisplayName("Quantity changes adds correct number of items to the cart")
    void quantityChangesAddsCorrectNumberOfItemsToTheCart() {
        shoppingCart.add("Milk", 5);
        shoppingCart.quantityChange("Milk", 5, 2);
        assertThat(shoppingCart.findAll()).extracting(Item::getName).containsExactly("Milk", "Milk");
    }

    @Test
    @DisplayName("Quantity change below zero throws exception")
    void quantityChangeBelowZeroThrowsException() {
        shoppingCart.add("Milk", 5);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.quantityChange("Milk", 5, -1));
        assertThat(exception.getMessage()).isEqualTo("Quantity cannot be less than zero");
    }

    @Test
    @DisplayName("Quantity change with null throws exception")
    void quantityChangeWithNullThrowsException(){
        shoppingCart.add("Milk", 5);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                shoppingCart.quantityChange("Milk", 5, null));
        assertThat(exception.getMessage()).isEqualTo("Quantity cannot be null");

    }

}
