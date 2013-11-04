@echo off
del Strings_zh_CN.properties
native2ascii Strings_zh_CN.properties.txt Strings_zh_CN.properties
rem copy /Y Strings_zh_CN.properties bin
copy /Y Strings_zh_CN.properties src