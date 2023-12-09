@echo off
TITLE Practica 2 Metaheuristicas
MODE con:cols=80 lines=40

:inicio
SET var=0
cls
echo ------------------------------------------------------------------------------
echo Selecciona un apartado - %DATE% ^| %TIME%
echo ------------------------------------------------------------------------------
echo  1    Ejecutar con funciones base  
echo  2    Ejecutar apartado MAPE y RMSE   
echo  3    Salir
echo ------------------------------------------------------------------------------
echo.

SET /p var= ^> Seleccione una opcion [1-3]:

if "%var%"=="0" goto inicio
if "%var%"=="1" goto op1
if "%var%"=="2" goto op2
if "%var%"=="3" goto salir

echo. El numero "%var%" no es una opcion valida, por favor intente de nuevo.
echo.
pause
echo.
goto:inicio

:op1
    echo.
    echo.
        java -jar setup.jar "config.txt"
        color 08
    echo.
    pause
    goto:inicio

:op2
    echo.
    echo.
        java -jar setup.jar "config.txt" "daido-tra.dat"
        color 09
    echo.
    pause
    goto:inicio


:salir
    @cls&exit