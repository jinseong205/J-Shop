import React, { useState, useEffect } from 'react';
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import { Link, useNavigate } from 'react-router-dom';
import jwt_decode from 'jwt-decode';

function Header() {
  const navigate = useNavigate();

  const [username, setUsername] = useState(null);
  const [userRoles, setUserRoles] = useState(null);
  const [itemSearchDto, setItemSearchDto] = useState({
    searchDateType: '',
    itemSellStatus: 'SELL',
    searchBy: 'itemName',
    searchQuery: '',
    items: '',
  });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token != null) {
      try{
        const decodedToken = jwt_decode(token);
        console.log(token);
        if (decodedToken) {
          setUserRoles(decodedToken.roles);
          setUsername(decodedToken.username);
        }
      }catch (err){
        console.log(err);
      }
    }
  }, []);

  const logout = () => {
    localStorage.removeItem('token', '');
    setUsername(null);
    setUserRoles(null);
  };

  const itemSearch = () => {
    const tempQuery = document.getElementById('searchBy').value;
    console.log(tempQuery);
    setItemSearchDto((prevState) => {
      return {
        ...prevState,
        searchBy: tempQuery,
      };
    });

    navigate('/itemMain', { state: { itemSearchDto: { ...itemSearchDto, searchQuery: tempQuery } } });
  };

  return (
    <>
      <Navbar bg="dark" expand="lg">
        <Container fluid>
          <Link to="/" className="navbar-brand me-5 text-light">
            J-Shop
          </Link>
          <Navbar.Toggle className="bg-light" aria-controls="navbarScroll" />
          <Navbar.Collapse id="navbarScroll">
            <Nav className="container-fluid my-2 my-lg-0" style={{ maxHeight: '100px' }} navbarScroll>
              <div className="d-flex  me-auto" style={{ minWidth: '40%' }} onSubmit={itemSearch}>
                <Form.Control type="search" placeholder="Search" className="me-2" aria-label="Search" id="searchBy" />
                <Button className="text-nowrap" variant="outline-light" onClick={itemSearch}  >
                  검색
                </Button>
              </div>

              <div className="d-flex">
                <Link className="nav-link text-light text-nowrap" to="/">
                  BEST
                </Link>
              </div>
              <div className="d-flex">
                <Link className="nav-link text-light text-nowrap" to="/">
                  전체
                </Link>
              </div>
              <div className="d-flex">
                <Link className="nav-link text-light text-nowrap" to="/">
                  공지사항
                </Link>
              </div>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
      <Navbar variant="outline-dark">
        <Container fluid>
          {username == null ? (
            <>
              <div className="d-flex me-auto"></div>
              <div className="d-flex me-4">
                <Link className="nav-link" to="/join">
                  회원가입
                </Link>
              </div>
              <div className="d-flex me-4">
                <Link className="nav-link" to="/login">
                  로그인
                </Link>
              </div>
            </>
          ) : (
            <>
              <div className="d-flex me-auto"></div>
              {userRoles.includes('ROLE_ADMIN') ? (
                <div className="d-flex me-4">
                  <Link className="nav-link" to="/userMange">
                    회원관리
                  </Link>
                </div>
              ) : (
                <></>
              )}
              {userRoles.includes('ROLE_MANAGER') || userRoles.includes('ROLE_ADMIN') ? (
                <>
                  <div className="d-flex me-4">
                    <Link className="nav-link" to="/itemSaveForm">
                      상품등록
                    </Link>
                  </div>
                  <div className="d-flex me-4">
                    <Link className="nav-link" to="/itemManage">
                      상품관리
                    </Link>
                  </div>
                </>

              ) : (
                <></>
              )}

              <div className="d-flex me-4">
                <Link className="nav-link" to="/orderMain">
                  내 주문
                </Link>
              </div>
              <div className="d-flex me-4">
                <Link className="nav-link" to="/cartMain">
                  장바구니
                </Link>
              </div>
              <div className="d-flex me-4">
                <Link className="nav-link" onClick={logout}>
                  로그아웃
                </Link>
              </div>
            </>
          )}
        </Container>
      </Navbar>
    </>
  );
}

export default Header;
