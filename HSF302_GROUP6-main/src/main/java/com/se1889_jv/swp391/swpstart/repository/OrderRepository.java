package com.se1889_jv.swp391.swpstart.repository;

import com.se1889_jv.swp391.swpstart.domain.Order;
import com.se1889_jv.swp391.swpstart.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT SUM(od.totalPrice * od.quantity) " +
            "FROM OrderDetail od " +
            "WHERE od.order.status.id = :statusId")
    Double getTotalRevenue(@Param("statusId") Long statusId);



    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countCompletedOrders(@Param("status") Status status);


    @Query("SELECT od.product.id, SUM(od.quantity) FROM OrderDetail od " +
            "WHERE od.order.status = :status " +
            "GROUP BY od.product.id " +
            "ORDER BY SUM(od.quantity) DESC LIMIT 1")
    List<Object[]> findBestSellingProduct(@Param("status") Status status);


    @Query(value = "SELECT CONVERT(DATE, o.date), SUM(od.total_price * od.quantity) " +
            "FROM orderdetails od " +
            "JOIN Orders o ON od.order_id = o.id " +
            "WHERE o.status_id = :statusId " +
            "GROUP BY CONVERT(DATE, o.date) " +
            "ORDER BY CONVERT(DATE, o.date)", nativeQuery = true)
    List<Object[]> getRevenueByDate(@Param("statusId") Long statusId);

    @Query(value = "SELECT CONVERT(DATE, o.date) AS revenue_date, SUM(od.total_price * od.quantity) " +
            "FROM orderdetails od " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE o.status_id = :statusId " +
            "AND o.date BETWEEN :fromDate AND :toDate " +
            "GROUP BY CONVERT(DATE, o.date) " +
            "ORDER BY revenue_date",
            nativeQuery = true)
    List<Object[]> getRevenueByDate(
            @Param("statusId") Long statusId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );

    @Query(value = "SELECT FORMAT(o.date, 'yyyy-MM') AS revenue_month, SUM(od.total_price * od.quantity) " +
            "FROM orderdetails od " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE o.status_id = :statusId " +
            "AND o.date BETWEEN :fromDate AND :toDate " +
            "GROUP BY FORMAT(o.date, 'yyyy-MM') " +
            "ORDER BY revenue_month",
            nativeQuery = true)
    List<Object[]> getRevenueByMonth(
            @Param("statusId") Long statusId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );
    @Query(value = "SELECT FORMAT(o.date, 'yyyy') AS revenue_year, SUM(od.total_price * od.quantity) " +
            "FROM orderdetails od " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE o.status_id = :statusId " +
            "AND o.date BETWEEN :fromDate AND :toDate " +
            "GROUP BY FORMAT(o.date, 'yyyy') " +
            "ORDER BY revenue_year",
            nativeQuery = true)
    List<Object[]> getRevenueByYear(
            @Param("statusId") Long statusId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );



}
