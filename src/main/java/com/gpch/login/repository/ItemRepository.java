package com.gpch.login.repository;


import com.gpch.login.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("itemRepository")
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemByNameContaining(String name);
    List<Item> findItemByCategory(String category);
    List<Item> findItemByNameContainingAndCategory(String name, String category);

    @Query(value = "SELECT i.item_id, i.item_cat, i.item_amount, i.item_name, i.price, i.pic_name FROM item i GROUP BY i.item_cat", nativeQuery = true)
    List<Item> listAllCat(String category);

    Item findItemById(int id);
}
