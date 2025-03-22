package com.se1889_jv.swp391.swpstart.service;

import com.se1889_jv.swp391.swpstart.domain.Order;
import com.se1889_jv.swp391.swpstart.domain.OrderDetail;
import com.se1889_jv.swp391.swpstart.domain.Product;
import com.se1889_jv.swp391.swpstart.domain.Status;
import com.se1889_jv.swp391.swpstart.repository.OrderDetailRepository;
import com.se1889_jv.swp391.swpstart.repository.OrderRepository;
import com.se1889_jv.swp391.swpstart.repository.ProductRepository;
import com.se1889_jv.swp391.swpstart.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final StatusRepository statusRepository;

    private final OrderDetailRepository orderDetailRepository;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> fetchAllOrders(Pageable page) {
        return this.orderRepository.findAll(page);
    }

    public Optional<Order> fetchOrderById(long id) {
        return this.orderRepository.findById(id);
    }

    public double getTotalRevenue() {
        Status completedStatus = statusRepository.findByName("COMPLETE"); // Lấy Status từ DB
        System.out.println("aaaaa "+completedStatus.getName());
        Double totalRevenue = orderRepository.getTotalRevenue(completedStatus.getId());
        System.out.println("bcd: "+totalRevenue);
        return totalRevenue != null ? totalRevenue : 0;
    }

    public long getTotalOrders() {
        Status completedStatus = statusRepository.findByName("COMPLETE"); // Lấy Status từ DB
        return orderRepository.countCompletedOrders(completedStatus);
    }

    public Product getBestSellingProduct() {
        Status completedStatus = statusRepository.findByName("COMPLETE"); // Lấy Status từ DB
        List<Object[]> result = orderRepository.findBestSellingProduct(completedStatus);
        if (!result.isEmpty()) {
            Long bestProductId = (Long) result.get(0)[0];
            return productRepository.findById(bestProductId).orElse(null);
        }
        return null;
    }

    public Map<String, Double> getRevenueByDate() {
        Status completedStatus = statusRepository.findByName("COMPLETE"); // Lấy Status từ DB
        Long statusId= completedStatus.getId();
        List<Object[]> results = orderRepository.getRevenueByDate(statusId);
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        for (Object[] row : results) {
            String date = row[0].toString();
            Double revenue = (Double) row[1];
            revenueMap.put(date, revenue);
        }
        return revenueMap;
    }

    public List<Object[]> getRevenue(String fromDate, String toDate, String type) {
        Status completedStatus = statusRepository.findByName("COMPLETE"); // Lấy Status từ DB

        Long statusId= completedStatus.getId();

        switch (type) {
            case "day":
                return orderRepository.getRevenueByDate(statusId, fromDate, toDate);
            case "month":
                return orderRepository.getRevenueByMonth(statusId, fromDate, toDate);
            case "year":
                return orderRepository.getRevenueByYear(statusId, fromDate, toDate);
            default:
                throw new IllegalArgumentException("Loại thống kê không hợp lệ");
        }
    }

    public void updateOrder(Order order) {
        Optional<Order> orderOptional = this.fetchOrderById(order.getId());
        if (orderOptional.isPresent()) {
            Order currentOrder = orderOptional.get();
            currentOrder.setStatus(order.getStatus());
            this.orderRepository.save(currentOrder);
        }
    }

    public void deleteOrderById(long id) {
        // delete order detail
        Optional<Order> orderOptional = this.fetchOrderById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderDetail> orderDetails = orderDetailRepository.getOrderDetailByOrder(order);
            for (OrderDetail orderDetail : orderDetails) {
                this.orderDetailRepository.deleteById(orderDetail.getId());
            }
        }

        this.orderRepository.deleteById(id);
    }


}
