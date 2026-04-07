#!/bin/bash

echo "Atualizando declarações de package: shared → common"
find src -name "*.java" -type f -exec sed -i 's/^package com\.controlfinance\.shared\./package com.controlfinance.common./g' {} \;

echo "Atualizando declarações de package: interfaces.rest → api.rest"
find src -name "*.java" -type f -exec sed -i 's/^package com\.controlfinance\.interfaces\.rest\./package com.controlfinance.api.rest./g' {} \;

echo "✓ Declarações de package corrigidas"
