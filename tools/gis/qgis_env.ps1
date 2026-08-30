[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

function Resolve-SuriQgisRoot {
    $candidates = @()

    if ($env:SURIOS_QGIS_ROOT) {
        $candidates += (Get-Item -LiteralPath $env:SURIOS_QGIS_ROOT).FullName
    }

    $programFilesRoot = if ($env:ProgramFiles) { $env:ProgramFiles } else { "C:\Program Files" }
    if (Test-Path -LiteralPath $programFilesRoot) {
        $candidates += Get-ChildItem -LiteralPath $programFilesRoot -Directory -Filter "QGIS *" |
            Sort-Object Name -Descending |
            ForEach-Object { $_.FullName }
    }

    foreach ($candidate in ($candidates | Select-Object -Unique)) {
        $pythonWrapper = Join-Path $candidate "bin\python-qgis-ltr.bat"
        $processWrapper = Join-Path $candidate "bin\qgis_process-qgis-ltr.bat"
        if ((Test-Path -LiteralPath $pythonWrapper -PathType Leaf) -and
            (Test-Path -LiteralPath $processWrapper -PathType Leaf)) {
            return $candidate
        }
    }

    throw "No se encontró una instalación QGIS LTR con los wrappers PyQGIS y qgis_process."
}

$resolvedQgisRoot = Resolve-SuriQgisRoot
$script:SuriQgisEnvironment = [pscustomobject]@{
    Root = $resolvedQgisRoot
    Prefix = (Join-Path $resolvedQgisRoot "apps\qgis-ltr")
    Python = (Join-Path $resolvedQgisRoot "bin\python-qgis-ltr.bat")
    Process = (Join-Path $resolvedQgisRoot "bin\qgis_process-qgis-ltr.bat")
    GdalInfo = (Join-Path $resolvedQgisRoot "bin\gdalinfo.exe")
}

function Get-SuriQgisEnvironment {
    return $script:SuriQgisEnvironment
}

function Invoke-SuriQgisPython {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true, Position = 0)]
        [string]$ScriptPath,
        [Parameter(ValueFromRemainingArguments = $true)]
        [string[]]$Arguments
    )

    if (-not (Test-Path -LiteralPath $ScriptPath -PathType Leaf)) {
        throw "No existe el script PyQGIS: $ScriptPath"
    }

    & $script:SuriQgisEnvironment.Python $ScriptPath @Arguments
    $exitCode = $LASTEXITCODE
    if ($exitCode -ne 0) {
        throw "PyQGIS terminó con código $exitCode."
    }
}

function Invoke-SuriQgisProcess {
    [CmdletBinding()]
    param(
        [Parameter(ValueFromRemainingArguments = $true)]
        [string[]]$Arguments
    )

    & $script:SuriQgisEnvironment.Process @Arguments
    $exitCode = $LASTEXITCODE
    if ($exitCode -ne 0) {
        throw "qgis_process terminó con código $exitCode."
    }
}
