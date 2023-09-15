package com.shop.server.unit.cotroller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.shop.server.common.jwt.JwtService;
import com.shop.server.controller.UserController;
import com.shop.server.custom.WithMockCustomUser;
import com.shop.server.entity.User;
import com.shop.server.service.UserService;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userService;

	@MockBean
	private JwtService jwtService;
	
    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
    @Nested
	class GetUserAll {
		private List<User> testUsers;

		@BeforeEach
		void beforeEach() {
			
            this.testUsers = new ArrayList<>();
			for (long id = 100; id > 0; id--) {
				this.testUsers.add(
						User.builder()
						.id(id)
						.username("test"+id)
						.email("test" + id + "@test.com")
						.password("12345678")
						.roles("ROLE_USER")
						.build());
			}
		}

		@Test
		@DisplayName("페이징 조회")
		@WithMockCustomUser
		void getUsers() throws Exception {
			int page = 1;
			int size = 10;

	        Pageable pageable  = (Pageable) PageRequest.of(page, size);
            int startIdx = (int) pageable.getOffset();	
            int endIdx = Math.min(startIdx + pageable.getPageSize(), testUsers.size());
            
			Page<User> pageRes = new PageImpl<>(this.testUsers.subList(startIdx, endIdx),pageable,endIdx);
            
			//given
			when(userService.getUsers(pageable))
			.thenReturn(pageRes);
			

			// when
			ResultActions resultActions = mockMvc
					.perform(get("/api/admin/users?page=" + page));

			// then
			resultActions.andExpect(status().isOk());
			int resultIdx = 0;
			int userIdx = startIdx;

			for (; resultIdx < size; resultIdx++, userIdx++) {
				String base = String.format("$.content[%d]", resultIdx);
				resultActions.andExpect(jsonPath(base + ".id").value(this.testUsers.get(userIdx).getId().toString()))
					.andExpect(jsonPath(base + ".username").value(this.testUsers.get(userIdx).getUsername()))
					.andExpect(jsonPath(base + ".email").value(this.testUsers.get(userIdx).getEmail()))
						.andExpect(jsonPath(base + ".roles").value(this.testUsers.get(userIdx).getRoles()));
			}

			var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
			verify(userService, times(1)).getUsers(pageableCaptor.capture());
			var passedPageable = pageableCaptor.getValue();
            assertThat(passedPageable.equals(pageable)).isTrue();
		}
	}

}
