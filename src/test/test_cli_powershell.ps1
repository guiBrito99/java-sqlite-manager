<#
.SYNOPSIS
    Automated integration test for the database CLI utility.

.DESCRIPTION
    This script serves as an end-to-end integration test. It validates the full 
    lifecycle of database interactions, ensuring that commands passed via the 
    CLI successfully update the persistent SQLite file and the in-memory structure.
#>

# 1. Setup: Navigate to project root.
# We need to ensure 'database.sqlite' is generated in the project root, not the test subfolder.
# $PSScriptRoot is the location of this script (src/test), so we step back two levels.
Set-Location -Path (Join-Path $PSScriptRoot "..\..")

# 2. Path Validation: Locate the compiled JAR artifact.
# We look into the target/ folder where Maven places the build output.
$JAR_PATH = "target\database-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

# Resolve-Path verifies the file exists and gives us the absolute system path.
$AbsoluteJarPath = (Resolve-Path $JAR_PATH -ErrorAction SilentlyContinue).Path

# Verify artifact existence before attempting execution to prevent 'silent' failures.
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "Error: Could not find the JAR file!" -ForegroundColor Red
    Write-Host "Expected location: $JAR_PATH" -ForegroundColor Yellow
    exit
}

# 3. Execution Configuration: Define JVM arguments.
# --enable-native-access is required by newer JDKs to interact with SQLite's native C library.
$JVM_ARGS = "--enable-native-access=ALL-UNNAMED"

Write-Host "=== Starting CLI Automated Test ===" -ForegroundColor Cyan

# --- Test Sequence Start ---

# Phase 1: Structural Creation
# Verify that the Create Table command successfully initializes the schema.
Write-Host "`n1. Creating table 'Users'..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath create Users id,name,role

# Phase 2: Data Ingestion
# Test basic INSERT functionality by adding multiple records.
Write-Host "`n2. Inserting first row..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath insert Users id,name,role 1,Alice,Admin

Write-Host "`n3. Inserting second row..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath insert Users id,name,role 2,Bob,Guest

# Phase 3: Verification
# Validate data storage by printing the current internal state.
Write-Host "`n4. Printing current database state..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath print

# Phase 4: Mutation Tests
# Verify UPDATE logic: changing existing values based on index referencing.
Write-Host "`n5. Updating the first row (Index 0)..." -ForegroundColor Green
# Updating Alice's role from Admin to SuperAdmin
java $JVM_ARGS -jar $AbsoluteJarPath update Users 0 id,name,role 1,Alice,SuperAdmin

# Verify DELETE logic: removing a specific record and ensuring remaining rows are intact.
Write-Host "`n6. Deleting the second row (Index 1)..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath delete Users 1

# Final Verification: Ensure state is correct after mutations.
Write-Host "`n7. Printing database after changes..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath print

# Phase 5: Cleanup
# Remove the table to reset the database state for the next run.
Write-Host "`n8. Dropping table 'Users' to clean up..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath drop Users

Write-Host "`n=== CLI Test Complete! ===" -ForegroundColor Cyan