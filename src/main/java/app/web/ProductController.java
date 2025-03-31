package app.web;

import app.Errand.ErrandRepository;
import app.Product.Product;
import app.Product.ProductService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.repository.UserRepository;
import app.web.dto.AddCartRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ErrandRepository errandRepository;
    private final UserRepository userRepository;


    public ProductController(ProductService productService, ErrandRepository errandRepository, UserRepository userRepository) {
        this.productService = productService;
        this.errandRepository = errandRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/products/{id}/product-details")
    public ModelAndView getProductPage(@PathVariable Long id, @AuthenticationPrincipal UserInfo userInfo) {

        Product product = productService.getById(id);
        String ingredients = productService.getProductIngredients(id);
        AddCartRequest addCartRequest = new AddCartRequest();
        User user = userRepository.findById(userInfo.getUserId()).get();

        addCartRequest.setQuantity(1);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("product", product);
        modelAndView.setViewName("product-details");
        modelAndView.addObject("addCartRequest", addCartRequest);
        modelAndView.addObject("user", user);

        modelAndView.addObject("ingredients", ingredients);

        return modelAndView;
    }
}
