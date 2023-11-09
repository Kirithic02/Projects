package com.supermarket.dao;

import java.util.List;
import java.util.Map;

import com.supermarket.model.custom.order.OrderFilterList;
import com.supermarket.model.entity.OrderDetails;
import com.supermarket.model.entity.OrderLineItemDetails;

public interface OrderDAO {

	void orderProduct(OrderLineItemDetails item);

	void createOrder(OrderDetails order);

	void updateOrderStatus(int orderId, String orderStatus);

	void updateOrderItemStatus(int orderId, String orderStatus);

	OrderDetails getOrderById(int orderId);

	List<OrderLineItemDetails> getOrderItemListByOrderId(int orderId);

	OrderLineItemDetails getOrderItemByOlidId(int orderId, int productId);

	Map<String, Object> getOrderItemListDTOByOrderId(Integer orderId);

	Long getProductReservedPackageCount(Integer productID, Integer orderId);

	Map<String, Object> listOrder(OrderFilterList orderFilterList);
}
