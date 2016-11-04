# Flow-Server

Access resources in vert.x.

## Getting started

Add the following dependency

```xml
<dependency>
    <groupId>com.bookislife</groupId>
    <artifactId>flow-rest-server</artifactId>
    <version>0.0.2</version>
</dependency>
```

## Usage

### 注解

#### @Path

值为字符串,用于指定 Resource 匹配的路径,可以标注在类上或方法上.
标注在类上表示类中所有方法的根路径,标注在方法上表示方法的子路径.

所有方法只有添加该注解才会被当做 Resource 进行处理.

例

```java
@Path("/hello")
public class HelloResource {
    @GET
    @Path("getString")
    public String getString(){}
}
```

以上示例中, `getString()` 方法匹配 `/hello/getString` 路径.

#### @GET, @POST

用于标示匹配的 Http 请求的 Method,省略时默认匹配 GET 请求.

#### @Consumes

用于标示匹配的 Http 请求的 Content-Type,省略时表示接受所有类型的 Content-Type.

例

```java
@Consumes(MediaType.TEXT_PLAIN)
public String getString(){}
```

其它常用类型为

- `MediaType.TEXT_PLAIN`  字符串
- `MediaType.APPLICATION_JSON` JSON 数据
- `MediaType.APPLICATION_FORM_URLENCODED` Form 表单
- `MediaType.MULTIPART_FORM_DATA` Multipart Form

#### @Produces

用于标示方法执行后返回的数据类型,省略时默认为 `MediaType.TEXT_PLAIN`.

返回字符串

```java
@Produces(MediaType.TEXT_PLAIN)
public String getString() {
    return "foobar";
}
```

返回 JSON

```java
@Produces(MediaType.APPLICATION_JSON)
public Map<String, Object> getJson() {
    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("x", 1);
    return jsonMap;
}
```

#### @PathParam

用于指定匹配的 `@Path` 标示的路径中的子路径名,子路径的值会被自动注入到其中

`@Path` 中的参数需要以 `:参数名` 形式进行定义,且一个路径中不能同时出现两个完全相同的参数名

例

```java
@Path("query/:name")
public Person queryPerson(@PathParam(value = "name" String name){
    System.out.println("name=" + name);
}
```

请求 `http://localhost/hello/query/peter` 该方法会打印 `name=peter`

#### @QueryParam

用于指定匹配的 `@Path` 标示的路径中的参数名,参数值会被自动注入到其中

例

```java
@Path("find")
public Person findPerson(@QueryParam(value = "name") String name,
                        @QueryParam(value = "age) int age){
    System.out.println("name=" + name);
    System.out.println("age=" + age);
}
```

请求 `http://localhost/hello/find?name=peter&age=20` 该方法会打印

```
name=peter
age=20
```

#### @FormParam


用于指定匹配的表单的参数名,表单可以为 `MULTIPART_FORM_DATA` 也可以为 `APPLICATION_FORM_URLENCODED`.

例

```java
@Post
@Path("postForm")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public void submit(@FormParam(value = "name") String name,
                        @FormParam(value = "age) int age){
    System.out.println("name=" + name);
    System.out.println("age=" + age);
}
```

请求 

```
curl -X POST -H "Content-Type: application/x-www-form-urlencoded" \
-d 'x=foo&y=100' "http://localhost/hello/postForm"
```

控制台会输出如下语句

```
name=peter
age=20
```


