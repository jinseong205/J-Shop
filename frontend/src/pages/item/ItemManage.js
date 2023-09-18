import React, { useState, useEffect } from 'react';
import { Form, Table, Pagination, Button, Container, Row, Col } from 'react-bootstrap';
import Header from '../../components/Header';
import { Link, useNavigate } from 'react-router-dom';

function ItemManage() {
  const [items, setItems] = useState(null); // 실제 데이터를 담을 상태
  const [searchDateType, setSearchDateType] = useState('all');
  const [searchSellStatus, setSearchSellStatus] = useState('SELL');
  const [searchBy, setSearchBy] = useState('itemName');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchedItems, setSearchedItems] = useState(null); // 검색 결과 아이템을 담을 상태

  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const maxPage = 10; // 페이지당 아이템 수, 필요에 따라 조정하십시오.
  
  useEffect(() => {
    retrieveItem();
  }, [page]);


  // 실제 API 호출을 통해 아이템 데이터를 가져오는 함수
  const retrieveItem = () => {

    const itemSearchDto = 
    {
      searchDateType: searchDateType,
      searchSellStatus: searchSellStatus,
      searchBy: searchBy,
      searchQuery: searchQuery,
      items: [],
    };

    console.log(itemSearchDto);
    console.log("-----");

    var queryString;
    if (itemSearchDto != null) {
      queryString = Object.keys(itemSearchDto)
        .map((key) => {
          if (key !== 'items') {
            return `${encodeURIComponent(key)}=${encodeURIComponent(itemSearchDto[key])}`;
          }
        })
        .join('&');
    }

    console.log(`http://localhost:8080/api/manager/items?page=${page}&${queryString}`);

    fetch(`http://localhost:8080/api/manager/items?page=${page}&${queryString}`, {
      method: "GET",
      headers: {
        'Authorization': localStorage.getItem("token"),
        "Content-Type": "application/json; charset=utf-8"
      }
    }).then(res => res.json())
      .then(data => {
        console.log(data);
        if (data.items != null) {
          if (page === 0)
            setItems(data.items.content);
          else
            setItems(data.items.content);
        } else {
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error('상품 정보 조회에 실패하였습니다 \n', error);
      });
  };

  // 검색 버튼 클릭 이벤트 핸들러
  const handleSearch = (e) => {
    e.preventDefault();
    retrieveItem();
  };


  const handlePageChange = (page) => {
    setPage(page);
  };

  return (
    <>

      <Header />
      <br />
      <Container>

        <Table >
          <thead>
            <tr>
              <th>상품아이디</th>
              <th>상품명</th>
              <th>상태</th>
              <th>등록자</th>
              <th>등록일</th>
            </tr>
          </thead>
          <tbody>
            {/* 검색 결과에 따라 아이템을 매핑합니다. */}
            {(searchedItems || items || []).map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>
                  <Link to={`/itemUpdateForm/${item.id}`} className="navbar-brand me-5 text-light">
                  {item.itemName}
                  </Link>
                </td>
                <td>{item.itemSellStatus === 'SELL' ? '판매중' : '품절'}</td>
                <td>{item.crtName}</td>
                <td>{item.crtDt.substr(0,10)}</td>
              </tr>
            ))}
          </tbody>
        </Table>

        {/* 페이지네이션 */}
        <div className="d-flex justify-content-center">
          <div>
            <ul className="pagination justify-content-center">
              <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                <button
                  className="page-link"
                  onClick={() => handlePageChange(page - 1)}
                  disabled={page === 0}
                >
                  Previous
                </button>
              </li>
              {Array.from({ length: orders.totalPages }).map((_, index) => (
                <li
                  key={index}
                  className={`page-item ${page === index ? 'active' : ''}`}
                >
                  <button
                    className="page-link"
                    onClick={() => handlePageChange(index)}
                  >
                    {index + 1}
                  </button>
                </li>
              ))}

              <li
                className={`page-item ${page + 1 >= orders.totalPages ? 'disabled' : ''
                  }`}
              >

                <button
                  className="page-link"
                  onClick={() => handlePageChange(page + 1)}
                  disabled={page + 1 >= orders.totalPages}
                >
                  Next
                </button>
              </li>
            </ul>
          </div>
        </div>

        {/* 검색 폼 */}
        <div className="form-inline justify-content-center">
          <Form>
            <Row className="justify-content-center">
              <Col md="auto" className="px-0">
                <Form.Select
                  value={searchDateType}
                  onChange={(e) => setSearchDateType(e.target.value)}
                >
                  <option value="all">전체 기간</option>
                  <option value="1d">1일</option>
                  <option value="1w">1주</option>
                  <option value="1m">1개월</option>
                  <option value="6m">6개월</option>
                </Form.Select>
              </Col>
              <Col md="auto" className="px-0" >
                <Form.Select
                  value={searchSellStatus}
                  onChange={(e) => setSearchSellStatus(e.target.value)}
                >
                  <option value="">판매 상태(전체)</option>
                  <option value="SELL">판매</option>
                  <option value="SOLD_OUT">품절</option>
                </Form.Select>
              </Col>
              <Col md="auto" className="px-0">
                <Form.Select
                  value={searchBy}
                  onChange={(e) => setSearchBy(e.target.value)}
                >
                  <option value="itemName">상품명</option>
                  <option value="createdBy">등록자</option>
                </Form.Select>
              </Col>
              <Col md="auto" className="py-1" >
                <Form.Control
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  size="sm"
                  placeholder="검색어를 입력해주세요"
                />
              </Col>
              <Col md="auto" className="py-1" >
                <Button type="submit" variant="primary" size="sm" onClick={handleSearch}>
                  검색
                </Button>
              </Col>
            </Row>
          </Form>
        </div>

      </Container>



    </>
  );
}

export default ItemManage;
