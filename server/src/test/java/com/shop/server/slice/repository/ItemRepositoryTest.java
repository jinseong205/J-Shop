package com.shop.server.slice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.shop.server.entity.Item;
import com.shop.server.repository.ItemRepository;

@DataJpaTest
@EnableJpaAuditing 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {
    
	@Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 등록 (테스트에서는 +1초 오차까지 허용)")
    void autoCreatedAt() {
        // given
        var item = Item.builder()
                .id(1L)
                .itemName("테스트 상품")
                .price(10000)
                .build();
        var expected = LocalDateTime.now().plusSeconds(1);

        // when
        var savedItem = this.itemRepository.saveAndFlush(item);

        // then
        var createdAt = savedItem.getCrtDt();
        assertThat(createdAt).isNotNull();
        assertThat(createdAt).isBeforeOrEqualTo(expected);
    }

    @Test
    @DisplayName("상품 수정 (테스트에서는 +1초 오차까지 허용)")
    void autoUpdatedAt() {
        // given
        var item = Item.builder()
                .id(1L)
                .itemName("테스트 상품")
                .price(10_000)
                .build();
        item = this.itemRepository.save(item);
        var createdAt = item.getCrtDt();

        var delaySeconds = 1;
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(delaySeconds))
                .until(() -> true);
        
        var expected1 = createdAt.plusSeconds(delaySeconds);
        var expected2 = LocalDateTime.now().plusSeconds(1);

        // when
        item.setItemName("수정한 테스트 상품");
        var updatedItem = this.itemRepository.saveAndFlush(item);

        // then
        var updatedAt = updatedItem.getUpdtDt();
        assertThat(updatedAt).isNotNull();
        assertThat(updatedAt).isAfterOrEqualTo(expected1);
        assertThat(updatedAt).isBeforeOrEqualTo(expected2);
    }
}