$javaBin = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot\bin"
if (Test-Path "$javaBin\java.exe") {
    $env:Path = "$javaBin;$env:Path"
}
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}
Write-Host "Compiling source files..." -ForegroundColor Yellow
javac -encoding UTF-8 -d bin -cp ".;lib/mysql-connector-j-8.3.0.jar" src/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}
Write-Host "Starting RapidRack Warehouse Management System..." -ForegroundColor Cyan
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" WarehouseApp
