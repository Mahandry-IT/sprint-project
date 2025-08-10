@echo off
setlocal EnableDelayedExpansion

rem D\u00E9finir le chemin d'acc\u00E8s au r\u00E9pertoire des sources et au r\u00E9pertoire de destination des fichiers compil\u00E9s
set "sourceDirectory=.\..\src\main\java"
set "destinationDirectory=.\..\bin"

rem Chemin vers le r\u00E9pertoire contenant les biblioth\u00E8ques n\u00E9cessaires
set "libDirectory=.\..\lib"  

rem Initialiser la liste des fichiers Java \u00E0 compiler
set "javaFiles="

mkdir "%destinationDirectory%"
rem R\u00E9cup\u00E9rer la liste de tous les fichiers Java dans les sous-dossiers de %sourceDirectory%
for /r "%sourceDirectory%" %%G in (*.java) do (
    rem Extraire la structure des packages \u00E0 partir du chemin complet du fichier source
    set "javaFile=%%~fG"
    set "packagePath=!javaFile:%sourceDirectory%=!"
    set "packagePath=!packagePath:~0,-\%%~nG%%~xG!"

    rem Cr\u00E9er les r\u00E9pertoires de sortie si n\u00E9cessaire
    if not exist "%destinationDirectory%!packagePath!" (
        mkdir "%destinationDirectory%!packagePath!" >nul
    )

    rem Ajouter le fichier Java \u00E0 la liste des fichiers \u00E0 compiler
    set "javaFiles=!javaFiles! "%%G""
)

rem Construire le chemin de classe pour toutes les biblioth\u00E8ques dans le dossier "lib"
set "classpath="
for %%I in ("%libDirectory%\*.jar") do (
    set "classpath=!classpath!;"%%I""
)

rem ajoute une option pour cela compile en java 17
rem Compiler tous les fichiers Java en une seule commande avec les biblioth\u00E8ques n\u00E9cessaires
javac -cp "%classpath%" -parameters -d "%destinationDirectory%" !javaFiles!

endlocal
