# Script para executar testes com cobertura de código JaCoCo
# Este script baixa as dependências necessárias e executa os testes

Write-Host "=== UberPB - Executador de Testes com Cobertura ===" -ForegroundColor Cyan
Write-Host ""

# Criar diretório para dependências
$libDir = "lib"
if (!(Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir | Out-Null
}

# URLs das dependências
$dependencies = @{
    "junit-platform-console-standalone-1.10.1.jar" = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar"
    "mockito-core-5.8.0.jar" = "https://repo1.maven.org/maven2/org/mockito/mockito-core/5.8.0/mockito-core-5.8.0.jar"
    "mockito-junit-jupiter-5.8.0.jar" = "https://repo1.maven.org/maven2/org/mockito/mockito-junit-jupiter/5.8.0/mockito-junit-jupiter-5.8.0.jar"
    "byte-buddy-1.14.10.jar" = "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.10/byte-buddy-1.14.10.jar"
    "byte-buddy-agent-1.14.10.jar" = "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.10/byte-buddy-agent-1.14.10.jar"
    "objenesis-3.3.jar" = "https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar"
    "jacoco-agent-0.8.11.jar" = "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.11/org.jacoco.agent-0.8.11-runtime.jar"
    "jacoco-cli-0.8.11.jar" = "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.11/org.jacoco.cli-0.8.11-nodeps.jar"
}

Write-Host "Verificando dependências..." -ForegroundColor Yellow

foreach ($jar in $dependencies.Keys) {
    $filePath = Join-Path $libDir $jar
    if (!(Test-Path $filePath)) {
        Write-Host "  Baixando $jar..." -ForegroundColor Gray
        try {
            Invoke-WebRequest -Uri $dependencies[$jar] -OutFile $filePath -UseBasicParsing
            Write-Host "  OK $jar baixado" -ForegroundColor Green
        } catch {
            Write-Host "  ERRO ao baixar $jar" -ForegroundColor Red
        }
    } else {
        Write-Host "  OK $jar ja existe" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "Compilando código principal..." -ForegroundColor Yellow

# Compilar código principal
$mainSources = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
javac -d classes -encoding UTF-8 -sourcepath src\main\java $mainSources

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO ao compilar codigo principal" -ForegroundColor Red
    exit 1
}

Write-Host "OK Codigo principal compilado" -ForegroundColor Green
Write-Host ""

# Construir classpath para testes
$classpath = "classes"
Get-ChildItem -Path $libDir -Filter "*.jar" | ForEach-Object {
    $classpath += ";" + $_.FullName
}

Write-Host "Compilando testes..." -ForegroundColor Yellow

# Compilar testes
$testSources = Get-ChildItem -Path "src\test\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
javac -d classes -cp $classpath -encoding UTF-8 $testSources 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO ao compilar testes" -ForegroundColor Red
    exit 1
}

Write-Host "OK Testes compilados" -ForegroundColor Green
Write-Host ""
Write-Host "Executando testes com cobertura JaCoCo..." -ForegroundColor Yellow

# Executar testes com JaCoCo
$jacocoAgent = Join-Path $libDir "jacoco-agent-0.8.11.jar"
$junitConsole = Join-Path $libDir "junit-platform-console-standalone-1.10.1.jar"

# Montar classpath completo incluindo JUnit Console
$testClasspath = "$classpath;$junitConsole"

java "-javaagent:$jacocoAgent=destfile=jacoco.exec" -cp $testClasspath org.junit.platform.console.ConsoleLauncher --class-path classes --scan-class-path 2>&1

Write-Host ""
Write-Host "Gerando relatório de cobertura..." -ForegroundColor Yellow

# Criar diretório para relatório
$reportDir = "target\site\jacoco"
if (!(Test-Path $reportDir)) {
    New-Item -ItemType Directory -Path $reportDir -Force | Out-Null
}

# Gerar relatório HTML
java -jar (Join-Path $libDir "jacoco-cli-0.8.11.jar") report jacoco.exec --classfiles classes --sourcefiles src\main\java --html $reportDir --xml target\site\jacoco\jacoco.xml

Write-Host ""
Write-Host "=== Relatório de Cobertura Gerado ===" -ForegroundColor Cyan
Write-Host "Abra o arquivo: $reportDir\index.html" -ForegroundColor Green
Write-Host ""

# Abrir relatório no navegador
Start-Process "$reportDir\index.html"
