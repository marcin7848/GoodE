#!/bin/bash
bold=$(tput bold)
underline_start=$(tput smul)
underline_stop=$(tput rmul)
normal=$(tput sgr0)
echo "Witaj w procesie konfiguracji internetowej platformy egzaminacyjnej GoodE."
echo "Wypełnij poniżej wymagane dane lub zostaw puste, by zachować ustawienia domyślne."
echo ""
read -p "Podaj ${underline_start}${bold}host bazy danych${normal}${underline_stop} [np. localhost]: " hostDatabase
read -p "Podaj ${underline_start}${bold}port bazy danych${normal}${underline_stop} [np. 5432]: " portDatabase
read -p "Podaj ${underline_start}${bold}nazwę bazy danych${normal}${underline_stop} [np. GoodE]: " nameDatabase
read -p "Podaj ${underline_start}${bold}nazwę użytkownika do bazy danych${normal}${underline_stop} [np. postgres]: " usernameDatabase
read -p "Podaj ${underline_start}${bold}hasło do bazy danych${normal}${underline_stop} : " passwordDatabase
read -p "Podaj ${underline_start}${bold}port serwera${normal}${underline_stop} [np. 8080]: " portServer
read -p "Podaj ${underline_start}${bold}host do serwera Angular${normal}${underline_stop} [np. localhost]: " hostAngular
read -p "Podaj ${underline_start}${bold}port do serwera Angular${normal}${underline_stop} [np. 4200]: " portAngular
read -p "Czy podane dane zostały wprowadzone poprawnie? (T/N): " confirm && [[ $confirm == [tT] || $confirm == [tT][aA][kK] ]] || exec bash "$0" "$@"
echo ""
echo "Proces instalacji został rozpoczęty. Czekaj na zakończenie."

echo "jdbc.driverClassName = org.postgresql.Driver" > application.properties
if [ -z "${hostDatabase}" ]; then
    hostDatabase="localhost"
fi
if [ -z "${portDatabase}" ]; then
    portDatabase="5432"
fi
if [ -z "${nameDatabase}" ]; then
    nameDatabase="GoodE"
fi
echo "jdbc.url = jdbc:postgresql://${hostDatabase}:${portDatabase}/${nameDatabase}" >> application.properties

if [ -z "${usernameDatabase}" ]; then
    usernameDatabase="postgres"
fi
echo "jdbc.username = ${usernameDatabase}" >> application.properties

if [ -z "${passwordDatabase}" ]; then
    passwordDatabase="postgres"
fi
echo "jdbc.password = ${passwordDatabase}" >> application.properties

echo "hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect" >> application.properties
echo "hibernate.show_sql = true" >> application.properties
echo "hibernate.format_sql = true" >> application.properties
echo "email.username = goode.sender" >> application.properties
echo "email.password = CMf0Cez3h*v9VB1f!^Ue#uK7T" >> application.properties
echo "email.fullAddress = goode.sender@gmail.com" >> application.properties
echo "email.host = smtp.gmail.com" >> application.properties
echo "email.port = 587" >> application.properties

if [ -z "${portServer}" ]; then
    portServer="8081"
fi
echo "server.port = ${portServer}" >> application.properties

if [ -z "${hostAngular}" ]; then
    hostAngular="localhost"
fi
if [ -z "${portAngular}" ]; then
    portAngular="4200"
fi
echo "angular.host =http://${hostAngular}:${portAngular}" >> application.properties

mv application.properties src/main/resources/

jar -cvf GoodE.war * 

echo ""
echo ""
read -p "Proces instalacji zakończony. Wygenerowany plik 'GoodE.war' uruchom na serwerze w wybranej aplikacji. Naciśnij enter by zamknąć to okno."