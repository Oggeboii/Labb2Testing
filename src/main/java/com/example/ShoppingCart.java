package com.example;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    List <Item> items = new ArrayList<>() {
    };
    private int totalPrice = 0;
    private int size = 0;

    public int size(){
        return items.size();
    }
    public void add(String name, int price){
        items.add(new Item(name, price));
        size++;
        totalPrice += price;
    }
    public void remove(String name, int price){
        items.removeIf(i -> i.getName().equals(name) && i.getPrice() == price);
        totalPrice -= price;
    }
    public int totalPrice(){
        return totalPrice;
    }
    public void totalDiscount(float discount){
        totalPrice = (int) (totalPrice - (totalPrice * discount));
    }
    public List<Item> findAll(){
        return items;
    }

}
