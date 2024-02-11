Para ejecutar se tiene que abrir en un IDE, abrir las clases Server y Client (editando la configuracion de "Run" para que permita más de una
ejecución de la clase Cliente a la vez), y los clientes se conectarán al servidor una vez esté abierto.

He incluido varias funcionalidades extra, como una persistencia de usuarios en un archivo CSV con la posibilidad de cambiar tanto la
contraseña como el usuario y que se actualice a tiempo real en ejecución.

Las salas pueden ser privadas y grupales, depende de si el usuario conoce la contraseña y el nombre de esa sala.
Puedes ver los comandos que se pueden hacer usando /help en el cliente.

También he incluido una clase TestCSV que prueba unitariamente los metodos que están en UserCSV, exceptuando el método cambiarUsuario que
se me ocurrió después de haberlo terminado.

Hay dos excepciones personalizadas pero no las he usado mucho porque he empezado bastante tarde y hoy he estado 10 horas seguidas con esto,
y se me hacen bola las excepciones y sus manejos con aplicaciones más grandes de las que vemos en clase.

Por último, la carpeta DAOs es porque me gustaría implementarle una base de datos en un futuro, además de una interfaz gráfica que también estoy
pensando en hacer.