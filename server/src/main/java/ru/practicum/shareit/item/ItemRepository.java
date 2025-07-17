package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>  {
    List<Item> findByUserId(long userId);
    @Query("SELECT i FROM Item i WHERE i.userId = ?1 " +
            "AND LOWER(i.name) LIKE LOWER(CONCAT('%', ?2, '%')) " +
            "AND i.available = true")
    List<Item> search(long userId, String text);
}