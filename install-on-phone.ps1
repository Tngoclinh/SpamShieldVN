$ErrorActionPreference = "Stop"
Remove-Item Env:ANDROID_PREFS_ROOT -ErrorAction SilentlyContinue
$sdk = "D:\extraproject\.android-sdk"
$adb = "$sdk\platform-tools\adb.exe"
$env:ANDROID_HOME = $sdk
$env:ANDROID_SDK_ROOT = $sdk
$env:ANDROID_USER_HOME = "D:\extraproject\.android-user"
$env:ADB_VENDOR_KEYS = "$env:ANDROID_USER_HOME\adbkey"

New-Item -ItemType Directory -Force $env:ANDROID_USER_HOME | Out-Null

$devices = & $adb devices
$authorized = @($devices | Select-String "\sdevice$")
if ($authorized.Count -eq 0) {
    Write-Host "Khong tim thay dien thoai da duoc cap quyen USB debugging." -ForegroundColor Yellow
    Write-Host "Hay mo khoa dien thoai, chon Cho phep USB debugging, roi chay lai script."
    & $adb devices -l
    exit 1
}

$apk = "$PSScriptRoot\app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apk)) {
    Write-Host "Chua co APK. Dang build truoc..."
    & "$PSScriptRoot\build-on-d.ps1"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

& $adb install -r $apk
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
& $adb shell am start -n "vn.spamshield/.MainActivity"
Write-Host "Da cai va mo La Chan Cuoc Goi tren dien thoai." -ForegroundColor Green
