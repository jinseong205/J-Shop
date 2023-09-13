package com.shop.server.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;

import lombok.extern.slf4j.Slf4j;

@Service
public class FileService {

	public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) {

		UUID uuid = UUID.randomUUID();
		String extendsion = originalFileName.substring(originalFileName.lastIndexOf("."));
		String savedFileName = uuid.toString() + extendsion;
		String fileUploadFullUrl = uploadPath + "/" + savedFileName;

		try {
			FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
			fos.write(fileData);
			fos.close();
		} catch (Exception e) {
			throw new CustomException(ExceptionCode.NO_REP_ITEM_IMG);
		}

		return savedFileName;
	}

	public void delteFile(String filePath) throws Exception {
		File deleteFile = new File(filePath);

		if (deleteFile.exists()) {
			deleteFile.delete();
		}

	}
}
