package com.shop.server.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.server.common.constant.ItemSellStatus;
import com.shop.server.dto.ItemMainDto;
import com.shop.server.dto.ItemSearchDto;
import com.shop.server.dto.QItemMainDto;
import com.shop.server.entity.Item;
import com.shop.server.entity.QItem;
import com.shop.server.entity.QItemImg;
import com.shop.server.entity.User;

import jakarta.persistence.EntityManager;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

	private JPAQueryFactory queryFactory;

	public ItemRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	// 상품 판매상태 기준 조회
	private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
		return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
	}

	// 상품 등록일 기준 조회
	private BooleanExpression crtDtsAfter(String searchDateType) {

		LocalDateTime dateTime = LocalDateTime.now();

		if (searchDateType == null || searchDateType.equals("all"))
			return null;
		else if (searchDateType.equals("1d"))
			dateTime = dateTime.minusDays(1);
		else if (searchDateType.equals("1w"))
			dateTime = dateTime.minusWeeks(1);
		else if (searchDateType.equals("1m"))
			dateTime = dateTime.minusMonths(1);
		else if (searchDateType.equals("6m"))
			dateTime = dateTime.minusMonths(6);

		return QItem.item.crtDt.after(dateTime);
	}

	// 상품명 or 등록자 기준 조회
	private BooleanExpression searchByLike(String searchBy, String searchQuery) {

		if (searchBy != null) {
			if (searchBy.equals("itemName"))
				return QItem.item.itemName.like("%" + searchQuery + "%");
			else if (searchBy.equals("createdBy"))
				return QItem.item.crtName.like("%" + searchQuery + "%");
		}
		return null;
	}

	// 상품 등록자 기준 조회
	private BooleanExpression searchByCrtId(String crtNmae) {
		return QItem.item.crtName.eq(crtNmae);
	}

	// 상품명 기준 조회
	private BooleanExpression itemNameLike(String searchQuery) {
		return searchQuery == null || searchQuery.equals("") ? null : QItem.item.itemName.like("%" + searchQuery + "%");
	}

	// 상품 관리 조회
	@Override
	public Page<Item> getItemManagePage(ItemSearchDto itemSearchDto, Pageable pageable, User user) {
		List<Item> content;

		if (user.getRoles().contains("ROLE_ADMIN")) {
			content = queryFactory.selectFrom(QItem.item)
					.where(crtDtsAfter(itemSearchDto.getSearchDateType()),
							searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
							searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
					.orderBy(QItem.item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize())
					.fetch();


			return new PageImpl<>(content, pageable, content.size());
		} else {
			content = queryFactory.selectFrom(QItem.item)
					.where(crtDtsAfter(itemSearchDto.getSearchDateType()),
							searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
							searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()),
							searchByCrtId(user.getUsername()))
					.orderBy(QItem.item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize())
					.fetch();
			return new PageImpl<>(content, pageable, content.size());
			
			
		}
	}

	// 상품 메인 페이지 조회
	@Override
	public Page<ItemMainDto> getItemMainPage(ItemSearchDto itemSearchDto, Pageable pageable) {
		QItem item = QItem.item;
		QItemImg itemImg = QItemImg.itemImg;

		List<ItemMainDto> content = queryFactory
				.select(new QItemMainDto(item.id, item.itemName, item.itemDetail, itemImg.imgUrl, item.price))
				.from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
				.where(itemNameLike(itemSearchDto.getSearchQuery())).orderBy(item.id.desc())
				.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
		
		return new PageImpl<ItemMainDto>(content, pageable, content.size());
	}

}
