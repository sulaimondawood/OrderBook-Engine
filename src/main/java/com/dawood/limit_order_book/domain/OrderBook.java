package com.dawood.limit_order_book.domain;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

public class OrderBook {
    private TreeMap<BigDecimal, LinkedList<Order>> bids = new TreeMap<>(Comparator.reverseOrder());

    private TreeMap<BigDecimal, LinkedList<Order>> asks = new TreeMap<>();
}
