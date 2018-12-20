package com.gpch.login.controller;

import com.gpch.login.model.Item;
import com.gpch.login.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
//Controller for GET,POST,PUT,DELETE on swagger 2.0
@RestController
@RequestMapping("/api")
public class ProductController {
    private ItemService itemService;
    @Autowired
    public void setProductService(ItemService itemService) {
        this.itemService = itemService;
    }


    @RequestMapping(value = "/list", method= RequestMethod.GET)
    public Iterable list(Model model){
        Iterable productList = itemService.listAllItems();
        return productList;
    }

    @RequestMapping(value = "/show/{id}", method= RequestMethod.GET)
    public Item showProduct(@PathVariable int id, Model model){
        Item product = itemService.getItemById(id);
        return product;
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity saveProduct(@RequestBody Item product){
        itemService.saveItem(product);
        return new ResponseEntity("Product saved successfully", HttpStatus.OK);
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateProduct(@PathVariable int id, @RequestBody Item product){
        Item storedProduct = itemService.getItemById(id);
        storedProduct.setName(product.getName());
        storedProduct.setCategory(product.getCategory());
        storedProduct.setPrice(product.getPrice());
        storedProduct.setAmount(product.getAmount());
        itemService.saveItem(storedProduct);
        return new ResponseEntity("Product updated successfully", HttpStatus.OK);
    }


    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable int id){
        itemService.deleteItemById(id);
        return new ResponseEntity("Product deleted successfully", HttpStatus.OK);

    }

}
