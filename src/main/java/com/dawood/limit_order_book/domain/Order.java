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

    public BigDecimal getRemainingQuantity(){
        return quantity.subtract(filledQuantity);
    }

    public boolean isFilled(){
        return getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0;
    }

    public void fill(BigDecimal quantityToFill){
        if(quantityToFill == null || quantityToFill.compareTo(BigDecimal.ZERO) <=0) return;

        BigDecimal newQuantityFill = filledQuantity.add(quantityToFill);

        if(newQuantityFill.compareTo(quantityToFill) > 0){
            filledQuantity = quantityToFill;
        }else {
            filledQuantity =newQuantityFill;
        }
    }
}
