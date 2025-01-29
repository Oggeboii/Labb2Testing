package com.example;

public class ShoppingCart {

    private int totalPrice = 0;
    private int size = 0;

    public int size(){
        return size;
    }
    public void add(String item, int price){
        size++;
        totalPrice += price;
    }
    public void remove(String item, int price){
        size--;
        totalPrice -= price;
    }
    public int totalPrice(){
        return totalPrice;
    }


}
