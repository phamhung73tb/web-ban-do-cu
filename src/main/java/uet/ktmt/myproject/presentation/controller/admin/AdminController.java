package uet.ktmt.myproject.presentation.controller.admin;

import uet.ktmt.myproject.service.OrderService;
import uet.ktmt.myproject.service.ProductService;
import uet.ktmt.myproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    @GetMapping("/home")
    public String home(Model model
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "from", required = false, defaultValue = "") String from
            , @RequestParam(name = "to", required = false, defaultValue = "") String to) {
        if (from.equals("")) {
            from = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (to.equals("")) {
            to = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        int newOrder = orderService.getNewOrder(from, to);
        int newUser = userService.getNewUser(from, to);
        int newProduct = productService.getNewProduct(from, to);
        List<String> months = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        orderService.getOrderByMonth(from, to).forEach((k, v) -> {
                    months.add(k);
                    orders.add(v);
                }
        );

        model.addAttribute("newOrder", newOrder);
        model.addAttribute("newUser", newUser);
        model.addAttribute("newProduct", newProduct);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("months", months);
        model.addAttribute("orders", orders);
        return "admin/admin_home";
    }

    @GetMapping("/get-info/get-name")
    public ResponseEntity<String> getName() throws Throwable {
        return ResponseEntity.ok().body(userService.getCurrentUser().getFullName());
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "admin/admin_change_password";
    }
}