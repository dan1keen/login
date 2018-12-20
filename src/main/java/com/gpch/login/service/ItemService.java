package com.gpch.login.service;


import com.gpch.login.model.Item;
import com.gpch.login.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("itemService")
public class ItemService {
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Iterable<Item> listAllItems(){

        return itemRepository.findAll();
    }
    public Item saveItem(Item product) {
        return itemRepository.save(product);
    }

    public Item getItemById(int id){return itemRepository.findItemById(id);}

    public List<Item> searchItemByName(String name){return itemRepository.findItemByNameContaining(name);}

    public void deleteItemById(int id){itemRepository.deleteById(id);}

    public List<Item> sortItemByCategory(String category){return itemRepository.findItemByCategory(category);}

    public List<Item> searchItemNameByCategory(String name, String category){return itemRepository.findItemByNameContainingAndCategory(name,category);}

    public List<Item> getDinstinctCategories(String category){return itemRepository.listAllCat(category);}

}
