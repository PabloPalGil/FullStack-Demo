# FullStack-Demo
A demo that proves full stack skills based on a backend of Spring Boot + mongoDb (Java) and a frontend of kotlin + jetpack compose linked through retrofit.

<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8" /></head><body><p><em>Autor: Pablo Palanques Gil</em></p>
<h1>FullStack-Demo</h1>
<h2>Guía de ejecución</h2>
<p>En este documento se explica cómo poner en marcha el proyecto en local.</p>
<h3>1 - Creación de la bd:</h3>
<p><em><strong>Nota: se adjunta una guía detallada con imágenes de cómo realizar este primer paso, pero se indica igualmente a continuación:</strong></em></p>
<p>Instalar Mongodb y crear una conexión sin credenciales al puerto por defecto 27017</p>
<p>Mediante el Shell de Mongo introducir los siguientes comandos para crear la bd Eventos, la colección eventos y darle contenido.</p>
<p>Esto creará la bd Eventos:</p>
<pre><code class="language-bash">
    use Eventos
</code></pre>
<p>Esto creará una colección llamada eventos:</p>
<pre><code class="language-bash">
    db.createCollection(eventos)
</code></pre>
<p>Finalmente introducimos el contenido:</p>
<pre><code class="language-bash">
db.eventos.insert (
[
{
titulo: "Proyecto intermodular",
    descripcion: "Realizar proyecto final de los módulos AD-DI-PMDM",
    categoria: "DAM",
    fechaRealizacion: new Date("2025-02-04T22:59:59Z"),
    favorito: true,
},
{
titulo: "Cumpleaños Lucía",
    descripcion: "Fecha de cumpleaños de Lucía",
    categoria: "Cumpleaños",
    fechaRealizacion: new Date("2025-11-09T17:00:00Z"),
    favorito: true,
},
{
titulo: "Celebración cumple 34 P. Renard",
    descripcion: "Tardeo en tascas. Regalo: botella blue label",
    categoria: "Cumpleaños",
    fechaRealizacion: new Date("2025-02-08T19:00:00Z"),
    favorito: true,
},
{
titulo: "Tomar antibiótico",
    descripcion: "Tomar antibiótico cada 12 horas durante 10 días.",
    categoria: "Salud",
    fechaRealizacion: new Date("2025-02-01T08:00:00Z"),
    favorito: false,
},
{
titulo: "Comida familiar",
    descripcion: "Comida familiar con los tíos de Albacete",
    categoria: "Familia",
    fechaRealizacion: new Date("2025-02-10T13:00:00Z"),
    favorito: false,
},
{
titulo: "Boda Patri y Adrián",
    descripcion: "Bodorrio primos",
    categoria: "Bodas",
    fechaRealizacion: new Date("2025-02-15T12:30:00Z"),
    favorito: true,
},
{
titulo:"Bajar la basura",
    categoria: "Hogar",
    fechaRealizacion: new Date("2025-02-07T21:00:00Z"),
    favorito: false,
},
{
titulo:"Alimentar gato",
    descripcion: "Poner 15g de pienso",
    categoria: "Hogar",
    fechaRealizacion: new Date("2025-02-01T07:00:00Z"),
    favorito: true,
},
{
titulo:"ITV Toyota RAV4",
    descripcion: "Pedir cita ITV 5567KBZ",
    categoria: "Mantenimiento RAV4",
    fechaRealizacion: new Date("2025-04-19T16:00:00Z"),
    favorito: false,
},
{
titulo:"Pasar ITV Toyota RAV4",
    descripcion: "Pasar ITV RAV4 en Villarreal",
    categoria: "Mantenimiento RAV4",
    fechaRealizacion: new Date("2025-04-12T17:15:00Z"),
    favorito: false,
},
{
titulo:"Aniversario",
    descripcion: "Aniversario P&J",
    categoria: "Pareja",
    fechaRealizacion: new Date("2025-06-16T00:00:00Z"),
    favorito: true,
},
{
    titulo: "Nochevieja 2024",
    descripcion: "Cena con amigos en Eslida",
    categoria: "Ocio",
    fechaRealizacion: new Date("2024-12-31T19:59:59Z"),
favorito: true
},
]
)
</code></pre>
<p>Nota 1: este Último comando tambiÉn se encuentra en un fichero de texto llamado Contenido base de datos.</p>
<p>Nota 2: en caso de necesidad, se puede reiniciar la colección introduciendo:</p>
<pre><code class="language-bash">db.eventos.remove({})
</code></pre>
<p>Esto eliminarÁ todos los documentos de la colección</p>
<p>Seguidamente, sólo queda introducir el comando previo con los datos.</p>
<h3>2 - Aplicación SpringBoot</h3>
<p>En el fichero .zip llamado Eventos_SpringMongo se encuentra la aplicación basada en SpringBoot que se comunica con una base de datos mongodb en local. Abrir el proyecto con un IDE como intelliJ y ejecutar la clase <em>EventosSpringMongoApplication</em> para poner en marcha el servidor local.</p>
<h3>3 - Aplicación Android</h3>
<p>En el fichero .zip Eventos-Retrofit se encuentra el proyecto con la aplicación Android que se comunica con el servidor local mediante el emulador de Android incorporado en Android Studio, utilizando Retrofit. Esta aplicación es la interfaz de usuario.</p>
<p>Despliegue: con el servidor en marcha (punto 2), ejecutar el MainActivity del proyecto de Android Studio en uno de sus emuladores y esperar a que se inicie.</p>
<hr />
<p>A continuación se especifica el entorno de desarrollo (validado):</p>
<ul>
<li>S.O. Windows 11 pro</li>
<li>MongoDB Compass 1.45.1</li>
<li>IntelliJ IDEA 2024.3 (Ultimate Edition)</li>
<li>Android Studio Ladybug | 2024.2.1 Patch 3</li>
<li>Emulador Android: Pixel 8 Pro API 35</li>
</ul>
<hr />
<p><strong>2025/02/04</strong></p>
</body></html>
