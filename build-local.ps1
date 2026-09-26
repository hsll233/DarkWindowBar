param([string]$MinecraftRoot)
$ErrorActionPreference = 'Stop'
$project = $PSScriptRoot
$dependencies = @(
    @{ Path='net/fabricmc/fabric-loader/0.19.5/fabric-loader-0.19.5.jar'; Repo='https://maven.fabricmc.net/'; Hash='93044e4dd46de5d8136701292f05e868da096d2c9fddb4793e4fdbcc63efc695' },
    @{ Path='net/java/dev/jna/jna/5.17.0/jna-5.17.0.jar'; Repo='https://repo.maven.apache.org/maven2/'; Hash='b3a9408e7c51e08ef0e3bfcc08f443f6ec0f6191ba8cd7c18d53d2b22e5bdbc0' },
    @{ Path='net/java/dev/jna/jna-platform/5.17.0/jna-platform-5.17.0.jar'; Repo='https://repo.maven.apache.org/maven2/'; Hash='b7e3d46c87bad2eb409b0e704916bcd81206168e357312dfddd0e253679cd9e0' },
    @{ Path='org/lwjgl/lwjgl/3.4.1/lwjgl-3.4.1.jar'; Repo='https://repo.maven.apache.org/maven2/'; Hash='9b1c3a3a078c2377219ecb8a2662730b3dece07c10592cf1d12f957286037b69' },
    @{ Path='org/lwjgl/lwjgl-glfw/3.4.1/lwjgl-glfw-3.4.1.jar'; Repo='https://repo.maven.apache.org/maven2/'; Hash='c2d93964f0a8b2a846708c7fb1b0a095e7d79dce8ea4c543969ffef508027190' }
)
$classpath = foreach ($dep in $dependencies) {
    $file = Join-Path (Join-Path $project '.deps') $dep.Path
    if ($MinecraftRoot) {
        $installed = Join-Path (Join-Path $MinecraftRoot 'libraries') $dep.Path
        if (Test-Path -LiteralPath $installed) { $file = $installed }
    }
    if (-not (Test-Path -LiteralPath $file) -or (Get-FileHash -LiteralPath $file -Algorithm SHA256).Hash.ToLowerInvariant() -ne $dep.Hash) {
        New-Item -ItemType Directory -Path (Split-Path $file) -Force | Out-Null
        $curl = Get-Command curl.exe -ErrorAction SilentlyContinue
        if (-not $curl) { $curl = Get-Command curl -ErrorAction Stop }
        & $curl.Source --fail --silent --show-error --location --http1.1 --retry 3 --retry-all-errors --retry-delay 1 --output $file ($dep.Repo + $dep.Path)
        if ($LASTEXITCODE -ne 0 -and (-not (Test-Path -LiteralPath $file) -or (Get-FileHash -LiteralPath $file -Algorithm SHA256).Hash.ToLowerInvariant() -ne $dep.Hash)) { throw "Download failed: $($dep.Path)" }
    }
    if ((Get-FileHash -LiteralPath $file -Algorithm SHA256).Hash.ToLowerInvariant() -ne $dep.Hash) {
        throw "Dependency checksum mismatch: $($dep.Path)"
    }
    $file
}
$classes = Join-Path $project 'build-local/classes'
$output = Join-Path $project 'build-local/darkwindowbar-fabric-26.2-1.0.0-port.1.jar'
New-Item -ItemType Directory -Path $classes -Force | Out-Null
$sources = @(
    (Join-Path $project 'src/main/java/toni/darkwindowbar/DarkWindowBar.java'),
    (Join-Path $project 'src/main/java/toni/darkwindowbar/DwmApi.java'),
    (Join-Path $project 'src/main/java/toni/darkwindowbar/fabric/DarkWindowBarClient.java')
)
& javac --release 21 -encoding UTF-8 -classpath ($classpath -join [IO.Path]::PathSeparator) -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
Copy-Item -LiteralPath (Join-Path $project 'src/main/resources/fabric.mod.json') -Destination $classes
$iconDir = Join-Path $classes 'assets/darkwindowbar/textures'
New-Item -ItemType Directory -Path $iconDir -Force | Out-Null
Copy-Item -LiteralPath (Join-Path $project 'src/main/resources/assets/darkwindowbar/textures/mod_logo.png') -Destination $iconDir
Copy-Item -LiteralPath (Join-Path $project 'LICENSE.md'),(Join-Path $project 'PORT-NOTES.md') -Destination $classes
& jar --create --file $output -C $classes .
if ($LASTEXITCODE -ne 0) { throw 'Packaging failed' }
Write-Output $output
