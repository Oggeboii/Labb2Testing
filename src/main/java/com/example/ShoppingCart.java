package com.example;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    List <Item> items = new ArrayList<>() {
    };
    private int totalPrice = 0;

    public int size(){
        return items.size();
    }
    public void add(String name, Integer price){
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name cannot be null or empty");
        if (price == null) {
            if (items.stream().anyMatch(item -> item.getName().equals(name))) {
                price = items.stream().filter(item -> item.getName().equals(name)).findFirst().get().getPrice();
            }
            else throw new IllegalArgumentException("Price cannot be null");
        }
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be less than 0");

        items.add(new Item(name, price));
        totalPrice += price;
    }
    public void add(String name){
        int price = items.stream().filter(item -> item.getName().equals(name)).findFirst().get().getPrice();
        add(name, price);
    }



    public void remove(String name, int price){
        if (items.stream().noneMatch(item -> item.getName().equals(name))) {
            throw new IllegalArgumentException("Item not found");
        }
        Item removeItem;
        removeItem = items.stream().filter(item -> item.getName().equals(name)).findFirst().get();
        items.remove(removeItem);

        totalPrice -= price;
    }
    public int totalPrice(){
        return totalPrice = items.stream().mapToInt(Item::getPrice).sum();
    }
    public void addDiscount(String name, float discount) {
        if(discount > 1)
            throw new IllegalArgumentException("Discount cannot be greater than 1");
        if (discount < 0)
            throw new IllegalArgumentException("Discount cannot be less than 0");

        for (Item item : items) {
            if (item.getName().equals(name)) {
                item.setPrice((int) (item.getPrice()-(item.getPrice()*discount)));
            }
        }
    }
    public void totalDiscount(float discount){
        if (discount > 1)
            throw new IllegalArgumentException("Discount cannot be greater than 1");
        if (discount < 0)
            throw new IllegalArgumentException("Discount cannot be less than 0");

        for (Item item : items) {
            item.setPrice((int) (item.getPrice() - (item.getPrice() * discount)));
        }
    }

    public List<Item> findAll(){
        return items;
    }

    public void quantityChange(String name, int price, Integer quantity) {
        if(quantity == null)
            throw new IllegalArgumentException("Quantity cannot be null");
        if (quantity<0)
            throw new IllegalArgumentException("Quantity cannot be less than zero");

    int result = (int) items.stream().filter(item -> item.getName().equals(name)).count();
    if (result > quantity) remove(name, price);
    if (result < quantity) add(name, price);
    }
}
