import React from "react";
import Login from "./Login";
import FormularioUsuario from "./FormularioUsuario";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/formulario" element={<FormularioUsuario />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;
