@echo off
del Strings_fr_FR.properties
native2ascii Strings_fr_FR.properties.txt Strings_fr_FR.properties
rem copy /Y Strings_fr_FR.properties bin
copy /Y Strings_fr_FR.properties src