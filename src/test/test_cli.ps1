# 1. Move to the root of the project so 'database.sqlite' is created in the right place!
# $PSScriptRoot is src/test, so \..\.. takes us to the main project folder.
Set-Location -Path (Join-Path $PSScriptRoot "..\..")

# 2. Now that we are in the root folder, we can look for the JAR directly inside target/
$JAR_PATH = "target\database-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

# Try to resolve to an absolute path for safety
$AbsoluteJarPath = (Resolve-Path $JAR_PATH -ErrorAction SilentlyContinue).Path

if (-not (Test-Path $JAR_PATH)) {
    Write-Host "Error: Could not find the JAR file!" -ForegroundColor Red
    Write-Host "Expected location: $JAR_PATH" -ForegroundColor Yellow
    exit
}

# 3. This flag tells Java to trust the SQLite native C libraries and suppress warnings
$JVM_ARGS = "--enable-native-access=ALL-UNNAMED"

Write-Host "=== Starting CLI Automated Test ===" -ForegroundColor Cyan

Write-Host "`n1. Creating table 'Users'..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath create Users id,name,role

Write-Host "`n2. Inserting first row..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath insert Users id,name,role 1,Alice,Admin

Write-Host "`n3. Inserting second row..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath insert Users id,name,role 2,Bob,Guest

Write-Host "`n4. Printing current database state..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath print

Write-Host "`n5. Updating the first row (Index 0)..." -ForegroundColor Green
# Updating Alice's role from Admin to SuperAdmin
java $JVM_ARGS -jar $AbsoluteJarPath update Users 0 id,name,role 1,Alice,SuperAdmin

Write-Host "`n6. Deleting the second row (Index 1)..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath delete Users 1

Write-Host "`n7. Printing database after changes..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath print

Write-Host "`n8. Dropping table 'Users' to clean up..." -ForegroundColor Green
java $JVM_ARGS -jar $AbsoluteJarPath drop Users

Write-Host "`n=== CLI Test Complete! ===" -ForegroundColor Cyan