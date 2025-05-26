import React from "react";
import FormularioUsuario from "./FormularioUsuario";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import FirmaCanvas from '../FirmaCanvas';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/formulario" />} />
        <Route path="/formulario" element={<FormularioUsuario />} />
        <Route path="/firma" element={<FirmaCanvas />} />
      </Routes>
    </Router>
  );
}

export default App;
