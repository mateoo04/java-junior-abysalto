import { useState } from 'react'
import { CreateOrderForm } from './components/CreateOrderForm'
import { Layout, type AppView } from './components/Layout'
import { MealsPage } from './components/MealsPage'
import { OrderList } from './components/OrderList'

function App() {
  const [view, setView] = useState<AppView>('list')
  const [listKey, setListKey] = useState(0)

  function handleCreated() {
    setListKey((k) => k + 1)
    setView('list')
  }

  return (
    <Layout view={view} onViewChange={setView}>
      {view === 'list' && <OrderList key={listKey} />}
      {view === 'create' && <CreateOrderForm onCreated={handleCreated} />}
      {view === 'meals' && <MealsPage />}
    </Layout>
  )
}

export default App
