import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Stock from './pages/Stock'
import Alertas from './pages/Alertas'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<h1>Hotel Pirámide - Sistema de Inventario</h1>} />
        <Route path="/stock" element={<Stock />} />
        <Route path="/alertas" element={<Alertas />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App