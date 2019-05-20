# Tarea Redes
----

### Instrucciones para la ejecución

    Para el correcto funcionamiento de la aplicacción es necesario ejecutar en consola el comando make en consola, en la carpeta del servicio que se quiera ejecutar, Servidor, Cliente o Almacenamiento

### Github

    Si se tiene el acceso solo a la consola de una maquina, es posible descargar el repositorio desde github, ocupando el comando: 
    $git clone https://github.com/vichinzi/TareaRedes
    Si es que no reconoce el comado git es necesario descargarlo
    centos: $yum install git 

### Consideraciones importantes

 - El puerto 11111 será ocupado por el servicio en ejecución, para servidores y almacenamiento indiscriminadamente, por lo que si esto no es modificado no se puede ejecutar en un mismo computador dos servicios en paralelo.
 - El comando delete y el get no están implementados.
 - El firewall puede estar bloqueando el acceso, si es que no está funcionando es necesario verificarlo, se puede botar el firewall con el comando:
  $service firewalld stop
 

### Detalles de la implementación
 - El servidor es informado de las maquinas conectadas mediante el arhivo ip.txt, en este se tiene que colocar la dirección ip de cada maquina de almacenamiento. Para obtener el ip de una maquina se tienen distintos comandos dependiendo del sistea operativo:
     Windows: ipconfig
     Linux: ifonfig
     Linux: ip addr show
 - El servidor administra la información de los archivos almacenados mediante INDEX.txt, el que tiene lieas tipo: nombre_archivo index_archivo
 - Los archivos index_archivo tienen la informacion de, en donde estan localizados los paquetes del archivo 
