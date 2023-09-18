package com.shop.server.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.CartDetailDto;
import com.shop.server.dto.CartItemDto;
import com.shop.server.dto.CartOrderDto;
import com.shop.server.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
	
    @GetMapping(value = "api/cart")
	public ResponseEntity<?> cart(@AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception{
		List<CartDetailDto> cartDetailList = cartService.getCartList(principalDetails);
		return new ResponseEntity<List<?>>(cartDetailList, HttpStatus.OK);
	}
	
    
	@PostMapping(value = "api/cart")
	public ResponseEntity<?> cart(@RequestBody @Validated CartItemDto cartItemDto, @AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception{
	    Long cartItemId = cartService.addCart(cartItemDto, principalDetails);
		return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
	}
	

	@PatchMapping(value = "api/cartItem/{id}")
	public ResponseEntity<?> updateCartItem(@PathVariable Long id, int count, @AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {
		
		if(count <= 0)
			throw new CustomException(ExceptionCode.NO_ORDER_ITEM_TO_GET);
		else if(!cartService.validateCartItem(id, principalDetails))
			throw new CustomException(ExceptionCode.PERMISSION_ERROR);
			
		cartService.updateCartItem(id, count);
		return new ResponseEntity<Long>(id, HttpStatus.OK);	
	}
	
	@DeleteMapping(value = "api/cartItem/{id}")
	public ResponseEntity<?> deleteCartItem(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception {
		if(!cartService.validateCartItem(id, principalDetails))
			throw new CustomException(ExceptionCode.PERMISSION_ERROR);
		cartService.deleteCartItem(id);
		return new ResponseEntity<Long>(id, HttpStatus.OK);	
	}
	
	@PostMapping(value="api/cart/orders")
	public ResponseEntity<?> orderCartItem(@RequestBody CartOrderDto cartOrderDto, @AuthenticationPrincipal PrincipalDetails principalDetails) throws Exception{
		
		List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderList();
		
		if(cartOrderDtoList == null || cartOrderDtoList.size() == 0)
			throw new CustomException(ExceptionCode.NO_ORDER_ITEM_TO_GET);
		
		for(CartOrderDto cartOrder : cartOrderDtoList) {
			if(!cartService.validateCartItem(cartOrder.getCartItemId(), principalDetails))
				throw new CustomException(ExceptionCode.PERMISSION_ERROR);
		}
		
		Long orderId = cartService.orderCartItem(cartOrderDtoList, principalDetails);
		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}
}
