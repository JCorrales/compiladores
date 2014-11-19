compiladores
============
Integrantes:
- juan andres corrales duarte
- manuel caceres

obs del traductor:
- el fuente xml se guarda en la direccion proveida por el usuario al comienzo del programa con el nombre out.xml

obs del parser:
- cuando se produce un error se muestra un mensaje que especifica cuantos tokens se consumieron hasta alcanzar
un token de sincronizacion
- se adjunta un pdf con la gramatica utilizada

obs del lexer:
- eL output.txt se guarda en la direccion proveida por el usuario al comienzo del programa
- el analizador lexico tiene en cuenta los caracteres de escape en los string
- los mensajes de error se imprimen en pantalla
- el fuente de ejemplo genera error en el conteo del numero de linea cuando se introducen errores
y no se ha podido resolver ese problema, aun asi los errores lexicos se detectan correctamente, 
en ningun otro fuente se produce errores de conteo de linea
- fuente2.txt es un fuente extraido de internet al que le introducimos algunos errores lexicos y no genera error
en el numero de linea