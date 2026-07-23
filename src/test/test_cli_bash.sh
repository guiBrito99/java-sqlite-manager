#!/bin/bash

# Define color codes for output formatting
CYAN='\033[0;36m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# 1. Setup: Navigate to project root.
# $0 is the script name. dirname gets the directory of the script (src/test).
# We then cd ../.. to reach the root of the project so database.sqlite is created in the right place.
cd "$(dirname "$0")/../.." || exit 1

# 2. Path Validation: Locate the compiled JAR artifact.
JAR_PATH="target/sqlite-manager-complete.jar"
ABS_JAR_PATH="$PWD/$JAR_PATH"

# Verify artifact existence before attempting execution
if [ ! -f "$JAR_PATH" ]; then
    echo -e "${RED}Error: Could not find the JAR file!${NC}"
    echo -e "${YELLOW}Expected location: $ABS_JAR_PATH${NC}"
    exit 1
fi

# 3. Execution Configuration: Define JVM arguments.
JVM_ARGS="--enable-native-access=ALL-UNNAMED"

echo -e "${CYAN}=== Starting CLI Automated Test ===${NC}"

# Phase 1: Structural Creation
echo -e "\n${GREEN}1. Creating table 'Users'...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" create Users id,name,role

# Phase 2: Data Ingestion
echo -e "\n${GREEN}2. Inserting first row...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" insert Users id,name,role 1,Alice,Admin

echo -e "\n${GREEN}3. Inserting second row...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" insert Users id,name,role 2,Bob,Guest

# Phase 3: Verification
echo -e "\n${GREEN}4. Printing current database state...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" print

# Phase 4: Mutation Tests
echo -e "\n${GREEN}5. Updating the first row (Index 0)...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" update Users 0 name,role Alice,SuperAdmin

echo -e "\n${GREEN}6. Deleting the second row (Index 1)...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" delete Users 1

# Final Verification
echo -e "\n${GREEN}7. Printing database after changes...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" print

# Phase 5: Cleanup
echo -e "\n${GREEN}8. Dropping table 'Users' to clean up...${NC}"
java $JVM_ARGS -jar "$ABS_JAR_PATH" drop Users

echo -e "\n${CYAN}=== CLI Test Complete! ===${NC}"