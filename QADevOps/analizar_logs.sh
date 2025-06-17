#!/bin/bash
echo "Análisis de logs de auditoría"
grep "AUDIT" /var/log/contratos.log | tail -n 20