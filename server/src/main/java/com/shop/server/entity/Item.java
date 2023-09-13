package com.shop.server.entity;

import com.shop.server.common.constant.ItemSellStatus;
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
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Table(name = "item")
@Entity
public class Item extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
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

	public void removeStock(int stockNum) throws Exception {
		int restStock = this.stockNum - stockNum;

		if (restStock < 0)
			throw new Exception("상품의 재고가 부족합니다. \n(현재 재고수량 : " + this.stockNum + ")");

		this.stockNum = restStock;
	}

	public void addStock(int stockNumber) {
		this.stockNum += stockNumber;
	}

}
