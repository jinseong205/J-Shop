import React, { useEffect, useState } from 'react';
import Header from '../../components/Header';
import { Container, Row, Col } from 'react-bootstrap';

const UserManage = () => {
  const [users, setUsers] = useState([]);
  
  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);

  useEffect(() => {
    retrieveUsers();
  }, [page]);

  // 사용자 목록을 서버에서 가져오는 함수
  const retrieveUsers = () => {
      fetch(`http://localhost:8080/api/users?page=${page}`, {
        method: "GET",
        headers: {
          'Authorization': localStorage.getItem("token"),
          "Content-Type": "application/json; charset=utf-8"
        }
      })
        .then(res => res.json())
        .then(data => {
          console.log(data);
          if (data.content != null) {
            setUsers(data.content);
          } else {
            alert(data.message);
          }
        })
        .catch((error) => {
          alert('사용자 역할 변경 도중 오류 발생 : ' + error);
        });
  };

  const updateUserRole = (userId, newRole) => {

    // 로컬 스토리지에서 토큰을 가져옵니다.
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('로컬 스토리지에서 토큰을 찾을 수 없습니다.');
      return;
    }


    const userRoles = 'ROLE_' + newRole;
    const user = {
      id: userId,
      roles: userRoles,
    }

    fetch("http://localhost:8080/api/user/roles", {
      method: "PATCH",
      headers: {
        'Authorization': localStorage.getItem("token"),
        "Content-Type": "application/json; charset=utf-8"
      },
      body: JSON.stringify(user)
    })
      .then((res) => {
        if (res.status === 200) {
          alert("권한변경이 완료되었습니다.");
          retrieveUsers();
        } else {
          return res.json();
        }
      })
      .then(data => {
        if (data != null) {
          alert(data.message);
        }
      })
      .catch((error) => {
        alert('사용자 역할 변경 도중 오류 발생 : ' + error);
      });

  };

  const handlePageChange = (page) => {
    setPage(page);
  };


  return (
    <>
      <Header />
      <br />
      <Container>
        <div>
          <h2>회원관리</h2>
          <br />
          <Row className="justify-content-center">
            <Col xs={10}>
              <table className="table table-bordered text-center">
                <thead>
                  <tr>
                    <th>이름</th>
                    <th>사용자명</th>
                    <th>이메일</th>
                    <th>역할</th>
                    <th>권한 변경</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id}>
                      <td>{user.name}</td>
                      <td>{user.username}</td>
                      <td>{user.email}</td>
                      <td>{user.roles}</td>
                      <td>
                        {(
                          <button onClick={() => updateUserRole(user.id, 'USER')} className='btn btn-secondary'>
                            일반
                          </button>
                        )}
                        {(
                          <button onClick={() => updateUserRole(user.id, 'MANAGER')} className='btn btn-secondary mx-1'>
                            매니저
                          </button>
                        )}
                        {(
                          <button onClick={() => updateUserRole(user.id, 'ADMIN')} className='btn btn-secondary'>
                            관리자
                          </button>
                        )}

                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </Col>
          </Row>
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
        </div>
      </Container>
    </>
  );
};

export default UserManage;
