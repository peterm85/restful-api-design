# RESTful API design & best practices

## ¿Qué vamos a ver?

En este apartado se pretende recoger, de manera resumida, los diferentes tipos de patrones y buenas prácticas a la hora de diseñar API RESTful. Además se plantearán algunos ejemplos sobre cómo llevarlos a cabo bajo el framework Spring.

## <a name="index">Índice:</a>
 - [¿Qué es una API vs API REST vs API RESTful?](#what-is)
 - [Buenas prácticas](#best-practices)
     + Nomenclatura consistente
     + Eficiencia
     + Seguridad
     + Versionado
     + Manejo de errores
     + Documentación
 - [API first](#api-first)
 - [Bibliografía](#bibliography)


## <a name="what-is">¿Qué es una API vs REST vs API RESTful?</a> [&#8593;](#index) 

Una [API](https://es.wikipedia.org/wiki/API) es un conjunto de reglas y protocolos que permite a diferentes sistemas de software comunicarse entre sí y que compartan información y funcionalidades.

- Orientado a máquinas, no a usuarios
- No está atado a un lenguaje de programación
- Permite desacoplar, escalar y reutilizar sistemas

Existen muchos tipos de APIs. Algunas de las más utilizadas pueden ser:

![APIs|100](doc/APIs.png)


[REST](https://es.wikipedia.org/wiki/Transferencia_de_Estado_Representacional) es un estilo de arquitectura software para servicios web con las siguientes restricciones:

- Arquitectura cliente-servidor a través de protocolo HTTP
- Sin estado, permite una mayor escalabilidad
- Interface uniforme bien definida. Cada recurso proporciona:
  + Identificación: URL
  + Manipulación: uso de formatos JSON, XML, HTML, …
  + Mensajes autodescriptivos: nombre del recurso, métodos HTTP y metadatos
  + Hipervínculos: HATEOAS, representa cómo cambiar el estado de un recurso
- Cacheable, permite una mejor eficiencia


La palabra RESTful describe a aquellas APIs o servicios que se adhieren a los principios REST. Para ello una API RESTful debe seguir las reglas definidas por REST.

Sólo con las restricciones vistas en el punto anterior aún queda todo bastante abierto a la interpretación de cada desarrollador. Por ello, aunque no hay un estándar claro aceptado por la comunidad, sí se hace necesario detallar unas buenas prácticas que ayuden a obtener APIs RESTful de calidad.

Para ello tendremos en cuenta los siguientes objetivos:
- Hacerla fácil de usar para los desarrolladores. La calidad de una API es directamente proporcional a la facilidad de su integración.
- Utilizar patrones, terminología estándar y desarrollar comportamientos homogéneos
- Manejar los errores de manera clara e intuitiva
- Proveer documentación


## <a name="best-practices">Buenas prácticas</a> [&#8593;](#index) 

### Nomenclatura consistente

##### URI's para representar recursos

Las URI's son la puerta de entrada para una API REST, por lo que es importante que tengan la estructura correcta.

![URI|100](doc/URI.png)

Recomendaciones generales:
- Claridad: Deben definir de forma clara y sencilla la representación del recurso.
- Legibilidad: Usar sólo minusculas, evitar guiones bajos (‘_’) y puntos (‘.’) y si es necesario, usar (‘-’) en palabras compuestas (kebab-case).
- Homogeneidad: Recursos en plural, acordar terminología (id, bookId, idBook, ...) y no incluir barra (‘/’) al final de la ruta.
- Seguridad: Nunca enviar información sensible en los parámetros, independientemente de si se usa HTTPS.


##### Modelado de recursos

Un recurso es un conjunto de datos que vamos a servir desde nuestra API RESTful.

El manejo de recursos deberá realizarse mediante representaciones, es decir, las peticiones y respuestas pueden ser un subconjunto de datos del recurso. De esta manera controlaremos también los datos a los que cada usuario/rol está autorizado.

Sobre cada recurso permitiremos realizar diferentes tipos de operaciones:

- CRUD
- ACTIONS: Definen una acción particular realizada sobre el recurso, utilizando el método POST y terminando en verbo.

Veamos algunos ejemplos:

**CRUD** (Create, Read, Update y Delete)

![CRUD|100](doc/CRUD.png)

**ACTIONS**

![ACTIONS|100](doc/ACTIONS.png)


##### Negociación del contenido

Ofrece la posibilidad de que un cliente pueda solicitar la información de un recurso bajo un formato o idioma determinado:


Se puede manejar de diferentes formas:
- **Mediante cabecera**: Accept=application/json ó Accept-languague=ES_es
- **Mediante queryParam**: ?accept=json ó ?accept-language=ES_es

<img src="doc/contentNegotiation.png" alt="Content Negotiation"/>

En caso de que el formato no sea soportado se recomienda informar debidamente al cliente, por ejemplo devolviendo un error con código *406 - Not Acceptable*.

<img src="doc/contentNegotiation_notAcceptable.png" alt="Content Negotiation - Not Acceptable"/>


### Eficiencia

##### Paginación

Estrategia diseñada para devolver un subconjunto de datos de un recurso en lugar de su totalidad. Por tanto se consideraría una representación de recursos, no un recurso en sí.

Beneficios:
- Mejor performance
- Mayor fiabilidad ante sobrecargas

La respuesta debería devolver un código *200 - Ok*, si se obtienen todos los recursos disponibles, ó *206 - Partial Content*, si se obtiene sólo una parte

Existen diferentes formas de implementar la paginación:

- **Cursor based**: utilizando parámetros como *page* y *size*
- **Time based**: utilizando parámetros como *since* y *until*
- **Offset based**: utilizando parámetros como *offset* y *limit*

<img src="doc/URI_query.png" alt="URI query"/>


#### Caché

La caché es la capacidad de almacenar copias de datos a los que se accede con frecuencia en varios lugares a lo largo de la ruta solicitud-respuesta. 

La optimización de la red mediante el almacenamiento en caché mejora la calidad general del servicio de las siguientes maneras:

- Reduce el ancho de banda
- Reduce la latencia
- Reducir la carga de los servidores
- Ocultar los fallos de la red

Las peticiones GET deberían ser almacenables en caché por defecto, hasta que se dé una condición especial.

<img src="doc/cache.png" alt="Cache"/>

>[StockControllerImpl.java](restful-sv/src/main/java/org/example/restful/adapter/rest/v1/controller/StockControllerImpl.java)

```
  @Override
  @RolesAllowed({USER, ADMIN})
  @Cacheable(value = "allstocks")
  @GetMapping(value = SUBPATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<StockResponse>> getAllStocks() {
    log.info("Getting all stocks");

    final List<StockResponse> responses = responseConverter.convert(stockService.getAllStocks());

    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(cacheTTL.getAllStocksTTL(), TimeUnit.MILLISECONDS))
        .lastModified(Instant.now())
        .body(responses);
  }
```


#### Operaciones asíncronas

Una operación 'bulk' se diferencia de una operación 'batch' en que la primera es una operación única con múltiples objetos y la segunda son múltiples operaciones con múltiples objetos.

Pongamos varios ejemplos:

- Bulk: registrar varias acciones a un inversor
- Batch: registrar varias acciones a varios inversores (por ejemplo en el caso de que el valor de las acciones baje de precio)

Para ello es posible utilizar el método PATCH del recurso de manera que el body provea el listado de objetos a incorporar.

Este tipo de operaciones pueden conllevar una baja performance por lo que podría ser necesario resolverlos de forma asíncrona (devolviendo *202 - Accepted*) o mediante la implementación de flujos en paralelo para una respuesta más rápida.


#### Idempotencia

La idempotencia es una característica basada en que ante una misma petición, la respuesta debería ser idéntica. Este patrón viene a solucionar sobre todo ciertos problemas de concurrencia o de peticiones repetidas. Por defecto los métodos GET, PUT y DELETE son considerados idempotentes, al contrario que los métodos POST y PATCH. 

En la mayoría de casos podría ser suficiente con adaptar algunos endpoints de manera que siempre devuelvan la misma respuesta. Un [ejemplo](restful-sv/src/main/java/org/example/restful/service/InvestorService.java) podría ser una petición DELETE que elimine un inversor: si el inversor existe lo elimina y si ya fue eliminado anteriormente no hace nada. De este modo el cliente no tendrá que preocuparse en caso de recibir un error por inversor no encontrado ya que no le aporta ningún valor.

En caso de que sea necesario puede hacerse uso de la cabecera ETag para validar si el resultado sigue siendo el mismo. De lo contrario se recomendaría notificar al cliente mediante un error HTTP *409 - Conflict*


#### BTF (Backend to Frontend)

Existe una problemática específica para las aplicaciones multiplataforma: aquellas que tienen una web y una app movil ligera. En estos casos, por ejemplo, la app podría necesitar menos información y una mejor performance que la que pudiera resolver la web. Por ello se propone la implementación de interfaces especializadas para cada uno de los clientes (*/api/web/resource* vs */api/app/resource*). Esto puede resultar controvertido dadas las ventajas e inconvenientes que conlleva:

**Ventajas:**
- Permite tener una representación de los recursos más reducida para aquellos clientes que no necesiten tanta información 
- Podríamos mejorar la performance para estos clientes que necesitaran una respuesta mucho más rápida

**Inconvenientes:**
- Sería necesario mantener ambas interfaces con un comportamiento muy similar


### Seguridad

Un aspecto importante en las APIs RESTful es la seguridad. A continuación enumeraremos algunos de los puntos más importantes:

- Política de mínimos permisos (@RolesAllowed)
- Hazlo simple. Cuanto más 'innecesariamente' compleja es una solución, más facil es dejar abierta alguna brecha
- Siempre usar el protocolo HTTPs para asegurar las conexiones
- Utiliza contraseñas con hash
- Nunca exponer información sensible en las URLs tales como usuarios, contraseñas, tokens, etc. tal y como ya comentamos en el apartado [URI query](#uri-query)
- Considera el uso de OAuth en lugar de la autenticación básica (aunque ésta sea suficiente)
- Valida los parámetros de entrada (@Valid)

>[InvestorControllerImpl.java](restful-sv/src/main/java/org/example/restful/adapter/rest/v1/controller/InvestorControllerImpl.java)

```
  @Override
  @RolesAllowed({USER, ADMIN})
  @PostMapping(value = SUBPATH,
               consumes = MediaType.APPLICATION_JSON_VALUE,
               produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InvestorResponse> createInvestor(
      @Valid @RequestBody final InvestorRequest investorRequest) throws Exception {

    final Investor investor =
        investorService.createInvestor(requestConverter.convert(investorRequest));

    final InvestorResponse response = responseConverter.convert(investor);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
```


### Versionado

Es muy dificil afirmar que una API va a mantenerse intacta a lo largo de su vida sin necesidad de ningún cambio. En el caso de cambios menores, corrección de errores, etc. no sería necesario un versionado del mismo. Sin embargo, para cambios importantes o que pudieran resultar incompatibles con el funcionamiento de la versión anterior se recomienda anotar con versiones cada uno de los comportamientos.

A continuación veremos diferentes formas de versionar una API:

- Mediante URI path: *http://localhost/v2/resource*
- Mediante query parameter: *?version=2.0*
- Mediante custom headers: *x-resource-version=2.0*
- Mediante content-negociation: *Accept=application/resource-v2.0+json*

<img src="doc/versioning.png" alt="URI path versioning"/>

Quedará en manos del negocio la decisión de durante cuanto tiempo se deberán mantener versiones antiguas de un *endPoint* y el momento oportuno para darlo totalmente de baja.


##### Redirecciones

Aquellos endpoints que quedaran obsoletos o temporalmente inutilizables deberían comunicar correctamente esta situación y, en caso de que sea posible, proveer una alternativa al cliente. De este modo podríamos devolver un error HTTP *301 - Permanent redirect* o *307 - Temporary redirect* e incluir la cabecera *location* con la url alternativa.

>[InvestorControllerImpl.java](restful-sv/src/main/java/org/example/restful/adapter/rest/v1/controller/InvestorControllerImpl.java)

```
  @Override
  @Deprecated
  @RolesAllowed(ADMIN)
  @GetMapping(value = SUBPATH)
  public ResponseEntity<List<InvestorResponse>> getAllInvestors() {

    return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
        .location(
            UriComponentsBuilder.newInstance()
                .path(NEW_PATH.concat(SUBPATH))
                .query("page=0&size=3")
                .build()
                .toUri())
        .build();
  }
```

Este patrón permite al cliente poder recuperarse y finalizar la operación de forma satisfactoria. En nuestro caso de ejemplo, la propia interface de Swagger ya se encarga de redireccionar y devolver los resultados desde la url alternativa:

<img src="doc/redirectSwagger.png" alt="Redirect swagger"/>


### Manejo de errores

Es recomendable asociar correctamente el código de error HTTP devuelto con el mensaje mostrado, con el objetivo de evitar malentendidos y ser más transparente de cara al usuario.
Para ello es una práctica común generar excepciones personalizadas que nos permitan identificar correctamente el momento y la causa del error.

<img src="doc/exception.png" alt="Exception"/>

>[CustomControllerAdvice.java](restful-sv/src/main/java/org/example/restful/configuration/CustomControllerAdvice.java)

```
  @ExceptionHandler({InvestorNotFoundException.class, StockNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public final Error handleNotFoundExceptions(final Exception ex, final WebRequest request) {
    log.error(
        "Response to {} with status {} and body {}",
        request,
        HttpStatus.NOT_FOUND,
        ex.getMessage());

    return new Error("ERR404", ex.getMessage());
  }
```

Sin embargo, por motivos de seguridad, no deberíamos mostrar información sensible demasiado detallada en estos mensajes por las siguientes razones:
- En caso de una brecha de seguridad, un atacante podría explotar la información obtenida en estas respuestas, ya sean trazas, identificadores, etc.
- Normalmente estos mensajes pueden ser almacenados en ficheros de logs en claro, dejando los datos expuestos para ser explotados más tarde.

La clave está en el equilibrio.


### Documentación

##### Swagger


##### Descubrimiento

También conocido como HATEOAS (Hypermedia As The Engine Of Application State). Provee un listado de URL’s con las operaciones que se permiten realizar sobre el recurso.

Por ejemplo, como respuesta a una operación de creación POST, la respuesta podría devolver la siguiente estructura:

<img src="doc/HATEOAS.png" alt="HATEOAS"/>

Del mismo modo también sería de utilidad limitar aquellos métodos HTTP que no están permitidos (*405 - Method Not Allowed*) para informar al cliente sobre qué operaciones puede y no puede realizar en el recurso.

<img src="doc/notAllowedMethod.png" alt="Method Not Allowed"/>


## <a name="api-first">API first</a> [&#8593;](#index)

Se trata de una metodología de definición de APIs que prioriza la definición del contrato antes de empezar a lanzar el resto de procesos, como la implementación, testing, despliegue,...

Sus principales ventajas son:

- Reduce el time to market casi un 50%.
- Mejora la calidad de la API puesto que permite que los consumidores puedan empezar a trabajar con el contrato sin haberse implementado y por tanto, es más susceptibles a cambios
- Mejora los procesos: Del primer contrato se suelen automatizar el resto de fases, lo que hace que se automaticen los procesos.
- Permite generar contract tests. Permite autogenerar las pruebas para los equipos de QA y desarrollo y además, que los equipos de Desarrollo puedan probarse con los tests realizados en QA
- Mejora la seguridad. Al definir el contrato primero, permite validar la seguridad basada en la definición y encontrar bugs de seguridad antes de haber empezado a implementar nada.

La metodología se implementa en 3 fases:

- Fase de definición y mocking: Se define el contrato openapi, se valida y se genera un mocking para los clientes.
- Fase de implementación: Gracias al contrato, desarrollamos tres actividades en paralelo:
  * Definición de los tests: Se desarrollan los tests utilizando el mock server mientras que los desarrolladores terminan la implementación
  * Desarrollo e implementación de la API: Utilizando herramientas de generación de arquetipos, como APIGen, generamos y desarrollamos la implementación de la API. Una vez desarrollada la API, se probarán con los tests automatizados por QA.
  * Consumo de la API: Las apis se exponen a traves de las herramientas de api management apuntando al mock server, lo que le permite a los consumidores empezar a probar y desarrollar la funcionalidad y mejorar el contrato api conjuntamente con el desarrollador de la API.
- Fase de integración: Una vez implementada la API debemos reapuntar la API del mockserver a la implementación realizada. 


## <a name="bibliography">Bibliografía</a> [&#8593;](#index)

- [https://restfulapi.net/](https://restfulapi.net/)
- [Representational state transfer](https://en.wikipedia.org/wiki/Representational_state_transfer)
- [RESTful API Design Patterns and Best Practices - Harihara Subramanian](https://www.packtpub.com/product/hands-on-restful-api-design-patterns-and-best-practices/9781788992664)
- [URI](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier)
- [Spring HATEOAS](https://www.baeldung.com/spring-hateoas-tutorial)
- [Spring JPA pagination](https://www.baeldung.com/spring-data-jpa-pagination-sorting)
- [Swagger annotations](https://www.baeldung.com/spring-rest-openapi-documentation)
- [API first](https://cloudappi.net/metodologia-api-first/)
- [API design interview questions](https://blog.postman.com/api-design-interview-questions/)
- [API documentation](https://apichangelog.substack.com/p/five-elements-of-good-api-documentation)