$ErrorActionPreference = "Stop"
Remove-Item Env:ANDROID_PREFS_ROOT -ErrorAction SilentlyContinue
$env:ANDROID_HOME = "D:\extraproject\.android-sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:ANDROID_USER_HOME = "D:\extraproject\.android-user"
$env:GRADLE_USER_HOME = "D:\extraproject\.gradle-home"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

New-Item -ItemType Directory -Force $env:ANDROID_USER_HOME | Out-Null
New-Item -ItemType Directory -Force $env:GRADLE_USER_HOME | Out-Null
New-Item -ItemType Directory -Force "D:\extraproject\.gradle-tmp" | Out-Null

& "$PSScriptRoot\gradlew.bat" --no-daemon clean testDebugUnitTest assembleDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "BUILD OK: $PSScriptRoot\app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Green
