package com.example;

public class ShoppingCart {

    private int size = 0;

    public int size(){
        return size;
    }
    public void add(String item){
        size++;
    }
    public void remove(String item){
        size--;
    }


}
