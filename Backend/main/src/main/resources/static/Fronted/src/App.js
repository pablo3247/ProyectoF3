import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import FormularioUsuario from "./components/FormularioUsuario";

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
