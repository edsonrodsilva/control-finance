#!/bin/bash

echo "Atualizando referências qualified: com.controlfinance.shared → com.controlfinance.common"
find src -name "*.java" -type f -exec sed -i 's/com\.controlfinance\.shared\./com.controlfinance.common./g' {} \;

echo "Atualizando referências qualified: com.controlfinance.interfaces → com.controlfinance.api"
find src -name "*.java" -type f -exec sed -i 's/com\.controlfinance\.interfaces\./com.controlfinance.api./g' {} \;

echo "✓ Referências qualified corrigidas"
