package app.Errand;


import app.Cart.Cart;
import app.Cart.CartRepository;
import app.Product.ProductRepository;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.repository.UserRepository;
import app.web.dto.AddCartRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        if(addCartRequest.getQuantity() > 0) {
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


            } else {
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

    }

    public List<Cart> getAllCartsByUser(UUID userId) {

       Errand errand = errandRepository
               .findByUserAndErrandStatus(userRepository.findById(userId).orElse(null), ErrandStatus.PREPARATION)
               .stream().findFirst().orElse(null);

        return errand.getCarts();

    }

    public void removeFromCart( UserInfo userInfo , UUID Id) {

        Errand errand = errandRepository
                .findByUserAndErrandStatus(userRepository.findById(userInfo.getUserId()).orElse(null), ErrandStatus.PREPARATION)
                .stream()
                .findFirst()
                .orElse(null);

        List<Cart> carts = errand.getCarts();

        carts.stream().filter(cart -> cart.getId().equals(Id)).findFirst().ifPresent(cartRepository::delete);
    }

    public BigDecimal getTotalPrice(UUID id) {

        List<Cart> carts = getAllCartsByUser(id);

        BigDecimal totalPrice = new BigDecimal(0);

        for (Cart cart : carts) {
            totalPrice = totalPrice.add(cart.getProduct().getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }

        return totalPrice;

    }

    public void finishErrandFromUserSide(UUID id) {

        BigDecimal totalPrice = getTotalPrice(id);

        User user = userRepository.findById(id).orElse(null);
        user.setAccountAmount(user.getAccountAmount().subtract(totalPrice));
        userRepository.save(user);

        Errand errand = errandRepository.findByUserAndErrandStatus(user, ErrandStatus.PREPARATION).stream().findFirst().orElse(null);
        errand.setErrandStatus(ErrandStatus.FOR_EXECUTION);
        errand.setPrice(totalPrice);
        errandRepository.save(errand);

    }

    public List<Errand> getAllErrandsForChefs() {

        return errandRepository.findByErrandStatus(ErrandStatus.FOR_EXECUTION).stream()
                .filter(errand -> errand.getCarts().stream()
                        .anyMatch(cart -> {
                            if (cart.getProduct() != null && cart.getProduct().getProductCategory() != null) {
                                String category = String.valueOf(cart.getProduct().getProductCategory());
                                return category.equals("SOUP") || category.equals("SALLAD") || category.equals("APPETIZER") || category.equals("MAIN_COURSE") || category.equals("DESSERT") || category.equals("OTHER") || category.equals("SPECIALS");
                            }
                            return false;
                        }))
                .toList();

    }

    public List<Cart> getCartsByErrandId(UUID id) {

        Errand errand = errandRepository.findById(id).orElse(null);
        List<Cart> carts = errand.getCarts();
        return carts;

    }

    public void checkStatus(UUID id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        cart.setIsReady(true);
        cartRepository.save(cart);

        Errand errand = cart.getErrand();
        List <Cart> carts = errand.getCarts().stream().filter(c -> !c.getIsReady()).toList();
        Boolean isAllReady = carts.isEmpty();
        if (isAllReady) {
            errand.setErrandStatus(ErrandStatus.FOR_DELIVERY);
            errandRepository.save(errand);
        }
    }

    public List<Errand> getAllErrandsForBartender() {
        return errandRepository.findByErrandStatus(ErrandStatus.FOR_EXECUTION).stream()
                .filter(errand -> errand.getCarts().stream()
                        .anyMatch(cart -> {
                            if (cart.getProduct() != null && cart.getProduct().getProductCategory() != null) {
                                String category = String.valueOf(cart.getProduct().getProductCategory());
                                return category.equals("SOFT_DRINK") || category.equals("ALCOHOL");
                            }
                            return false;
                        }))
                .toList();
    }
}
