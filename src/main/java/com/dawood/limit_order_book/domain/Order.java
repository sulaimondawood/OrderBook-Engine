package com.dawood.limit_order_book.domain;


import com.dawood.limit_order_book.domain.enums.OrderStatus;
import com.dawood.limit_order_book.domain.enums.OrderType;
import com.dawood.limit_order_book.domain.enums.Side;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Order {
    private UUID id;

    private UUID orderId;

    private Side side;

    private OrderType type;

    private BigDecimal price;

    private BigDecimal quantity;

    private BigDecimal filledQuantity;

    private OrderStatus status;

    private LocalDateTime timestamp;

    private String userId;
}
