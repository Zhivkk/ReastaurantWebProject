package app.Errand;


import app.Cart.Cart;
import app.Cart.CartRepository;
import app.Product.ProductRepository;
import app.User.model.User;
import app.User.repository.UserRepository;
import app.web.dto.AddCartRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ErrandService {

    private final ErrandRepository errandRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ErrandService(ErrandRepository errandRepository, CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.errandRepository = errandRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void addCartToErrand(User user, AddCartRequest addCartRequest, Long id) {

        if (errandRepository.findByUserAndErrandStatus(user, ErrandStatus.PREPARATION).isEmpty()) {

            Errand errand = Errand.builder()
                    .user(user)
                    .errandStatus(ErrandStatus.PREPARATION)
                    .addressForDelivery(user.getAddress())
                    .createdOn(LocalDateTime.now())
                    .updatedOn(LocalDateTime.now())
                    .build();

            Cart cart = Cart.builder()
                    .errand(errand)
                    .product(productRepository.findById(id).orElse(null))
                    .quantity(addCartRequest.getQuantity())
                    .updatedOn(LocalDateTime.now())
                    .createdOn(LocalDateTime.now())
                    .isReady(false)
                    .build();

            errandRepository.save(errand);
            cartRepository.save(cart);


        }else {
            Errand errand = errandRepository.findByUserAndErrandStatus(user, ErrandStatus.PREPARATION).stream().findFirst().orElse(null);

            Cart cart = Cart.builder()
                    .errand(errand)
                    .product(productRepository.findById(id).orElse(null))
                    .quantity(addCartRequest.getQuantity())
                    .updatedOn(LocalDateTime.now())
                    .createdOn(LocalDateTime.now())
                    .isReady(false)
                    .build();

            errandRepository.save(errand);
            cartRepository.save(cart);

        }

    }

    public List<Cart> getAllCartsByUser(UUID userId) {

       Errand errand = errandRepository.findByUserAndErrandStatus(userRepository.findById(userId).orElse(null), ErrandStatus.PREPARATION).stream().findFirst().orElse(null);

        return errand.getCarts();

    }
}
