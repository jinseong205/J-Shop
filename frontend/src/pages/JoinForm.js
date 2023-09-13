import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Form, Row, Col, Container } from "react-bootstrap";
import Header from "../components/Header";
import Footer from "../components/Footer";

const JoinForm = () => {
    const navigate = useNavigate();

    const [user, setUser] = useState({
        username: "",
        password: "",
        name: "",
        email: "",
        addr: ""
    });

    const changeValue = (e) => {
        setUser({
            ...user,
            [e.target.name]: e.target.value,
        });
    }

    const createUser = (e) => {
        e.preventDefault();

        fetch("http://localhost:8080/api/join", {
            method: "POST",
            headers: {
                "Content-Type": "application/json; charset=utf-8"
            },
            body: JSON.stringify(user)

        })
            .then(res => {
                if (res.status === 200) {
                    alert("회원가입이 완료되었습니다.");
                    navigate("/login");
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
                alert("회원가입 중 오류가 발생 하였습니다.");
            });
    };

    return (
        <>
            <Header />
            <Container>
                <div className="col-md-12">
                    <h2>회원가입</h2>
                    <br />
                    <form onSubmit={createUser}>
                        <div className="form-group">
                            <Row className='mb-1'>
                                <Col xs="1">
                                    <label htmlFor="username">ID</label>
                                </Col>
                                <Col xs="2">
                                    <input
                                        type="text"
                                        className="form-control"
                                        id="username"
                                        onChange={changeValue}
                                        name="username"
                                    />
                                </Col>
                            </Row>
                        </div>

                        <div className="form-group">
                            <Row className='mb-1'>
                                <Col xs="1">
                                    <label htmlFor="password">Password</label>
                                </Col>
                                <Col xs="2">
                                    <input
                                        type="password"
                                        className="form-control"
                                        id="password"
                                        onChange={changeValue}
                                        name="password"
                                    />
                                </Col>
                            </Row>
                        </div>

                        <div className="form-group">
                            <Row className='mb-1'>
                                <Col xs="1">
                                    <label htmlFor="password">이름</label>
                                </Col>
                                <Col xs="2">
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="name"
                                        onChange={changeValue}
                                        required
                                    />
                                </Col>
                            </Row>
                        </div>

                        <div className="form-group">
                            <Row className='mb-1'>
                                <Col xs="1">
                                    <label htmlFor="email">E-Mail</label>
                                </Col>
                                <Col xs="3">
                                    <input
                                        type="email"
                                        className="form-control"
                                        name="email"
                                        onChange={changeValue}
                                        required
                                    />
                                </Col>
                            </Row>
                        </div>

                        <div className="form-group">
                            <Row className='mb-1'>
                                <Col xs="1">
                                    <label htmlFor="email">주소</label>
                                </Col>
                                <Col xs="5">
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="addr"
                                        onChange={changeValue}
                                    />
                                </Col>
                            </Row>
                        </div>

                        <br/>
                        <div className="form-group mt-1">
                            <button className="btn btn-secondary btn-block">회원가입</button>
                        </div>

                    </form>
                </div >
            </Container >
            <Footer />
        </>


    );
};

export default JoinForm;