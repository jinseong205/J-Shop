package com.shop.server.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.dto.OrderDto;
import com.shop.server.dto.OrderHistDto;
import com.shop.server.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@GetMapping(value = "api/orders")
	public ResponseEntity<?> orderHist(Optional<Integer> page,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {

		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

		Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principalDetails, pageable);

		return new ResponseEntity<Page<OrderHistDto>>(orderHistDtoList, HttpStatus.OK);
	}
	
	@PostMapping(value = "api/order")
	public ResponseEntity<?> order(@RequestBody @Validated OrderDto createOrderDto, BindingResult bindingResult,
			@AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {

		Long orderId;
		orderId = orderService.order(createOrderDto, principalDetails);

		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}



	@PatchMapping("api/order/{orderId}")
	public ResponseEntity<?> cnacelOrder (@PathVariable Long orderId, @AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception{
		
		if(!orderService.validateOrder(orderId, principalDetails)) {
			throw new Exception("주문 취소 권한이 없습니다.");
		}
		
		orderService.cancelOrder(orderId);
		
		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}
	
}
