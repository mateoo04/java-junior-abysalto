import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import { CreateOrderForm } from './components/CreateOrderForm'
import { Layout } from './components/Layout'
import { MealsPage } from './components/MealsPage'
import { OrderList } from './components/OrderList'

function App() {
  const navigate = useNavigate()

  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/orders" replace />} />
        <Route path="/orders" element={<OrderList />} />
        <Route
          path="/orders/new"
          element={<CreateOrderForm onCreated={() => navigate('/orders')} />}
        />
        <Route path="/menu" element={<MealsPage />} />
        <Route path="*" element={<Navigate to="/orders" replace />} />
      </Routes>
    </Layout>
  )
}

export default App
