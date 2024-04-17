# RESTful API design & best practices

## �Qu� vamos a ver?

En este apartado se pretende recoger, de manera resumida, los diferentes tipos de patrones y buenas pr�cticas a la hora de dise�ar API RESTful. Adem�s se plantear�n algunos ejemplos sobre c�mo llevarlos a cabo bajo el framework Spring.

## <a name="index">�ndice:</a>
 - [�Qu� es una API vs API REST vs API RESTful?](#what-is)
 - [Buenas pr�cticas](#best-practices)
     + Nomenclatura consistente
     + Eficiencia
     + Seguridad
     + Versionado
     + Manejo de errores
     + Documentaci�n
 - [API first](#api-first)
 - [Bibliograf�a](#bibliography)


## <a name="what-is">�Qu� es una API vs REST vs API RESTful?</a> [&#8593;](#index) 

Una [API](https://es.wikipedia.org/wiki/API) es un conjunto de reglas y protocolos que permite a diferentes sistemas de software comunicarse entre s� y que compartan informaci�n y funcionalidades.

- Orientado a m�quinas, no a usuarios
- No est� atado a un lenguaje de programaci�n
- Permite desacoplar, escalar y reutilizar sistemas

Existen muchos tipos de APIs. Algunas de las m�s utilizadas pueden ser:

![APIs|100](doc/APIs.png)


[REST](https://es.wikipedia.org/wiki/Transferencia_de_Estado_Representacional) es un estilo de arquitectura software para servicios web con las siguientes restricciones:

- Arquitectura cliente-servidor a trav�s de protocolo HTTP
- Sin estado, permite una mayor escalabilidad
- Interface uniforme bien definida. Cada recurso proporciona:
  + Identificaci�n: URL
  + Manipulaci�n: uso de formatos JSON, XML, HTML, �
  + Mensajes autodescriptivos: nombre del recurso, m�todos HTTP y metadatos
  + Hiperv�nculos: HATEOAS, representa c�mo cambiar el estado de un recurso
- Cacheable, permite una mejor eficiencia


La palabra RESTful describe a aquellas APIs o servicios que se adhieren a los principios REST. Para ello una API RESTful debe seguir las reglas definidas por REST.

S�lo con las restricciones vistas en el punto anterior a�n queda todo bastante abierto a la interpretaci�n de cada desarrollador. Por ello, aunque no hay un est�ndar claro aceptado por la comunidad, s� se hace necesario detallar unas buenas pr�cticas que ayuden a obtener APIs RESTful de calidad.

Para ello tendremos en cuenta los siguientes objetivos:
- Hacerla f�cil de usar para los desarrolladores. La calidad de una API es directamente proporcional a la facilidad de su integraci�n.
- Utilizar patrones, terminolog�a est�ndar y desarrollar comportamientos homog�neos
- Manejar los errores de manera clara e intuitiva
- Proveer documentaci�n


## <a name="best-practices">Buenas pr�cticas</a> [&#8593;](#index) 

### Nomenclatura consistente

##### URI's para representar recursos

Las URI's son la puerta de entrada para una API REST, por lo que es importante que tengan la estructura correcta.

![URI|100](doc/URI.png)

Recomendaciones generales:
- Claridad: Deben definir de forma clara y sencilla la representaci�n del recurso.
- Legibilidad: Usar s�lo minusculas, evitar guiones bajos (�_�) y puntos (�.�) y si es necesario, usar (�-�) en palabras compuestas (kebab-case).
- Homogeneidad: Recursos en plural, acordar terminolog�a (id, bookId, idBook, ...) y no incluir barra (�/�) al final de la ruta.
- Seguridad: Nunca enviar informaci�n sensible en los par�metros, independientemente de si se usa HTTPS.


##### Modelado de recursos

Un recurso es un conjunto de datos que vamos a servir desde nuestra API RESTful.

El manejo de recursos deber� realizarse mediante representaciones, es decir, las peticiones y respuestas pueden ser un subconjunto de datos del recurso. De esta manera controlaremos tambi�n los datos a los que cada usuario/rol est� autorizado.

Sobre cada recurso permitiremos realizar diferentes tipos de operaciones:

- CRUD
- ACTIONS: Definen una acci�n particular realizada sobre el recurso, utilizando el m�todo POST y terminando en verbo.

Veamos algunos ejemplos:

**CRUD** (Create, Read, Update y Delete)

![CRUD|100](doc/CRUD.png)

**ACTIONS**

![ACTIONS|100](doc/ACTIONS.png)


##### Negociaci�n del contenido

Ofrece la posibilidad de que un cliente pueda solicitar la informaci�n de un recurso bajo un formato o idioma determinado:


Se puede manejar de diferentes formas:
- **Mediante cabecera**: Accept=application/json � Accept-languague=ES_es
- **Mediante queryParam**: ?accept=json � ?accept-language=ES_es

<img src="doc/contentNegotiation.png" alt="Content Negotiation"/>

En caso de que el formato no sea soportado se recomienda informar debidamente al cliente, por ejemplo devolviendo un error con c�digo *406 - Not Acceptable*.

<img src="doc/contentNegotiation_notAcceptable.png" alt="Content Negotiation - Not Acceptable"/>


### Eficiencia

##### Paginaci�n

Estrategia dise�ada para devolver un subconjunto de datos de un recurso en lugar de su totalidad. Por tanto se considerar�a una representaci�n de recursos, no un recurso en s�.

Beneficios:
- Mejor performance
- Mayor fiabilidad ante sobrecargas

La respuesta deber�a devolver un c�digo *200 - Ok*, si se obtienen todos los recursos disponibles, � *206 - Partial Content*, si se obtiene s�lo una parte

Existen diferentes formas de implementar la paginaci�n:

- **Cursor based**: utilizando par�metros como *page* y *size*
- **Time based**: utilizando par�metros como *since* y *until*
- **Offset based**: utilizando par�metros como *offset* y *limit*

<img src="doc/URI_query.png" alt="URI query"/>


#### Cach�

La cach� es la capacidad de almacenar copias de datos a los que se accede con frecuencia en varios lugares a lo largo de la ruta solicitud-respuesta. 

La optimizaci�n de la red mediante el almacenamiento en cach� mejora la calidad general del servicio de las siguientes maneras:

- Reduce el ancho de banda
- Reduce la latencia
- Reducir la carga de los servidores
- Ocultar los fallos de la red

Las peticiones GET deber�an ser almacenables en cach� por defecto, hasta que se d� una condici�n especial.

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


#### Operaciones as�ncronas

Una operaci�n 'bulk' se diferencia de una operaci�n 'batch' en que la primera es una operaci�n �nica con m�ltiples objetos y la segunda son m�ltiples operaciones con m�ltiples objetos.

Pongamos varios ejemplos:

- Bulk: registrar varias acciones a un inversor
- Batch: registrar varias acciones a varios inversores (por ejemplo en el caso de que el valor de las acciones baje de precio)

Para ello es posible utilizar el m�todo PATCH del recurso de manera que el body provea el listado de objetos a incorporar.

Este tipo de operaciones pueden conllevar una baja performance por lo que podr�a ser necesario resolverlos de forma as�ncrona (devolviendo *202 - Accepted*) o mediante la implementaci�n de flujos en paralelo para una respuesta m�s r�pida.


#### Idempotencia

La idempotencia es una caracter�stica basada en que ante una misma petici�n, la respuesta deber�a ser id�ntica. Este patr�n viene a solucionar sobre todo ciertos problemas de concurrencia o de peticiones repetidas. Por defecto los m�todos GET, PUT y DELETE son considerados idempotentes, al contrario que los m�todos POST y PATCH. 

En la mayor�a de casos podr�a ser suficiente con adaptar algunos endpoints de manera que siempre devuelvan la misma respuesta. Un [ejemplo](restful-sv/src/main/java/org/example/restful/service/InvestorService.java) podr�a ser una petici�n DELETE que elimine un inversor: si el inversor existe lo elimina y si ya fue eliminado anteriormente no hace nada. De este modo el cliente no tendr� que preocuparse en caso de recibir un error por inversor no encontrado ya que no le aporta ning�n valor.

En caso de que sea necesario puede hacerse uso de la cabecera ETag para validar si el resultado sigue siendo el mismo. De lo contrario se recomendar�a notificar al cliente mediante un error HTTP *409 - Conflict*


#### BTF (Backend to Frontend)

Existe una problem�tica espec�fica para las aplicaciones multiplataforma: aquellas que tienen una web y una app movil ligera. En estos casos, por ejemplo, la app podr�a necesitar menos informaci�n y una mejor performance que la que pudiera resolver la web. Por ello se propone la implementaci�n de interfaces especializadas para cada uno de los clientes (*/api/web/resource* vs */api/app/resource*). Esto puede resultar controvertido dadas las ventajas e inconvenientes que conlleva:

**Ventajas:**
- Permite tener una representaci�n de los recursos m�s reducida para aquellos clientes que no necesiten tanta informaci�n 
- Podr�amos mejorar la performance para estos clientes que necesitaran una respuesta mucho m�s r�pida

**Inconvenientes:**
- Ser�a necesario mantener ambas interfaces con un comportamiento muy similar


### Seguridad

Un aspecto importante en las APIs RESTful es la seguridad. A continuaci�n enumeraremos algunos de los puntos m�s importantes:

- Pol�tica de m�nimos permisos (@RolesAllowed)
- Hazlo simple. Cuanto m�s 'innecesariamente' compleja es una soluci�n, m�s facil es dejar abierta alguna brecha
- Siempre usar el protocolo HTTPs para asegurar las conexiones
- Utiliza contrase�as con hash
- Nunca exponer informaci�n sensible en las URLs tales como usuarios, contrase�as, tokens, etc. tal y como ya comentamos en el apartado [URI query](#uri-query)
- Considera el uso de OAuth en lugar de la autenticaci�n b�sica (aunque �sta sea suficiente)
- Valida los par�metros de entrada (@Valid)

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

Es muy dificil afirmar que una API va a mantenerse intacta a lo largo de su vida sin necesidad de ning�n cambio. En el caso de cambios menores, correcci�n de errores, etc. no ser�a necesario un versionado del mismo. Sin embargo, para cambios importantes o que pudieran resultar incompatibles con el funcionamiento de la versi�n anterior se recomienda anotar con versiones cada uno de los comportamientos.

A continuaci�n veremos diferentes formas de versionar una API:

- Mediante URI path: *http://localhost/v2/resource*
- Mediante query parameter: *?version=2.0*
- Mediante custom headers: *x-resource-version=2.0*
- Mediante content-negociation: *Accept=application/resource-v2.0+json*

<img src="doc/versioning.png" alt="URI path versioning"/>

Quedar� en manos del negocio la decisi�n de durante cuanto tiempo se deber�n mantener versiones antiguas de un *endPoint* y el momento oportuno para darlo totalmente de baja.


##### Redirecciones

Aquellos endpoints que quedaran obsoletos o temporalmente inutilizables deber�an comunicar correctamente esta situaci�n y, en caso de que sea posible, proveer una alternativa al cliente. De este modo podr�amos devolver un error HTTP *301 - Permanent redirect* o *307 - Temporary redirect* e incluir la cabecera *location* con la url alternativa.

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

Este patr�n permite al cliente poder recuperarse y finalizar la operaci�n de forma satisfactoria. En nuestro caso de ejemplo, la propia interface de Swagger ya se encarga de redireccionar y devolver los resultados desde la url alternativa:

<img src="doc/redirectSwagger.png" alt="Redirect swagger"/>


### Manejo de errores

Es recomendable asociar correctamente el c�digo de error HTTP devuelto con el mensaje mostrado, con el objetivo de evitar malentendidos y ser m�s transparente de cara al usuario.
Para ello es una pr�ctica com�n generar excepciones personalizadas que nos permitan identificar correctamente el momento y la causa del error.

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

Sin embargo, por motivos de seguridad, no deber�amos mostrar informaci�n sensible demasiado detallada en estos mensajes por las siguientes razones:
- En caso de una brecha de seguridad, un atacante podr�a explotar la informaci�n obtenida en estas respuestas, ya sean trazas, identificadores, etc.
- Normalmente estos mensajes pueden ser almacenados en ficheros de logs en claro, dejando los datos expuestos para ser explotados m�s tarde.

La clave est� en el equilibrio.


### Documentaci�n

##### Swagger


##### Descubrimiento

Tambi�n conocido como HATEOAS (Hypermedia As The Engine Of Application State). Provee un listado de URL�s con las operaciones que se permiten realizar sobre el recurso.

Por ejemplo, como respuesta a una operaci�n de creaci�n POST, la respuesta podr�a devolver la siguiente estructura:

<img src="doc/HATEOAS.png" alt="HATEOAS"/>

Del mismo modo tambi�n ser�a de utilidad limitar aquellos m�todos HTTP que no est�n permitidos (*405 - Method Not Allowed*) para informar al cliente sobre qu� operaciones puede y no puede realizar en el recurso.

<img src="doc/notAllowedMethod.png" alt="Method Not Allowed"/>


## <a name="api-first">API first</a> [&#8593;](#index)

Se trata de una metodolog�a de definici�n de APIs que prioriza la definici�n del contrato antes de empezar a lanzar el resto de procesos, como la implementaci�n, testing, despliegue,...

Sus principales ventajas son:

- Reduce el time to market casi un 50%.
- Mejora la calidad de la API puesto que permite que los consumidores puedan empezar a trabajar con el contrato sin haberse implementado y por tanto, es m�s susceptibles a cambios
- Mejora los procesos: Del primer contrato se suelen automatizar el resto de fases, lo que hace que se automaticen los procesos.
- Permite generar contract tests. Permite autogenerar las pruebas para los equipos de QA y desarrollo y adem�s, que los equipos de Desarrollo puedan probarse con los tests realizados en QA
- Mejora la seguridad. Al definir el contrato primero, permite validar la seguridad basada en la definici�n y encontrar bugs de seguridad antes de haber empezado a implementar nada.

La metodolog�a se implementa en 3 fases:

- Fase de definici�n y mocking: Se define el contrato openapi, se valida y se genera un mocking para los clientes.
- Fase de implementaci�n: Gracias al contrato, desarrollamos tres actividades en paralelo:
  * Definici�n de los tests: Se desarrollan los tests utilizando el mock server mientras que los desarrolladores terminan la implementaci�n
  * Desarrollo e implementaci�n de la API: Utilizando herramientas de generaci�n de arquetipos, como APIGen, generamos y desarrollamos la implementaci�n de la API. Una vez desarrollada la API, se probar�n con los tests automatizados por QA.
  * Consumo de la API: Las apis se exponen a traves de las herramientas de api management apuntando al mock server, lo que le permite a los consumidores empezar a probar y desarrollar la funcionalidad y mejorar el contrato api conjuntamente con el desarrollador de la API.
- Fase de integraci�n: Una vez implementada la API debemos reapuntar la API del mockserver a la implementaci�n realizada. 


## <a name="bibliography">Bibliograf�a</a> [&#8593;](#index)

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