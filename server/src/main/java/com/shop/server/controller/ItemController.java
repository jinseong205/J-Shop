package com.shop.server.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.shop.server.auth.PrincipalDetails;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.ItemFormDto;
import com.shop.server.dto.ItemMainDto;
import com.shop.server.dto.ItemSearchDto;
import com.shop.server.entity.Item;
import com.shop.server.entity.User;
import com.shop.server.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ItemController {
	
	private final ItemService itemService;
	
	@GetMapping(value = "api/items")
	public ResponseEntity<?> itemMain(ItemSearchDto itemSearchDto, Optional<Integer> page){
		
		Pageable pegeable = PageRequest.of(page.isPresent()? page.get(): 0,9);
		Page<ItemMainDto> items = itemService.getItemMainPage(itemSearchDto, pegeable);
		
		itemSearchDto.setItems(items);
		
	    return new ResponseEntity<>(itemSearchDto , HttpStatus.OK);
	}
	
	
	@GetMapping(value = "api/item/{id}")
	public ResponseEntity<?> item(@PathVariable long id) throws Exception {
		ItemFormDto itemFormDto = itemService.getItemDtl(id);
	    return new ResponseEntity<>(itemFormDto , HttpStatus.OK);
	}
	
	@PostMapping(value = "api/manager/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> itemNew(@RequestPart("itemFormDto") @Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestPart(name = "itemImgFile", required =  false) List<MultipartFile> itemImgFileList) throws Exception {

	    if(bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
	    }
		
		if(itemImgFileList.get(0).isEmpty()) {
			throw new CustomException(ExceptionCode.NO_REP_ITEM_IMG);
		}
		
		Item item = itemService.saveItem(itemFormDto, itemImgFileList);
	    
	    return new ResponseEntity<>(item , HttpStatus.OK);
	}

	@PutMapping(value = "api/manager/item/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> itemUpdate(@PathVariable long id, @RequestPart(name = "itemFormDto") @Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestPart(name = "itemImgFile", required =  false) List<MultipartFile> itemImgFileList) throws Exception {
	    
	    if(bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
	    }
		
		Item item = itemService.updateItem(itemFormDto, itemImgFileList);
	    return new ResponseEntity<>(item , HttpStatus.OK);
	}
	
	@GetMapping(value = "api/manager/items")
	public ResponseEntity<?> itemManage(ItemSearchDto itemSearchDto, Optional<Integer> page, @AuthenticationPrincipal PrincipalDetails principalDetails){
		
		
		Pageable pegealbe = PageRequest.of(page.isPresent()? page.get(): 0,10);
		User user = principalDetails.getUser();
		
		Page<Item> items = itemService.getItemManagePage(itemSearchDto, pegealbe, user);
		
		itemSearchDto.setItems(items);
		
	    return new ResponseEntity<>(itemSearchDto , HttpStatus.OK);
	}
	

}

	