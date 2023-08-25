# RESTful API design & best practices

## �Qu� vamos a ver?

En este apartado se pretende recoger, de manera resumida, los diferentes tipos de patrones y buenas pr�cticas a la hora de dise�ar API RESTful. Adem�s se plantear�n algunos ejemplos sobre c�mo llevarlos a cabo bajo el framework Spring.

## �Qu� es una API REST vs API RESTful?

Una API REST es una interfaz que permite acceder a un servicio web mediante peticiones HTTP.

Una API RESTful es m�s un estilo arquitect�nico: un conjunto de principios que las aplicaciones web utilizan para comunicarse entre s�. Las API RESTful est�n construidas de tal forma que permiten a la aplicaci�n web acceder a los datos y recursos que necesita sin necesidad de conocimientos especiales de programaci�n. 

Algunos de los objetivos son:

- Hacerlas sencillas y f�ciles de usar tanto para los desarrolladores como para los usuarios.
- Utilizar patrones, terminolog�a estandar y desarrollar comportamientos homog�neos
- Manejo de errores claro e intuitivo
- Proveer documentaci�n

## Reglas

### URI's para representar recursos

Las URI's son la puerta de entrada para una API REST, por lo que es importante que tengan la estructura correcta.

> *scheme://authority/path[?query]*

Ejemplos:

*https://developers.google.com/identity/protocols/oauth2/web-server?hl=es-419*

�

<img src="doc/URI.png" alt="URI"/>

Recomendaciones generales:
- Deben definir de forma clara y sencilla la representaci�n del recurso
- Usar s�lo minusculas
- No incluir '/' al final de la ruta

#### Scheme

Indica c�mo interpretar la parte de la URI despu�s de los ':'. Aunque algunos de los m�s comunes son http y https, no est� asociado a ning�n protocolo espec�fico.

[Ver tipos](https://en.wikipedia.org/wiki/List_of_URI_schemes)


#### Authority

Indica qui�n es el propietario del servicio. Por ejemplo: *en.wikipedia.org*

#### URI path

Indica la ruta completa del recurso

##### Arquetipos de recursos

Los recursos podr�an clasificarse en dos arquetipos principales:

- CRUD: incluye aquellos recursos de tipo almacen, coleccion y documento
- ACTIONS: incluye aquellos recursos de tipo controlador

Veamos algunos ejemplos:

CRUD (Create, Read, Update y Delete)

<img src="doc/" alt="swagger CRUD"/> PENDIENTE

ACTIONS

<img src="doc/" alt="swagger ACTION"/> PENDIENTE

##### Recomendaciones:
- Evitar el uso de guiones bajos ('_') y puntos ('.') ya que, seg�n donde se visualice, puede superponerse con el subrayado de los links
- Usar '-' en caso de necesitar palabras compuestas
- Utilizar nombres en singular para recursos de tipo documento
- Utilizar nombres en plugar para recursos de tipo almacenes o colecciones
- Utilizar verbos para recursos de tipo controlador junto al m�todo HTTP POST.

#### URI query

Recopila una lista de pares par�metro=valor que ser�n incluidos en la misma URI

MUY IMPORTANTE: las URI's siempre se env�an en claro durante las comunicaciones entre servicios, por lo que SIEMPRE se recomienda evitar usar estos par�metros si se env�a informaci�n sensible, independientemente de si la comunicaci�n est� asegurada con HTTPS o no.

Un uso muy com�n de estos par�metros est� dirigido a la paginaci�n, ordenaci�n y/o filtrado de resultados, como veremos m�s adelante.

<img src="doc/URI_query.png" alt="URI query"/>

### Metadatos

Los metadatos son otro tipo de informaci�n que se env�a durante las comunicaciones entre servicios y a los que en ocasiones ni siquiera prestamos atenci�n. A continuaci�n veremos algunos de los m�s utilizados:

<img src="doc/headers.png" alt="headers"/>

- **Content-type**: indica el formato de la respuesta
- **Content-lenght**: indica el tama�o de los resultados en bytes de manera que el cliente pueda saber con anterioridad si se trata de una llamada muy pesada y podr�a causarle problemas de performance
- **[ETag](https://es.wikipedia.org/wiki/HTTP_ETag) (Entity Tag)**: hash MD5 proporcionado como una manera de ayudar a prevenir que actualizaciones simult�neas de un recurso se sobrescriban entre s� 
- **Cache**: conjunto de cabeceras que indican el uso o no de sistemas de cache, tales como 'cache-control', 'expires', 'date-response', 'no-cache', etc.
- **Authorization**: env�a el token de autorizaci�n en el caso de APIs securizadas
- **[Accept-Encoding](https://http.dev/accept-encoding)**: indica los tipos de codificaci�n permitidos 


## Patrones

### Sin estado

Una de principales caracter�sticas de las APIs RESTful es su grado de independencia y escalabilidad, por lo que es importante evitar el uso de sesiones o estados. Esto obligar�a a pasar las peticiones de una misma sesi�n a unas instancias concretas, volvi�ndolo todo mucho m�s complejo.

Para ello lo m�s importante es que en cada petici�n exista la informaci�n necesaria y suficiente requerida para resolverla. Dejaremos en manos de los clientes (frontales/m�viles) el control de estas sesiones en caso de que sean necesarias.

### Negociaci�n del contenido

Ofrece la posibilidad de que un cliente pueda solicitar la informaci�n de un recurso bajo un formato determinado:

<img src="doc/Accept.png" alt="headers"/>

Dos posibles alternativas:
- **Enviando la cabecera**: Accept=application/json
- **Enviando el queryParam**: ?accept=json

En caso de que el formato no sea soportado se recomienda informar debidamente al cliente, por ejemplo devolviendo un error con c�digo *406 - Not Acceptable* y un mensaje de 'Formato no soportado'

<img src="doc/" alt="Format selection"/> PENDIENTE

### URI templates

Consiste en la utilizaci�n de variables durante la construcci�n de URIs, de forma que sea mucho m�s pr�ctico e intuitivo para el resto de desarrolladores.

<img src="doc/URI_templates.png" alt="URI templates"/>

### Paginaci�n

La paginaci�n consiste en devolver un conjunto de resultados en subgrupos llamados p�ginas. De esta forma nos permite limitar el tama�o de los resultados que estamos obteniendo y no sobrecargar la memoria del cliente.

Otro aspecto importante: la paginaci�n es una representaci�n de un recurso y no un recurso en s�. Por tanto, no cabe la opci�n de obtener a esta informaci�n de la forma */api/../recurso/page/2*. En su lugar ser�a necesario el uso de queryParams para su obtenci�n */api/../recurso?page=2*.

Existen diferentes formas de implementar la paginaci�n:

- **Offset based**: utilizando par�metros como *offset* y *limit*
- **Time based**: utilizando par�metros como *since* y *until*
- **Cursor based**: utilizando par�metros como *page* y *size*

<img src="doc/URI_query.png" alt="URI query"/>

### Descubrimiento

Provee al cliente de las URI que pueden serles de utilizad para otras operaciones con el recurso.

Por ejemplo, como respuesta a una operaci�n de creaci�n POST, la respuesta podr�a devolver la siguiente estructura:

<img src="doc/HATEOAS.png" alt="HATEOAS"/>

Del mismo modo tambi�n ser�a de utilidad limitar aquellos m�todos HTTP que no est�n permitidos (*405 - Method Not Allowed*) para informar al cliente qu� operaciones puede y no puede realizar sobre el recurso.

### Errores y logs de excepciones

Es recomendable asociar correctamente el c�digo de error HTTP devuelto con el mensaje mostrado, con el objetivo de evitar malentendidos y ser m�s transparente de cara al usuario.
Para ello es una pr�ctica com�n generar excepciones personalizadas que nos permitan identificar correctamente el momento y la causa del error.

<img src="doc/exception.png" alt="Exception"/>
<img src="doc/controllerAdvice.png" alt="Controller advice"/>

### Unicode

Finalmente tambi�n es importante el uso correcto del charset, de manera que el conjunto de caracteres devueltos se adec�e a lo solicitado por el cliente (por ejemplo: UTF8)
Esto ayudar� a que la API pueda ser internacionalizada.

<img src="doc/" alt="Charset"/> PENDIENTE

## Patrones avanzados

## Bibliograf�a

- [https://restfulapi.net/](https://restfulapi.net/)
- [RESTful API Design Patterns and Best Practices - Harihara Subramanian](https://www.packtpub.com/product/hands-on-restful-api-design-patterns-and-best-practices/9781788992664)
- [https://en.wikipedia.org/wiki/Uniform_Resource_Identifier](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier)
