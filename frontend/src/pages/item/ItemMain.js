import React, { useState, useEffect } from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import Header from '../../components/Header';
import { Link, useLocation, useNavigate } from 'react-router-dom';

const ItemMain = () => {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [itemSearchDto, setItemSearchDto] = useState(null);
  const pageSize = 9;

  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    setItemSearchDto(location.state?.itemSearchDto || null);
  }, [location.state]);

  useEffect(() => {
    setPage(0);
    setLoading(true); // 로딩 상태를 true로 설정하여 첫 렌더링 시에 getItems 함수 호출
  }, [itemSearchDto]);

  useEffect(() => {
    if (loading) {
      retrieveItems(); // 로딩 상태가 true일 때에만 getItems 함수 호출
      setLoading(false); // getItems 호출 후 로딩 상태를 false로 변경
    }
    window.addEventListener('scroll', handleScroll);
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, [loading]);

  const retrieveItems = () => {

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

    fetch(`http://localhost:8080/api/items?page=${page}&${queryString}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json; charset=utf-8"
      }
    })
      .then(res => res.json())
      .then(data => {
        console.log(data);
        if (data.items != null) {
          if (page === 0)
            setItems(data.items.content);
          else
            setItems((prevItems) => [...prevItems, ...data.items.content]);
        } else {
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error('상품 정보 조회에 실패하였습니다 \n', error);
      });


  };
  useEffect(() => {
    if (page > 0) {
      retrieveItems();
    }
  }, [page]);

  const handleScroll = () => {
    const { scrollHeight, scrollTop, clientHeight } = document.documentElement;
    if (scrollTop + clientHeight >= scrollHeight - 200) {
      setPage((prevPage) => prevPage + 1);
    }
  };

  const handleImageError = (event) => {
    event.target.src = 'http://localhost:8080/no-image.png';
  };

  return (
    <>
      <Header />
      <br />
      <Container>
        <div>
          <Row>
            {items.map((item, index) => (
              <Col key={item.id} md={4} className="mb-4">
                <Link className="nav-link text-light text-nowrap" to={`/itemDetail/${item.id}`}>
                  <div className="card">
                    <input type="hidden" value={item.id} />
                    <div style={{ height: '250px', textAlign: 'center', display: 'flex', justifyContent: 'center' }}>
                      <img
                        src={"http://localhost:8080" + item.imgUrl}
                        alt={item.name}
                        style={{ height: '100%', width: '100%', objectFit: 'contain' }}
                        onError={handleImageError}
                      />
                    </div>

                    <div className="mx-4">
                      <div><h5>{item.itemName}</h5></div>
                      <div className="justify-content-end"><b>가격: {item.price}원</b></div>
                    </div>
                  </div>
                </Link>
              </Col>
            ))}
          </Row>
        </div>
      </Container>
    </>
  );
};

export default ItemMain;
