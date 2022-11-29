package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.Product;
import uet.ktmt.myproject.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void createAdmin(User user);

    void createUser(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email, long userId) throws Throwable;

    boolean existsByCellphone(String cellphone, long userId) throws Throwable;

    User getCurrentUser() throws Throwable;

    Page<User> getAllUser(int page, int size, String keyword);

    void deleteUser(long userId);

    User getUserById(long userId) throws Throwable;

    void editUser(User user, boolean resetPassword) throws Throwable;

    String updateAvatar(long userId, String originalFilename) throws Throwable;

    void changeInfo(User convertToUser) throws Throwable;

    void changPassword(String password) throws Throwable;

    String changPasswordForUserForgetPassword(String email) throws Throwable;

    Page<Product> getMyListProduct(int page, String status) throws Throwable;

    void addWishlist(long productId) throws Throwable;

    Page<Product> getWishlist(int page) throws Throwable;

    void removeWishlist(long productId) throws Throwable;

    int getNewUser(String from, String to);

    User getUserByUsername(String username) throws Throwable;
}
