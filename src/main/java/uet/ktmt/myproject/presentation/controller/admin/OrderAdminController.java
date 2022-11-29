package uet.ktmt.myproject.presentation.controller.admin;

import uet.ktmt.myproject.presentation.mapper.OrderMapper;
import uet.ktmt.myproject.presentation.response.OrderResponse;
import uet.ktmt.myproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/order")
public class OrderAdminController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/get-all")
    public String getAll(Model model
            , @RequestParam(name = "page", required = false, defaultValue = "1") int page
            , @RequestParam(name = "search", required = false, defaultValue = "") String id
            , @RequestParam(name = "from", required = false, defaultValue = "") String from
            , @RequestParam(name = "to", required = false, defaultValue = "") String to
            , @RequestParam(name = "status", required = false, defaultValue = "all") String status) throws Throwable {
        Page<OrderResponse> orderResponseList = orderService.getAll(page - 1, id, status, from, to)
                .map(OrderMapper::convertToOrderResponse);
        model.addAttribute("list_order", orderResponseList.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", orderResponseList.getTotalPages());
        model.addAttribute("keyword", id);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("status", status);
        return "admin/admin_list_order";
    }

    @GetMapping("/view/{id}")
    public String info(Model model, @PathVariable(value = "id") long orderId) throws Throwable {
        OrderResponse orderResponse = OrderMapper.convertToOrderResponse(orderService.getDetail(orderId));
        model.addAttribute("orderResponse", orderResponse);
        return "admin/admin_order_detail";
    }

    @GetMapping("/confirm-refund/{id}")
    public String confirm(@PathVariable(value = "id") long orderId, HttpServletRequest request) throws Throwable {
        orderService.confirmRefund(orderId);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/pay-seller/{id}")
    public String pay(@PathVariable(value = "id") long orderId, HttpServletRequest request) throws Throwable {
        orderService.paySeller(orderId);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}
