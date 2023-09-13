package com.shop.server.repository;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.server.common.constant.ItemSellStatus;
import com.shop.server.entity.Item;
import com.shop.server.entity.QItem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
	
@SpringBootTest
public class ItemRepositoryTest {


	@Autowired
	ItemRepository itemRepository;
	
	@PersistenceContext
	EntityManager em;

	@DisplayName("--- 상품 저장 테스트1 ---")
	public void createItemTest() {

		for (int i = 0; i < 10; i++) {

			Item item = new Item();
			item.setItemName("Test Item" + i);
			item.setPrice(10000 * i);
			item.setItemDetail("Test Desc" + i);
			item.setItemSellStatus(ItemSellStatus.SELL);
			item.setStockNum(100 * i);

			itemRepository.save(item);
		}
	}
	
	@DisplayName("--- 상품 저장 테스트2 ---")
	public void createItemTest2() {

		for (int i = 0; i < 7; i++) {
			Item item = new Item();
			item.setItemName("Test Item" + i);
			item.setPrice(10000 * i);
			item.setItemDetail("Test Desc" + i);
			item.setItemSellStatus(ItemSellStatus.SELL);
			item.setStockNum(100 * i);

			itemRepository.save(item);
		}
		
		for (int i = 7; i < 10; i++) {
			Item item = new Item();
			item.setItemName("Test Item" + i);
			item.setPrice(10000 * i);
			item.setItemDetail("Test Desc" + i);
			item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
			item.setStockNum(100 * i);

			itemRepository.save(item);
		}
	}

	@Test
	@DisplayName("--- 상품명 조회 테스트 ---")
	public void findByItemList() {

		this.createItemTest();
		
		List<Item> ItemList = itemRepository.findByItemName("Test Item1");
		for(Item item : ItemList) {
			System.out.println(item.toString());
		}
		
	}
	
	@Test
	@DisplayName("--- 상품명, 상품상세설명 or 조회 테스트 ---")
	public void findByItemNameOrProductDetail() {

		this.createItemTest();
		
		List<Item> ItemList = itemRepository.findByItemNameOrItemDetail("Test Item2","Test Desc3");
		for(Item item : ItemList) {
			System.out.println(item.toString());
		}
		
	}
	
	@Test
	@DisplayName("--- 가격 LessThan 조회 테스트 ---")
	public void findByPriceLessThanTest() {

		this.createItemTest();
		
		List<Item> itemList = itemRepository.findByPriceLessThan(60000);
		for(Item item : itemList) {
			System.out.println(item.toString());
		}
		
	}
	
	@Test
	@DisplayName("--- 가격 LessThan 내림차순 조회 테스트 ---")
	public void findByPriceLessThanOrderByPriceTest() {

		this.createItemTest();
		
		List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(60000);
		for(Item item : itemList) {
			System.out.println(item.toString());
		}
		
	}
	
	@DisplayName("--- 상품상세설명 조회 테스트 ---")
	public void findByItemDetail() {

		this.createItemTest();
		
		List<Item> productList = itemRepository.findByProdctDetail("Desc");
		for(Item product : productList) {
			System.out.println(product.toString());
		}
		
	}

	@Test
	@DisplayName("--- QueryDsl 조회 테스트1 ---")
	public void queryDslTest() {
		
		this.createItemTest();
		
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		QItem qItem = QItem.item;
		
		JPAQuery<Item> query = queryFactory.selectFrom(qItem)
				.where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
				.where(qItem.itemDetail.like("%" + "Desc" + "%"))
				.orderBy(qItem.price.desc());
		
		List<Item> productList = query.fetch();

		for(Item product : productList) {
			System.out.println(product.toString());
		}
		
	}
	
	@Test
	@DisplayName("--- QueryDsl 조회 테스트2 ---")
	public void queryDslTest2(){
		
		this.createItemTest2();
		
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		QItem item = QItem.item;
		
		String productDetail = "Desc";
		int price = 10000;
		//String productSellStat = "SELL";
		
		booleanBuilder.and(item.itemDetail.like("%" + productDetail + "%"));
		booleanBuilder.and(item.price.gt(price));
		booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
	
		Pageable pagealbe = PageRequest.of(0, 5);
		Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pagealbe);
		
		System.out.println("total elements : " + itemPagingResult.getTotalElements());
		
		List<Item> resultProductList= itemPagingResult.getContent();
		
		for(Item p : resultProductList) {
			System.out.println(p.toString());
		}
		
	}
	

	
	
}
