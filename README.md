# RESTful API design & best practices

## �Qu� vamos a ver?

En este apartado se pretende recoger, de manera resumida, los diferentes tipos de patrones y buenas pr�cticas a la hora de dise�ar API RESTful. Adem�s se plantear�n algunos ejemplos sobre c�mo llevarlos a cabo bajo el framework Spring.

## �Qu� es una API REST vs API RESTful?

Una API REST es una interfaz que permite acceder a un servicio web mediante peticiones HTTP.

Una API RESTful es m�s un estilo arquitect�nico: un conjunto de principios que las aplicaciones web utilizan para comunicarse entre s�. Las API RESTful est�n construidas de tal forma que permiten a la aplicaci�n web acceder a los datos y recursos que necesita sin necesidad de conocimientos especiales de programaci�n. El objetivo es hacerlas sencillas y f�ciles de usar tanto para los desarrolladores como para los usuarios.

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

Indica c�mo interpretar la parte de la URI despu�s de los :. Aunque algunos de los m�s comunes son http y https, no est� asociado a ning�n protocolo espec�fico.

[Ver tipos](https://en.wikipedia.org/wiki/List_of_URI_schemes)


#### Authority

Indica qui�n es el propietario del servicio. Por ejemplo: *en.wikipedia.org*

#### URI path

Indica la ruta completa del recurso

##### Arquetipos de recursos

Los recursos podr�an clasificarse dos arquetipos principales:

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

**MUY IMPORTANTE**: las URI's siempre se env�an en claro durante las comunicaciones entre servicios, por lo que SIEMPRE se recomienda evitar usar estos par�metros si se env�a informaci�n sensible, independientemente de si la comunicaci�n est� asegurada con HTTPS o no.

Un uso muy com�n de estos par�metros est� dirigido a la paginaci�n, ordenaci�n y/o filtrado de resultados, como veremos m�s adelante.

<img src="doc/URI_query.png" alt="URI query"/>

### Metadatos

Los metadatos son otro tipo de informaci�n que se env�a durante las comunicaciones entre servicios y a los que en ocasiones ni siquiera prestamos atenci�n. A continuaci�n veremos algunos de los m�s utilizados:

#### Cabeceras

<img src="doc/headers.png" alt="headers"/>

- Content-type: indica el formato de la respuesta
- Content-lenght: indica el tama�o de los resultados en bytes de manera que el cliente pueda saber con anterioridad si se trata de una llamada muy pesada y podr�a causarle problemas de performance
- [ETag](https://es.wikipedia.org/wiki/HTTP_ETag) (Entity Tag): hash MD5 proporcionado como una manera de ayudar a prevenir que actualizaciones simult�neas de un recurso se sobrescriban entre s� 
- Cache: conjunto de cabeceras que indican el uso o no de sistemas de cache, tales como 'cache-control', 'expires', 'date-response', 'no-cache', etc.
- Authorization: envia el token de autorizaci�n en el caso de APIs securizadas

#### MIME

Permite que un cliente pueda solicitar la informaci�n de un recurso bajo un formato determinado:

<img src="doc/Accept.png" alt="headers"/>

- Enviando la cabecera: Accept=application/json
- Enviando el queryParam: ?accept=json

## Patrones

### Sin estado

### Negociaci�n del contenido

### URI templates

### Paginaci�n

### Discoverability

### Errores y logs de excepciones

### Unicode

## Patrones avanzados

## Bibliograf�a

- RESTful API Design Patterns and Best Practices - Harihara Subramanian
- https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
