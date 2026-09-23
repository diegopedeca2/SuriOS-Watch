[CmdletBinding()]
param(
    [string]$ProjectRoot,
    [string]$OutputRoot,
    [string]$Sprint = "036",
    [string]$Version = "3.4",
    [switch]$SkipBuild,
    [switch]$Force,
    [switch]$AllowTesterRelease
)

$ErrorActionPreference = "Stop"

if (-not $AllowTesterRelease) {
    throw "Las APK tester son versiones fijas. Usa -AllowTesterRelease solo con una orden expresa del propietario para generar una nueva distribución."
}

if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
} else {
    $ProjectRoot = (Resolve-Path $ProjectRoot).Path
}

if ([string]::IsNullOrWhiteSpace($OutputRoot)) {
    $OutputRoot = Join-Path $ProjectRoot "output\SPRINT_${Sprint}_BETA"
} else {
    $OutputRoot = [IO.Path]::GetFullPath($OutputRoot)
}

$gradle = Join-Path $ProjectRoot "gradlew.bat"
$csv = Join-Path $ProjectRoot "docs\PRS_FIELD_DATA_TEMPLATE.csv"
$guide = Join-Path $ProjectRoot "docs\BETA_TEST_GUIDE_SPRINT_${Sprint}.md"
$gradleFile = Join-Path $ProjectRoot "app\build.gradle.kts"
$profiles = @("FENRIR", "ALTAMIRA", "CHECHU", "JAVI")

if (-not (Test-Path -LiteralPath $gradle)) { throw "No se encuentra gradlew.bat en $ProjectRoot" }
if (-not (Test-Path -LiteralPath $csv)) { throw "No se encuentra la plantilla CSV en $csv" }
if (-not (Test-Path -LiteralPath $guide)) { throw "No se encuentra la guía BETA en $guide" }

if ([string]::IsNullOrWhiteSpace($Version)) {
    $gradleText = Get-Content -LiteralPath $gradleFile -Raw
    $versionMatch = [regex]::Match($gradleText, 'versionName\s*=\s*"([^"]+)"')
    if (-not $versionMatch.Success) { throw "No se pudo leer versionName desde $gradleFile" }
    $Version = $versionMatch.Groups[1].Value
}

$resolvedOutput = [IO.Path]::GetFullPath($OutputRoot)
$allowedPrefix = ((Resolve-Path (Join-Path $ProjectRoot "output")).Path + [IO.Path]::DirectorySeparatorChar)
if (-not $resolvedOutput.StartsWith($allowedPrefix, [StringComparison]::OrdinalIgnoreCase)) {
    throw "La salida BETA solo puede estar dentro de output del proyecto."
}
if (Test-Path -LiteralPath $resolvedOutput) {
    if (-not $Force) { throw "La carpeta de salida ya existe. Usa -Force para reemplazarla: $resolvedOutput" }
    Remove-Item -LiteralPath $resolvedOutput -Recurse -Force
}
New-Item -ItemType Directory -Path $resolvedOutput -Force | Out-Null

foreach ($profile in $profiles) {
    $commonMap = Join-Path $ProjectRoot "distribution-assets\common\maps\navy_7_terrain.mbtiles"
    $profileMap = Join-Path $ProjectRoot "distribution-assets\$profile\maps\testing_terrain.mbtiles"
    if (-not (Test-Path -LiteralPath $commonMap)) { throw "Falta el mapa común de $profile." }
    if (-not (Test-Path -LiteralPath $profileMap)) { throw "Falta el mapa TESTING de $profile." }

    if (-not $SkipBuild) {
        Push-Location $ProjectRoot
        try {
            & $gradle ":app:assembleFullDebug" "-PdistributionProfile=$profile"
            if ($LASTEXITCODE -ne 0) { throw "La compilación de $profile terminó con error." }
        } finally {
            Pop-Location
        }
    }

    $apkPath = Join-Path $ProjectRoot "app\build\outputs\apk\full\debug\app-full-debug.apk"
    if (-not (Test-Path -LiteralPath $apkPath)) { throw "No se encuentra la APK compilada de $profile." }

    $profileDir = Join-Path $resolvedOutput $profile
    New-Item -ItemType Directory -Path $profileDir -Force | Out-Null
    Copy-Item -LiteralPath $apkPath -Destination (Join-Path $profileDir "PIP-SuriOS_${profile}_BETA_v${Version}.apk")
    Copy-Item -LiteralPath $guide -Destination (Join-Path $profileDir (Split-Path $guide -Leaf))
    Copy-Item -LiteralPath $csv -Destination (Join-Path $profileDir "PRS_FIELD_DATA_TEMPLATE.csv")

    $hashes = Get-ChildItem -LiteralPath $profileDir -File | Get-FileHash -Algorithm SHA256
    $hashes | ForEach-Object { "$($_.Hash)  $($_.Path | Split-Path -Leaf)" } |
        Set-Content -LiteralPath (Join-Path $profileDir "SHA256SUMS.txt") -Encoding UTF8
    Compress-Archive -Path (Join-Path $profileDir "*") -DestinationPath (Join-Path $resolvedOutput "PIP-SuriOS_${profile}_BETA_SPRINT_${Sprint}.zip") -CompressionLevel Optimal
}

Write-Output "Paquetes BETA creados en: $resolvedOutput"
