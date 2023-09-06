import React, { useState, useEffect } from 'react';
import { Container } from 'react-bootstrap';
import Header from '../../components/Header';
import { useNavigate } from "react-router-dom";


function OrderItem({ order }) {
  return (
    <div className="card d-flex">
      {order.orderItemDtoList.map((orderItem) => (
        <div key={orderItem.itemId} className="d-flex m-1">
          <div className="repImgDiv m-1">
            <img
              src={"http://localhost:8080" + orderItem.imgUrl}
              className="rounded repImg"
              alt={orderItem.itemNm}
            />
          </div>
          <div className="align-self-center w-75">
            <span className="fs24 font-weight-bold">{orderItem.itemNm}</span>
            <div className="fs18 font-weight-light">
              <span className="mx-3">{orderItem.orderPrice}원</span>
              <span className="mx-3">{orderItem.count}개</span>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

function OrderMain() {
  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);

  const navigate = useNavigate();
  
  const cancelOrder = (orderId) => {

    console.log(`http://localhost:8080/api/order/${orderId}`);
    fetch(`http://localhost:8080/api/order/${orderId}`, {
      method: "PATCH",
      headers: {
        'Authorization': localStorage.getItem("token"),
        "Content-Type": "application/json; charset=utf-8"
      },
    })
      .then(res => {
        if (res.status === 200) retrieveOrder();
        else return res.json();
      })
      .then(data => {
        if (data != null) {
          console.log(data);
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error('주문 취소에 실패하였습니다 \n', error);
      });
  };

  const handlePageChange = (page) => {
    setPage(page);
  };

  useEffect(() => {
    retrieveOrder();
  }, [page]);

  const retrieveOrder = () => {
    console.log(`http://localhost:8080/api/orders?page=${page}`)

    fetch(`http://localhost:8080/api/orders?page=${page}`, {
      method: "GET",
      headers: {
        'Authorization': localStorage.getItem("token"),
        'Content-Type': 'application/json; charset=utf-8'
      }
    })
      .then(res => res.json())
      .then(data => {
        console.log(data);
        if (data.content != null) {
          setOrders(data.content);
        } else {
          alert(data.message);
        }
      })
      .catch((error) => {
        alert('상품 정보 조회에 실패하였습니다 \n', error);
      });
  }

  return (
    <>
      <Header />
      <Container>
        <div className="content-mg">
          <h1 className="mb-4">주문이력</h1>
          {orders.map((order) => (
            <div key={order.orderId}>
              <br />
              <div className="d-flex m-1 align-self-center">
                <h4>
                  {order.orderDate} 주문
                </h4>
                <div className="mx-3">
                  {order.orderStatus === 'ORDER' ? (
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={() => cancelOrder(order.orderId)}
                    >
                      주문취소
                    </button>
                  ) : (
                    <h4>(취소 완료)</h4>
                  )}
                </div>
              </div>
              <OrderItem order={order} />
            </div>
          ))}
          <br/>
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
      </Container>
    </>
  );
}

export default OrderMain;
