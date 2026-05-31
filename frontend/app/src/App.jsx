import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Login from './pages/Login'
import Stock from './pages/Stock'
import Alertas from './pages/Alertas'
import Entradas from './pages/Entradas'
import Distribuciones from './pages/Distribuciones'
import Productos from './pages/Productos'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/stock" element={<Stock />} />
        <Route path="/alertas" element={<Alertas />} />
        <Route path="/entradas" element={<Entradas />} />
        <Route path="/distribuciones" element={<Distribuciones />} />
        <Route path="/productos" element={<Productos />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App