import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Row, Col } from "react-bootstrap";
import Header from '../components/Header';
import Footer from '../components/Footer';
import Form from 'react-bootstrap/Form';

const LoginForm = () => {

  const navigate = useNavigate();

  const [user, setUser] = useState({
    username: "",
    password: "",
  });

  const changeValue = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value,
    });
  }

  const login = async (e) => {
    e.preventDefault();

    fetch("http://localhost:8080/api/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8"
      },
      body: JSON.stringify(user)

    })
      .then(res => {
        if (res.status === 200) {
          console.log(res.headers.get("Authorization"));
          if (res.headers.get("Authorization")) {
            localStorage.setItem('token', res.headers.get("Authorization"));
            navigate("/");
          }
        } else {
          return res.json();
        }
      })
      .then(data => {
        if (data != null) {
          console.log(data);
          alert(data.message);
        }
      })
      .catch(err => {
        if (err) {
          alert("로그인 중 오류가 발생 하였습니다." + err);
        }
      });
  }

  return (
    <>
      <Header />
      <Container>

        <div className="col-md-12">

          <h2>로그인</h2>
          <br />

          <Form onSubmit={login}>
            <div className="form-group">
              <label htmlFor="username">ID</label>
              &nbsp;
              <Row>
                <Col xs="3">
                  <input
                    type="text"
                    className="form-control"
                    name="username"
                    onChange={changeValue}
                    required
                  />
                </Col>
                <Col xs="1"></Col>
              </Row>
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              &nbsp;
              <Row>
                <Col xs="3">

                  <input
                    type="password"
                    className="form-control"
                    name="password"
                    onChange={changeValue}
                    required
                  />
                </Col>
                <Col xs="1"></Col>
              </Row>
            </div>
            <br />
            <div className="form-group mt-1">
              <button
                className="btn btn-secondary btn-block"
              >
                <span>Login</span>
              </button>
            </div>


          </Form>
        </div>
      </Container>
    </>
  );
};

export default LoginForm;