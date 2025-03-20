package app.web;

import app.Errand.ErrandRepository;
import app.Product.Product;
import app.Product.ProductService;
import app.web.dto.AddCartRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ErrandRepository errandRepository;



    public ProductController(ProductService productService, ErrandRepository errandRepository) {
        this.productService = productService;
        this.errandRepository = errandRepository;
    }

    @GetMapping("/products/{id}/product-details")
    public ModelAndView getProductPage(@PathVariable Long id) {

        Product product = productService.getById(id);
        String ingredients = productService.getProductIngredients(id);


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("product", product);
        modelAndView.setViewName("product-details");
        modelAndView.addObject("addCartRequest", new AddCartRequest());

        modelAndView.addObject("ingredients", ingredients);

        return modelAndView;
    }





}
