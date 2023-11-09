package com.supermarket.service;

import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.order.CustomerOrderDTO;
import com.supermarket.model.custom.order.OrderFilterList;

public interface OrderService {

	Response updateStatus(Integer orderId, String orderStatus);

	Response orderProduct(CustomerOrderDTO customerOrderDTO);

	Response updateOrder(CustomerOrderDTO customerOrderDTO);

	Response getOrderItemListByOrderId(Integer orderId);

	FilteredResponse listOrder(OrderFilterList orderFilterList);
}
