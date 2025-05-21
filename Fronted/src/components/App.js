import React from "react";
import FormularioUsuario from "./FormularioUsuario";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/formulario" element={<FormularioUsuario />} />
      </Routes>
    </Router>
  );
}

export default App;
