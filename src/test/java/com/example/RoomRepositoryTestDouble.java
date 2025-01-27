package com.example;

import java.util.List;
import java.util.Optional;

public class RoomRepositoryTestDouble implements RoomRepository {

    @Override
    public Optional<Room> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Room> findAll() {
        return List.of();
    }

    @Override
    public void save(Room room) {

    }
}
