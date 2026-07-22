package com.dawood.limit_order_book.domain;

import com.dawood.limit_order_book.domain.enums.OrderStatus;
import com.dawood.limit_order_book.domain.enums.OrderType;
import com.dawood.limit_order_book.domain.enums.Side;

import java.math.BigDecimal;
import java.util.*;

public class OrderBook {
    private TreeMap<BigDecimal, LinkedList<Order>> bids = new TreeMap<>(Comparator.reverseOrder());

    private TreeMap<BigDecimal, LinkedList<Order>> asks = new TreeMap<>();

    private Map<UUID, Order> orderMap = new HashMap<>();

    public void processOrder(Order order) {
        Side side = order.getSide();
        OrderType type = order.getType();

        if (side.equals(Side.BUY)) {
            matchOrder(order, asks, true);
        } else {
            matchOrder(order, bids, false);
        }
    }

    public void cancelOrder(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order id is missing");
        }

    }

    private void matchOrder(Order incomingOrder, TreeMap<BigDecimal, LinkedList<Order>> ordersTree, boolean isBidding) {
        BigDecimal incomingPrice = incomingOrder.getPrice();
        BigDecimal filledQuantity = incomingOrder.getFilledQuantity();
        BigDecimal quantity = incomingOrder.getQuantity();

        if (ordersTree.isEmpty()) {
            LinkedList<Order> newOrderList = new LinkedList<>();
            newOrderList.push(incomingOrder);
            ordersTree.put(incomingOrder.getPrice(), newOrderList);
            orderMap.put(incomingOrder.getId(), incomingOrder);
            return;
        }

        while ((filledQuantity.compareTo(quantity) <= 0 && !ordersTree.isEmpty()) {
            BigDecimal bestPrice = ordersTree.firstKey();

            boolean priceCross = isBidding ? incomingPrice.compareTo(bestPrice) >= 0 :
                    incomingPrice.compareTo(bestPrice) <= 0;

            if (!priceCross) {
                ordersTree.computeIfAbsent(incomingOrder.getPrice(), order -> new LinkedList<>())
                        .addLast(incomingOrder);
                orderMap.put(incomingOrder.getId(), incomingOrder);
                break;
            }

            LinkedList<Order> ordersQueue = ordersTree.get(bestPrice);

            while (!ordersQueue.isEmpty() && filledQuantity.compareTo(quantity) <= 0) {

                Order restingOrder = ordersQueue.peekFirst();

                BigDecimal remainingRestingOrderQty = restingOrder.getQuantity().subtract(restingOrder.getFilledQuantity());
                BigDecimal remainingIncomingOrderQty = incomingOrder.getQuantity().subtract(incomingOrder.getFilledQuantity());
                BigDecimal matchQty = remainingIncomingOrderQty.min(remainingRestingOrderQty);

                incomingOrder.setFilledQuantity(incomingOrder.getQuantity().subtract(matchQty));
                restingOrder.setFilledQuantity(matchQty);

                if (restingOrder.getFilledQuantity().compareTo(restingOrder.getQuantity()) == 0) {
                    restingOrder.setStatus(OrderStatus.FILLED);
                    ordersQueue.removeFirst();
                } else {
                    restingOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
                }

                if (incomingOrder.getFilledQuantity().compareTo(incomingOrder.getQuantity()) == 0) {
                    incomingOrder.setStatus(OrderStatus.FILLED);
                } else {
                    incomingOrder.setStatus(OrderStatus.PARTIALLY_FILLED);
                }

                if (ordersQueue.isEmpty()) {
                    ordersTree.remove(bestPrice);
                }

            }

            if (incomingOrder.getFilledQuantity().compareTo(incomingOrder.getQuantity()) < 0) {
                Side side = incomingOrder.getSide();
                TreeMap<BigDecimal, LinkedList<Order>> ordersTreeSide = side == Side.BUY ? asks : bids;

                ordersTreeSide.computeIfAbsent(incomingOrder.getPrice(), (order) -> new LinkedList<>())
                        .addLast(incomingOrder);

            }

        }

    }
}
