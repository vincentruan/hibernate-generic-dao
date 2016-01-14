

# Introduction #

The framework features a powerful and flexible search functionality. This is used by passing a search object to search methods on general and generic DAOs.

The search object provides flexible search options:
  * Filtering on properties using standard operators ( =, !=, >, <, >=, <=, LIKE, IN, IS NULL, IS EMPTY ).
  * Filtering on collections and associations using ( SOME, ALL, NONE ).
  * Combining individual filters with any combination of logical operators ( AND, OR, NOT ).
  * Sorting on properties.
  * Paging.
  * Defining a search remotely from client code.
  * Transforming search results into objects, lists, arrays and maps
  * Specifying which associations to fetch eagerly.
  * Specifying column operators such as COUNT, SUM, AVG, MAX, etc.
  * All of the above work with nested properties (i.e. properties of related objects)

# Details #
A Search object is the POJO that carries the various parameters to pass to a search. The core elements are lists of Filter, Sort and Field objects, and there are a number of other options.

**Javadocs**:
  * http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/search/ISearch.html
  * http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/search/IMutableSearch.html
  * http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/search/Search.html

For more help with Searches look at the SearchExamples page.

# Full API Reference #

### General ###
The framework's search functionality API takes an `ISearch`. This interface is meant
to be a read-only interface for a search object. The framework will not modify an `ISearch`. There is another interface, `IMutableSearch` which provides more methods for altering search parameters.

Anyone can provide their own implementation of the `ISearch` and/or `IMutableSearch` objects, but the framework includes a general one with lots of extra convenience methods for manipulating search parameters. This is the `Search` class. This `Search` class will probably be all you need for all your server-side operations. (Note: The framework also has an example of another `ISearch` implementation with the Flex search.)

### What to search For ###
A search object has a `searchClass` property that specifies which entity to search for. This may be left `null` when using a specific generic DAO; the DAO already knows what entity it is searching for.

**_Example:_**
```
//create a new search for Projects
new Search(Project.class);

//or set the search class on an existing IMutableSearch
search.setSearchClass(Project.class);
```

### Filtering ###
A search can have a collection of _filters_. By default there is an 'AND' condition between these; however, the _disjunction_ property can be set to TRUE, and an 'OR' condition will be used.

See http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/search/Filter.html.

Each Filter object has three fields:
  * **property:** The name of the property to filter on. It may be nested. Examples: "name", "dateOfBirth", "employee.age", "employee.spouse.job.title". _NOTE: the empty string ("") or the constant Filter.ROOT\_ENTITY can be used to specify the root entity._
  * **operator:** The type of comparison to do between the property and the value.
  * **value:** The value to compare the property with. Should be of a compatible type with the property. Examples: "Fred", new Date(), 45

A filter can be created simply setting the fields and adding it to the search...
```
Filter f = new Filter();
f.setProperty("name");
f.setOperator(Filter.OP_EQUAL);
f.setValue("Bob");
search.addFilter(f);

//or
Filter f = new Filter("name", "Bob", Filter.OP_EQUAL);
search.addFilter(f);
```

The Filter class also has static methods for creating filters with certain operators...
```
Filter f = Filter.equal("name", "Bob"); //creates a filter with the OP_EQUAL operator
search.addFilter(f);

//or another example with the OP_GREATER_THAN operator
search.addFilter( Filter.greaterThan("age", 7) );
```

Finally, the `Search` class has methods for creating and adding a filter all in one step...
```
search.addFilterEqual("name", "Bob");
search.addFilterGreaterThan("age", 7);
```

The following filtering operators are available:
| **Operator** | **Explanation** | **Example** |
|:-------------|:----------------|:------------|
| EQUAL        |                 | `Filter.equal("name", "Bob")` |
| NOT\_EQUAL   |                 | `Filter.notEqual("age", 5)` |
| GREATER\_THAN |                 | `Filter.greaterThan("age", 5)` |
| GREATER\_OR\_EQUAL |                 | `Filter.greaterOrEqual("name", "M")` |
| LESS\_THAN   |                 | `Filter.lessThan("name", "N")` |
| LESS\_OR\_EQUAL |                 | `Filter.lessOrEqual("age", 65)` |
| IN           | Equal to one of the items in the list of values. Value can be a collection or an array. | `Filter.in("eyeColor", EyeColor.BLUE, EyeColor.HAZEL)` |
| NOT\_IN      |                 | `Filter.notIn("hairColor", HairColor.BROWN, HairColor.BLACK)` |
| LIKE         | Takes a SQL like expression | `Filter.like("name", "Wil%")` |
| ILIKE        | LIKE + ignore case (Note: Some databases ignore case by default) | `Filter.ilike("name", "wiL%")` |
| NULL         | SQL IS NULL     | `Filter.isNull("primaryDoctor")` |
| NOT\_NULL    |                 | `Filter.isNotNull("phone")` |
| EMPTY        | NULL or empty string or empty collection/assocation | `Filter.isEmpty("children")` |
| NOT\_EMPTY   |                 | `Filter.isNotEmpty("primaryDoctor.firstName")` |
| SOME         | Applies to collection/association properties. Takes another `Filter` as a value, and matches when at least one of the values in the collection matches the filter. | `Filter.some("children", Filter.equal("name", "Joey")) //has a child named 'Joey'` |
| ALL          | Same as SOME, except that all values must match the filter. | `Filter.all("children", Filter.greaterOrEqual("age", 18)) //all children are 18 or older` |
| NONE         | Same as SOME, except that none of the values may match the filter. | `Filter.none("pets", Filter.and(Filter.equal("species", "cat"), Filter.lessThan("age", .75)) //has no cats under 9 months old` |
| AND          | Takes no property. Takes an array or collection of `Filter`s as a value. Matches when all the filters in the value match. | `Filter.and(Filter.greaterOrEqual("age",40), Filter.lessThan("age", 65))` |
| OR           | Same as AND, except that it matches when any of the filters match. | `Filter.or(Filter.like("firstName","Wil%"), Filter.like("lastName","Wil%"))` |
| NOT          | Takes no property. Takes a single `Filter` as a value. Matches when the filter in the value does not match. | `Filter.not(Filter.ilike("name", "W%")) //name does not start with 'w'` |
| `**` For more examples of all these operators, see SearchExamples | | |

**NOTE:** If any filter with an operator that expects a value has a null value, that filter is ignored. This is very handy in the case of writing a find method where null arguments are ignored. For example:
```
/**
 * This is a DAO method that returns all the people with the given first and/or last name.
 * If any parameter is null, that parameter is ignored. So only non-null parameters are used in defining the search criteria.
 */
public List<Person> findPeopleByName(String firstName, String lastName) {
    return search(new Search(Person.class).addFilterEqual("firstName", firstName).addFilterEqual("lastName", lastName));
}
```

**NOTE:** Filters also support using the special HQL properties ".id", ".class" and ".size". For example, the following expression would produce a filter that would match objects whose members collection contains more than 10 elements:
```
Filter.greaterThan("members.size", 10)
```

**_Filter by Example_**

The framework has a built-in utility for converting example objects into Filters that can be used in a search. All DAOs and the SearchFacade provide two methods for doing this:
```
public Filter getFilterFromExample(Object example);
public Filter getFilterFromExample(Object example, ExampleOptions options);
```
The first method uses the default options for doing the conversion, while the second provides several options for controlling the result. See http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/com/googlecode/genericdao/search/ExampleOptions.html for all the options.

### Selecting and Transforming Results ###
By default, the results of a search will be a list of the entity objects that were searched on. However, as with Hibernate queries, it is possible to get different information back in each row.

**Fields**

A search can have a collection of _fields_. These are analogous to the "SELECT" clause in SQL or HQL. Each field corresponds to a property value or a column operator applied to a property. Each field may also be assigned a key string. This will be used instead of the property name as a map key when using the MAP result mode. If no fields are specified, the entity itself is used as the single result for each record.

See http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/search/Field.html.

**NOTE:** the empty string ("") or the constant Filter.ROOT\_ENTITY can be used to specify the root entity.

Here are the available field operators:
  * PROPERTY (i.e. no operator)
  * COUNT
  * COUNT\_DISTINCT
  * MAX
  * MIN
  * SUM
  * AVG

**Result Modes**

The following result modes are available for a search:
| AUTO | The result mode is automatically determined according to the following rules: If any field is specified with a key, use MAP mode. Otherwise, if zero or one fields are specified, use SINGLE mode. Otherwise, use ARRAY mode. |
|:-----|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ARRAY | returns each result as an Object array (`Object[]`) with the entries corresponding to the fields added to the search.                                                                                                         |
| LIST | returns each result as a list of Objects (`List<Object>`).                                                                                                                                                                    |
| MAP  | returns each row as a map with properties' names or keys for keys to the corresponding values.                                                                                                                                |
| SINGLE | Exactly one field or no fields must be specified to use this result mode. The result list contains just the value of that property for each element or the entity if no fields are specified.                                 |

**Distinct**

The `distinct` option can be set on a search in order to filter out duplicate results. As with plain HQL or SQL, only use this option if your search requires it because it does affect performance.

"Distinct" can only be used on queries that return a single field. Here are some examples:
```
search(new Search().setDistinct(true)); //would work. Only the root entity is returned for each result.
search(new Search().setDistinct(true).addField("firstName"); //would work. Only the single "firstName" value is returned for each result.
search(new Search().setDistinct(true).addField("firstName").addField("lastName")); //would NOT work. Two fields are returned for each result.
```

**_Examples:_**
```
Search search = new Search(Project.class);

//The first field is the name of the project. 
search.addField("name");
//Project has a many-to-one relationship with type. Type objects have a 'name' property.
//We are using "type" for the key (or alias) of this field.
search.addField("type.name", "type");

//Using resultMode AUTO, this will produce a Map for each result because a key is
//specified. Each result will be something like
//Map{ 'name': 'Hibernate Generic DAO', 'type': 'cooler than cool' }.
dao.search(search);

search.setResultMode(Search.RESULT_ARRAY);
//Now using resultMode ARRAY, each result will be something like
//Array[ 'Hibernate Generic DAO', 'cooler than cool' ]
dao.search(search);


```

### Sorting ###
A search can have a collection of _sorts_. These sorts are applied in order, just as in the SQL "ORDER BY" clause. Each sort specifies a property and a direction (asc or desc). There is also a flag for whether or not to ignore case. (Note: Some databases ignore case by default.)

See http://hibernate-generic-dao.googlecode.com/svn/trunk/docs/api/index.html?com/googlecode/genericdao/search/Sort.html.

**_Examples:_**
```
//this produces: ORDER BY startDate DESC
search.addSort( Sort.desc("startDate") );

//this produces: ORDER BY lower(name) ASC
search.addSort( Sort.asc("name", true) ); //second parameter specifies ignore case

//this produces: ORDER BY lastName ASC, firstName ASC
search.addSortAsc("lastName").addSortAsc("firstName");
```

### Paging ###
Searches have three properties that define paging behavior.

  * `maxResults` - the maximum number of results to return. (If `<= 0, maxResults` is ignored.)
  * `firstResult` - the zero-based offset of the first result to return. (If `<= 0, firstResult` is ignored.)
  * `page` - the zero-based offset of the first page to return. `page` applies only if `firstResult` is `<= 0`. `maxResults` is used as the page size for determining the record offset. For example, if `maxResults` is 10 and `page` is 3, results 30 through 39 (zero-based) will be returned.

**_Example:_**
```
search.setMaxResults(10);
search.setPage(3);
```

### Controlling Association Fetching ###
Search objects can also have a _fetches_ collection. Each fetch is simply a string that identifies an association to eagerly fetch.

**_Example:_**
```
Search search = new Search(Project.class);
search.addFetch("members");

//The members of each project in the result set will be join fetched with
//the results of this search.
```

### Miscellaneous ###
Search supports the use of HQL special properties ".id", ".class" and ".size" where ever they are supported in HQL. Just include them within the property. Ex: `"members.size"`, `"class"`, `"id.project"`.

# Additional Usage examples #
```
Search search = new Search(Project.class);

search.addFilterEqual("name", "hibernate-generic-dao");

search.addFilterLessThan("completionDate", new Date());

search.addFilterOr(
		Filter.equal("name", "Jack"),
		Filter.and(
				Filter.equal("name", "Jill"),
				Filter.like("location", "%Chicago%"),
				Filter.greaterThan("age", 5)
		)
);

search.addFilterIn("name", "Jack", "Jill", "Bob");

search.addFilterNot(Filter.in("name","Jack", "Jill", "Bob"));


search.addSort("name");
search.addSort("age", true); //descending

//paging
search.setMaxResults(15);
search.setPage(3);
```

Nested properties are also fully supported...
```
search.addFilterEqual("status.name", "active");
search.addFilterGreaterThan("workgroup.manager.salary", 75000.00);

search.addSort("status.name");
```

Calling a search:
```
Search search = new Search();
search.addFilterGreaterThan("userCount", 500);
search.setMaxResults(15);

//get one page of results
List<Project> results = projectDAO.search(search);

//get the total number of results (ignores paging)
int totalResults = projectDAO.count(search);

//get one page of results and the total number of results without paging
SearchResult<Project> result = projectDAO.searchAndCount(search);
results = result.getResults();
totalResults = results.getTotalCount();

//get the average userCount for project matching the filter criteria
search.addField("userCount", Field.OP_AVG);
long avgCount = (long) projectDAO.searchUnique(search);
```

More examples... SearchExamples