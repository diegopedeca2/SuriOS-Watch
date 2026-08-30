[CmdletBinding()]
param(
    [string]$OutputPath
)

$ErrorActionPreference = "Stop"
. (Join-Path $PSScriptRoot "qgis_env.ps1")

if (-not $OutputPath) {
    $OutputPath = Join-Path $PSScriptRoot "..\..\output\gis\orca_qgis_smoke_test.qgz"
}

$qgisOutput = [IO.Path]::GetFullPath($OutputPath)
if (Test-Path -LiteralPath $qgisOutput) {
    throw "El resultado de prueba ya existe y no se sobrescribirá: $qgisOutput"
}

$qgisDirectory = Split-Path -Parent $qgisOutput
New-Item -ItemType Directory -Path $qgisDirectory -Force | Out-Null

$qgis = Get-SuriQgisEnvironment
Write-Output "QGIS_ROOT=$($qgis.Root)"
Write-Output "QGIS_PYTHON=$($qgis.Python)"

Invoke-SuriQgisPython -ScriptPath (Join-Path $PSScriptRoot "qgis_smoke_test.py") --output $qgisOutput

$processOutput = (& $qgis.Process list --json 2>&1) -join [Environment]::NewLine
if ($LASTEXITCODE -ne 0) {
    throw "No se pudo consultar qgis_process."
}

$processInfo = $processOutput | ConvertFrom-Json
$providerIds = @($processInfo.providers.PSObject.Properties.Name)
Write-Output "PROCESSING_PROVIDERS=$($providerIds -join ',')"
foreach ($requiredProvider in @("gdal", "qgis", "quickosm")) {
    if ($providerIds -notcontains $requiredProvider) {
        throw "Falta el proveedor Processing requerido: $requiredProvider"
    }
}

Write-Output "SMOKE_TEST=PASS"
Write-Output "PROJECT_CREATED=$qgisOutput"
