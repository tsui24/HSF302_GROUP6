package com.se1889_jv.swp391.swpstart.service;


import com.se1889_jv.swp391.swpstart.domain.Order;
import com.se1889_jv.swp391.swpstart.domain.OrderDetail;
import com.se1889_jv.swp391.swpstart.repository.OrderDetailRepository;
import com.se1889_jv.swp391.swpstart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public List<OrderDetail> getOrderDetailByOrder(Order order) {
        return orderDetailRepository.getOrderDetailByOrder(order);
    }
}
