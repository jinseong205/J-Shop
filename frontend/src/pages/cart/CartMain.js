import React, { useState, useEffect } from 'react';
import { Container } from 'react-bootstrap';
import Header from '../../components/Header';

const CartMain = () => {
  const [cartItems, setCartItems] = useState([]);
  const [orderTotalPrice, setOrderTotalPrice] = useState(0);

  useEffect(() => {
    retrieveCart();
    getOrderTotalPrice();
  }, []);


  useEffect(() => {
    getOrderTotalPrice();
  }, [cartItems]);


  const retrieveCart = () => {
    fetch(`http://localhost:8080/api/cart`, {
      method: "GET",
      headers: {
        'Authorization': localStorage.getItem("token"),
        'Content-Type': 'application/json; charset=utf-8'
      }
    })
      .then(res => res.json())
      .then(data => {
        console.log(data);
        if (data.message != null) {
          alert(data.message);
        } else {
          setCartItems(data);
        }
      })
      .catch((error) => {
        alert('상품 정보 조회에 실패하였습니다 \n', error);
      });
  }


  const updateCartItemCount = (event, cartItemId) => {


    var count = parseInt(event.target.value, 10);
    
    if(isNaN(count)){
      count = 0;
    }
      
    fetch(`http://localhost:8080/api/cartItem/${cartItemId}?count=${count}`, {
      method: "PATCH",
      headers: {
        'Authorization': localStorage.getItem("token"),
        "Content-Type": "application/json; charset=utf-8"
      },
    })
      .then(res => {
        if (res.status === 200) {
          const updatedCartItems = cartItems.map((item) => {
            if (item.cartItemId === cartItemId) {
              return { ...item, count };
            }
            return item;
          });
          setCartItems(updatedCartItems);
        }
        else return res.json()
      })
      .then(data => {
        if (data != null) {
          console.log(data);
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error('장바구니 수량 변경에 실패하였습니다 \n', error);
      });

  }

  const deleteCartItem = (cartItemId) => {
    var result = false;

    fetch(`http://localhost:8080/api/cartItem/${cartItemId}`, {
      method: "DELETE",
      headers: {
        'Authorization': localStorage.getItem("token"),
        "Content-Type": "application/json; charset=utf-8"
      },
    })
      .then(res => {
        if (res.status === 200) {
          const updatedCartItems = cartItems.filter((item) => item.cartItemId !== cartItemId);
          setCartItems(updatedCartItems);
        }
        else return res.json()
      })
      .then(data => {
        if (data != null) {
          console.log(data);
          alert(data.message);
        }
      })
      .catch((error) => {
        console.error('장바구니 상품 삭제에 실패하였습니다 \n', error);
      });

    return result;
  }

  const getOrderTotalPrice = () => {
    let total = 0;
    cartItems.forEach((item) => {
      total += item.price * item.count;
    });
    setOrderTotalPrice(total);
  };

  const handleCheckAll = (event) => {
    const isChecked = event.target.checked;
    const updatedCartItems = cartItems.map((item) => ({
      ...item,
      checked: isChecked,
    }));
    setCartItems(updatedCartItems);
  };

  const handleCheck = (cartItemId) => {
    const updatedCartItems = cartItems.map((item) => {
      if (item.cartItemId === cartItemId) {
        return { ...item, checked: !item.checked };
      }
      return item;
    });
    setCartItems(updatedCartItems);
  };

const createOrder = () => {

  const selectedCartItems = cartItems.filter((item) => item.checked);
  console.log('Order processing for:', selectedCartItems);

  if (selectedCartItems == null || selectedCartItems.length == 0) {
    alert("주문할 상품을 선택해주세요.")
    return;
  }

  const cartOrderDto = {
    cartOrderList: selectedCartItems.map((item) => {
      return {
        cartItemId: item.cartItemId,
        count: item.count,
      };
    }),
  };

  console.log(cartOrderDto);
  fetch(`http://localhost:8080/api/cart/orders`, {
    method: "POST",
    headers: {
      'Authorization': localStorage.getItem("token"),
      "Content-Type": "application/json; charset=utf-8"
    },
    body: JSON.stringify(cartOrderDto)
  })
    .then(res => {
      if (res.status === 200) {
        alert('상품주문을 성공하였습니다.');

        const remainingCartItems = cartItems.filter((item) => !item.checked);
        setCartItems(remainingCartItems);
      }
      else return res.json();
    })
    .then(data => {
      if (data != null) {
        console.log(data);
        alert(data.message);
      }
    })
    .catch((error) => {
      alert('상품 주문에 실패하였습니다 \n', error);
    });

};

return (
  <>
    <Header />
    <Container>
      <div className="content-mg">
        <h2 className="mb-4">장바구니 목록</h2>
        <div>
          <table className="table">
            <colgroup>
              <col width="15%" />
              <col width="70%" />
              <col width="15%" />
            </colgroup>
            <thead>
              <tr className="text-center">
                <td>
                  <input type="checkbox" id="checkall" onChange={handleCheckAll} /> 전체선택
                </td>
                <td>상품정보</td>
                <td>상품금액</td>
              </tr>
            </thead>
            <tbody>
              {cartItems.map((cartItem) => (
                <tr key={cartItem.cartItemId}>
                  <td className="text-center align-middle">
                    <input
                      type="checkbox"
                      name="cartChkBox"
                      checked={cartItem.checked || false}
                      onChange={() => handleCheck(cartItem.cartItemId)}
                    />
                  </td>
                  <td className="d-flex">
                    <div className="repImgDiv align-self-center">
                      <img
                        src={"http://localhost:8080" + cartItem.imgUrl}
                        className="rounded repImg"
                        alt={cartItem.itemName}
                      />
                    </div>
                    <div className="align-self-center">
                      <span className="fs24 font-weight-bold">{cartItem.itemName}</span>
                      <div className="fs18 font-weight-light">
                        <span className="input-group mt-2">
                          <span
                            id={`price_${cartItem.cartItemId}`}
                            data-price={cartItem.price}
                            className="align-self-center mr-2"
                          >
                            {cartItem.price}원
                          </span>
                          <input
                            type="number"
                            name="count"
                            id={`count_${cartItem.cartItemId}`}
                            value={cartItem.count}
                            min="1"
                            onChange={(event) =>
                              updateCartItemCount(event, cartItem.cartItemId)
                            }
                            className="form-control mr-2"
                          />
                          <button
                            type="button"
                            className="close"
                            aria-label="Close"
                            onClick={() => deleteCartItem(cartItem.cartItemId)}
                          >
                            <span aria-hidden="true">&times;</span>
                          </button>
                        </span>
                      </div>
                    </div>
                  </td>
                  <td className="text-center align-middle">
                    <span id={`totalPrice_${cartItem.cartItemId}`} name="totalPrice">
                      {cartItem.price * cartItem.count}원
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <br/>
          <h2 className="text-center">
            총 주문 금액 : <span id="orderTotalPrice">{orderTotalPrice}원</span>
          </h2>
          <div className="text-center mt-3">
            <button
              type="button"
              className="btn btn-primary btn-lg"
              onClick={createOrder}
            >
              주문하기
            </button>
          </div>
        </div>
      </div>
    </Container>
  </>
);
};

export default CartMain;
