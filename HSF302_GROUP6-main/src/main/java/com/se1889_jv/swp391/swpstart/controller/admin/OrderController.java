package com.se1889_jv.swp391.swpstart.controller.admin;


import com.se1889_jv.swp391.swpstart.domain.Order;
import com.se1889_jv.swp391.swpstart.domain.OrderDetail;
import com.se1889_jv.swp391.swpstart.domain.Product;
import com.se1889_jv.swp391.swpstart.service.OrderDetailService;
import com.se1889_jv.swp391.swpstart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    @GetMapping("/admin/order")
    public String listOrder(Model model,
                            @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                // convert from String to int
                page = Integer.parseInt(pageOptional.get());
            } else {
                // page = 1
            }
        } catch (Exception e) {
            // page = 1
            // TODO: handle exception
        }

        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<Order> ordersPage = this.orderService.fetchAllOrders(pageable);
        List<Order> orders = ordersPage.getContent();

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        return "admin/order/show";
    }

    @GetMapping("/admin/order/{id}")
    public String getOrderDetailPage(Model model, @PathVariable long id) {
        Order order = this.orderService.fetchOrderById(id).get();
        List<OrderDetail> orderDetail = orderDetailService.getOrderDetailByOrder(order);
        model.addAttribute("order", order);
        model.addAttribute("id", id);
        model.addAttribute("orderDetails", orderDetail);
        return "admin/order/detail";
    }

    @GetMapping("/admin/revenue")
    public String showRevenueDashboard(Model model,
                                       @RequestParam(value = "fromDate", required = false) String fromDateStr,
                                       @RequestParam(value = "toDate", required = false) String toDateStr,
                                       @RequestParam(value = "filterType", required = false, defaultValue = "day") String filterType) {

        // Chuyển đổi từ chuỗi sang LocalDate
        LocalDate fromDate = (fromDateStr != null) ? LocalDate.parse(fromDateStr) : LocalDate.now().minusMonths(1);
        LocalDate toDate = (toDateStr != null) ? LocalDate.parse(toDateStr) : LocalDate.now();

        double totalRevenue = orderService.getTotalRevenue();
        long totalOrders = orderService.getTotalOrders();
        Product bestSellingProduct = orderService.getBestSellingProduct();
        System.out.println(bestSellingProduct);
        System.out.println(totalRevenue);
        System.out.println(totalOrders);
        List<Object[]> chartDataList = orderService.getRevenue(fromDateStr, toDateStr, filterType);

        model.addAttribute("chartData", chartDataList);


        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("bestSellingProduct", bestSellingProduct);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("filterType", filterType);

        return "admin/order/revenue";
    }


}
