# RESTful API design & best practices

## ¿Qué vamos a ver?

En este apartado se pretende recoger, de manera resumida, los diferentes tipos de patrones y buenas prácticas a la hora de diseñar API RESTful. Además se plantearán algunos ejemplos sobre cómo llevarlos a cabo bajo el framework Spring.

## ¿Qué es una API REST vs API RESTful?

Una API REST es una interfaz que permite acceder a un servicio web mediante peticiones HTTP.

Una API RESTful es más un estilo arquitectónico: un conjunto de principios que las aplicaciones web utilizan para comunicarse entre sí. Las API RESTful están construidas de tal forma que permiten a la aplicación web acceder a los datos y recursos que necesita sin necesidad de conocimientos especiales de programación. El objetivo es hacerlas sencillas y fáciles de usar tanto para los desarrolladores como para los usuarios.

## Reglas

### URI's para representar recursos

Las URI's son la puerta de entrada para una API REST, por lo que es importante que tengan la estructura correcta.

> *scheme://authority/path[?query]*

Ejemplos:

*https://developers.google.com/identity/protocols/oauth2/web-server?hl=es-419*

ó

<img src="doc/URI.png" alt="URI"/>

Recomendaciones generales:
- Deben definir de forma clara y sencilla la representación del recurso
- Usar sólo minusculas
- No incluir '/' al final de la ruta

#### Scheme

Indica cómo interpretar la parte de la URI después de los :. Aunque algunos de los más comunes son http y https, no está asociado a ningún protocolo específico.

[Ver tipos](https://en.wikipedia.org/wiki/List_of_URI_schemes)


#### Authority

Indica quién es el propietario del servicio. Por ejemplo: *en.wikipedia.org*

#### URI path

Indica la ruta completa del recurso

##### Arquetipos de recursos

Los recursos podrían clasificarse dos arquetipos principales:

- CRUD: incluye aquellos recursos de tipo almacen, coleccion y documento
- ACTIONS: incluye aquellos recursos de tipo controlador

Veamos algunos ejemplos:

CRUD (Create, Read, Update y Delete)

<img src="doc/" alt="swagger CRUD"/> PENDIENTE

ACTIONS

<img src="doc/" alt="swagger ACTION"/> PENDIENTE

##### Recomendaciones:
- Evitar el uso de guiones bajos ('_') y puntos ('.') ya que, según donde se visualice, puede superponerse con el subrayado de los links
- Usar '-' en caso de necesitar palabras compuestas
- Utilizar nombres en singular para recursos de tipo documento
- Utilizar nombres en plugar para recursos de tipo almacenes o colecciones
- Utilizar verbos para recursos de tipo controlador junto al método HTTP POST.

#### URI query

Recopila una lista de pares parámetro=valor que serán incluidos en la misma URI

**MUY IMPORTANTE**: las URI's siempre se envían en claro durante las comunicaciones entre servicios, por lo que SIEMPRE se recomienda evitar usar estos parámetros si se envía información sensible, independientemente de si la comunicación está asegurada con HTTPS o no.

Un uso muy común de estos parámetros está dirigido a la paginación, ordenación y/o filtrado de resultados, como veremos más adelante.

<img src="doc/URI_query.png" alt="URI query"/>

### Metadatos

Los metadatos son otro tipo de información que se envía durante las comunicaciones entre servicios y a los que en ocasiones ni siquiera prestamos atención. A continuación veremos algunos de los más utilizados:

#### Cabeceras

<img src="doc/headers.png" alt="headers"/>

- Content-type: indica el formato de la respuesta
- Content-lenght: indica el tamaño de los resultados en bytes de manera que el cliente pueda saber con anterioridad si se trata de una llamada muy pesada y podría causarle problemas de performance
- [ETag](https://es.wikipedia.org/wiki/HTTP_ETag) (Entity Tag): hash MD5 proporcionado como una manera de ayudar a prevenir que actualizaciones simultáneas de un recurso se sobrescriban entre sí 
- Cache: conjunto de cabeceras que indican el uso o no de sistemas de cache, tales como 'cache-control', 'expires', 'date-response', 'no-cache', etc.
- Authorization: envia el token de autorización en el caso de APIs securizadas

#### MIME

Permite que un cliente pueda solicitar la información de un recurso bajo un formato determinado:

<img src="doc/Accept.png" alt="headers"/>

- Enviando la cabecera: Accept=application/json
- Enviando el queryParam: ?accept=json

## Patrones

### Sin estado

### Negociación del contenido

### URI templates

### Paginación

### Discoverability

### Errores y logs de excepciones

### Unicode

## Patrones avanzados

## Bibliografía

- RESTful API Design Patterns and Best Practices - Harihara Subramanian
- https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
