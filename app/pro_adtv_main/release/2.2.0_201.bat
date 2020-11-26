set curdir=%~dp0
cd /d %curdir%
certutil -hashfile app-pro_adtv_main-release.apk MD5
pause