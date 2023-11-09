package com.supermarket.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.order.CustomerOrderDTO;
import com.supermarket.model.custom.order.OrderFilterList;
import com.supermarket.service.OrderService;

@RestController
@RequestMapping(value = "/order")
public class OrderRestController {

	/**
	 * Instance of {@link OrderServiceImpl}
	 */
	@Autowired
	private OrderService orderService;

	/**
	 * Place New Order
	 * 
	 * @param customerOrderDTO
	 * @return
	 */
	@RequestMapping(value = "/orderProduct", method = RequestMethod.POST)
	public ResponseEntity<Response> orderProduct(@RequestBody CustomerOrderDTO customerOrderDTO) {
		return new ResponseEntity<>(orderService.orderProduct(customerOrderDTO), HttpStatus.OK);
	}

	/**
	 * Update the order with the 'New' status
	 * 
	 * @param customerOrderDTO
	 * @return
	 */
	@RequestMapping(value = "/updateOrder", method = RequestMethod.POST)
	public ResponseEntity<Response> updateOrder(@RequestBody CustomerOrderDTO customerOrderDTO) {
		return new ResponseEntity<>(orderService.updateOrder(customerOrderDTO), HttpStatus.OK);
	}

	/**
	 * Update Order Status
	 * 
	 * @param orderId
	 * @param newStatus
	 * @return
	 */
	@RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
	public ResponseEntity<?> updateStatus(@RequestParam Integer orderId,
			@RequestParam("orderStatus") String orderStatus) {
		return new ResponseEntity<>(orderService.updateStatus(orderId, orderStatus), HttpStatus.OK);
	}

	/**
	 * Retrieves OrderItemList Using OrderId
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/showOrder", method = RequestMethod.GET)
	public ResponseEntity<Response> show(@RequestParam() Integer orderId) {
		return new ResponseEntity<>(orderService.getOrderItemListByOrderId(orderId), HttpStatus.OK);
	}

	/**
	 * Retrieves All OrderItemList
	 * 
	 * @param orderFilterList
	 * @return
	 */
	@RequestMapping(value = "/listOrder", method = RequestMethod.GET)
	public ResponseEntity<FilteredResponse> listOrder(@RequestBody OrderFilterList orderFilterList) {
		return new ResponseEntity<>(orderService.listOrder(orderFilterList), HttpStatus.OK);
	}
}
