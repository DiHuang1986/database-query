# database-query

database-query is a small project based on JDBC, and can be used to simplify SQL build and execution.

database-query contains mainly two parts

* Query
* DatabaseWrapper

## Query

`Query` is a java class, which can be used to simply SQL build.

It requires a `Connection` for use.

```java
Query query = Query.connect(connection);
```

### building Query

#### .table()

`.table()` method is used to specify `table` in SQL statement.

```java
query.table("people").all();
```

#### .select()

`.select()` is used to specify the `select` part in SQL statement.

If no `select` specified, use `*` as default.

```java
query.select("id, name, age").table("people").all();
// if no select spe
```

#### .where()

`.where()` method is used to specify `where` in SQL statement.

```java
query.table("people")
    .where("name", "TEST")
    .all();
```

##### .whereRaw()

`.whereRaw()` is used to handle complex `where` in SQL statement.

```java
query.table("people")
    // people.last_name is a reference of other column, can not use .where()
    .whereRaw("name = people.last_name")
    .all();
```

##### .whereIn()

`.whereIn()` used to handle `where xx in (xx, xx, xx)` in SQL statement

```java
query.table("people")
    .whereIn("name", new String[] {"TEST 1", "TEST 2", "TEST 3"})
    .all();
```

##### .whereLike()

`.whereLike()` used to handle `where xx like '%test%'` in SQL statement.

```java
query.table("people")
    .whereLike("name", "test")
    .all();
```

#### .join()

`.join()` method is used to specify `join` in SQL statement.

`Query` also provide `.leftJoin()` and `.rightJoin()` as well

```java
query.table("people")
    .join("chair", "people.id = chair.owner_id")
    .join("chair_leg", "chair.id = chair_leg.chair_id")
    .all();
```

#### .paginate()

`.paginate()` method is used to specify pagination logic in SQL statement.

```java
query.table("people")
    // page 1, and one page 20 items
    .paginate(1, 20)
    .get();
```

#### .orderBy()

`.orderBy()` method is used to append `order by **` to SQL statement.

```java
query.table("people")
    .orderBy("age", "desc")
    .get();
```

#### .groupBy()

`.groupBy()` method is used to append `group by **` to SQL statement.

```java
query.table("people")
    .select("name, count(*) count")
    .groupBy("name")
    .get();
```

#### .statement()

`.statement()` method is used for complex SQL statement, while use statement, all other building methods will be ignored.

```java
query.statement(
    "select * from people" + 
    "join chair on people.id = chair.owner_id join " + 
    "join chair_leg on chair.id = chair_leg.chair_id" + 
    "where chair.name like '%old%'" +
    "and chair.height < 100" + 
    "and chair.width > 10"
)
    .execute();
```

### assign parameters

#### .param()

`.param()` used to assign a parameter to the Query object, when Query execute itself, it will use these parameters.

parameter in SQL statement should be `:paramName`.

```java
query.table("people")
	.whereRaw("name = :name")
    .param("name", "test")
    .get();
```

#### .params()

`.params()` takes a Map<String, Object> Object, and do multi assignment in one step.

```java
query.table("people")
	.whereRaw("name = :name")
    .params(Literals.Map(
    	"name", "test"
    ))
    .get();
```

### execute

#### .get() and .all()

`.get()` method is used to execute the Query object, and will consider the `paginate` parameters. 

`.get()` method can also take one `IRowToEntityHandler<T extends Entity>` implementation as handler.

`.all()` is same as `.get()` but will ignore the `paginate` parameters.

`.all()` can also take one `IRowToEntityHanlder` implementation as parameter.

```java
class People {
    String name;
    int age;
}

List<People> peoples = query.table("people")
    .get(row -> {
        People p = new People();
        p.name = row.getAsString("name");
        p.age = row.getAsInteger("age");
        return p;
    });
```

#### .first()

`.first()` return the first row of `.get()`. 

`.first()` can take `IRowToEntityHandler` to convert the row to entity.

```java
People people = query.table("people")
    .first(row -> {
        People p = new People();
        p.name = row.getAsString("name");
        p.age = row.getAsInteger("age");
        return p;
    });
```

#### .execute()

`.execute()` method just execute the Query Object, and return a `boolean`.

`.execute()` method can also take a `String` as SQL statement directly.

```java
query.statement("update people set age = 2 where name = 'some'")
    .execute();
// they get the same result 
query.execute("update people set age = 2 where name = 'some'");
```

#### .executeQuery()

`.executeQuery()` method can return result.

```java
List<Row> rows = query.executeQuery("select * from people");
```

#### .count()

`.count()` method return the count of rows. If no parameter provided, count `*`;

```java
int count 1 = query.table("people")
    .count();

int count2 = query.table("people")
    .count("distinct name");
```

## DatabaseWrapper

`DatabaseWrapper` is a class used to handle `IDatabaseExecution<T>` and `IDatabaseExecutionVoid`. 

It requires a `DataSource` as a constructor parameter.

### IDatabaseExecution and IDatabaseExecutionVoid

Both them are interfaces, and contains only one method

```java
T execute(Connection conn) throws SQLException;

// for void
void execute(Connection conn) throws SQLException;
```

### methods

#### .execute()

`.execute()` method takes IDatabaseExecution or IDatabaseExecutionVoid as parameter, and will provide a `connection` for that execution and execute it.

```java
databaseWrapper.execute(conn -> {
    // do things with the conn
    // if no return, void
    // else return needed result
});
```

#### .transaction()

`.transaction()` method do the same thing as `.execute()` but will put them in a transaction process, if any error happened, the transaction will rollback.

```java
databaseWrapper.transaction(conn -> {
    // do things with the conn
    // if no return, void
    // else return needed result
});
```

