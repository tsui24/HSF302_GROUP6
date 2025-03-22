package com.se1889_jv.swp391.swpstart.controller.admin;


import com.se1889_jv.swp391.swpstart.domain.Product;
import com.se1889_jv.swp391.swpstart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RevenueController {

    private final OrderService orderService;

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

        return "admin/revenue/show";
    }
}
