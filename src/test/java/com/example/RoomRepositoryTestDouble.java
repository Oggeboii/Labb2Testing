package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomRepositoryTestDouble implements RoomRepository {
    List<Room> rooms = new ArrayList<Room>();

    @Override
    public Optional<Room> findById(String id) {
        return rooms.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public List<Room> findAll() {
        return rooms;
    }

    @Override
    public void save(Room room) {
        rooms.add(room);
    }
}
