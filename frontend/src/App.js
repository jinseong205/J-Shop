import { Routes, Route } from 'react-router-dom';
import JoinForm from './pages/JoinForm';
import LoginForm from './pages/LoginForm';
import UserMange from './pages/user/UserManage';

import ItemSaveForm from './pages/item/ItemSaveForm';
import ItemUpdateForm from './pages/item/ItemUpdateForm';
import ItemMain from './pages/item/ItemMain';
import ItemDetail from './pages/item/ItemDetail';
import ItemManage from './pages/item/ItemManage';

import OrderMain from './pages/orders/OrderMain';
import CartMain from './pages/cart/CartMain';



function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<ItemMain />} />

        <Route path="/login" element={<LoginForm />} />
        <Route path="/join" element={<JoinForm />} />
        <Route path="/userMange" element={<UserMange />} />

        <Route path="/itemMain" element={<ItemMain />} />
        <Route path="/itemManage" element={<ItemManage/>} />
        <Route path="/itemSaveForm" element={<ItemSaveForm/>} />
        <Route path="/itemUpdateForm/:id" element={<ItemUpdateForm/>} />
        <Route path="/itemDetail/:id" element={<ItemDetail/>} />

        <Route path="/orderMain" element={<OrderMain />} />
        
        <Route path="/cartMain" element={<CartMain />} />


      </Routes>
    </>
  );
}

export default App;
