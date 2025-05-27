import React, { useEffect, useRef } from "react";
import { pdfjs } from "react-pdf";

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;

function VisorPDF({ url = "/contrato.pdf" }) {
  const canvasRef = useRef();

  useEffect(() => {
    const renderPDF = async () => {
      const loadingTask = pdfjs.getDocument(url);
      const pdf = await loadingTask.promise;
      const page = await pdf.getPage(1);

      const viewport = page.getViewport({ scale: 1.5 });
      const canvas = canvasRef.current;
      const context = canvas.getContext("2d");

      canvas.height = viewport.height;
      canvas.width = viewport.width;

      const renderContext = {
        canvasContext: context,
        viewport: viewport,
      };

      await page.render(renderContext).promise;
    };

    renderPDF().catch(console.error);
  }, [url]);

  return (
    <div style={{ textAlign: "center" }}>
      <h2>Visor PDF</h2>
      <canvas ref={canvasRef}></canvas>
    </div>
  );
}

export default VisorPDF;
