package app.web;

import app.Product.Product;
import app.Product.ProductCategory;
import app.Product.ProductRepository;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final UserService userService;

    public HomeController(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal UserInfo userInfo) {

        User user = userService.getById(userInfo.getUserId());

        List<Product> soups = productRepository.findByProductCategory(ProductCategory.SOUP);
        List<Product> sallads = productRepository.findByProductCategory(ProductCategory.SALLAD);
        List<Product> appetizers = productRepository.findByProductCategory(ProductCategory.APPETIZER);
        List<Product> mainCourses = productRepository.findByProductCategory(ProductCategory.MAIN_COURSE);
        List<Product> desserts = productRepository.findByProductCategory(ProductCategory.DESSERT);
        List<Product> softDrinks = productRepository.findByProductCategory(ProductCategory.SOFT_DRINK);
        List<Product> alcohols = productRepository.findByProductCategory(ProductCategory.ALCOHOL);
        List<Product> others = productRepository.findByProductCategory(ProductCategory.OTHER);
        List<Product> specials = productRepository.findByProductCategory(ProductCategory.SPECIALS);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("soups", soups);
        modelAndView.addObject("sallads", sallads);
        modelAndView.addObject("appetizers", appetizers);
        modelAndView.addObject("mainCourses", mainCourses);
        modelAndView.addObject("desserts", desserts);
        modelAndView.addObject("softDrinks", softDrinks);
        modelAndView.addObject("alcohols", alcohols);
        modelAndView.addObject("others", others);
        modelAndView.addObject("specials", specials);

        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
