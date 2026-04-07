#!/bin/bash

# Atualizar imports de interfaces.rest → api.rest
echo "Atualizando imports: interfaces.rest → api.rest"
find src -name "*.java" -type f -exec sed -i 's/import com\.controlfinance\.interfaces\.rest\./import com.controlfinance.api.rest./g' {} \;
find src -name "*.java" -type f -exec sed -i 's/from com\.controlfinance\.interfaces\.rest\./from com.controlfinance.api.rest./g' {} \;

# Atualizar imports de shared → common
echo "Atualizando imports: shared → common"
find src -name "*.java" -type f -exec sed -i 's/import com\.controlfinance\.shared\./import com.controlfinance.common./g' {} \;
find src -name "*.java" -type f -exec sed -i 's/from com\.controlfinance\.shared\./from com.controlfinance.common./g' {} \;

echo "✓ Imports atualizados"
