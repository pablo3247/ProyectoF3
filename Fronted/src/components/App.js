import React from "react";
import Login from "./Login";
import FormularioUsuario from "./FormularioUsuario";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import FirmaCanvas from './components/FirmaCanvas';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/formulario" element={<FormularioUsuario />} />
        <Route path="/login" element={<Login />} />
        <Route path="/firma" element={<FirmaCanvas />} />
      </Routes>
    </Router>
  );
}

export default App;
