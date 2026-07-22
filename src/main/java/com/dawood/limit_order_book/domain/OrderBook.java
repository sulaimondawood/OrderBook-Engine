package com.dawood.limit_order_book.domain;

import com.dawood.limit_order_book.domain.enums.OrderStatus;
import com.dawood.limit_order_book.domain.enums.OrderType;
import com.dawood.limit_order_book.domain.enums.Side;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderBook {
    private TreeMap<BigDecimal, LinkedList<Order>> bids = new TreeMap<>(Comparator.reverseOrder());

    private TreeMap<BigDecimal, LinkedList<Order>> asks = new TreeMap<>();

//    private Map<UUID, Order> orderMap = new HashMap<>();

    public void processOrder(Order order) {

        if(order == null){
            throw new IllegalArgumentException("Invalid order");
        }

        Side side = order.getSide();
        OrderType type = order.getType();

        if (side.equals(Side.BUY)) {
            matchOrder(order, asks, true );
            bids.computeIfAbsent(order.getPrice(),k->new LinkedList<>())
                    .addLast(order);
        } else {
            matchOrder(order, bids, false);
            asks.computeIfAbsent(order.getPrice(),k->new LinkedList<>())
                    .addLast(order);
        }
    }


    private void matchOrder(Order incomingOrder, TreeMap<BigDecimal, LinkedList<Order>> oppositeOrdersTree, boolean isBidding) {

       while(!oppositeOrdersTree.isEmpty() && !incomingOrder.isFilled()){
          BigDecimal bestOppositePrice = oppositeOrdersTree.firstKey();

          boolean canMatch = isBidding? incomingOrder.getPrice().compareTo(bestOppositePrice) <=0:
                  incomingOrder.getPrice().compareTo(bestOppositePrice) >=0;

          if(!canMatch) break;

          LinkedList<Order> restingOrders = oppositeOrdersTree.get(bestOppositePrice);

          if(restingOrders.isEmpty()){
              oppositeOrdersTree.remove(bestOppositePrice);
              continue;
          }

          Order restingOrder = restingOrders.peekFirst();

          BigDecimal matchedQty = incomingOrder.getRemainingQuantity().min(restingOrder.getRemainingQuantity());

          incomingOrder.fill(matchedQty);
          restingOrder.fill(matchedQty);

           if(restingOrder.isFilled()){
               restingOrders.remove(restingOrder);
               if(restingOrders.isEmpty()){
                   oppositeOrdersTree.remove(bestOppositePrice);
               }
           }



       }



    }

}
