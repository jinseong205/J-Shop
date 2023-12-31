package com.shop.server.entity;

import com.shop.server.common.constant.ItemSellStatus;
import com.shop.server.common.exception.CustomException;
import com.shop.server.common.exception.ExceptionCode;
import com.shop.server.dto.ItemFormDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name = "item")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ITEM_ID")
	private Long id;

	@Column(name = "ITEM_NAME", nullable = false, length = 100)
	private String itemName;

	@Column(nullable = false)
	private int price;

	@Column(name = "STOCK_NUM", nullable = false)
	private int stockNum;

	@Lob
	@Column(name = "ITEM_DETAIL")
	private String itemDetail;

	@Enumerated(EnumType.STRING)
	@Column(name = "IMG_SELL_STATUS")
	private ItemSellStatus itemSellStatus;

	public void updateItem(ItemFormDto itemFormDto) {
		this.itemName = itemFormDto.getItemName();
		this.price = itemFormDto.getPrice();
		this.stockNum = itemFormDto.getStockNum();
		this.itemDetail = itemFormDto.getItemDetail();
		this.itemSellStatus = itemFormDto.getItemSellStatus();
	}

	@Version
	private int version;
	
	public void removeStock(int stockNum) throws Exception {
		int restStock = this.stockNum - stockNum;

		if (restStock < 0)
			throw new CustomException(ExceptionCode.OUT_OF_STOCK);
			
		this.stockNum = restStock;
	}

	public void addStock(int stockNumber) {
		this.stockNum += stockNumber;
	}

}
