package com.shop.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.ItemFormDto;
import com.shop.server.dto.ItemImgDto;
import com.shop.server.dto.ItemMainDto;
import com.shop.server.dto.ItemSearchDto;
import com.shop.server.entity.Item;
import com.shop.server.entity.ItemImg;
import com.shop.server.entity.User;
import com.shop.server.repository.ItemImgRepository;
import com.shop.server.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final ItemImgService itemImgService;
	private final ItemImgRepository itemImgRepository;

	// 상품 생성
	@Transactional
	public Item saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws IOException, Exception {
		Item item = itemFormDto.createItem();
		itemRepository.save(item);

		for (int i = 0; i < itemImgFileList.size(); i++) {
			ItemImg itemImg = new ItemImg();
			itemImg.setItem(item);
			if (i == 0)
				itemImg.setRepImgYn("Y");
			else
				itemImg.setRepImgYn("N");
			itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
		}
		return item;
	}

	// 상품 수정
	@Transactional
	public Item updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

		Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(() -> new Exception("해당 상품을 찾을 수 없습니다."));
		item.updateItem(itemFormDto);

		List<Long> imgImgDtoUpdateList = itemFormDto.getUpdateItemImgIds();
		
		if (itemImgFileList != null) {

			for (int i = 0; i < itemImgFileList.size(); i++) {
				if (i < imgImgDtoUpdateList.size()) {
					itemImgService.updateItemImg(imgImgDtoUpdateList.get(i), itemImgFileList.get(i));
				} else {
					ItemImg itemImg = new ItemImg();
					itemImg.setItem(item);
					itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
				}
			}
		}

		List<Long> imgImgDtoDeleteList = itemFormDto.getDeleteItemImgIds();
		
		for (int i = 0; i < imgImgDtoDeleteList.size(); i++) {
				itemImgService.deleteItemImg(imgImgDtoDeleteList.get(i));
		}
		return item;

	}

	// 단일 상품 조회
	@Transactional(readOnly = true)
	public ItemFormDto getItemDtl(Long itemId) throws Exception {

		List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
		List<ItemImgDto> itemImgDtoList = new ArrayList<>();

		for (ItemImg itemImg : itemImgList) {
			ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
			itemImgDtoList.add(itemImgDto);
		}

		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_GET));
		ItemFormDto itemFormDto = ItemFormDto.of(item);
		itemFormDto.setItemImgDtoList(itemImgDtoList);

		return itemFormDto;
	}

	@Transactional(readOnly = true)
	public Page<Item> getItemManagePage(ItemSearchDto itemSearchDto, Pageable pageable, User user ){
		return itemRepository.getItemManagePage(itemSearchDto, pageable, user);
	}
	
	@Transactional(readOnly = true)
	public Page<ItemMainDto> getItemMainPage(ItemSearchDto itemSearchDto, Pageable pageable ){
		return itemRepository.getItemMainPage(itemSearchDto, pageable);
	}
	
	
}
