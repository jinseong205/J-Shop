import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from "react-router-dom";
import { Form, Row, Col, Container } from "react-bootstrap";
import { useParams } from "react-router";
import Header from '../../components/Header';
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

const ItemSaveForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  const [itemFormDto, setItemFormDto] = useState({
    itemName: "",
    price: "",
    stockNum: "",
    itemDetail: "",
    itemSellStatus: "SELL",
    itemImgDtoList: [],
    itemImgIds: []
  });


  useEffect(() => {
    retrieveItem();
  }, [id]);

  const retrieveItem = () => {
    if (id) {
      // id 값이 존재하는 경우, 해당 id에 해당하는 상품 정보를 가져온다.
      fetch(`http://localhost:8080/api/item/${id}`)
        .then(res => res.json())
        .then(data => {
          if (data.id != null) {
            //setItem(data); // 가져온 상품 정보로 item 상태를 업데이트한다.
          } else {
            alert(data.message);
          }
        })
        .catch(err => {
          alert("상품 정보를 가져오는 중 오류가 발생했습니다. \n" + err);
        });
    }
  }


  const changeItemValue = (e) => {
    let value = e.target.value;

    if (e.target.name === "price" || e.target.name === "stockNum") {
      value = value.replace(/\D/g, "");
      document.getElementById(e.target.name).value = value;
    }

    setItemFormDto({
      ...itemFormDto,
      [e.target.name]: value,
    });
  };


  const changeEditorValue = (e, v) => {
    setItemFormDto({
      ...itemFormDto,
      itemDetail: v,
    });
  };


  const onFileChange = (e, index) => {
    const file = e.target.files[0];
    const reader = new FileReader();

    reader.onloadend = () => {
      const itemImgName = document.getElementById(`itemImgName${index}`);
      if (itemImgName) {
        itemImgName.textContent = file ? file.name : "";
      }
    };

    if (file) {
      reader.readAsDataURL(file);
    } else {
      const itemImgName = document.getElementById(`itemImgName${index}`);
      itemImgName.textContent = "";
    }

  };

  const onFileDelete = (e, index) => {

    var tempId = document.getElementById('itemImgId' + index).value;

    setItemFormDto(prevState => {
      var updateItemImgDtoList = [...prevState.itemImgDtoList];
      updateItemImgDtoList = updateItemImgDtoList.splice(index);

      return {
        ...prevState,
        itemImgDtoList: updateItemImgDtoList,
      };
    });

    console.log(itemFormDto);

    if (tempId) {

      const itemImgName = document.getElementById(`itemImgName${index}`);

      setItemFormDto(prevState => {
        var deleteItemImgIds = [...prevState.deleteItemImgIds, tempId];
        var deleteItemImgIdsSet = new Set(deleteItemImgIds);
        var deleteItemImgIdsArray = [...deleteItemImgIdsSet];

        var updatedItemImgIdsArray = [...prevState.deleteItemImgIds];

        if (updatedItemImgIdsArray.includes(tempId)) {
          updatedItemImgIdsArray = updatedItemImgIdsArray.filter((element) => element !== tempId);
        }

        console.log(updatedItemImgIdsArray);
        console.log(deleteItemImgIdsArray);

        return {
          ...prevState,
          updateItemImgIds: updatedItemImgIdsArray,
          deleteItemImgIds: deleteItemImgIdsArray
        };
      });
    }


    document.getElementById(`itemImgFile${index}`).value = "";
    document.getElementById(`itemImgName${index}`).textContent = "";

  };




  const createItem = (e) => {
    e.preventDefault();
    const formData = new FormData();

    formData.append('itemFormDto', new Blob([JSON.stringify(itemFormDto)], { type: 'application/json' }));

    const fileInputs = document.querySelectorAll('input[type="file"]');

    fileInputs.forEach((input) => {
      const files = input.files;
      if (files.length > 0) {
        const file = files[0];
        formData.append('itemImgFile', file);
      }
    });


    console.log([...formData.keys()]);

    fetch('http://localhost:8080/api/manager/item', {
      method: 'POST',
      headers: {
        'Authorization': localStorage.getItem("token"),
      },
      body: formData,
    })
      .then(res => {
        //console.log(res);  
        return res.json();
      })
      .then(data => {
        if (data.id != null) {
          alert("상품등록에 성공하였습니다.");
          navigate(`/ItemUpdateForm/${data.id}`)
        } else {
          alert(data.message);
        }
      })
      .catch(err => {
        alert("상품등록 중 오류가 발생 하였습니다.");
      });
  };

  const renderFileUploadFields = () => {
    const fileUploadFields = [];

    for (let i = 1; i <= 5; i++) {
      fileUploadFields.push(
        <Form.Group key={i}>
          <input type="hidden" name="itemImgId" id={`itemImgId${i}`} />
          <Row className="align-items-center">
            <Col xs="auto">
              <label className="input-group-text file-label" htmlFor={`itemImgFile${i}`} style={{ padding: "3px", fontSize: "10px" }}>
                이미지 첨부
                <input
                  type="file"
                  id={`itemImgFile${i}`}
                  name={`itemImgFile`}
                  style={{ display: "none" }}
                  onChange={(e) => onFileChange(e, i)}
                />
              </label>
            </Col>
            <Col xs="auto">
              <div style={{ fontSize: "12px" }} id={`itemImgName${i}`}></div>
            </Col>
            <Col>
              <div onClick={(e) => onFileDelete(e, i)} style={{ fontSize: "12px" }}>[삭제]</div>
            </Col>
          </Row>
        </Form.Group>
      );
    }

    return fileUploadFields;
  };


  return (
    <>
      <Header />
      <Container>

        <div className="col-md-12">
          <h2>상품 등록</h2>
          <br />
          <form>
            <div className="form-group">
              <Row className='mb-1'>
                <Col xs="1">
                  <label htmlFor="itemName">판매상태</label>
                </Col>
                <Col xs="5">
                  <select className="form-select" name="itemSellStatus" onChange={changeItemValue}>
                    <option value="SELL">판매중</option>
                    <option value="SOLD_OUT" >품절</option>
                  </select>
                </Col>
              </Row>
            </div>

            <div className="form-group">
              <Row className='mb-1'>
                <Col xs="1">
                  <label htmlFor="itemName">상품명</label>
                </Col>
                <Col xs="5">
                  <input
                    type="text"
                    className="form-control"
                    id="itemName"
                    name="itemName"
                    onChange={changeItemValue}
                  />
                </Col>
              </Row>
            </div>

            <div className="form-group">
              <Row className='mb-1'>
                <Col xs="1">
                  <label htmlFor="price">상품 가격</label>
                </Col>
                <Col xs="5">
                  <input
                    type="text"
                    className="form-control"
                    id="price"
                    name="price"
                    onChange={changeItemValue}
                  />
                </Col>
              </Row>
            </div>

            <div className="form-group">
              <Row className='mb-1'>
                <Col xs="1">
                  <label htmlFor="stockNum">재고</label>
                </Col>
                <Col xs="5">
                  <input
                    type="text"
                    className="form-control"
                    id="stockNum"
                    name="stockNum"
                    onChange={changeItemValue}
                  />
                </Col>
              </Row>
            </div>

            <div>
              <label htmlFor="price">상품 상세 내용</label>
              <CKEditor
                editor={ClassicEditor}
                data=""
                onReady={editor => {
                  editor.editing.view.change((writer) => {
                    writer.setStyle(
                      "height",
                      "250px",
                      editor.editing.view.document.getRoot()
                    );
                  });
                }}
                onChange={(event, editor) => {
                  const data = editor.getData();
                  changeEditorValue(event, data);
                }}
                onBlur={(event, editor) => {
                }}
                onFocus={(event, editor) => {
                }}
              />
            </div>

            <br />
            <label htmlFor="itemName">상품 이미지</label>
            <Row className="align-items-center">
              {renderFileUploadFields()}
            </Row>
            <br />

            <div className="form-group mt-1">
              <button className="btn btn-secondary btn-block" onClick={createItem}>상품 등록</button>
            </div>


          </form>
        </div >
      </Container >
    </>
  );
};

export default ItemSaveForm;