@echo off
setlocal

rem D\u00E9claration des variables
set projet=Sprint
set temp=.\..\temp
set conf=.\..\src\main\java\resources
set config=.\..\application.properties
set lib=.\..\lib
set dist=.\..\dist
set src=.\..\src
set bin=.\..\bin
set package=mg\itu
set web=.\..\src\main\java\resources\templates
set destination=D:\Logiciels\Tomcat 10.1\webapps
set project=D:\Travail\ITU\ticketing\lib

rem V\u00E9rifier si le dossier temp existe
if exist "%temp%\" (
    rd /S /Q "%temp%"
)

rem Cr\u00E9ation d'un dossier temp avec les contenu de base si le dossier temp n'existe pas
mkdir "%dist%"
mkdir "%temp%"
mkdir "%temp%\views"
mkdir "%temp%\WEB-INF"
mkdir "%temp%\WEB-INF\lib"
mkdir "%temp%\WEB-INF\classes"  

rem Copie des \u00E9lements indispensables pour tomcat vers temp
copy /Y  "%config%" "%temp%\WEB-INF\"
xcopy /E /I /Y "%web%\" "%temp%\views"
xcopy /E /I /Y "%conf%\" "%temp%\WEB-INF\"

rem Compilation des codes java vers le dossier bin
call compilateur.bat

rem cr\u00E9ation de fichier .jar
jar cvf "%lib%\%projet%.jar" -C "%bin%" . 
copy /Y  "%lib%\%projet%.jar" "%project%"

rem Copie des \u00E9lements n\u00E9cessaires vers classes de tomcat
xcopy /E /I /Y "%lib%\" "%temp%\WEB-INF\lib"
xcopy /E /I /Y "%bin%\" "%temp%\WEB-INF\classes"

rem D\u00E9placement du r\u00E9pertoire actuel vers temp
cd /D "%temp%"

rem Compresser dans un fichier war
jar -cvf "%dist%\%projet%".war *

rem D\u00E9placement du r\u00E9pertoire actuel vers le projet
cd /D ..\

rem Copie des \u00E9lements indispensables pour tomcat vers temp
copy /Y  ".\dist\%projet%.war" "%destination%"

rem D\u00E9placement du r\u00E9pertoire actuel vers le dossier script
cd /D .\script

rem Suppression du dossier temporaire avant la fin
for %%D in ("%temp%" "%dist%" "%bin%") do (
    if exist %%D (
        rd /S /Q %%D
    )
)

endlocal
