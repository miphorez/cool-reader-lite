param(
    [string]$RepositoryRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
)

$ErrorActionPreference = 'Stop'
$edgePath = 'C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe'
$normalSource = Join-Path $RepositoryRoot 'icons_src\logo\cr3_logo.svg'
$adaptiveSource = Join-Path $RepositoryRoot 'icons_src\logo\cr3_logo_adaptive_foreground.svg'
$highContrastSource = Join-Path $RepositoryRoot 'icons_src\high-contrast\cr3_logo_button_hc-48x48-src.svg'

Add-Type -AssemblyName System.Drawing

function Render-VerifiedMaster {
    param(
        [string]$SourcePath,
        [string]$Name
    )

    for ($attempt = 1; $attempt -le 5; $attempt++) {
        $masterPath = Join-Path $env:TEMP "coolreader-$Name-master-$attempt.png"
        $profilePath = Join-Path $env:TEMP "coolreader-$Name-edge-$attempt"
        New-Item -ItemType Directory -Path $profilePath -Force | Out-Null
        $sourceUri = 'file:///' + ($SourcePath -replace '\\', '/')

        & $edgePath `
            --headless=new `
            --disable-gpu `
            --hide-scrollbars `
            --force-device-scale-factor=1 `
            --default-background-color=00000000 `
            --window-size=1024,1024 `
            "--user-data-dir=$profilePath" `
            "--screenshot=$masterPath" `
            $sourceUri | Out-Null

        for ($waitAttempt = 0; $waitAttempt -lt 30 -and -not (Test-Path -LiteralPath $masterPath); $waitAttempt++) {
            Start-Sleep -Milliseconds 200
        }

        if (Test-Path -LiteralPath $masterPath) {
            $master = [System.Drawing.Bitmap]::FromFile($masterPath)
            try {
                if ($master.Width -eq 1024 -and $master.Height -eq 1024 -and
                    $master.GetPixel(512, 512).A -gt 0 -and $master.GetPixel(0, 0).A -eq 0) {
                    return $masterPath
                }
            }
            finally {
                $master.Dispose()
            }
        }
    }

    throw "Unable to render a valid transparent master from $SourcePath"
}

function Resize-Master {
    param(
        [string]$MasterPath,
        [string]$TargetPath,
        [int]$Size
    )

    $source = [System.Drawing.Bitmap]::FromFile($MasterPath)
    $target = New-Object System.Drawing.Bitmap($Size, $Size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($target)
    try {
        $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
        $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $graphics.DrawImage($source, 0, 0, $Size, $Size)
        $stream = [System.IO.File]::Open($TargetPath, [System.IO.FileMode]::Create)
        try {
            $target.Save($stream, [System.Drawing.Imaging.ImageFormat]::Png)
        }
        finally {
            $stream.Dispose()
        }
    }
    finally {
        $graphics.Dispose()
        $target.Dispose()
        $source.Dispose()
    }
}

$normalMaster = Render-VerifiedMaster -SourcePath $normalSource -Name 'normal'
$adaptiveMaster = Render-VerifiedMaster -SourcePath $adaptiveSource -Name 'adaptive'
$highContrastMaster = Render-VerifiedMaster -SourcePath $highContrastSource -Name 'high-contrast'

$renderJobs = @(
    @($normalMaster, 'android\res\mipmap-ldpi\cr3_logo.png', 36),
    @($normalMaster, 'android\res\mipmap-mdpi\cr3_logo.png', 48),
    @($normalMaster, 'android\res\mipmap-hdpi\cr3_logo.png', 72),
    @($normalMaster, 'android\res\mipmap-xhdpi\cr3_logo.png', 96),
    @($normalMaster, 'android\res\mipmap-xxhdpi\cr3_logo.png', 144),
    @($normalMaster, 'android\res\mipmap-xxxhdpi\cr3_logo.png', 192),
    @($adaptiveMaster, 'android\res\mipmap-ldpi\cr3_logo_foreground.png', 81),
    @($adaptiveMaster, 'android\res\mipmap-mdpi\cr3_logo_foreground.png', 108),
    @($adaptiveMaster, 'android\res\mipmap-hdpi\cr3_logo_foreground.png', 162),
    @($adaptiveMaster, 'android\res\mipmap-xhdpi\cr3_logo_foreground.png', 216),
    @($adaptiveMaster, 'android\res\mipmap-xxhdpi\cr3_logo_foreground.png', 324),
    @($adaptiveMaster, 'android\res\mipmap-xxxhdpi\cr3_logo_foreground.png', 432),
    @($normalMaster, 'android\res\drawable-ldpi\cr3_logo_button.png', 24),
    @($normalMaster, 'android\res\drawable-mdpi\cr3_logo_button.png', 32),
    @($normalMaster, 'android\res\drawable-hdpi\cr3_logo_button.png', 48),
    @($normalMaster, 'android\res\drawable-xhdpi\cr3_logo_button.png', 64),
    @($normalMaster, 'android\res\drawable-xxhdpi\cr3_logo_button.png', 96),
    @($highContrastMaster, 'android\res\drawable-ldpi\cr3_logo_button_hc.png', 24),
    @($highContrastMaster, 'android\res\drawable-mdpi\cr3_logo_button_hc.png', 32),
    @($highContrastMaster, 'android\res\drawable-hdpi\cr3_logo_button_hc.png', 48),
    @($highContrastMaster, 'android\res\drawable-xhdpi\cr3_logo_button_hc.png', 64),
    @($highContrastMaster, 'android\res\drawable-xxhdpi\cr3_logo_button_hc.png', 96),
    @($highContrastMaster, 'android\res\drawable-ldpi\cr3_logo_hc.png', 36),
    @($highContrastMaster, 'android\res\drawable-mdpi\cr3_logo_hc.png', 48),
    @($highContrastMaster, 'android\res\drawable-hdpi\cr3_logo_hc.png', 72),
    @($highContrastMaster, 'android\res\drawable-xhdpi\cr3_logo_hc.png', 96),
    @($highContrastMaster, 'android\res\drawable-xxhdpi\cr3_logo_hc.png', 144)
)

$index = 0
foreach ($renderJob in $renderJobs) {
    $index++
    $targetPath = Join-Path $RepositoryRoot $renderJob[1]
    Resize-Master -MasterPath $renderJob[0] -TargetPath $targetPath -Size $renderJob[2]
    Write-Output "[$index/$($renderJobs.Count)] $($renderJob[1])"
}
