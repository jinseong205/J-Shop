# Jshop

**개발 기간**

2023.06 ~ 2023.09 

**개발 환경**

- Spring Boot - 3.1.3
- React.js - 18.2.0
- MySql - 8.0
- Docker / Nginx

**실행방법**

```bash
docker-compose up
```

**기능 요약**

**[계정]**

기본적인 회원 가입, 로그인 기능과 관리자가 유저의 권한을 관리합니다.

**[상품]**

회원은 기본적인 상품을 조건에 따라 검색 할 수 있으며 매니저/관리자는 상품을 등록/수정 관리합니다. Optimistic Lock을 이용하여 동시성 이슈로 인한 재고 부족 이슈를 해결하였습니다.

**[장바구니]**

상품을 장바구니에 등록/삭제와 수량 변경 그리고 해당 상품들을 한번에 주문 할 수 있습니다.

**[주문]** 

회원은 자신의 상품을 주문할 수 있고 또한 주문 정보를 확인 할 수 있습니다. 매니저/관리자는 주문 된 정보에 한해 주문 취소가 가능합니다.

**REST API**

 Rest API을 이용해서 Client와 Server의 데이터를 전송하였습니다.
데이터 요청에 맞게 get, post, put, patch, delete 메소드를 이용하여 구현하였습니다.

**JWT / Spring Security 를 이용한 인증/인가 구현**

 기본적으로 Spring Security를 이용하여 Server측으로 오는 요청을 관리하고 있습니다.
 로그인 요청과 관련하여 UsernamePasswordAuthenticationFilter를 상속한 인증 필터가 JWT Token을 제공하며 jwt 토큰을 인증하여 권한을 확인합니다.

**JPA/QueryDSL**

 ORM을 이용하여 Entity와 Database Table을 매핑하고 있습니다.
 간단한 CURD에 대해서는 JpaRepository 인터페이스에서 Named Query와 NativeQuery를 다소 복잡한 조건에는 QueryDSL을 사용하여 쿼리를 생성하였습니다. 

**Auditing 을 이용한 Entity 공통 속성 설정**

다수의 데이터에서 필요로 하는 데이터의 생성자 / 수정자 , 그리고 생성시간/수정시간을 Auditing을 이용하여 공통 속성으로 설정하였습니다.

**JUnit5을 활용한 테스트 코드 작성**

JUnit5를 활용하여 Entity, Service, Controller 테스트를 진행하였습니다.