@echo off
chcp 65001 >nul
echo 正在启动前端开发服务器...
cd /d "%~dp0"
npm run dev
pause

