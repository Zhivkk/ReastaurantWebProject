package app.Errand;


import app.APIMessage.MailClient;
import app.APIMessage.MailRequest;
import app.Cart.Cart;
import app.Cart.CartRepository;
import app.Product.ProductCategory;
import app.Product.ProductRepository;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.repository.UserRepository;
import app.web.dto.AddCartRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ErrandService {

    private final ErrandRepository errandRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MailClient mailClient;

    public ErrandService(ErrandRepository errandRepository, CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository, MailClient mailClient) {
        this.errandRepository = errandRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.mailClient = mailClient;
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

    @Transactional
    public void finishErrandFromUserSide(UUID id) {

        BigDecimal totalPrice = getTotalPrice(id);

        User user = userRepository.findById(id).orElse(null);
        user.setAccountAmount(user.getAccountAmount().subtract(totalPrice));
        userRepository.save(user);

        Errand errand = errandRepository.findByUserAndErrandStatus(user, ErrandStatus.PREPARATION).stream().findFirst().orElse(null);
        errand.setErrandStatus(ErrandStatus.FOR_EXECUTION);
        errand.setPrice(totalPrice);
        errandRepository.save(errand);

//        MailRequest mailRequest = MailRequest.builder()
//                .recipient(user.getEmail())
//                .subject("Order Placed")
//                .body("Здравейте! Вие успешно регистрирахте поръчка в ресторант Вистоди")
//                .build();
//        mailClient.sendMail(mailRequest);

    }

    public List<Errand> getAllErrandsForChefs() {

        List<Errand> errands = errandRepository.findByErrandStatus(ErrandStatus.FOR_EXECUTION);
        List<Errand> errandsFiltered = new ArrayList<>();
        Boolean isReadyForAdd = false;

        for (Errand errand : errands) {
            List<Cart> carts = errand.getCarts();
            for (Cart cart : carts) {
                if (cart.getIsReady().equals(false) && !cart.getProduct().getProductCategory().equals(ProductCategory.ALCOHOL) || !cart.getProduct().getProductCategory().equals(ProductCategory.SOFT_DRINK)) {
                    isReadyForAdd = true;
                }
            }
            if (isReadyForAdd) {errandsFiltered.add(errand);}

            isReadyForAdd = false;
        }
        return errandsFiltered;

    }

    public List<Cart> getCartsByErrandIdForChef(UUID id) {

        Errand errand = errandRepository.findById(id).orElse(null);

        List<Cart> carts = errand.getCarts().stream()
                .filter(cart -> !cart.getProduct().getProductCategory().equals(ProductCategory.ALCOHOL)
                        && !cart.getProduct().getProductCategory().equals(ProductCategory.SOFT_DRINK)
                        && cart.getIsReady().equals(false))
                .toList();

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
        List<Errand> errands = errandRepository.findByErrandStatus(ErrandStatus.FOR_EXECUTION);
        List<Errand> errandsFiltered = new ArrayList<>();
        Boolean isReadyForAdd = false;

        for (Errand errand : errands) {
            List<Cart> carts = errand.getCarts();
            for (Cart cart : carts) {
                if (cart.getIsReady().equals(false) && cart.getProduct().getProductCategory().equals(ProductCategory.ALCOHOL)) {
                    isReadyForAdd = true;
                }else if (cart.getIsReady().equals(false) && cart.getProduct().getProductCategory().equals(ProductCategory.SOFT_DRINK)) {
                    isReadyForAdd = true;
                }
            }
            if (isReadyForAdd) {errandsFiltered.add(errand);}

            isReadyForAdd = false;
        }
        return errandsFiltered;



//        List<Errand> errands = errandRepository.findByErrandStatus(ErrandStatus.FOR_EXECUTION).stream()
//                .filter(errand -> errand.getCarts().stream()
//                        .anyMatch(cart -> cart.getIsReady().equals(false)))
//                .filter(errand -> errand.getCarts().stream()
//                        .anyMatch(cart -> cart.getProduct().getProductCategory().equals(ProductCategory.ALCOHOL)
//                                    || cart.getProduct().getProductCategory().equals(ProductCategory.SOFT_DRINK)))
//                .toList();
//

    }

    public List<Cart> getCartsByErrandIdForBartender(UUID id) {

        Errand errand = errandRepository.findById(id).orElse(null);

        List<Cart> carts = errand.getCarts().stream()
                .filter(cart -> cart.getProduct().getProductCategory().equals(ProductCategory.ALCOHOL)
                        || cart.getProduct().getProductCategory().equals(ProductCategory.SOFT_DRINK))
                .filter(cart -> !cart.getIsReady().equals(true))
                .toList();

        return carts;

    }

    public String getErrandId(UUID id) {

        Cart cart = cartRepository.findById(id).orElse(null);

        return cart.getErrand().getId().toString();
    }

    public List<Errand> getAllErrandsForDeliverry() {

        List<Errand> errands = errandRepository.findByErrandStatus(ErrandStatus.FOR_DELIVERY);

        return errands;
    }

    public void finishDeliverryStatus(UUID id) {

        Errand errand = errandRepository.findById(id).orElse(null);

        errand.setErrandStatus(ErrandStatus.DELIVERED);
        errandRepository.save(errand);

    }
}
