import React, { useState, useEffect } from 'react';
import { Container } from 'react-bootstrap';
import Header from '../../components/Header';
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";

const ItemDetail = () => {
  const [item, setItem] = useState(null);
  const [count, setCount] = useState(1);
  const [totalPrice, setTotalPrice] = useState(0);
  const [mainImage, setMainImage] = useState(null);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    retrieveItemDetail();
  }, []);

  useEffect(() => {
    calculateTotalPrice();
  }, [count]);

  const retrieveItemDetail = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/item/${id}`);
      const data = await response.json();
      setItem(data); // 받아온 데이터로 아이템 상태를 업데이트
      setMainImage(data.itemImgDtoList[0].imgUrl); // 처음 이미지를 메인 이미지로 설정
    } catch (error) {
      console.error('Error fetching item:', error);
    }
  };

  const calculateTotalPrice = () => {
    if (item) {
      const price = item.price;
      const totalCount = parseInt(count, 10);
      const calculatedTotalPrice = price * totalCount;
      setTotalPrice(calculatedTotalPrice);
    }
  };

  const handleCountChange = (event) => {
    setCount(event.target.value);
  };

  const handleImageClick = (imgUrl) => {
    setMainImage(imgUrl);
  };

  const handleImageError = (event) => {
    event.target.src = 'http://localhost:8080/no-image.png';
  };

  const createOrder = () => {
    const orderDto = {
      itemId : item.id,
      count : count
    }

    fetch(`http://localhost:8080/api/order`, {
      method: "POST",
      headers: {
        'Authorization': localStorage.getItem("token"),
        "Content-Type": "application/json; charset=utf-8"
      },
      body: JSON.stringify(orderDto)
    })
      .then(res => {
        if (res.status === 200){navigate("/orderMain");}
        else {return res.json()};
      })
      .then(data => {
        if (data != null) {
          console.log(data);
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error('상품 주문에 실패하였습니다 \n', error);
      });

  };

  const createCart = () => {
    
    const cartItemDto = {
      itemId : item.id,
      count : count
    }
    
    console.log(cartItemDto);
    fetch(`http://localhost:8080/api/cart`, {
      method: "POST",
      headers: {
        'Authorization': localStorage.getItem("token"),
        "Content-Type": "application/json; charset=utf-8"
      },
      body: JSON.stringify(cartItemDto)
    })
      .then(res => {
        if (res.status === 200)navigate("/cartMain");
        else return res.json();
      })
      .then(data => {
        if (data != null) {
          console.log(data);
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error('상품 주문에 실패하였습니다 \n', error);
      });
  };

  if (!item) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Header />
      <Container>
        <div style={{ marginLeft: '25%', marginRight: '25%' }}>
          <input type="hidden" id="itemId" value={item.id} />

          <div className="d-flex">
            <div className="repImgDiv m-4" style={{ width: '300px', height: '300px', textAlign: 'center', display: 'flex', justifyContent: 'center' }}>
              <img
                src={"http://localhost:8080" + mainImage}
                className="rounded repImg"
                alt={item.itemNm}
                style={{ height: '100%', width: '100%', objectFit: 'contain' }}
                onError={handleImageError} // 이미지 로드 실패 시 onError 이벤트 처리
              />
            </div>
            <div className="wd50">
              {/* 기존의 HTML 코드들을 React 컴포넌트로 대체 */}
              <span className={`badge ${item.itemSellStatus === 'SELL' ? 'badge-primary' : 'btn-danger'} mgb-15`}>
                {item.itemSellStatus === 'SELL' ? '판매중' : '품절'}
              </span>
              <div className="h4">{item.itemName}</div>
              <hr className="my-4" />

              <div className="text-right">
                <div className="h4 text-danger text-left">
                  <input type="hidden" value={item.price} id="price" name="price" />
                  <span>{item.price}</span>원
                </div>
                <div className="input-group w-50">
                  <div className="input-group-prepend">
                    <span className="input-group-text">수량</span>
                  </div>
                  <input
                    type="number"
                    name="count"
                    id="count"
                    className="form-control"
                    value={count}
                    min="1"
                    onChange={handleCountChange}
                  />
                </div>
              </div>
              <hr className="my-4" />

              <div className="text-right mgt-50">
                <h5>결제 금액</h5>
                <h3 name="totalPrice" id="totalPrice" className="font-weight-bold">
                  {totalPrice}원
                </h3>
              </div>
              <div className="text-right">
                {item.itemSellStatus === 'SELL' ? (
                  <>
                    <button
                      type="button"
                      className="btn btn-light border border-primary btn-lg"
                      onClick= {createCart}
                    >
                      장바구니 담기
                    </button>
                    <button
                      type="button"
                      className="btn btn-primary btn-lg"
                      onClick={createOrder}
                    >
                      주문하기
                    </button>
                  </>
                ) : (
                  <button type="button" className="btn btn-danger btn-lg">
                    품절
                  </button>
                )}
              </div>
            </div>
          </div>

          <br />
          <div className="text-center my-3">
            {item.itemImgDtoList.map((itemImg, index) => (
              <button key={index} onClick={() => handleImageClick(itemImg.imgUrl)} style={{ cursor: 'pointer' }}>
                <div style={{ width: "75px" }}>
                  <img
                    src={"http://localhost:8080" + itemImg.imgUrl}
                    className="rounded mgb-15 squareImage" // 여기서 squareImage 클래스를 추가해줍니다.
                    height="75px"
                    alt=""
                    onError={handleImageError}
                  />
                </div>
              </button>
            ))}
          </div>

          <div className="jumbotron jumbotron-fluid mgt-30">
            <div className="container">
              <br />
              <h4> 상품 상세 설명</h4>
              <hr className="my-4" />
              <div dangerouslySetInnerHTML={{ __html: item.itemDetail }} />
            </div>
          </div>

        </div>
      </Container>
    </>
  );
};

export default ItemDetail;
