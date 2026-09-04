[CmdletBinding()]
param(
    [string]$ProjectRoot,
    [string]$OutputRoot,
    [string]$Sprint = "033",
    [string]$Version,
    [string]$GuidePath,
    [switch]$SkipBuild,
    [switch]$Force,
    [switch]$AllowTesterRelease
)

$ErrorActionPreference = "Stop"

if (-not $AllowTesterRelease) {
    throw "Las APK tester son versiones fijas. Usa -AllowTesterRelease solo con una orden expresa del propietario para generar una nueva distribucion."
}

if ([string]::IsNullOrWhiteSpace($ProjectRoot)) {
    $ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
} else {
    $ProjectRoot = (Resolve-Path $ProjectRoot).Path
}

if ([string]::IsNullOrWhiteSpace($OutputRoot)) {
    $OutputRoot = Join-Path $ProjectRoot "output\SPRINT_${Sprint}_APK"
} else {
    $OutputRoot = [IO.Path]::GetFullPath($OutputRoot)
}

$gradle = Join-Path $ProjectRoot "gradlew.bat"
$csv = Join-Path $ProjectRoot "docs\PRS_FIELD_DATA_TEMPLATE.csv"
$guideGenerator = Join-Path $ProjectRoot "tools\create_alpha_test_guide.py"
$gradleFile = Join-Path $ProjectRoot "app\build.gradle.kts"
$guideName = "PIP-SuriOS_ALPHA_TEST_GUIDE_SPRINT_${Sprint}.docx"
$profiles = @("FENRIR", "ALTAMIRA", "CHECHU")
$profileIcons = @{
    FENRIR = "pip_f_icon.png"
    ALTAMIRA = "pip_a_icon.png"
    CHECHU = "pip_c_icon.png"
}

function Remove-PreviousTesterArtifacts {
    param(
        [Parameter(Mandatory = $true)][string]$CurrentOutputRoot
    )

    $outputBase = Join-Path $ProjectRoot "output"
    if (-not (Test-Path -LiteralPath $outputBase)) { return }

    $resolvedOutputBase = (Resolve-Path -LiteralPath $outputBase).Path
    $resolvedCurrentOutput = [IO.Path]::GetFullPath($CurrentOutputRoot)
    $allowedPrefix = $resolvedOutputBase.TrimEnd([IO.Path]::DirectorySeparatorChar) + [IO.Path]::DirectorySeparatorChar

    foreach ($sprintRoot in Get-ChildItem -LiteralPath $resolvedOutputBase -Directory -Filter "SPRINT_*_APK") {
        $resolvedSprintRoot = $sprintRoot.FullName
        if ($resolvedSprintRoot.Equals($resolvedCurrentOutput, [StringComparison]::OrdinalIgnoreCase)) { continue }
        if (-not $resolvedSprintRoot.StartsWith($allowedPrefix, [StringComparison]::OrdinalIgnoreCase)) {
            throw "Por seguridad, solo se pueden limpiar paquetes dentro de output del proyecto."
        }

        foreach ($profile in $profiles) {
            $profileDir = Join-Path $resolvedSprintRoot $profile
            if (Test-Path -LiteralPath $profileDir) {
                Remove-Item -LiteralPath $profileDir -Recurse -Force
            }
            Get-ChildItem -LiteralPath $resolvedSprintRoot -File -Filter "PIP-SuriOS_${profile}_v*.apk" -ErrorAction SilentlyContinue |
                ForEach-Object { Remove-Item -LiteralPath $_.FullName -Force }
            Get-ChildItem -LiteralPath $resolvedSprintRoot -File -Filter "PIP-SuriOS_${profile}_SPRINT_*.zip" -ErrorAction SilentlyContinue |
                ForEach-Object { Remove-Item -LiteralPath $_.FullName -Force }
        }
    }
}

if (-not (Test-Path -LiteralPath $gradle)) { throw "No se encuentra gradlew.bat en $ProjectRoot" }
if (-not (Test-Path -LiteralPath $csv)) { throw "No se encuentra la plantilla CSV en $csv" }
if (-not (Test-Path -LiteralPath $gradleFile)) { throw "No se encuentra la configuración Gradle en $gradleFile" }
if (-not (Test-Path -LiteralPath $guideGenerator) -and [string]::IsNullOrWhiteSpace($GuidePath)) {
    throw "No se encuentra el generador de la guía en $guideGenerator"
}

if (Test-Path -LiteralPath $OutputRoot) {
    if (-not $Force) {
        throw "La carpeta de salida ya existe. Usa -Force solo si quieres reemplazarla: $OutputRoot"
    }
    $resolvedOutput = (Resolve-Path $OutputRoot).Path
    $allowedPrefix = ((Resolve-Path (Join-Path $ProjectRoot "output")).Path + [IO.Path]::DirectorySeparatorChar)
    if (-not $resolvedOutput.StartsWith($allowedPrefix, [StringComparison]::OrdinalIgnoreCase)) {
        throw "Por seguridad, -Force solo puede limpiar una carpeta dentro de output del proyecto."
    }
    Remove-Item -LiteralPath $resolvedOutput -Recurse -Force
}

New-Item -ItemType Directory -Path $OutputRoot -Force | Out-Null

if ([string]::IsNullOrWhiteSpace($GuidePath)) {
    $GuidePath = Join-Path $OutputRoot "PIP-SuriOS_ALPHA_TEST_GUIDE_SPRINT_${Sprint}.docx"
    $python = Get-Command python -ErrorAction SilentlyContinue
    if ($null -eq $python -or $python.Source -like "*WindowsApps*") {
        throw "No hay un Python utilizable para generar la guía. Usa -GuidePath con un DOCX ya generado."
    }
    if ([string]::IsNullOrWhiteSpace($Version)) {
        $gradleText = Get-Content -LiteralPath $gradleFile -Raw
        $versionMatch = [regex]::Match($gradleText, 'versionName\s*=\s*"([^"]+)"')
        if (-not $versionMatch.Success) { throw "No se pudo leer versionName desde $gradleFile" }
        $Version = $versionMatch.Groups[1].Value
    }
    & $python.Source $guideGenerator --root $ProjectRoot --output $GuidePath --sprint $Sprint --date (Get-Date -Format "dd/MM/yyyy") --version $Version
    if ($LASTEXITCODE -ne 0) { throw "El generador de la guía terminó con error." }
} else {
    $GuidePath = (Resolve-Path $GuidePath).Path
}

if (-not (Test-Path -LiteralPath $GuidePath)) { throw "No se encuentra la guía DOCX: $GuidePath" }

foreach ($profile in $profiles) {
    $commonAssetRoot = Join-Path $ProjectRoot "distribution-assets\common\maps"
    $profileAssetRoot = Join-Path $ProjectRoot "distribution-assets\$profile\maps"
    if (-not (Test-Path -LiteralPath (Join-Path $commonAssetRoot "navy_7_terrain.mbtiles"))) {
        throw "Falta el recurso versionado común: navy_7_terrain.mbtiles"
    }
    if (-not (Test-Path -LiteralPath (Join-Path $profileAssetRoot "testing_terrain.mbtiles"))) {
        throw "Falta el recurso versionado de ${profile}: testing_terrain.mbtiles"
    }
    $resRoot = Join-Path $ProjectRoot "distribution-res\$profile\drawable-nodpi"
    $iconPath = Join-Path $resRoot $profileIcons[$profile]
    if (-not (Test-Path -LiteralPath $iconPath)) { throw "Falta el icono local de ${profile}: $iconPath" }

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
    if (-not (Test-Path -LiteralPath $apkPath)) { throw "No se encuentra la APK compilada de ${profile}: $apkPath" }

    $profileDir = Join-Path $OutputRoot $profile
    New-Item -ItemType Directory -Path $profileDir -Force | Out-Null
    if ([string]::IsNullOrWhiteSpace($Version)) {
        $gradleText = Get-Content -LiteralPath $gradleFile -Raw
        $versionMatch = [regex]::Match($gradleText, 'versionName\s*=\s*"([^"]+)"')
        if (-not $versionMatch.Success) { throw "No se pudo leer versionName desde $gradleFile" }
        $Version = $versionMatch.Groups[1].Value
    }
    $apkName = "PIP-SuriOS_${profile}_v${Version}.apk"
    Copy-Item -LiteralPath $apkPath -Destination (Join-Path $profileDir $apkName)
    Copy-Item -LiteralPath $GuidePath -Destination (Join-Path $profileDir $guideName)
    Copy-Item -LiteralPath $csv -Destination (Join-Path $profileDir "PRS_FIELD_DATA_TEMPLATE.csv")

    $hashes = Get-ChildItem -LiteralPath $profileDir -File | Get-FileHash -Algorithm SHA256
    $hashes | ForEach-Object { "$($_.Hash)  $($_.Path | Split-Path -Leaf)" } | Set-Content -LiteralPath (Join-Path $profileDir "SHA256SUMS.txt") -Encoding UTF8
    Compress-Archive -Path (Join-Path $profileDir "*") -DestinationPath (Join-Path $OutputRoot "PIP-SuriOS_${profile}_SPRINT_${Sprint}.zip") -CompressionLevel Optimal
}

# Only remove previous tester snapshots after the new set has been generated
# successfully. MAIN and unrelated output such as GIS work files are kept.
Remove-PreviousTesterArtifacts -CurrentOutputRoot $OutputRoot

Write-Output "Paquetes creados en: $OutputRoot"
