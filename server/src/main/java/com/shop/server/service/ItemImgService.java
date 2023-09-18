package com.shop.server.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.entity.ItemImg;
import com.shop.server.repository.ItemImgRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

	@Value("${application.itemImgLocation}")
	private String itemImgLocation;

	private final ItemImgRepository itemImgRepository;

	private final FileService fileService;

	public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws IOException, Exception {
		String oriImgName = itemImgFile.getOriginalFilename();
		String imgName = "";
		String imgUrl = "";


		
		// fileUpload
		if (oriImgName != null && !oriImgName.equals("")) {
			imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
			imgUrl = "/images/item/" + imgName;
		}

		// 상품 이미지 정보 저장
		itemImg.updateItemImg(imgName, oriImgName, imgUrl);
		itemImgRepository.save(itemImg);
	}

	public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
		if (!itemImgFile.isEmpty()) {
			ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
					.orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_GET));

			String tempName = savedItemImg.getImgName();
			if (tempName != null && tempName != "") {
				fileService.delteFile(itemImgLocation + "/" + tempName);
			}

			String oriImgName = itemImgFile.getOriginalFilename();
			String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
			String imgUrl = "/images/item/" + imgName;

			savedItemImg.updateItemImg(imgName, oriImgName, imgUrl);
		}
	}

	public void deleteItemImg(Long itemImgId) throws Exception {

		ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_ITEM_TO_GET));

		try {
			String tempName = savedItemImg.getImgName();
			if (tempName != null && tempName != "") {
				fileService.delteFile(itemImgLocation + "/" + tempName);
			}
		} catch (Exception e) {
			throw new CustomException(ExceptionCode.FILE_DELETE_ERROR);
		} finally {
			itemImgRepository.deleteById(itemImgId);
		}

	}
}
