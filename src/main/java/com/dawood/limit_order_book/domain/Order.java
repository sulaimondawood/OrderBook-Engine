package com.dawood.limit_order_book.domain;


import com.dawood.limit_order_book.domain.enums.OrderStatus;
import com.dawood.limit_order_book.domain.enums.OrderType;
import com.dawood.limit_order_book.domain.enums.Side;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Order {
    private String id;

    private Side side;

    private OrderType type;

    private BigDecimal price;

    private BigDecimal quantity;

    private BigDecimal filledQuantity;

    private OrderStatus status;

    private LocalDateTime timestamp;

    private String userId;
}
