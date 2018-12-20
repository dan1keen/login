package com.gpch.login.controller;

import javax.validation.Valid;

import com.gpch.login.model.Item;
import com.gpch.login.model.User;
import com.gpch.login.service.ItemService;
import com.gpch.login.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.*;
import java.util.List;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }


    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }



    //Admin home if role equals to "ADMIN"
    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.addObject("item",new Item());
        modelAndView.addObject("listItems", itemService.listAllItems());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }
    @RequestMapping(value = "admin/home", method = RequestMethod.POST)
    public ModelAndView saveProduct(Item item) {
        ModelAndView modelAndView = new ModelAndView();
        itemService.saveItem(item);
        modelAndView.addObject("listItems",itemService.listAllItems());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }
    //Main view for all authorized users
    @RequestMapping(value="/view", method = RequestMethod.GET)
    public ModelAndView view(String category){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", user.getName());
        //modelAndView.addObject("items", new Item());
        modelAndView.addObject("listItems",itemService.listAllItems());
        modelAndView.addObject("listCat", itemService.getDinstinctCategories(category));
        modelAndView.setViewName("view");
        return modelAndView;
    }

    @RequestMapping(value="view/item/{id}", method = RequestMethod.GET)
    public ModelAndView viewItem(@PathVariable int id,String category){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        ModelAndView modelAndView = new ModelAndView();
        Item item = itemService.getItemById(id);
        modelAndView.addObject("userName",user.getName());
        modelAndView.addObject("item",item);
        modelAndView.addObject("listCat", itemService.getDinstinctCategories(category));
        modelAndView.setViewName("item");
        return modelAndView;
    }

    @RequestMapping(value = "admin/new")
    public ModelAndView addItem(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("item",new Item());
        modelAndView.setViewName("admin/form");
        return modelAndView;
    }

    @RequestMapping(value = "admin/edit/{id}")
    public ModelAndView editItem(@PathVariable int id,Item product){
        ModelAndView modelAndView = new ModelAndView();
        product = itemService.getItemById(id);
        modelAndView.addObject("item", product);
        modelAndView.setViewName("admin/form");
        return modelAndView;
    }

    @RequestMapping(value = "view/category/{category}", method = RequestMethod.GET)
    public ModelAndView sortByCat(@PathVariable String category){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("listItems",itemService.sortItemByCategory(category));

        modelAndView.addObject("userName", user.getName());
        modelAndView.addObject("listCat",itemService.getDinstinctCategories(category));
        modelAndView.setViewName("sort");
        return modelAndView;
    }

    //The point is in sort.html we should search item by name which is already sorted
    @RequestMapping(value = "view/{category}", method = RequestMethod.GET)
    public ModelAndView sortByNameAndCat(@RequestParam("res") String search,@PathVariable String category){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("listItems",itemService.searchItemNameByCategory(search,category));
        modelAndView.addObject("listCat",itemService.getDinstinctCategories(category));
        modelAndView.setViewName("sort");
        return modelAndView;
    }
    private static String UPLOAD_FOLDER = "C://Users//Limtonov//Desktop//4course//spring//spring-login-crud//spring-login-master//src//main//resources//static//images//";


    @RequestMapping(value = "addOrEdit", method = RequestMethod.POST)
    public ModelAndView saveItem(@RequestParam("file") MultipartFile file, Item item){

        ModelAndView modelAndView = new ModelAndView();
        if (file.isEmpty()) {
            modelAndView.addObject("message", "Please select a file to upload");
            modelAndView.setViewName("redirect:/admin/form");
            return modelAndView;
        }
        // add picture into resource/static/images/
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            item.setPicName(file.getOriginalFilename());
            itemService.saveItem(item);
        }catch (IOException e){
            e.printStackTrace();
        }
        modelAndView.setViewName("redirect:/admin/home");
        return modelAndView;
    }
    //Overall search among listed items which are not searched yet
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView searchItem(@RequestParam("result") String searchName, String category){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("listItems",itemService.searchItemByName(searchName));
        modelAndView.addObject("listCat",itemService.getDinstinctCategories(category));
        modelAndView.setViewName("view");
        return modelAndView;
    }

    @RequestMapping(value = "item/delete/{id}", method = RequestMethod.GET)
    public ModelAndView deleteItem(@PathVariable int id){
        ModelAndView modelAndView = new ModelAndView();
        itemService.deleteItemById(id);
        modelAndView.setViewName("redirect:/admin/home");
        return modelAndView;
    }
    // Method for buying a product, amount decreases by one until it reaches 0
    @RequestMapping(value = "addToBox", method = RequestMethod.POST)
    public ModelAndView addItemToBox(Item item){
        ModelAndView modelAndView = new ModelAndView();

        item.setAmount(item.getAmount()-1);

        if(item.getAmount()>0) {
            itemService.saveItem(item);
            modelAndView.setViewName("/view");
        }else{
            modelAndView.addObject("message", "Amount is less than one!!");
            modelAndView.setViewName("redirect:/view/item/" + item.getId());
        }
        return modelAndView;
    }


}
